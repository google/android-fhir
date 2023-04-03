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

import com.google.gson.Gson

val careConfig =
  """
    {           
        "supportedImplementationGuides": [
            {
                "location": "http://hostname/fhir/ImplementationGuide/NCDIG",
                "carePlanPolicy": "ACCEPT_ALL", 
                "implementationGuideConfig": {
                    "implementationGuideId": "ImplementationGuide/NCDIG",
                    "entryPoint": "PlanDefinition/Configurable-Screenings-NCD-Master-Plan-Definition",
                    "requestResourceConfigurations": [
                        {
                            "resourceType": "Task",
                            "values": [
                                {
                                    "field": "owner",
                                    "value": "PractitionerRole/81417c6a-a6ab-453c-a26b-c08791161045"
                                }
                            ]
                        },
                        {
                            "resourceType": "ServiceRequest",
                            "values": [
                                {
                                    "field": "requester",
                                    "value": "PractitionerRole/81417c6a-a6ab-453c-a26b-c08791161045"
                                }
                            ]
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
            }
        ]
    }
  """.trimIndent()

data class RequestResourceConfig(
  var resourceType: String,
  var values: List<Value>,
) {
  data class Value(var field: String, var value: String)
}

class ImplementationGuideConfig(
  var implementationGuideId: String,
  var entryPoint: String,
  var requestResourceConfigurations: List<RequestResourceConfig>
)

data class SupportedImplementationGuide(
  var location: String,
  var carePlanPolicy: String,
  var implementationGuideConfig: ImplementationGuideConfig
)

data class CareConfiguration(var supportedImplementationGuides: List<SupportedImplementationGuide>)

object ConfigurationManager {
  private var careConfiguration: CareConfiguration? = null

  fun getCareConfiguration(): CareConfiguration {
    if (careConfiguration == null) {
      val gson = Gson()
      careConfiguration = gson.fromJson(careConfig, CareConfiguration::class.java)
    }
    return careConfiguration!!
  }
}
