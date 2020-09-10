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
import androidx.room.Transaction
import ca.uhn.fhir.parser.IParser
import com.google.fhirengine.db.ResourceNotFoundInDbException
import com.google.fhirengine.db.impl.entities.SyncedResourceEntity
import com.google.fhirengine.index.FhirIndexer
import com.google.fhirengine.resource.ResourceUtils
import com.google.fhirengine.search.impl.Query
import com.google.fhirengine.sync.model.Update
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
        Room.inMemoryDatabaseBuilder(context, ResourceDatabase::class.java)
    } else {
        Room.databaseBuilder(context, ResourceDatabase::class.java, databaseName)
    }
    val db = builder
            // TODO https://github.com/jingtang10/fhir-engine/issues/32
            //  don't allow main thread queries
            .allowMainThreadQueries()
            .build()
    val resourceDao by lazy {
        db.resourceDao().also {
            it.fhirIndexer = fhirIndexer
            it.iParser = iParser
        }
    }
    val syncedResourceDao = db.syncedResourceDao()

    override fun <R : Resource> insert(resource: R) {
        resourceDao.insert(resource)
    }

    override fun <R : Resource> insertAll(resources: List<R>) {
        resourceDao.insertAll(resources)
    }

    override fun <R : Resource> update(resource: R) {
        resourceDao.update(resource)
    }

    override fun <R : Resource> select(clazz: Class<R>, id: String): R {
        val type = ResourceUtils.getResourceType(clazz)
        return resourceDao.getResource(
                resourceId = id,
                resourceType = type
        )?.let {
            iParser.parseResource(clazz, it)
        } ?: throw ResourceNotFoundInDbException(type.name, id)
    }

    override suspend fun lastUpdate(resourceType: ResourceType): String? {
        return syncedResourceDao.getLastUpdate(resourceType)
    }

    @Transaction
    override suspend fun insertSyncedResources(
      syncedResourceEntity: SyncedResourceEntity,
      resources: List<Resource>
    ) {
        syncedResourceDao.insert(syncedResourceEntity)
        insertAll(resources)
    }

    override fun <R : Resource> delete(clazz: Class<R>, id: String) {
        val type = ResourceUtils.getResourceType(clazz)
        resourceDao.deleteResource(
                resourceId = id,
                resourceType = type
        )
    }

    override fun <R : Resource> searchByReference(
      clazz: Class<R>,
      reference: String,
      value: String
    ): List<R> {
        return resourceDao.getResourceByReferenceIndex(
                ResourceUtils.getResourceType(clazz).name, reference, value)
                .map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource> searchByString(
      clazz: Class<R>,
      string: String,
      value: String
    ): List<R> {
        return resourceDao.getResourceByStringIndex(
            resourceType = ResourceUtils.getResourceType(clazz).name,
            indexPath = string,
            indexValue = value
        ).map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource> searchByCode(
      clazz: Class<R>,
      code: String,
      system: String,
      value: String
    ): List<R> {
        return resourceDao.getResourceByCodeIndex(
            resourceType = ResourceUtils.getResourceType(clazz).name,
            indexPath = code,
            indexSystem = system,
            indexValue = value
        ).map { iParser.parseResource(it) as R }
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

    override fun <R : Resource> search(query: Query): List<R> =
            resourceDao.getResources(query.getSupportSQLiteQuery())
                .map { iParser.parseResource(it) as R }

    companion object {
        private const val DEFAULT_DATABASE_NAME = "ResourceDatabase"
    }

    /**
     * Get a list of all updates
     */
    override fun getUpdates(): List<Update> {
        val TEST_INSERT = "{\n" +
                "  \"resourceType\": \"Patient\",\n" +
                "  \"id\": \"animal\",\n" +
                "  \"text\": {\n" +
                "    \"status\": \"generated\",\n" +
                "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"></div>\"\n" +
                "  },\n" +
                "  \"extension\": [\n" +
                "    {\n" +
                "      \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-animal\",\n" +
                "      \"extension\": [\n" +
                "        {\n" +
                "          \"url\": \"species\",\n" +
                "          \"valueCodeableConcept\": {\n" +
                "            \"coding\": [\n" +
                "              {\n" +
                "                \"system\": \"http://hl7.org/fhir/animal-species\",\n" +
                "                \"code\": \"canislf\",\n" +
                "                \"display\": \"Dog\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"url\": \"breed\",\n" +
                "          \"valueCodeableConcept\": {\n" +
                "            \"coding\": [\n" +
                "              {\n" +
                "                \"system\": \"http://snomed.info/sct\",\n" +
                "                \"code\": \"58108001\",\n" +
                "                \"display\": \"Golden retriever\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"system\": \"http://example.org/fhir/CodeSystem/animal-breed\",\n" +
                "                \"code\": \"gret\",\n" +
                "                \"display\": \"Golden Retriever\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"url\": \"genderStatus\",\n" +
                "          \"valueCodeableConcept\": {\n" +
                "            \"coding\": [\n" +
                "              {\n" +
                "                \"system\": \"http://hl7.org/fhir/animal-genderstatus\",\n" +
                "                \"code\": \"neutered\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"identifier\": [\n" +
                "    {\n" +
                "      \"type\": {\n" +
                "        \"text\": \"Dog Tag\"\n" +
                "      },\n" +
                "      \"system\": \"http://www.maroondah.vic.gov.au/AnimalRegFees.aspx\",\n" +
                "      \"value\": \"1234123\",\n" +
                "      \"period\": {\n" +
                "        \"start\": \"2010-05-31\"\n" +
                "      },\n" +
                "      \"assigner\": {\n" +
                "        \"display\": \"Maroondah City Council\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"active\": true,\n" +
                "  \"name\": [\n" +
                "    {\n" +
                "      \"use\": \"usual\",\n" +
                "      \"given\": [\n" +
                "        \"Kenzi\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"gender\": \"female\",\n" +
                "  \"birthDate\": \"2010-03-23\",\n" +
                "  \"contact\": [\n" +
                "    {\n" +
                "      \"relationship\": [\n" +
                "        {\n" +
                "          \"coding\": [\n" +
                "            {\n" +
                "              \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0131\",\n" +
                "              \"code\": \"C\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"name\": {\n" +
                "        \"family\": \"Chalmers\",\n" +
                "        \"given\": [\n" +
                "          \"Peter\",\n" +
                "          \"James\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"telecom\": [\n" +
                "        {\n" +
                "          \"system\": \"phone\",\n" +
                "          \"value\": \"(03) 5555 6473\",\n" +
                "          \"use\": \"work\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"managingOrganization\": {\n" +
                "    \"display\": \"Pete's Vetinary Services\"\n" +
                "  }\n" +
                "}"
        val TEST_UPDATE = "[\n" +
                " {\n" +
                "  \"op\": \"replace\",\n" +
                "  \"path\": \"/contact/0/name/family\",\n" +
                "  \"value\": \"Rogers\"\n" +
                " },\n" +
                " {\n" +
                "  \"op\": \"replace\",\n" +
                "  \"path\": \"/name/0/given/0\",\n" +
                "  \"value\": \"Binny\"\n" +
                " },\n" +
                " {\n" +
                "  \"op\": \"replace\",\n" +
                "  \"path\": \"/extension/0/extension/1/valueCodeableConcept/coding/1/display\",\n" +
                "  \"value\": \"Labrador Retriever\"\n" +
                " },\n" +
                " {\n" +
                "  \"op\": \"replace\",\n" +
                "  \"path\": \"/extension/0/extension/1/valueCodeableConcept/coding/1/code\",\n" +
                "  \"value\": \"lbret\"\n" +
                " },\n" +
                " {\n" +
                "  \"op\": \"replace\",\n" +
                "  \"path\": \"/extension/0/extension/1/valueCodeableConcept/coding/0/display\",\n" +
                "  \"value\": \"Labrador retriever\"\n" +
                " },\n" +
                " {\n" +
                "  \"op\": \"replace\",\n" +
                "  \"path\": \"/extension/0/extension/1/valueCodeableConcept/coding/0/code\",\n" +
                "  \"value\": \"62137007\"\n" +
                " }\n" +
                "]"
        return listOf(
                Update("Patient", "animal", TEST_INSERT, Update.Type.INSERT),
                Update("Patient", "animal", TEST_UPDATE, Update.Type.UPDATE),
                Update("Patient", "animal", "", Update.Type.DELETE)
        )
    }

    /**
     * Delete local changes for resource with given id and type. Call this after
     * a successful sync with server to apply the squashed change locally and
     * remove all local changes.
     *
     * This effectively marks a resource as synced with the remote server.
     */
    override fun deleteLocalChanges(resourceId: String, resourceType: String) {
        TODO("Not yet implemented")
    }
}
