/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.fhirengine.db.impl

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import ca.uhn.fhir.parser.IParser
import com.google.fhirengine.db.ResourceAlreadyExistsInDbException
import com.google.fhirengine.db.ResourceNotFoundInDbException
import com.google.fhirengine.index.FhirIndexer
import com.google.fhirengine.resource.ResourceUtils
import javax.inject.Inject
import org.hl7.fhir.r4.model.Resource

/**
 * The implementation for the persistence layer using Room.
 * See docs for [com.google.fhirengine.db.Database] for the API docs.
 */
internal class DatabaseImpl(
  context: Context,
  private val iParser: IParser,
  fhirIndexer: FhirIndexer,
  inMemory: Boolean
) : com.google.fhirengine.db.Database {
    @Inject constructor(
      context: Context,
      iParser: IParser,
      fhirIndexer: FhirIndexer
    ) : this(
        context = context,
        iParser = iParser,
        fhirIndexer = fhirIndexer,
        inMemory = false)
    val builder = if (inMemory) {
        Room.inMemoryDatabaseBuilder(context, RoomResourceDb::class.java)
    } else {
        Room.databaseBuilder(context, RoomResourceDb::class.java, DATABASE_NAME)
    }
    val db = builder
            // TODO https://github.com/jingtang10/fhir-engine/issues/32
            //  don't allow main thread queries
            .allowMainThreadQueries()
            .build()
    val dao by lazy {
        db.dao().also {
            it.fhirIndexer = fhirIndexer
            it.iParser = iParser
        }
    }

    override fun <R : Resource> insert(resource: R) {
        try {
            dao.insert(resource)
        } catch (constraintException: SQLiteConstraintException) {
            throw ResourceAlreadyExistsInDbException(
                    resource.resourceType.name,
                    resource.id,
                    constraintException
            )
        }
    }

    override fun <R : Resource> update(resource: R) {
        dao.update(resource)
    }

    override fun <R : Resource> select(clazz: Class<R>, id: String): R {
        val type = ResourceUtils.getResourceType(clazz)
        return dao.getResource(
                resourceId = id,
                resourceType = type
        )?.let {
            iParser.parseResource(clazz, it)
        } ?: throw ResourceNotFoundInDbException(type.name, id)
    }

    override fun <R : Resource> delete(clazz: Class<R>, id: String) {
        val type = ResourceUtils.getResourceType(clazz)
        dao.deleteResource(
                resourceId = id,
                resourceType = type
        )
    }

    override fun <R : Resource?> searchByReference(
      clazz: Class<R>,
      reference: String,
      value: String
    ): List<R>? {
        return dao.getResourceByReferenceIndex(
                ResourceUtils.getResourceType(clazz).name, reference, value)
                .map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource?> searchByString(
      clazz: Class<R>?,
      string: String,
      value: String
    ): List<R> {
        return dao.getResourceByStringIndex(ResourceUtils.getResourceType(clazz).name, string,
                value).map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource?> searchByCode(
      clazz: Class<R>?,
      code: String,
      system: String,
      value: String
    ): List<R> {
        return dao.getResourceByCodeIndex(ResourceUtils.getResourceType(clazz).name, code, system,
                value).map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource?> searchByReferenceAndCode(
      clazz: Class<R>,
      reference: String,
      refvalue: String,
      string: String,
      system: String,
      value: String
    ): List<R>? {
        val refs = searchByReference(clazz, reference, refvalue)?.map { it?.id }
        return searchByCode(clazz, string, system, value).filter { refs!!.contains(it?.id) }
    }

    companion object {
        private const val DATABASE_NAME = "ResourceDatabase"
    }
}
