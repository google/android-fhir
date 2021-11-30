package com.google.android.fhir.search.filter

import ca.uhn.fhir.rest.gclient.UriClientParam
import com.google.android.fhir.search.SearchQuery
import com.google.android.fhir.search.StringFilterModifier
import java.math.BigDecimal
import org.hl7.fhir.r4.model.ResourceType

class UriParamFilterCriterion(val parameter: UriClientParam, var value: String? = null) :
  FilterCriterion {

  override fun query(type: ResourceType): SearchQuery {
    return SearchQuery(
      """
      SELECT resourceId FROM UriIndexEntity
      WHERE resourceType = ? AND index_name = ? AND index_value = ? 
      """,
      listOf(type.name, parameter.paramName, value!!)
    )
  }
}
