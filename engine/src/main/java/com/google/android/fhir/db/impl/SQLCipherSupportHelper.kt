package com.google.android.fhir.db.impl

import android.database.sqlite.SQLiteException
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.google.android.fhir.DatabaseErrorStrategy
import com.google.android.fhir.db.DatabaseEncryptionException
import com.google.android.fhir.db.DatabaseEncryptionException.DatabaseEncryptionErrorCode.TIMEOUT
import com.google.android.fhir.db.DatabaseEncryptionException.DatabaseEncryptionErrorCode.UNKNOWN
import com.google.android.fhir.db.impl.DatabaseImpl.Companion.UNENCRYPTED_DATABASE_NAME
import java.time.Duration
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import timber.log.Timber

/** A [SupportSQLiteOpenHelper] which initializes SQLCipher with a passphrase. */
internal class SQLCipherSupportHelper(
    private val configuration: SupportSQLiteOpenHelper.Configuration,
    private val hook: SQLiteDatabaseHook? = null,
    private val databaseErrorStrategy: DatabaseErrorStrategy,
    private val passphraseFetcher: () -> ByteArray,
) : SupportSQLiteOpenHelper {

    init {
        System.loadLibrary("sqlcipher")
    }

    @Volatile private var delegate: SupportSQLiteOpenHelper? = null
    @Volatile private var walEnabled: Boolean = false

    override val databaseName: String?
        get() = configuration.name

    override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
        walEnabled = enabled
        delegate?.setWriteAheadLoggingEnabled(enabled)
    }

    override val writableDatabase: SupportSQLiteDatabase
        get() {
            check(!configuration.context.getDatabasePath(UNENCRYPTED_DATABASE_NAME).exists()) {
                "Unexpected unencrypted database, $UNENCRYPTED_DATABASE_NAME, already exists. " +
                        "Check if you have accidentally disabled database encryption across releases."
            }

            val helper = delegate ?: createDelegate().also { delegate = it }

            return try {
                helper.writableDatabase
            } catch (ex: SQLiteException) {
                if (databaseErrorStrategy == DatabaseErrorStrategy.RECREATE_AT_OPEN) {
                    Timber.w("Fail to open database. Recreating database.")
                    configuration.context.getDatabasePath(databaseName).delete()

                    // Reset and retry with a fresh helper instance
                    delegate?.close()
                    delegate = null
                    createDelegate().also { delegate = it }.writableDatabase
                } else {
                    throw ex
                }
            }
        }

    override val readableDatabase: SupportSQLiteDatabase
        get() = writableDatabase

    override fun close() {
        delegate?.close()
        delegate = null
    }

    /**
     * Creates a SQLCipher-aware SupportSQLiteOpenHelper using SupportOpenHelperFactory.
     */
    private fun createDelegate(): SupportSQLiteOpenHelper {
        val passphrase = runBlocking { getPassphraseWithRetry() }

        val factory =
            if (hook == null) {
                SupportOpenHelperFactory(passphrase)
            } else {
                SupportOpenHelperFactory(
                    passphrase,
                    hook,
                    /* enableWriteAheadLogging = */ walEnabled,
                )
            }

        return factory.create(configuration)
    }

    private suspend fun getPassphraseWithRetry(): ByteArray {
        var lastException: DatabaseEncryptionException? = null
        for (retryAttempt in 1..MAX_RETRY_ATTEMPTS) {
            try {
                return passphraseFetcher()
            } catch (exception: DatabaseEncryptionException) {
                lastException = exception
                if (exception.errorCode == TIMEOUT) {
                    Timber.i("Fail to get the encryption key on attempt: $retryAttempt")
                    delay(retryDelay.toMillis() * retryAttempt)
                } else {
                    throw exception
                }
            }
        }
        Timber.w("Can't access the database encryption key after $MAX_RETRY_ATTEMPTS attempts.")
        throw lastException ?: DatabaseEncryptionException(Exception(), UNKNOWN)
    }

    private companion object {
        const val MAX_RETRY_ATTEMPTS = 3

        /** The time delay before retrying a database operation. */
        val retryDelay: Duration = Duration.ofSeconds(1)
    }
}
