/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.demo.care

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.hl7.fhir.r4.model.Resource

val careConfig =
  """{
    "supportedImplementationGuides": [
        {
            "location": "http://hostname/fhir/ImplementationGuide/NCDIG",
            "carePlanPolicy": "ACCEPT_ALL",
            "implementationGuideConfig": {
                "implementationGuideId": "ImplementationGuide/NCDIG",
                "entryPoint": "PlanDefinition/NCD-Master-Plan-Definition",
                "requestResourceConfigurations": [
                    {
                        "resourceType": "Task",
                        "values": [
                            {
                                "field": "owner",
                                "value": "PractitionerRole/81417c6a-a6ab-453c-a26b-c08791161045"
                            }
                        ],
                        "maxDuration": "1",
                        "unit": "years"
                    },
                    {
                        "resourceType": "ServiceRequest",
                        "values": [
                            {
                                "field": "requester",
                                "value": "PractitionerRole/81417c6a-a6ab-453c-a26b-c08791161045"
                            }
                        ],
                        "maxDuration": "6",
                        "unit": "months"
                    }
                ],
                "supportedValueSets": [
                    {
                        "resourceType": "ValueSet",
                        "id": "ncd-facilities-valueset",
                        "url": "http://localhost/ncd-facilities",
                        "status": "draft",
                        "description": "List of Facilities",
                        "expansion": {
                            "contains": [
                                {
                                    "display": "Facility 1"
                                },
                                {
                                    "display": "Facility 2"
                                },
                                {
                                    "display": "Facility 3"
                                }
                            ]
                        }
                    }
                ]
            }
        },
        {
            "location": "http://hostname/fhir/ImplementationGuide/CBACIG",
            "carePlanPolicy": "ACCEPT_ALL",
            "implementationGuideConfig": {
                "implementationGuideId": "ImplementationGuide/CBACIG",
                "entryPoint": "PlanDefinition/CBAC-Master-Plan-Definition",
                "requestResourceConfigurations": [
                    {
                        "resourceType": "Task",
                        "values": [
                            {
                                "field": "owner",
                                "value": "PractitionerRole/81417c6a-a6ab-453c-a26b-c08791161045"
                            },
                            {
                                "field": "requester",
                                "value": "PractitionerRole/81417c6a-a6ab-453c-a26b-c08791161045"
                            }
                        ],
                        "maxDuration": "6",
                        "unit": "months"
                    },
                    {
                        "resourceType": "ServiceRequest",
                        "values": [
                            {
                                "field": "requester",
                                "value": "PractitionerRole/81417c6a-a6ab-453c-a26b-c08791161045"
                            }
                        ],
                        "maxDuration": "6",
                        "unit": "months"
                    }
                ],
                "supportedValueSets": [
                    {
                        "resourceType": "ValueSet",
                        "id": "facilities-valueset",
                        "url": "http://localhost/facilities",
                        "status": "draft",
                        "description": "List of Facilities",
                        "expansion": {
                            "contains": [
                                {
                                    "system": "http://localhost/facilities",
                                    "value": "Location/81417c6a-a6ab-453c-a26b-c08791161300",
                                    "display": "Arendelle PHC"
                                },
                                {
                                    "system": "http://localhost/facilities",
                                    "value": "Location/81417c6a-a6ab-453c-a26b-c08791161200",
                                    "display": "Townsville PHC"
                                },
                                {
                                    "system": "http://localhost/facilities",
                                    "value": "Location/81417c6a-a6ab-453c-a26b-c08791161044",
                                    "display": "Pleasantville PHC"
                                }
                            ]
                        }
                    },
                    {
                        "resourceType": "ValueSet",
                        "id": "assignees-valueset",
                        "url": "http://localhost/assignees",
                        "status": "draft",
                        "description": "List of Assignees",
                        "expansion": {
                            "contains": [
                                {
                                    "system": "http://localhost/assignees",
                                    "code": "PractitionerRole/81417c6a-a6ab-453c-a26b-c08791161045",
                                    "display": "Community Health Worker"
                                },
                                {
                                    "system": "http://localhost/assignees",
                                    "code": "PractitionerRole/81417c6a-a6ab-453c-a26b-c08791161400",
                                    "display": "Auxiliary Nurse and Midwife"
                                }
                            ]
                        }
                    }
                ]
            }
        }
    ]
}
  """.trimIndent()

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
  var supportedValueSets: JsonArray
)

data class SupportedImplementationGuide(
  var location: String,
  var carePlanPolicy: String,
  var implementationGuideConfig: ImplementationGuideConfig,
)

data class CareConfiguration(var supportedImplementationGuides: List<SupportedImplementationGuide>)

object ConfigurationManager {
  private var careConfiguration: CareConfiguration? = null
  private var taskConfigMap: MutableMap<String, String> = mutableMapOf()
  private var serviceRequestConfigMap: MutableMap<String, String> = mutableMapOf()

  fun getCareConfiguration(): CareConfiguration {
    if (careConfiguration == null) {
      val gson = Gson()
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

  fun setTaskConfigMap(planDefinitionId: String): MutableMap<String, String> {
    val taskConfig =
      careConfiguration
        ?.supportedImplementationGuides
        ?.firstOrNull { it.implementationGuideConfig.entryPoint.contains(planDefinitionId) }
        ?.implementationGuideConfig
        ?.requestResourceConfigurations?.firstOrNull { it.resourceType == "Task" }!!

    for (entry in taskConfig.values) {
      taskConfigMap[entry.field] = entry.value
    }
    taskConfigMap["maxDuration"] = taskConfig.maxDuration
    taskConfigMap["unit"] = taskConfig.unit

    return taskConfigMap
  }

  fun getTaskConfigMap(): MutableMap<String, String> {
    return taskConfigMap
  }

  fun setServiceRequestConfigMap(planDefinitionId: String): MutableMap<String, String> {
    val serviceRequestConfig =
      careConfiguration
        ?.supportedImplementationGuides
        ?.firstOrNull { it.implementationGuideConfig.entryPoint.contains(planDefinitionId) }
        ?.implementationGuideConfig
        ?.requestResourceConfigurations?.firstOrNull { it.resourceType == "ServiceRequest" }!!

    for (entry in serviceRequestConfig.values) {
      serviceRequestConfigMap[entry.field] = entry.value
    }
    serviceRequestConfigMap["maxDuration"] = serviceRequestConfig.maxDuration
    serviceRequestConfigMap["unit"] = serviceRequestConfig.unit

    return serviceRequestConfigMap
  }

  fun getServiceRequestConfigMap(): MutableMap<String, String> {
    return serviceRequestConfigMap
  }
}
