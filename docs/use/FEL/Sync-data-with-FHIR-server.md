# Sync Data with FHIR Server

This guide explains how to synchronize data between your Android app and a FHIR server using the Android FHIR Engine. The FHIR Engine provides two ways to achieve this: **one-time sync** and **periodic sync**.

**One-time sync** allows you to trigger a synchronization job manually, on demand. This is useful for scenarios where you want to control when data is synchronized, such as when a user explicitly requests it.

**Periodic sync** allows you to schedule a job that automatically synchronizes data with the FHIR server at regular intervals. This is useful for keeping the local data in your app up-to-date with the server.

## Initial setup

Before you can start syncing data, you need to perform some initial setup.

### Server requirements

The FHIR server you are syncing with must support the [optional capability to allow clients to `PUT` resources to locations that don't yet exist (upsert)](https://www.hl7.org/fhir/http.html#upsert).

### Add dependencies and permissions

* Add the `work-runtime-ktx` dependency to your app-level `build.gradle` file, typically `app/build.gradle`:

```kotlin
dependencies {
  // ...
  implementation("androidx.work:work-runtime-ktx:2.8.1")
}
```

* Include the Internet permission in your `AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

  <application ...>
    ...
  </application>
  <uses-permission android:name="android.permission.INTERNET" />
</manifest>
```

### Configure FhirEngine

* During `FhirEngineProvider.init()`, provide a `ServerConfiguration` object with at least the `baseUrl` set to the URL of your FHIR server as part of the [`FhirEngineConfiguration`](https://github.com/google/android-fhir/blob/master/engine/src/main/java/com/google/android/fhir/FhirEngineProvider.kt#L106):

```kotlin
FhirEngineProvider.init(
  FhirEngineConfiguration(
    enableEncryptionIfSupported = true,
    databaseErrorStrategy = DatabaseErrorStrategy.RECREATE_AT_OPEN,
    serverConfiguration = ServerConfiguration("https://your-fhir-server-url"),
  )
)
```

#### Advanced configuration

The [`ServerConfiguration`](https://github.com/google/android-fhir/blob/master/engine/src/main/java/com/google/android/fhir/FhirEngineProvider.kt#L143) takes in other parameters as well.

Optionally, you can customize network connection parameters like timeouts and gzip compression using [`NetworkConfiguration`](https://github.com/google/android-fhir/blob/master/engine/src/main/java/com/google/android/fhir/FhirEngineProvider.kt#L164). By default, `uploadWithGzip` is set to `false`. If you are uploading to Google Cloud FHIR Store or a HAPI FHIR Server, we recommend setting it to `true` as those two servers support gzip compression.

If your server requires authentication, implement the [`HttpAuthenticator`](https://github.com/google/android-fhir/blob/master/engine/src/main/java/com/google/android/fhir/sync/HttpAuthenticator.kt#L33) interface and provide it to `ServerConfiguration`. This allows you to specify how the FHIR Engine should authenticate with the server. For example, let's say your server requires OAuth 2.0 Bearer Token authentication. To use it, implement the `HttpAuthenticator` interface using the [HttpAuthenticationMethod.Bearer](https://github.com/google/android-fhir/blob/master/engine/src/main/java/com/google/android/fhir/sync/HttpAuthenticator.kt#L52) function, which should return the bearer token string required by the server. It will be added to the header of all requests.

For debugging purposes, you can configure the `httpLogger` parameter in `ServerConfiguration` to log the communication between the FHIR Engine and the server.

Putting all this together, we can have a ServerConfiguration that looks like this:

```kotlin
        ServerConfiguration(
          "https://hapi.fhir.org/baseR4/",
          httpLogger =
            HttpLogger(
              HttpLogger.Configuration(
                if (BuildConfig.DEBUG) HttpLogger.Level.BODY else HttpLogger.Level.BASIC
              )
            ) { Timber.tag("App-HttpLog").d(it) },
          networkConfiguration = NetworkConfiguration(uploadWithGzip = true),
          authenticator = { HttpAuthenticationMethod.Bearer("mySecureToken") }
        )
```

### Implement DownloadWorkManager

Create a subclass of [`DownloadWorkManager`](https://github.com/google/android-fhir/blob/master/engine/src/main/java/com/google/android/fhir/sync/DownloadWorkManager.kt) to define how the engine should generate download requests and process responses from the server. You can refer to the [`TimestampBasedDownloadWorkManagerImpl`](https://github.com/google/android-fhir/blob/master/demo/src/main/java/com/google/android/fhir/demo/data/TimestampBasedDownloadWorkManagerImpl.kt) in the demo app for an example implementation.

### Implement FhirSyncWorker

Finally, provide an implementation of `FhirSyncWorker`. This class is responsible for defining how your app interacts with the FHIR Engine during synchronization. It has four functions to implement:

* **`getDownloadWorkManager()`**: This should return the implementation of `DownloadWorkManager` you created earlier.
* **`getConflictResolver()`**: This controls how conflicts between the local and remote versions of resources are resolved. You can set it to `AcceptLocalConflictResolver` if the local version should take precedence, or `AcceptRemoteConflictResolver` if the remote version should.
* **`getFhirEngine()`**: This should return your application's `FhirEngine` instance.
* **`getUploadStrategy()`**: This defines how local changes are uploaded to the FHIR server. Currently, the only supported strategy is `UploadStrategy.AllChangesSquashedBundlePut`, which squashes all local changes into a single bundle and uses the `PUT` method to upload it.

Here's an example implementation:

```kotlin
class FhirPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  FhirSyncWorker(appContext, workerParams) {

  override fun getDownloadWorkManager(): DownloadWorkManager {
    return DownloadWorkManagerImpl() // Replace with your implementation
  }

  override fun getConflictResolver() = AcceptLocalConflictResolver

  override fun getFhirEngine() = FhirApplication.fhirEngine(applicationContext)

  override fun getUploadStrategy(): UploadStrategy {
    return UploadStrategy.AllChangesSquashedBundlePut
  }
}
```

Remember to replace `DownloadWorkManagerImpl` with your actual implementation.

See the [`DemoFhirSyncWorker`](https://github.com/google/android-fhir/blob/master/demo/src/main/java/com/google/android/fhir/demo/data/DemoFhirSyncWorker.kt) in the demo app for a more complete example.

## Perform synchronization

Once you have completed the initial setup, you can start syncing data with the FHIR server.

### One-Time Sync

Use the [Sync.oneTimeSync](https://github.com/google/android-fhir/blob/master/engine/src/main/java/com/google/android/fhir/sync/Sync.kt#L54) API:

```kotlin
Sync.oneTimeSync<FhirPeriodicSyncWorker>(applicationContext)
```

This triggers a single sync job. You can collect the `SyncJobStatus` from the returned `Flow` to monitor the progress and result of the job.

```kotlin
CoroutineScope.launch {
  Sync.oneTimeSync<DemoFhirSyncWorker>(applicationContext)
    .shareIn(this, SharingStarted.Eagerly, 0)
    .collect {  /* Handle SyncJobStatus here */ }
}
```

### Periodic Sync

Use the [Sync.periodicSync](https://github.com/google/android-fhir/blob/master/engine/src/main/java/com/google/android/fhir/sync/Sync.kt#L80) API.

```kotlin
Sync.periodicSync<YourFhirSyncWorker>(
  applicationContext,
  PeriodicSyncConfiguration(
    syncConstraints = Constraints.Builder().build(),
    repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES)
  )
)
```

This schedules a periodic sync job that runs according to the specified `PeriodicSyncConfiguration`. You can customize the sync constraints and repeat interval as needed. Similar to one-time sync, you can collect the `PeriodicSyncJobStatus` from the returned `Flow` to monitor the job.

```kotlin
CoroutineScope.launch {
  Sync.periodicSync<DemoFhirSyncWorker>(
      application.applicationContext,
      periodicSyncConfiguration =
        PeriodicSyncConfiguration(
          syncConstraints = Constraints.Builder().build(),
              repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES)
        )
    )
    .shareIn(this, SharingStarted.Eagerly, 10)
    .collect { /**Handle SyncJobStatus Here*/ }
}
```

Note: Both sync methods utilize the WorkManager library under the hood. For more information on WorkManager, refer to the [official documentation](https://developer.android.com/reference/androidx/work/WorkManager)
