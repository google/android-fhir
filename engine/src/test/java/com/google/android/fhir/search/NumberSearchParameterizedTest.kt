/*
 * Copyright 2021-2023 Google LLC
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
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.common.truth.Truth.assertThat
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.RiskAssessment
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config

/** Unit tests for when [NumberParamFilterCriterion] used in [MoreSearch]. */
@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class NumberSearchParameterizedTest(
  private val num: BigDecimal,
  private val lowerBound: BigDecimal,
  private val upperBound: BigDecimal,
) {
  companion object {
    @JvmStatic
    @ParameterizedRobolectricTestRunner.Parameters
    fun params(): Collection<Array<BigDecimal>> {
      return listOf(
        arrayOf(BigDecimal("100.00"), BigDecimal("99.995"), BigDecimal("100.005")),
        arrayOf(BigDecimal("100.0"), BigDecimal("99.95"), BigDecimal("100.05")),
        arrayOf(BigDecimal("1e-1"), BigDecimal("0.5e-1"), BigDecimal("1.5e-1")),
      )
    }
  }

  private val baseQuery: String =
    """
    SELECT a.serializedResource
    FROM ResourceEntity a
    WHERE a.resourceType = ?
    AND a.resourceUuid IN (
    SELECT resourceUuid FROM NumberIndexEntity
        """
      .trimIndent()

  @Test
  fun `should search equal values`() {
    val search =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.EQUAL
              value = num
            },
          )
        }
        .getQuery()
    assertThat(search.query)
      .isEqualTo(
        """
        |$baseQuery
        |WHERE resourceType = ? AND index_name = ? AND (index_value >= ? AND index_value < ?)
        |)
                """
          .trimMargin(),
      )

    assertThat(search.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          lowerBound.toDouble(),
          upperBound.toDouble(),
        ),
      )
  }

  @Test
  fun `should search unequal values`() {
    val search =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.NOT_EQUAL
              value = num
            },
          )
        }
        .getQuery()
    assertThat(search.query)
      .isEqualTo(
        """
        |$baseQuery
        |WHERE resourceType = ? AND index_name = ? AND (index_value < ? OR index_value >= ?)
        |)
                """
          .trimMargin(),
      )

    assertThat(search.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          lowerBound.toDouble(),
          upperBound.toDouble(),
        ),
      )
  }

  @Test
  fun `should search values greater than a number`() {
    val search =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.GREATERTHAN
              value = num
            },
          )
        }
        .getQuery()
    assertThat(search.query)
      .isEqualTo(
        """
        |$baseQuery
        |WHERE resourceType = ? AND index_name = ? AND index_value > ?
        |)
                """
          .trimMargin(),
      )

    assertThat(search.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble(),
        ),
      )
  }

  @Test
  fun `should search values greater than or equal to a number`() {
    val search =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
              value = num
            },
          )
        }
        .getQuery()
    assertThat(search.query)
      .isEqualTo(
        """
        |$baseQuery
        |WHERE resourceType = ? AND index_name = ? AND index_value >= ?
        |)
                """
          .trimMargin(),
      )

    assertThat(search.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble(),
        ),
      )
  }

  @Test
  fun `should search values less than a number`() {
    val search =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.LESSTHAN
              value = num
            },
          )
        }
        .getQuery()
    assertThat(search.query)
      .isEqualTo(
        """
        |$baseQuery
        |WHERE resourceType = ? AND index_name = ? AND index_value < ?
        |)
                """
          .trimMargin(),
      )

    assertThat(search.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble(),
        ),
      )
  }

  @Test
  fun `should search values less than or equal to a number`() {
    val search =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
              value = num
            },
          )
        }
        .getQuery()
    assertThat(search.query)
      .isEqualTo(
        """
        |$baseQuery
        |WHERE resourceType = ? AND index_name = ? AND index_value <= ?
        |)
                """
          .trimMargin(),
      )
    assertThat(search.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble(),
        ),
      )
  }

  @Test
  fun `should throw error when ENDS_BEFORE prefix given with integer value`() {
    val illegalArgumentException =
      Assert.assertThrows(IllegalArgumentException::class.java) {
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.ENDS_BEFORE
                value = BigDecimal("100")
              },
            )
          }
          .getQuery()
      }
    assertThat(illegalArgumentException.message)
      .isEqualTo("Prefix ENDS_BEFORE not allowed for Integer type")
  }

  @Test
  fun `should search value when ENDS_BEFORE prefix given with decimal value`() {
    val search =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.ENDS_BEFORE
              value = num
            },
          )
        }
        .getQuery()
    assertThat(search.query)
      .isEqualTo(
        """
        |$baseQuery
        |WHERE resourceType = ? AND index_name = ? AND index_value < ?
        |)
                """
          .trimMargin(),
      )

    assertThat(search.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble(),
        ),
      )
  }

  @Test
  fun `should throw error when STARTS_AFTER prefix given with integer value`() {
    val illegalArgumentException =
      Assert.assertThrows(IllegalArgumentException::class.java) {
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.STARTS_AFTER
                value = BigDecimal(100)
              },
            )
          }
          .getQuery()
      }
    assertThat(illegalArgumentException.message)
      .isEqualTo("Prefix STARTS_AFTER not allowed for Integer type")
  }

  @Test
  fun `should search value when STARTS_AFTER prefix given with decimal value`() {
    val search =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.STARTS_AFTER
              value = num
            },
          )
        }
        .getQuery()
    assertThat(search.query)
      .isEqualTo(
        """
        |$baseQuery
        |WHERE resourceType = ? AND index_name = ? AND index_value > ?
        |)
                """
          .trimMargin(),
      )

    assertThat(search.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble(),
        ),
      )
  }

  @Test
  fun `should search approximate values`() {
    val search =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.APPROXIMATE
              value = BigDecimal("1e-1")
            },
          )
        }
        .getQuery()
    assertThat(search.query)
      .isEqualTo(
        """
        |$baseQuery
        |WHERE resourceType = ? AND index_name = ? AND (index_value >= ? AND index_value <= ?)
        |)
                """
          .trimMargin(),
      )

    assertThat(search.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          0.09,
          0.11,
        ),
      )
  }
}
