/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.engine.benchmarks.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.tracing.traceAsync
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.engine.benchmarks.app.data.ResourcesDataProvider
import com.google.android.fhir.search.LOCAL_LAST_UPDATED_PARAM
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.has
import com.google.android.fhir.search.include
import com.google.android.fhir.search.revInclude
import com.google.android.fhir.search.search
import java.math.BigDecimal
import kotlin.time.Duration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Organization
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Practitioner
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RiskAssessment

@OptIn(ExperimentalUuidApi::class)
internal class SearchApiViewModel(
  private val resourcesDataProvider: ResourcesDataProvider,
  private val fhirEngine: FhirEngine,
) : ViewModel() {

  private val _benchmarkProgressMutableStateFlow = MutableStateFlow(false)
  val benchmarkProgressStateFlow: StateFlow<Boolean> =
    _benchmarkProgressMutableStateFlow.asStateFlow()

  private val _searchApiUiMutableStateFlow = MutableStateFlow(listOf<SearchApiUiState>())
  val searchApiUiStateFlow: StateFlow<List<SearchApiUiState>> =
    _searchApiUiMutableStateFlow.asStateFlow()

  init {
    viewModelScope.launch(benchmarkingViewModelWorkDispatcher) {
      _benchmarkProgressMutableStateFlow.update { true }
      preLoadData()

      traceSearchString()
      traceSearchNumber()
      traceSearchDate()
      traceSearchQuantity()
      traceSearchPatientIdWithTokenIdentifier()
      traceSearchPatientHasEncounter()
      traceSearchPatientSortedByBirthDate()
      traceSearchPatientSortedByName()
      traceSearchPatientGivenWithDisjunctValues()
      traceSearchPatientWithIncludeGeneralPractitioner()
      traceSearchEncounterLocalLastUpdated()
      traceSearchPatientWithEitherGivenNameOrBirthDate()
      traceSearchPatientWithRevIncludeCondition()

      _benchmarkProgressMutableStateFlow.update { false }
    }
  }

  private suspend fun preLoadData() {
    withContext(currentCoroutineContext()) {
      fhirEngine.clearDatabase()
      resourcesDataProvider.collectResources { fhirEngine.create(*it.toTypedArray()) }
    }
  }

  private suspend fun triggerChangeInSqlitePageCache() {
    withContext(currentCoroutineContext()) {
      // Search to add sqlite pages with Organization/Practitioner to cache
      fhirEngine.search<Organization> { count = 1 }

      fhirEngine.search<Practitioner> { count = 1 }
    }
  }

  private suspend fun namedTrace(name: String, traceFunction: suspend (String) -> Duration) {
    triggerChangeInSqlitePageCache()

    val duration = traceFunction.invoke(name)
    _searchApiUiMutableStateFlow.update { it + SearchApiUiState(name, duration) }
  }

  private suspend fun traceSearchString() =
    namedTrace("searchWithTypeStringSearchParameter") {
      measureTimeAsync {
        traceAsync(it, 0) {
          fhirEngine.search<Patient> {
            filter(
              Patient.NAME,
              {
                value = "Mirian768"
                modifier = StringFilterModifier.CONTAINS
              },
            )
          }
        }
      }
    }

  private suspend fun traceSearchNumber() =
    namedTrace("searchWithTypeNumberSearchParameter") {
      (0 until 1000)
        .asSequence()
        .map {
          RiskAssessment().apply {
            id = Uuid.random().toString()
            addPrediction(
              RiskAssessment.RiskAssessmentPredictionComponent()
                .setProbability(DecimalType(it * 3L)),
            )
          }
        }
        .chunked(250)
        .forEach { fhirEngine.create(*it.toTypedArray()) }

      RiskAssessment()
        .apply {
          id = Uuid.random().toString()
          addPrediction(
            RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5)),
          )
        }
        .apply { fhirEngine.create(this@apply) }

      measureTimeAsync {
        traceAsync(it, 1) {
          fhirEngine.search<RiskAssessment> {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.EQUAL
                value = BigDecimal("100")
              },
            )
          }
        }
      }
    }

  private suspend fun traceSearchDate() =
    namedTrace("searchWithTypeDateSearchParameter") {
      val patientBirthDate = DateType("2025-02-08")
      val patient =
        Patient().apply {
          id = Uuid.random().toString()
          birthDateElement = patientBirthDate
        }
      fhirEngine.create(patient)

      measureTimeAsync {
        traceAsync(it, 2) {
          fhirEngine.search<Patient> { filter(Patient.BIRTHDATE, { value = of(patientBirthDate) }) }
        }
      }
    }

  private suspend fun traceSearchQuantity() =
    namedTrace("searchWithTypeQuantitySearchParameter") {
      (0 until 1000)
        .map {
          Observation().apply {
            id = Uuid.random().toString()
            value =
              Quantity().apply {
                value = BigDecimal(it * 3L)
                system = "http://unitsofmeasure.org"
                code = "g"
              }
          }
        }
        .chunked(250)
        .forEach { fhirEngine.create(*it.toTypedArray()) }

      Observation()
        .apply {
          id = "1"
          value =
            Quantity().apply {
              value = BigDecimal("5.403")
              system = "http://unitsofmeasure.org"
              code = "g"
            }
        }
        .apply { fhirEngine.create(this@apply) }

      measureTimeAsync {
        traceAsync(it, 3) {
          fhirEngine.search<Observation> {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.EQUAL
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
        }
      }
    }

  private suspend fun traceSearchPatientIdWithTokenIdentifier() =
    namedTrace("searchPatientIdWithTokenIdentifier") {
      measureTimeAsync {
        traceAsync(it, 4) {
          fhirEngine.search<Patient> {
            filter(Patient.RES_ID, { value = of("92675303-ca5b-136a-169b-e764c5753f06") })
          }
        }
      }
    }

  private suspend fun traceSearchPatientHasEncounter() =
    namedTrace("searchPatientHasEncounter") {
      measureTimeAsync {
        traceAsync(it, 5) {
          fhirEngine.search<Patient> {
            count = 1

            has<Encounter>(Encounter.PATIENT) {
              filter(
                Encounter.TYPE,
                {
                  value =
                    of(
                      Coding().apply {
                        system = "http://snomed.info/sct"
                        code = "162673000"
                      },
                    )
                },
              )
            }
          }
        }
      }
    }

  private suspend fun traceSearchPatientSortedByBirthDate() =
    namedTrace("searchPatientSortedByBirthDate") {
      measureTimeAsync {
        traceAsync(it, 6) {
          fhirEngine.search<Patient> {
            sort(Patient.BIRTHDATE, Order.DESCENDING)
            count = 1
          }
        }
      }
    }

  private suspend fun traceSearchPatientWithEitherGivenNameOrBirthDate() =
    namedTrace("searchPatientWithEitherGivenNameOrBirthDate") {
      val patientBirthDate = DateType("2025-02-09")
      val patient =
        Patient().apply {
          id = Uuid.random().toString()
          birthDateElement = patientBirthDate
        }
      fhirEngine.create(patient)

      measureTimeAsync {
        traceAsync(it, 7) {
          fhirEngine.search<Patient> {
            operation = Operation.OR
            filter(
              Patient.GIVEN,
              {
                value = "Mirian768"
                modifier = StringFilterModifier.MATCHES_EXACTLY
              },
            )
            filter(Patient.BIRTHDATE, { value = of(patientBirthDate) })
          }
        }
      }
    }

  private suspend fun traceSearchPatientGivenWithDisjunctValues() =
    namedTrace("searchPatientGivenWithDisjunctValues") {
      measureTimeAsync {
        traceAsync(it, 9) {
          fhirEngine.search<Patient> {
            filter(
              Patient.GIVEN,
              {
                value = "Mirian768"
                modifier = StringFilterModifier.MATCHES_EXACTLY
              },
              {
                value = "Julio255"
                modifier = StringFilterModifier.MATCHES_EXACTLY
              },
              operation = Operation.OR,
            )
          }
        }
      }
    }

  private suspend fun traceSearchPatientSortedByName() =
    namedTrace("searchPatientSortedByName") {
      measureTimeAsync {
        traceAsync(it, 8) {
          fhirEngine.search<Patient> {
            sort(Patient.NAME, Order.DESCENDING)
            count = 1
          }
        }
      }
    }

  private suspend fun traceSearchEncounterLocalLastUpdated() =
    namedTrace("searchEncounterLocalLastUpdated") {
      measureTimeAsync {
        traceAsync(it, 10) {
          fhirEngine.search<Encounter> {
            count = 1
            sort(LOCAL_LAST_UPDATED_PARAM, Order.DESCENDING)
          }
        }
      }
    }

  private suspend fun traceSearchPatientWithIncludeGeneralPractitioner() =
    namedTrace("searchPatientWithIncludeGeneralPractitioner") {
      val gp01 =
        Practitioner().apply {
          id = Uuid.random().toString()
          addName(
            HumanName().apply {
              family = "\"Practitioner-01\""
              addGiven("General-01")
            },
          )
        }
      val patient1 =
        Patient().apply {
          id = Uuid.random().toString()
          addName(
            HumanName().apply {
              addGiven("James")
              family = "Gorden-01"
            },
          )
          addGeneralPractitioner(Reference("Practitioner/${gp01.logicalId}"))
        }
      fhirEngine.create(gp01, patient1)

      measureTimeAsync {
        traceAsync(it, 11) {
          fhirEngine.search<Patient> {
            filter(
              Patient.NAME,
              {
                value = "Gorden-01"
                modifier = StringFilterModifier.CONTAINS
              },
            )
            include<Practitioner>(Patient.GENERAL_PRACTITIONER)
          }
        }
      }
    }

  private suspend fun traceSearchPatientWithRevIncludeCondition() =
    namedTrace("searchPatientWithRevIncludeConditions") {
      val patient01 =
        Patient().apply {
          id = Uuid.random().toString()
          addName(
            HumanName().apply {
              addGiven("James-12")
              family = "Bond-01"
            },
          )
        }
      val diabetesCondition =
        Condition().apply {
          id = Uuid.random().toString()
          code = CodeableConcept(Coding("http://snomed.info/sct", "44054006", "Diabetes"))
          subject = Reference("Patient/${patient01.logicalId}")
        }
      fhirEngine.create(patient01, diabetesCondition)

      measureTimeAsync {
        traceAsync(it, 12) {
          fhirEngine.search<Patient> {
            filter(
              Patient.NAME,
              {
                value = "Bond-01"
                modifier = StringFilterModifier.CONTAINS
              },
            )
            revInclude<Condition>(Condition.SUBJECT)
          }
        }
      }
    }
}

internal typealias SearchApiUiState = Pair<String, Duration>
