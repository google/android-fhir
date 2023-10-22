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
import java.io.File
import org.apache.commons.io.FileUtils
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.utilities.npm.NpmPackage
import org.junit.Assert
import org.junit.Test

class StructureMapUtilitiesTest {

  @Test()
  fun `perform extraction for ot`() {
    val locationQuestionnaireResponseString: String =
      "QuestionnaireResponse_ot_all_answers.json".readFile()
    val locationStructureMap = "MeaslesQuestionnaireToResources.map".readFile()
    val immunizationIg = "package.r4.tgz"
    val baseIg = "package.tgz"

    val packages =
      arrayListOf(
        NpmPackage.fromPackage(
          File(
              ClassLoader.getSystemResource(immunizationIg).file,
            )
            .inputStream(),
        ),
        NpmPackage.fromPackage(
          File(
              ClassLoader.getSystemResource(baseIg).file,
            )
            .inputStream(),
        ),
      )

    val contextR4 = ComplexWorkerContext()
    contextR4.loadFromMultiplePackages(packages, true)
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
    Assert.assertEquals("Bundle", targetResource.resourceType.toString())
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
        "resources${File.separator}"
  }
}
