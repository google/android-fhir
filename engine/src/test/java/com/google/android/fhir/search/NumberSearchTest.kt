package com.google.android.fhir.search


import ca.uhn.fhir.rest.param.ParamPrefixEnum
import java.math.BigDecimal
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.RiskAssessment
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import com.google.common.truth.Truth.assertThat
import java.lang.IllegalArgumentException
import org.junit.Assert


@RunWith(Parameterized::class)
class NumberSearchTest(
  private val num: BigDecimal,
  private val lowerBound: BigDecimal,
  private val upperBound: BigDecimal
) {
    companion object {
    @JvmStatic
    @Parameterized.Parameters
    fun data() : Collection<Array<Any>> {
      return listOf(
        arrayOf(BigDecimal("100.00"),BigDecimal("99.995"),BigDecimal("100.005")),
        arrayOf(BigDecimal("100"),BigDecimal("99.5"),BigDecimal("100.5")),
        arrayOf(BigDecimal("1e-1"), BigDecimal("0.95e-1"), BigDecimal("1.05e-1")),
        arrayOf(BigDecimal("1e2"),BigDecimal("95"),BigDecimal("105"))
      )
    }
  }
  @Test
  fun search_filter_number_equals() {
    /* x contains pairs of values and their corresponding range (see BigDecimal.getRange() in
    MoreSearch.KT) */
      val query =
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.EQUAL
                value = num
              }
            )
          }
          .getQuery()
      assertThat(query.query)
        .isEqualTo(
          """ 
          SELECT a.serializedResource
          FROM ResourceEntity a
          WHERE a.resourceType = ?
          AND a.resourceId IN (
          SELECT resourceId FROM NumberIndexEntity
          WHERE resourceType = ? AND index_name = ? AND (index_value >= ? AND index_value < ?)
          )
          """.trimIndent()
        )

      assertThat(query.args)
        .isEqualTo(
          listOf(
            ResourceType.RiskAssessment.name,
            ResourceType.RiskAssessment.name,
            RiskAssessment.PROBABILITY.paramName,
            lowerBound.toDouble(),
            upperBound.toDouble()
          )
        )
  }

  @Test
  fun search_filter_number_notEquals() {
    val query =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.NOT_EQUAL
              value = num
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value < ? OR index_value >= ?)
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          lowerBound.toDouble(),
          upperBound.toDouble()
        )
      )
  }

  @Test
  fun search_filter_number_greater() {
    val query =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.GREATERTHAN
              value = num
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value > ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble()
        )
      )
  }

  @Test
  fun search_filter_number_greaterThanEqual() {
    val query =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
              value = num
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value >= ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble()
        )
      )
  }

  @Test
  fun search_filter_number_less() {
    val query =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.LESSTHAN
              value = num
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value < ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble()
        )
      )
  }

  @Test
  fun search_filter_number_lessThanEquals() {
    val query =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
              value = num
            }
          )
        }
        .getQuery()
    assertThat(query.query).isEqualTo(""" 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value <= ?
        )
        """.trimIndent())
    assertThat(query.args).isEqualTo(listOf(
      ResourceType.RiskAssessment.name,
      ResourceType.RiskAssessment.name,
      RiskAssessment.PROBABILITY.paramName,
      num.toDouble()
    ))
  }

  @Test
  fun search_filter_integer_endsBefore_error() {
    val illegalArgumentException =
      Assert.assertThrows(IllegalArgumentException::class.java) {
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.ENDS_BEFORE
                value = num
              }
            )
          }
          .getQuery()
      }
    assertThat(illegalArgumentException.message)
      .isEqualTo("Prefix ENDS_BEFORE not allowed for Integer type")
  }

  @Test
  fun search_filter_decimal_endsBefore() {
    val query =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.ENDS_BEFORE
              value = num
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value < ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble()
        )
      )
  }

  @Test
  fun search_filter_integer_startsAfter_error() {
    val illegalArgumentException =
      Assert.assertThrows(IllegalArgumentException::class.java) {
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.STARTS_AFTER
                value = num
              }
            )
          }
          .getQuery()
      }
    assertThat(illegalArgumentException.message)
      .isEqualTo("Prefix STARTS_AFTER not allowed for Integer type")
  }

  @Test
  fun search_filter_decimal_startsAfter() {
    val query =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.STARTS_AFTER
              value = num
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value > ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          num.toDouble()
        )
      )
  }

  @Test
  fun search_filter_number_approximate() {
    val query =
      Search(ResourceType.RiskAssessment)
        .apply {
          filter(
            RiskAssessment.PROBABILITY,
            {
              prefix = ParamPrefixEnum.APPROXIMATE
              value = num
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value >= ? AND index_value <= ?)
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          lowerBound.toDouble(),
          upperBound.toDouble()
        )
      )
  }
}

