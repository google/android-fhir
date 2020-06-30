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

package com.google.fhirengine.impl

import com.google.fhirengine.FhirEngine
import com.google.fhirengine.ResourceNotFoundException
import com.google.fhirengine.db.Database
import com.google.fhirengine.db.ResourceNotFoundInDbException
import com.google.fhirengine.resource.ResourceUtils
import com.google.fhirengine.search.Search
import com.google.fhirengine.sync.FhirDataSource
import com.google.fhirengine.sync.FhirSynchronizer
import com.google.fhirengine.sync.Result
import com.google.fhirengine.sync.SyncConfiguration
import java.util.EnumSet
import org.cqframework.cql.elm.execution.VersionedIdentifier
import org.hl7.fhir.r4.model.Resource
import org.opencds.cqf.cql.data.DataProvider
import org.opencds.cqf.cql.execution.CqlEngine
import org.opencds.cqf.cql.execution.EvaluationResult
import org.opencds.cqf.cql.execution.LibraryLoader
import org.opencds.cqf.cql.terminology.TerminologyProvider

/** Implementation of [FhirEngine].  */
class FhirEngineImpl constructor(
  private val database: Database,
  private val search: Search,
  libraryLoader: LibraryLoader,
  dataProviderMap: Map<String, @JvmSuppressWildcards DataProvider>,
  terminologyProvider: TerminologyProvider,
  private val periodicSyncConfiguration: SyncConfiguration,
  private val dataSource: FhirDataSource
) : FhirEngine {

    private val cqlEngine: CqlEngine = CqlEngine(
        libraryLoader,
        dataProviderMap,
        terminologyProvider,
        EnumSet.noneOf(CqlEngine.Options::class.java)
    )

    override fun <R : Resource> save(resource: R) {
        database.insert(resource)
    }

    override fun <R : Resource> saveAll(resources: List<R>) {
        database.insertAll(resources)
    }

    override fun <R : Resource> update(resource: R) {
        database.update(resource)
    }

    @Throws(ResourceNotFoundException::class)
    override fun <R : Resource> load(clazz: Class<R>, id: String): R {
        return try {
            database.select(clazz, id)
        } catch (e: ResourceNotFoundInDbException) {
            throw ResourceNotFoundException(ResourceUtils.getResourceType(clazz).name, id, e)
        }
    }

    override fun <R : Resource> remove(clazz: Class<R>, id: String): R {
        throw UnsupportedOperationException("Not implemented yet!")
    }

    override fun evaluateCql(
      libraryVersionId: String,
      context: String,
      expression: String
    ): EvaluationResult {
        val contextMap: MutableMap<String, Any> = HashMap()
        val contextSplit = context.split("\\/").toTypedArray()
        contextMap[contextSplit[0]] = contextSplit[1]
        val versionedIdentifier = VersionedIdentifier().withId(libraryVersionId)
        val expressions: MutableSet<String> = HashSet()
        expressions.add(expression)
        val map: MutableMap<VersionedIdentifier, Set<String>> = HashMap()
        map[versionedIdentifier] = expressions
        return cqlEngine.evaluate(contextMap, null, map)
    }

    override fun search(): Search {
        return search
    }

    override suspend fun sync(syncConfiguration: SyncConfiguration): Result {
        return FhirSynchronizer(syncConfiguration, dataSource, database).sync()
    }

    override suspend fun periodicSync(): Result {
        return FhirSynchronizer(periodicSyncConfiguration, dataSource, database).sync()
    }
}
