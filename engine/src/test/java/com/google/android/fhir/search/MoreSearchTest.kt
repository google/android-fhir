/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.search

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.FhirServices
import com.google.android.fhir.sync.FhirDataSource
import com.google.common.truth.Truth
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.RiskAssessment
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreSearchTest {
  private val dataSource =
    object : FhirDataSource {
      override suspend fun loadData(path: String): Bundle {
        return Bundle()
      }

      override suspend fun insert(
        resourceType: String,
        resourceId: String,
        payload: String
      ): Resource {
        return Patient()
      }

      override suspend fun update(
        resourceType: String,
        resourceId: String,
        payload: String
      ): OperationOutcome {
        return OperationOutcome()
      }

      override suspend fun delete(resourceType: String, resourceId: String): OperationOutcome {
        return OperationOutcome()
      }
    }
  private val services =
    FhirServices.builder(dataSource, ApplicationProvider.getApplicationContext()).inMemory().build()
  private val database = services.database

  @Test
  fun search_number_equal() {
    val riskAssessment1 =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5))
        )
      }
    val riskAssessment2 =
      RiskAssessment().apply {
        id = "2"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100.5))
        )
      }
    val riskAssessment3 =
      RiskAssessment().apply {
        id = "3"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0))
        )
      }

    val res = runBlocking {
      database.insert(riskAssessment1, riskAssessment2, riskAssessment3)
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(RiskAssessment.PROBABILITY) {
              prefix = ParamPrefixEnum.EQUAL
              value = BigDecimal("100")
            }
          }
          .getQuery()
      )
    }

    Truth.assertThat(res).hasSize(1)
    Truth.assertThat(res[0].predictionFirstRep.probabilityDecimalType.valueAsString)
      .isEqualTo("99.5")
  }

  @Test
  fun search_number_notEqual() {
    val riskAssessment1 =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5))
        )
      }
    val riskAssessment2 =
      RiskAssessment().apply {
        id = "2"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100.5))
        )
      }
    val riskAssessment3 =
      RiskAssessment().apply {
        id = "3"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0))
        )
      }

    val res = runBlocking {
      database.insert(riskAssessment1, riskAssessment2, riskAssessment3)
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(RiskAssessment.PROBABILITY) {
              prefix = ParamPrefixEnum.NOT_EQUAL
              value = BigDecimal("100")
            }
          }
          .getQuery()
      )
    }

    Truth.assertThat(res).hasSize(2)
    Truth.assertThat(
        res.all {
          it.predictionFirstRep.probabilityDecimalType < DecimalType(99.5) ||
            it.predictionFirstRep.probabilityDecimalType >= DecimalType(100.5)
        }
      )
      .isTrue()
  }

  @Test
  fun search_number_Greater() {
    val riskAssessment1 =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5))
        )
      }
    val riskAssessment2 =
      RiskAssessment().apply {
        id = "2"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100.5))
        )
      }
    val riskAssessment3 =
      RiskAssessment().apply {
        id = "3"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0))
        )
      }

    val res = runBlocking {
      database.insert(riskAssessment1, riskAssessment2, riskAssessment3)
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(RiskAssessment.PROBABILITY) {
              prefix = ParamPrefixEnum.GREATERTHAN
              value = BigDecimal("99.5")
            }
          }
          .getQuery()
      )
    }

    Truth.assertThat(res).hasSize(1)
    Truth.assertThat(res.all { it.predictionFirstRep.probabilityDecimalType > DecimalType(99.5) })
      .isTrue()
  }

  @Test
  fun search_number_GreaterThanEqual() {
    val riskAssessment1 =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5))
        )
      }
    val riskAssessment2 =
      RiskAssessment().apply {
        id = "2"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100.5))
        )
      }
    val riskAssessment3 =
      RiskAssessment().apply {
        id = "3"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0))
        )
      }

    val res = runBlocking {
      database.insert(riskAssessment1, riskAssessment2, riskAssessment3)
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(RiskAssessment.PROBABILITY) {
              prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
              value = BigDecimal("99.5")
            }
          }
          .getQuery()
      )
    }

    Truth.assertThat(res).hasSize(2)
    Truth.assertThat(res.all { it.predictionFirstRep.probabilityDecimalType >= DecimalType(99.5) })
      .isTrue()
  }

  @Test
  fun search_number_Less() {
    val riskAssessment1 =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5))
        )
      }
    val riskAssessment2 =
      RiskAssessment().apply {
        id = "2"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100.5))
        )
      }
    val riskAssessment3 =
      RiskAssessment().apply {
        id = "3"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0))
        )
      }

    val res = runBlocking {
      database.insert(riskAssessment1, riskAssessment2, riskAssessment3)
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(RiskAssessment.PROBABILITY) {
              prefix = ParamPrefixEnum.LESSTHAN
              value = BigDecimal("99.5")
            }
          }
          .getQuery()
      )
    }

    Truth.assertThat(res).hasSize(1)
    Truth.assertThat(res.all { it.predictionFirstRep.probabilityDecimalType < DecimalType(99.5) })
      .isTrue()
  }

  @Test
  fun search_number_LessThanEquals() {
    val riskAssessment1 =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5))
        )
      }
    val riskAssessment2 =
      RiskAssessment().apply {
        id = "2"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100.5))
        )
      }
    val riskAssessment3 =
      RiskAssessment().apply {
        id = "3"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0))
        )
      }

    val res = runBlocking {
      database.insert(riskAssessment1, riskAssessment2, riskAssessment3)
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(RiskAssessment.PROBABILITY) {
              prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
              value = BigDecimal("99.5")
            }
          }
          .getQuery()
      )
    }

    Truth.assertThat(res).hasSize(2)
    Truth.assertThat(res.all { it.predictionFirstRep.probabilityDecimalType <= DecimalType(99.5) })
      .isTrue()
  }

  @Test
  fun search_decimal_EndsBefore() {
    val riskAssessment1 =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5))
        )
      }
    val riskAssessment2 =
      RiskAssessment().apply {
        id = "2"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100.5))
        )
      }
    val riskAssessment3 =
      RiskAssessment().apply {
        id = "3"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0))
        )
      }

    val res = runBlocking {
      database.insert(riskAssessment1, riskAssessment2, riskAssessment3)
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(RiskAssessment.PROBABILITY) {
              prefix = ParamPrefixEnum.ENDS_BEFORE
              value = BigDecimal("99.5")
            }
          }
          .getQuery()
      )
    }

    Truth.assertThat(res).hasSize(1)
    Truth.assertThat(res.all { it.predictionFirstRep.probabilityDecimalType < DecimalType(99.5) })
      .isTrue()
  }

  @Test
  fun search_decimal_StartAfter() {
    val riskAssessment1 =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5))
        )
      }
    val riskAssessment2 =
      RiskAssessment().apply {
        id = "2"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100.5))
        )
      }
    val riskAssessment3 =
      RiskAssessment().apply {
        id = "3"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0))
        )
      }

    val res = runBlocking {
      database.insert(riskAssessment1, riskAssessment2, riskAssessment3)
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(RiskAssessment.PROBABILITY) {
              prefix = ParamPrefixEnum.STARTS_AFTER
              value = BigDecimal("99.5")
            }
          }
          .getQuery()
      )
    }

    Truth.assertThat(res).hasSize(1)
    Truth.assertThat(res.all { it.predictionFirstRep.probabilityDecimalType > DecimalType(99.5) })
      .isTrue()
  }
  @Test
  fun search_number_Approximate() {
    val riskAssessment1 =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(89.5))
        )
      }
    val riskAssessment2 =
      RiskAssessment().apply {
        id = "2"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(110.0))
        )
      }
    val riskAssessment3 =
      RiskAssessment().apply {
        id = "3"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(90.0))
        )
      }

    val res = runBlocking {
      database.insert(riskAssessment1, riskAssessment2, riskAssessment3)
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(RiskAssessment.PROBABILITY) {
              prefix = ParamPrefixEnum.APPROXIMATE
              value = BigDecimal("100")
            }
          }
          .getQuery()
      )
    }

    Truth.assertThat(res).hasSize(2)
    Truth.assertThat(
        res.all {
          it.predictionFirstRep.probabilityDecimalType <= DecimalType(110) &&
            it.predictionFirstRep.probabilityDecimalType >= DecimalType(90)
        }
      )
      .isTrue()
  }
}
