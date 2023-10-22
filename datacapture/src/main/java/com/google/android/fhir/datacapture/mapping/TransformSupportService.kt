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

package com.google.android.fhir.datacapture.mapping

import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.elementmodel.Manager
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceFactory
import org.hl7.fhir.r4.model.StructureDefinition
import org.hl7.fhir.r4.utils.StructureMapUtilities
import timber.log.Timber

open class TransformSupportService(
  private val iWorkerContext: IWorkerContext,
  private val outputs: MutableList<Base>,
) : StructureMapUtilities.ITransformerServices {

  override fun createType(appInfo: Any, name: String): Base {
    return try {
      val dataType = Enumerations.DataType.fromCode(name)
      ResourceFactory.createResourceOrType(name)
    } catch (fhirException: FHIRException) {
      Manager.build(
        iWorkerContext,
        iWorkerContext.fetchResource(
          StructureDefinition::class.java,
          name,
        ),
      )
    }
  }

  override fun createResource(appInfo: Any, res: Base, atRootofTransform: Boolean): Base {
    if (atRootofTransform) outputs.add(res)
    return try {
      val fhirType = Enumerations.FHIRAllTypes.fromCode(res.fhirType())
      val constructor =
        Class.forName(
            "org.hl7.fhir.r4.model." + fhirType.display,
          )
          .getConstructor()
      constructor.newInstance() as Base
    } catch (e: Exception) {
      res
    }
  }

  override fun translate(appInfo: Any, source: Coding, conceptMapUrl: String): Coding? {
    val conceptMapEngine = ConceptMapEngine(iWorkerContext)
    return conceptMapEngine.translate(source, conceptMapUrl)
  }

  override fun resolveReference(
    appContext: Any,
    url: String,
  ): Base {
    return iWorkerContext.fetchResource(
      Resource::class.java,
      url,
    )
  }

  @Throws(FHIRException::class)
  override fun performSearch(appContext: Any, url: String): List<Base> {
    throw FHIRException("performSearch is not supported yet")
  }

  override fun log(message: String) {
    Timber.d(message)
  }
}
