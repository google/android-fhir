/*
 * Copyright 2022-2023 Google LLC
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

import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.utils.StructureMapUtilities

/** Data used during StructureMap-based extraction. */
data class StructureMapExtractionContext(
  /**
   * Optionally pass a custom version of [StructureMapUtilities.ITransformerServices] to support
   * specific use cases.
   */
  val transformSupportServices: StructureMapUtilities.ITransformerServices? = null,
  /**
   * A lambda function which returns a [StructureMap]. Depending on your app this could be
   * hard-coded or use the [String] parameter to fetch the appropriate structure map.
   *
   * @param String The canonical URL for the Structure Map referenced in the
   *   [Target structure map extension](http://hl7.org/fhir/uv/sdc/StructureDefinition-sdc-questionnaire-targetStructureMap.html)
   *   of the questionnaire.
   * @param IWorkerContext May be used with other HAPI FHIR classes, like using
   *   [StructureMapUtilities.parse] to parse content in the FHIR Mapping Language.
   */
  /**
   * Optionally pass a custom version of [IWorkerContext].
   *
   * @default [SimpleWorkerContext]
   */
  val workerContext: IWorkerContext = SimpleWorkerContext(),
  val structureMapProvider: (suspend (String, IWorkerContext) -> StructureMap?),
)
