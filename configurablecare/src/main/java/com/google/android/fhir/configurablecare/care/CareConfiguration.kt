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
package com.google.android.fhir.configurablecare.care

import android.content.Context
import android.os.Environment
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.gson.Gson
import com.google.gson.JsonArray
import java.io.File
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Resource

data class RequestResourceConfig(
  var resourceType: String,
  var values: List<Value>,
  var maxDuration: String,
  var unit: String
) {
  data class Value(var field: String, var value: String)
}

class ImplementationGuideConfig(
  var implementationGuideId: String,
  var entryPoint: String,
  var requestResourceConfigurations: List<RequestResourceConfig>,
  var supportedValueSets: JsonArray,
  var triggers: List<Trigger>
)

data class Trigger(
  var event: String,
  var planDefinition: String
)


data class SupportedImplementationGuide(
  var location: String,
  var carePlanPolicy: String,
  var implementationGuideConfig: ImplementationGuideConfig,
)

// fun moveAllIGResourcesIntoFilesDir(igName: String) {
//   val inputBaseIgDir = "/$igName/ig"
//
//   javaClass
//     .getResourceAsStream("$inputBaseIgDir/contents.txt")
//     ?.bufferedReader()
//     ?.use { bufferReader -> bufferReader.readText() }
//     ?.split("\n")
//     ?.forEach { fileName ->
//       runCatching { copyResourceIntoApp(igName, "$inputBaseIgDir/$fileName") }
//         .onFailure { println("Ignoring $inputBaseIgDir/$fileName. Not a valid Fhir Resource") }
//     }
// }

data class CareConfiguration(var supportedImplementationGuides: List<SupportedImplementationGuide>)

object ConfigurationManager {
  var careConfiguration: CareConfiguration? = null

  fun getCareConfiguration(context: Context): CareConfiguration {
    if (careConfiguration == null) {
      val gson = Gson()
      val careConfig = readFileFromAssets(context, "care-config.json").trimIndent()
      // val rootDirPath = "/storage/emulated/0/Documents/configurablecare/smart-imm/ig"
      // val fileDirectory = File(Environment.DIRECTORY_DOCUMENTS + "/configurablecare/smart-imm/ig")
      // // val rootDirectory = File(javaClass.getResource("/smart-imm/ig/")!!.file)
      careConfiguration = gson.fromJson(careConfig, CareConfiguration::class.java)
    }
    return careConfiguration!!
  }

  fun getCareConfigurationResources(): Collection<Resource> {
    var bundleCollection: Collection<Resource> = mutableListOf()
    val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

    for (implementationGuide in careConfiguration?.supportedImplementationGuides!!) {
      val resourceJsonList = implementationGuide.implementationGuideConfig.supportedValueSets

      for (resourceJson in resourceJsonList) {
        val resource = jsonParser.parseResource(resourceJson.toString()) as Resource
        bundleCollection += resource
      }
    }
    return bundleCollection
  }

  private fun readFileFromAssets(context: Context, filename: String): String {
    return context.assets.open(filename).bufferedReader().use { it.readText() }
  }
}
