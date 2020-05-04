// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.fhirengine.db.impl

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import ca.uhn.fhir.parser.IParser
import com.google.fhirengine.db.ResourceAlreadyExistsInDbException
import com.google.fhirengine.db.ResourceNotFoundInDbException
import com.google.fhirengine.index.FhirIndexer
import com.google.fhirengine.resource.ResourceUtils
import org.hl7.fhir.r4.model.Resource
import javax.inject.Inject

/**
 * The implementation for the persistence layer using Room.
 * See docs for [com.google.fhirengine.db.Database] for the API docs.
 */
internal class DatabaseImpl @Inject constructor(
        context: Context,
        private val iParser: IParser,
        fhirIndexer: FhirIndexer
) : com.google.fhirengine.db.Database {
    val db = Room.inMemoryDatabaseBuilder(context, RoomResourceDb::class.java)
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
}