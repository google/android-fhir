/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.workflow.testing

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.model.api.IQueryParameterType
import ca.uhn.fhir.parser.IParser
import ca.uhn.fhir.rest.api.EncodingEnum
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException
import ca.uhn.fhir.util.BundleBuilder
import com.google.common.collect.ImmutableMap
import java.io.File
import java.io.FileNotFoundException
import java.util.Locale
import java.util.Objects
import java.util.function.Consumer
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseConformance
import org.hl7.fhir.instance.model.api.IBaseParameters
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IIdType
import org.opencds.cqf.fhir.api.Repository
import org.opencds.cqf.fhir.utility.Ids
import org.opencds.cqf.fhir.utility.dstu3.AttachmentUtil
import org.opencds.cqf.fhir.utility.matcher.ResourceMatcher
import org.opencds.cqf.fhir.utility.repository.IGLayoutMode
import org.opencds.cqf.fhir.utility.repository.Repositories
import org.opencds.cqf.fhir.utility.repository.ResourceCategory

/**
 * This class implements the Repository interface on onto a directory structure that matches the
 * standard IG layout.
 */
class IGInputStreamStructureRepository(
  private val fhirContext: FhirContext,
  private val root: String? = null,
  private val layoutMode: IGLayoutMode = IGLayoutMode.DIRECTORY,
  private val encodingEnum: EncodingEnum = EncodingEnum.JSON,
) : Loadable(), Repository {
  private val resourceCache: MutableMap<String, IBaseResource> = HashMap()
  private val parser: IParser = parserForEncoding(fhirContext, encodingEnum)
  private val resourceMatcher: ResourceMatcher = Repositories.getResourceMatcher(fhirContext)

  fun clearCache() {
    resourceCache.clear()
  }

  protected fun <T : IBaseResource?, I : IIdType?> locationForResource(
    resourceType: Class<T>,
    id: I,
  ): String {
    val directory = directoryForType(resourceType)
    return directory + "/" + fileNameForLayoutAndEncoding(resourceType.simpleName, id!!.idPart)
  }

  protected fun fileNameForLayoutAndEncoding(resourceType: String, resourceId: String): String {
    val name = resourceId + fileExtensions[encodingEnum]
    return if (layoutMode === IGLayoutMode.DIRECTORY) {
      // TODO: case sensitivity!!
      resourceType.lowercase(Locale.getDefault()) + "/" + name
    } else {
      "$resourceType-$name"
    }
  }

  protected fun <T : IBaseResource?> directoryForType(resourceType: Class<T>): String {
    val category = ResourceCategory.forType(resourceType.simpleName)
    val directory = categoryDirectories[category]

    // TODO: what the heck is the path separator?
    return (if (root!!.endsWith("/")) root else "$root/") + directory
  }

  protected fun <T : IBaseResource?> directoryForResource(resourceType: Class<T>): String {
    val directory = directoryForType(resourceType)
    return if (layoutMode === IGLayoutMode.DIRECTORY) {
      directory + "/" + resourceType.simpleName.lowercase(Locale.getDefault())
    } else {
      directory
    }
  }

  protected fun <T : IBaseResource, I : IIdType> readLocation(
    resourceClass: Class<T>?,
    location: String,
  ): T {
    return resourceCache.computeIfAbsent(location) { l ->
      try {
        val x = parser.parseResource(resourceClass, open(l))
        return@computeIfAbsent handleLibrary<T>(x, l)
      } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException(e)
      }
    } as T
  }

  protected fun <T : IBaseResource> handleLibrary(resource: T, location: String?): T {
    var resourceOutput = resource
    if (resourceOutput.fhirType() == "Library") {
      val cqlLocation: String?
      when (fhirContext.version.version) {
        FhirVersionEnum.DSTU3 -> {
          cqlLocation = AttachmentUtil.getCqlLocation(resourceOutput)
          if (cqlLocation != null) {
            resourceOutput =
              AttachmentUtil.addData(
                resourceOutput,
                getCqlContent(location, cqlLocation),
              ) as T
          }
        }
        FhirVersionEnum.R4 -> {
          cqlLocation =
            org.opencds.cqf.fhir.utility.r4.AttachmentUtil.getCqlLocation(resourceOutput)
          if (cqlLocation != null) {
            resourceOutput =
              org.opencds.cqf.fhir.utility.r4.AttachmentUtil.addData(
                resourceOutput,
                getCqlContent(location, cqlLocation),
              ) as T
          }
        }
        FhirVersionEnum.R5 -> {
          cqlLocation =
            org.opencds.cqf.fhir.utility.r5.AttachmentUtil.getCqlLocation(resourceOutput)
          if (cqlLocation != null) {
            resourceOutput =
              org.opencds.cqf.fhir.utility.r5.AttachmentUtil.addData(
                resourceOutput,
                getCqlContent(location, cqlLocation),
              ) as T
          }
        }
        else ->
          throw IllegalArgumentException(
            String.format(
              "unsupported FHIR version: %s",
              fhirContext,
            ),
          )
      }
    }
    return resourceOutput
  }

  protected fun getCqlContent(rootPath: String?, relativePath: String?): String {
    val p = File(File(rootPath).parent, relativePath).normalize().toString()
    return try {
      load(p)
    } catch (e: Exception) {
      e.printStackTrace()
      throw RuntimeException(e)
    }
  }

  protected fun <T : IBaseResource> readLocation(resourceClass: Class<T>): Map<IIdType, T> {
    val location = directoryForResource(resourceClass)
    val resources = HashMap<IIdType, T>()

    val inputFiles = listFiles(location)

    for (file in inputFiles) {
      if (
        layoutMode.equals(IGLayoutMode.DIRECTORY) ||
          (layoutMode.equals(IGLayoutMode.TYPE_PREFIX) &&
            file.startsWith(resourceClass.simpleName + "-"))
      ) {
        try {
          val r = this.readLocation<T, IIdType>(resourceClass, "$location/$file")
          if (r.fhirType() == resourceClass.simpleName) {
            resources[r.idElement.toUnqualifiedVersionless()] = r
          }
        } catch (e: RuntimeException) {
          e.printStackTrace()
        }
      }
    }
    return resources
  }

  override fun fhirContext(): FhirContext {
    return fhirContext
  }

  override fun <T : IBaseResource, I : IIdType> read(
    resourceType: Class<T>,
    id: I,
    headers: Map<String, String>,
  ): T {
    Objects.requireNonNull(resourceType, "resourceType can not be null")
    Objects.requireNonNull(id, "id can not be null")
    val location = locationForResource(resourceType, id)
    var r: T? = null
    try {
      r = readLocation<T, IIdType>(resourceType, location)
    } catch (e: RuntimeException) {
      e.printStackTrace()
      if (e.cause is FileNotFoundException) {
        throw ResourceNotFoundException(id)
      }
    }
    if (r == null) {
      throw ResourceNotFoundException(id)
    }
    if (r.idElement == null) {
      throw ResourceNotFoundException(
        String.format(
          "Expected to find a resource with id: %s at location: %s. Found resource without an id instead.",
          id.toUnqualifiedVersionless(),
          location,
        ),
      )
    }
    if (r.idElement.toUnqualifiedVersionless() != id.toUnqualifiedVersionless()) {
      throw ResourceNotFoundException(
        String.format(
          "Expected to find a resource with id: %s at location: %s. Found resource with an id %s instead.",
          id.toUnqualifiedVersionless(),
          location,
          r.idElement.toUnqualifiedVersionless(),
        ),
      )
    }
    return r
  }

  override fun <T : IBaseResource?> create(
    resource: T,
    headers: Map<String, String>,
  ): MethodOutcome {
    throw UnsupportedOperationException("IGInputStream is Readonly")
  }

  override fun <I : IIdType?, P : IBaseParameters?> patch(
    id: I,
    patchParameters: P,
    headers: Map<String, String>,
  ): MethodOutcome {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'patch'")
  }

  override fun <T : IBaseResource?> update(
    resource: T,
    headers: Map<String, String>,
  ): MethodOutcome {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("IGInputStream is Readonly")
  }

  override fun <T : IBaseResource?, I : IIdType?> delete(
    resourceType: Class<T>,
    id: I,
    headers: Map<String, String>,
  ): MethodOutcome {
    throw UnsupportedOperationException("IGInputStream is Readonly")
  }

  override fun <B : IBaseBundle, T : IBaseResource> search(
    bundleType: Class<B>,
    resourceType: Class<T>,
    searchParameters: MutableMap<String, List<IQueryParameterType>>,
    headers: Map<String, String>,
  ): B {
    val builder = BundleBuilder(fhirContext)
    val resourceIdMap = readLocation(resourceType)
    if (searchParameters == null || searchParameters.isEmpty()) {
      resourceIdMap.values.forEach(
        Consumer { theResource: T ->
          builder.addCollectionEntry(
            theResource,
          )
        },
      )
      builder.setType("searchset")
      return builder.bundle as B
    }
    val candidates =
      if (searchParameters.containsKey("_id")) {
        // We are consuming the _id parameter in this if statement
        val idQueries = searchParameters["_id"]!!
        searchParameters.remove("_id")
        val idResources = ArrayList<T>(idQueries.size)
        for (idQuery in idQueries) {
          val idToken = idQuery as TokenParam
          // Need to construct the equivalent "UnqualifiedVersionless" id that the map is
          // indexed by. If an id has a version it won't match. Need apples-to-apples Ids types
          val id = Ids.newId<IIdType>(fhirContext, resourceType.simpleName, idToken.value)
          val r = resourceIdMap[id]
          if (r != null) {
            idResources.add(r)
          }
        }
        idResources
      } else {
        resourceIdMap.values
      }
    for (resource in candidates) {
      var include = true
      for ((paramName, value) in searchParameters) {
        if (!resourceMatcher.matches(paramName, value, resource)) {
          include = false
          break
        }
      }
      if (include) {
        builder.addCollectionEntry(resource)
      }
    }
    builder.setType("searchset")
    return builder.bundle as B
  }

  override fun <B : IBaseBundle?> link(
    bundleType: Class<B>,
    url: String,
    headers: Map<String, String>,
  ): B {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'link'")
  }

  override fun <C : IBaseConformance?> capabilities(
    resourceType: Class<C>,
    headers: Map<String, String>,
  ): C {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'capabilities'")
  }

  override fun <B : IBaseBundle?> transaction(transaction: B, headers: Map<String, String>): B {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'transaction'")
  }

  override fun <R : IBaseResource?, P : IBaseParameters?> invoke(
    name: String,
    parameters: P,
    returnType: Class<R>,
    headers: Map<String, String>,
  ): R {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'invoke'")
  }

  override fun <P : IBaseParameters?> invoke(
    name: String,
    parameters: P,
    headers: Map<String, String>,
  ): MethodOutcome {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'invoke'")
  }

  override fun <R : IBaseResource?, P : IBaseParameters?, T : IBaseResource?> invoke(
    resourceType: Class<T>,
    name: String,
    parameters: P,
    returnType: Class<R>,
    headers: Map<String, String>,
  ): R {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'invoke'")
  }

  override fun <P : IBaseParameters?, T : IBaseResource?> invoke(
    resourceType: Class<T>,
    name: String,
    parameters: P,
    headers: Map<String, String>,
  ): MethodOutcome {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'invoke'")
  }

  override fun <R : IBaseResource?, P : IBaseParameters?, I : IIdType?> invoke(
    id: I,
    name: String,
    parameters: P,
    returnType: Class<R>,
    headers: Map<String, String>,
  ): R {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'invoke'")
  }

  override fun <P : IBaseParameters?, I : IIdType?> invoke(
    id: I,
    name: String,
    parameters: P,
    headers: Map<String, String>,
  ): MethodOutcome {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'invoke'")
  }

  override fun <B : IBaseBundle?, P : IBaseParameters?> history(
    parameters: P,
    returnType: Class<B>,
    headers: Map<String, String>,
  ): B {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'history'")
  }

  override fun <B : IBaseBundle?, P : IBaseParameters?, T : IBaseResource?> history(
    resourceType: Class<T>,
    parameters: P,
    returnType: Class<B>,
    headers: Map<String, String>,
  ): B {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'history'")
  }

  override fun <B : IBaseBundle?, P : IBaseParameters?, I : IIdType?> history(
    id: I,
    parameters: P,
    returnType: Class<B>,
    headers: Map<String, String>,
  ): B {
    // TODO Auto-generated method stub
    throw UnsupportedOperationException("Unimplemented method 'history'")
  }

  companion object {
    private val categoryDirectories: Map<ResourceCategory, String> =
      ImmutableMap.Builder<ResourceCategory, String>()
        .put(ResourceCategory.CONTENT, "resources")
        .put(ResourceCategory.DATA, "tests")
        .put(ResourceCategory.TERMINOLOGY, "vocabulary")
        .build()
    private val fileExtensions: Map<EncodingEnum?, String> =
      ImmutableMap.Builder<EncodingEnum?, String>()
        .put(EncodingEnum.JSON, ".json")
        .put(EncodingEnum.XML, ".xml")
        .put(EncodingEnum.RDF, ".rdf")
        .build()

    private fun parserForEncoding(
      fhirContext: FhirContext,
      encodingEnum: EncodingEnum?,
    ): IParser {
      return when (encodingEnum) {
        EncodingEnum.JSON -> fhirContext.newJsonParser()
        EncodingEnum.XML -> fhirContext.newXmlParser()
        EncodingEnum.RDF -> fhirContext.newRDFParser()
        EncodingEnum.NDJSON -> throw IllegalArgumentException("NDJSON is not supported")
        else -> throw IllegalArgumentException("NDJSON is not supported")
      }
    }
  }
}
