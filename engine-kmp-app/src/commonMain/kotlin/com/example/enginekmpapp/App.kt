/*
 * Copyright 2025-2026 Google LLC
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

package com.example.enginekmpapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.get
import com.google.android.fhir.delete
import com.google.android.fhir.index.SearchParamDefinition
import com.google.android.fhir.index.SearchParamType
import com.google.android.fhir.registerResourceType
import com.google.android.fhir.search.StringClientParam
import com.google.android.fhir.search.search
import com.google.android.fhir.search.count
import com.google.fhir.model.r4.Patient
import com.google.fhir.model.r4.HumanName
import com.google.fhir.model.r4.terminologies.ResourceType
import kotlinx.coroutines.launch

/** Initializes FhirEngineProvider with resource types and search params for the demo. */
fun initFhirEngine(platformContext: Any = Unit) {
  registerResourceType(Patient::class, ResourceType.Patient)

  FhirEngineProvider.init(
    FhirEngineConfiguration(
      customSearchParameters =
        listOf(
          SearchParamDefinition(
            name = "name",
            type = SearchParamType.STRING,
            path = "Patient.name",
          ),
          SearchParamDefinition(
            name = "family",
            type = SearchParamType.STRING,
            path = "Patient.name.family",
          ),
          SearchParamDefinition(
            name = "given",
            type = SearchParamType.STRING,
            path = "Patient.name.given",
          ),
        ),
    ),
  )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun App(platformContext: Any = Unit) {
  val scope = rememberCoroutineScope()
  var log by remember { mutableStateOf("Ready. Tap a button to start.\n") }
  var initialized by remember { mutableStateOf(false) }

  fun appendLog(msg: String) {
    log += "$msg\n"
  }

  fun getEngine(): FhirEngine {
    if (!initialized) {
      initFhirEngine(platformContext)
      initialized = true
    }
    return FhirEngineProvider.getInstance(platformContext)
  }

  MaterialTheme {
    Scaffold(
      topBar = {
        TopAppBar(title = { Text("Engine KMP Demo") })
      },
    ) { padding ->
      Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
      ) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Button(onClick = {
            scope.launch {
              try {
                val engine = getEngine()
                val patient =
                  Patient(
                    id = "patient-1",
                    name = listOf(
                      HumanName(
                        family = com.google.fhir.model.r4.String(value = "Doe"),
                        given = listOf(com.google.fhir.model.r4.String(value = "John")),
                      ),
                    ),
                  )
                val ids = engine.create(patient)
                appendLog("[Create] Patient created with id: ${ids.first()}")
              } catch (e: Exception) {
                appendLog("[Create] ERROR: ${e.message}")
              }
            }
          }) { Text("Create Patient") }

          Button(onClick = {
            scope.launch {
              try {
                val engine = getEngine()
                val patient = engine.get<Patient>("patient-1")
                val name = patient.name?.firstOrNull()
                val given = name?.given?.firstOrNull()?.value ?: "?"
                val family = name?.family?.value ?: "?"
                appendLog("[Get] Patient: $given $family (id=${patient.id})")
              } catch (e: Exception) {
                appendLog("[Get] ERROR: ${e.message}")
              }
            }
          }) { Text("Get Patient") }

          Button(onClick = {
            scope.launch {
              try {
                val engine = getEngine()
                val results = engine.search<Patient> {
                  filter(StringClientParam("name"), { value = "Doe" })
                }
                appendLog("[Search] Found ${results.size} patient(s)")
                results.forEach { result ->
                  val name = result.resource.name?.firstOrNull()
                  val given = name?.given?.firstOrNull()?.value ?: "?"
                  val family = name?.family?.value ?: "?"
                  appendLog("  - $given $family (id=${result.resource.id})")
                }
              } catch (e: Exception) {
                appendLog("[Search] ERROR: ${e.message}")
              }
            }
          }) { Text("Search") }

          Button(onClick = {
            scope.launch {
              try {
                val engine = getEngine()
                val patient = engine.get<Patient>("patient-1")
                val updated =
                  patient.copy(
                    name = listOf(
                      HumanName(
                        family = com.google.fhir.model.r4.String(value = "Smith"),
                        given = listOf(com.google.fhir.model.r4.String(value = "Jane")),
                      ),
                    ),
                  )
                engine.update(updated)
                appendLog("[Update] Patient updated to Jane Smith")
              } catch (e: Exception) {
                appendLog("[Update] ERROR: ${e.message}")
              }
            }
          }) { Text("Update") }

          Button(onClick = {
            scope.launch {
              try {
                val engine = getEngine()
                val c = engine.count<Patient> {}
                appendLog("[Count] Total patients: $c")
              } catch (e: Exception) {
                appendLog("[Count] ERROR: ${e.message}")
              }
            }
          }) { Text("Count") }

          Button(onClick = {
            scope.launch {
              try {
                val engine = getEngine()
                engine.delete<Patient>("patient-1")
                appendLog("[Delete] Patient patient-1 deleted")
              } catch (e: Exception) {
                appendLog("[Delete] ERROR: ${e.message}")
              }
            }
          }) { Text("Delete") }

          Button(onClick = {
            scope.launch {
              try {
                val engine = getEngine()
                engine.clearDatabase()
                appendLog("[Clear] Database cleared")
              } catch (e: Exception) {
                appendLog("[Clear] ERROR: ${e.message}")
              }
            }
          }) { Text("Clear DB") }

          Button(onClick = {
            scope.launch {
              try {
                val engine = getEngine()
                // Full flow: create 3 patients, search, count, update, search again, delete, count
                appendLog("--- Full Flow Demo ---")

                engine.clearDatabase()
                appendLog("1. Cleared database")

                val p1 = Patient(
                  id = "demo-1",
                  name = listOf(HumanName(
                    family = com.google.fhir.model.r4.String(value = "Garcia"),
                    given = listOf(com.google.fhir.model.r4.String(value = "Maria")),
                  )),
                )
                val p2 = Patient(
                  id = "demo-2",
                  name = listOf(HumanName(
                    family = com.google.fhir.model.r4.String(value = "Garcia"),
                    given = listOf(com.google.fhir.model.r4.String(value = "Carlos")),
                  )),
                )
                val p3 = Patient(
                  id = "demo-3",
                  name = listOf(HumanName(
                    family = com.google.fhir.model.r4.String(value = "Chen"),
                    given = listOf(com.google.fhir.model.r4.String(value = "Wei")),
                  )),
                )
                engine.create(p1, p2, p3)
                appendLog("2. Created 3 patients")

                val count1 = engine.count<Patient> {}
                appendLog("3. Count: $count1")

                val garcias = engine.search<Patient> {
                  filter(StringClientParam("family"), { value = "Garcia" })
                }
                appendLog("4. Search 'Garcia': found ${garcias.size}")

                val maria = engine.get<Patient>("demo-1")
                engine.update(
                  maria.copy(
                    name = listOf(HumanName(
                      family = com.google.fhir.model.r4.String(value = "Garcia-Lopez"),
                      given = listOf(com.google.fhir.model.r4.String(value = "Maria")),
                    )),
                  ),
                )
                appendLog("5. Updated demo-1 family to Garcia-Lopez")

                val updated = engine.get<Patient>("demo-1")
                appendLog("6. Verified: ${updated.name?.first()?.family?.value}")

                engine.delete<Patient>("demo-2")
                appendLog("7. Deleted demo-2")

                val count2 = engine.count<Patient> {}
                appendLog("8. Final count: $count2")

                appendLog("--- Flow Complete ---")
              } catch (e: Exception) {
                appendLog("[Flow] ERROR: ${e.message}")
              }
            }
          }) { Text("Full Flow") }
        }

        Text(
          text = log,
          modifier =
            Modifier
              .fillMaxWidth()
              .weight(1f)
              .padding(top = 16.dp)
              .verticalScroll(rememberScrollState()),
          fontFamily = FontFamily.Monospace,
          fontSize = 13.sp,
        )
      }
    }
  }
}
