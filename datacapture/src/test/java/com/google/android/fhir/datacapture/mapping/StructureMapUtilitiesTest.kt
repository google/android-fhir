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

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.common.truth.Truth
import java.io.File
import org.apache.commons.io.FileUtils
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.utilities.npm.NpmPackage
import org.junit.Test

class StructureMapUtilitiesTest {

  /*
   * This test case checks if :
   *   1. Checks if multiple packages can be loaded into the context
   *   2. Checks if we are able to retrieve Structure Definitions from the packages loaded for custom resources
   *   3. Checks if a structure
   * */
  @Test()
  fun `perform extraction for out-break toolkit`() {
    val locationQuestionnaireResponseString: String =
      "QuestionnaireResponse_ot_all_answers.json".readFile()
    val locationStructureMap = "MeaslesQuestionnaireToResources.map".readFile()
    val immunizationIg = "${ASSET_BASE_PATH}${File.separator}package.r4.tgz"
    val baseIg = "${ASSET_BASE_PATH}${File.separator}package.tgz"

    val packages =
      arrayListOf(
        NpmPackage.fromPackage(
          File(
              immunizationIg,
            )
            .inputStream(),
        ),
        NpmPackage.fromPackage(
          File(
              baseIg,
            )
            .inputStream(),
        ),
      )

    val contextR4 = ComplexWorkerContext()
    contextR4.loadFromMultiplePackages(packages)
    val outputs = mutableListOf<Base>()
    val transformSupportServices =
      TransformSupportService(
        contextR4,
        outputs,
      )
    val structureMapUtilities =
      org.hl7.fhir.r4.utils.StructureMapUtilities(contextR4, transformSupportServices)
    val structureMap =
      structureMapUtilities.parse(locationStructureMap, "MeaslesQuestionnaireToResources")
    val iParser: IParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
    val targetResource = Bundle()
    val baseElement =
      iParser.parseResource(
        QuestionnaireResponse::class.java,
        locationQuestionnaireResponseString,
      )
    structureMapUtilities.transform(contextR4, baseElement, structureMap, targetResource)
    Truth.assertThat("Bundle").isEqualTo(targetResource.resourceType.toString())
    val patient =
      targetResource.entry.find { it.resource.resourceType == ResourceType.Patient }?.resource
        as Patient
    Truth.assertThat("John Doe").isEqualTo(patient.name.first().family)
    Truth.assertThat("234phone").isEqualTo(patient.telecom.first().value)
    val observation =
      targetResource.entry
        .find {
          it.resource.resourceType == ResourceType.Observation &&
            (it.resource as Observation).code.coding.first().code == "DE66"
        }
        ?.resource as Observation
    Truth.assertThat("Fever").isEqualTo(observation.code.coding.first().display)
  }

  private fun String.readFile(systemPath: String = ASSET_BASE_PATH): String {
    val file = File("$systemPath/$this")
    return FileUtils.readFileToString(file, "UTF-8")
  }

  companion object {
    private val ASSET_BASE_PATH =
      "${System.getProperty("user.dir")}${File.separator}" +
        "src${File.separator}" +
        "test${File.separator}" +
        "resources${File.separator}" +
        "StructureMapUtilitiesTestResources${File.separator}"
  }
}
