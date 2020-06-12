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
import androidx.room.Room
import ca.uhn.fhir.parser.IParser
import com.google.fhirengine.db.ResourceNotFoundInDbException
import com.google.fhirengine.index.FhirIndexer
import com.google.fhirengine.resource.ResourceUtils
import com.google.fhirengine.search.impl.ResourceQuery
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * The implementation for the persistence layer using Room.
 * See docs for [com.google.fhirengine.db.Database] for the API docs.
 */
internal class DatabaseImpl(
  context: Context,
  private val iParser: IParser,
  fhirIndexer: FhirIndexer,
  databaseName: String?
) : com.google.fhirengine.db.Database {
    constructor(
      context: Context,
      iParser: IParser,
      fhirIndexer: FhirIndexer
    ) : this(
            context = context,
            iParser = iParser,
            fhirIndexer = fhirIndexer,
            databaseName = DEFAULT_DATABASE_NAME)
    val builder = if (databaseName == null) {
        Room.inMemoryDatabaseBuilder(context, RoomResourceDb::class.java)
    } else {
        Room.databaseBuilder(context, RoomResourceDb::class.java, databaseName)
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
        dao.insert(resource)
    }

    override fun <R : Resource> insertAll(resources: List<R>) {
        dao.insertAll(resources)
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

    // TODO implement the last update date query
    override fun lastUpdate(resourceType: ResourceType): String? {
        return "2020-04-28T11:27:40.123+00:00"
    }

    override fun <R : Resource> delete(clazz: Class<R>, id: String) {
        val type = ResourceUtils.getResourceType(clazz)
        dao.deleteResource(
                resourceId = id,
                resourceType = type
        )
    }

    override fun <R : Resource> searchByReference(
      clazz: Class<R>,
      reference: String,
      value: String
    ): List<R> {
        return dao.getResourceByReferenceIndex(
                ResourceUtils.getResourceType(clazz).name, reference, value)
                .map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource> searchByString(
      clazz: Class<R>,
      string: String,
      value: String
    ): List<R> {
        return dao.getResourceByStringIndex(ResourceUtils.getResourceType(clazz).name, string,
                value).map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource> searchByCode(
      clazz: Class<R>,
      code: String,
      system: String,
      value: String
    ): List<R> {
        return dao.getResourceByCodeIndex(ResourceUtils.getResourceType(clazz).name, code, system,
            value).map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource> searchByReferenceAndCode(
      clazz: Class<R>,
      reference: String,
      referenceValue: String,
      code: String,
      codeSystem: String,
      codeValue: String
    ): List<R> {
        val refs = searchByReference(clazz, reference, referenceValue).map { it.id }
        return searchByCode(clazz, code, codeSystem, codeValue).filter { refs.contains(it.id) }
    }

    override fun <R : Resource> search(query: ResourceQuery): List<R> =
            dao.getResources(query.getSupportSQLiteQuery()).map { iParser.parseResource(it) as R }

    companion object {
        private const val DEFAULT_DATABASE_NAME = "ResourceDatabase"
    }
}
