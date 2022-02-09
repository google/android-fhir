package com.google.android.fhir.search


import ca.uhn.fhir.rest.param.ParamPrefixEnum
import java.math.BigDecimal
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.RiskAssessment
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import com.google.common.truth.Truth.assertThat


@RunWith(Parameterized::class)
class NumberSearch(
  private val num: BigDecimal,
  private val expected1: String,
  private val expected2: String
) {
    companion object {
    @JvmStatic
    @Parameterized.Parameters
    fun data() : Collection<Array<Any>> {
      return listOf(
        arrayOf(BigDecimal("100.00"),""" 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value <= ?
        )
        """.trimIndent(),listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          BigDecimal("100.00").toDouble()
        )),
        arrayOf(BigDecimal("100"),""" 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value <= ?
        )
        """.trimIndent(),listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          BigDecimal("100").toDouble()
        )),
        arrayOf(BigDecimal("1e-1"), """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value <= ?
        )
        """.trimIndent(),listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          BigDecimal("1e-1").toDouble()
        )),
        arrayOf(BigDecimal("1e2"), """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM NumberIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value <= ?
        )
        """.trimIndent(),listOf(
          ResourceType.RiskAssessment.name,
          ResourceType.RiskAssessment.name,
          RiskAssessment.PROBABILITY.paramName,
          BigDecimal("1e2").toDouble()
        ))
      )
    }
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
    assertThat(query.query).isEqualTo(expected1)
    assertThat(query.args).isEqualTo(expected2)
  }
}

