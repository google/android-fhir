/*
 * Copyright 2022-2024 Google LLC
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

import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.QuantityClientParam
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.gclient.TokenClientParam
import ca.uhn.fhir.rest.gclient.UriClientParam
import com.google.android.fhir.search.filter.DateParamFilterCriterion
import com.google.android.fhir.search.filter.NumberParamFilterCriterion
import com.google.android.fhir.search.filter.QuantityParamFilterCriterion
import com.google.android.fhir.search.filter.ReferenceParamFilterCriterion
import com.google.android.fhir.search.filter.StringParamFilterCriterion
import com.google.android.fhir.search.filter.TokenParamFilterCriterion
import com.google.android.fhir.search.filter.UriParamFilterCriterion
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient

@DslMarker @Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE) annotation class BaseSearchDsl

/**
 * Defines the basic functionality provided by the search including [filter], [sort] and logical
 * [operation] on filters.
 */
@BaseSearchDsl
interface BaseSearch {
  /** Logical operator between the filters. */
  var operation: Operation

  /** Count of the maximum expected search results. */
  var count: Int?

  /** Index from which the matching search results should be returned. */
  var from: Int?

  fun filter(
    stringParameter: StringClientParam,
    vararg init: StringParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR,
  )

  fun filter(
    referenceParameter: ReferenceClientParam,
    vararg init: ReferenceParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR,
  )

  fun filter(
    dateParameter: DateClientParam,
    vararg init: DateParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR,
  )

  fun filter(
    quantityParameter: QuantityClientParam,
    vararg init: QuantityParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR,
  )

  fun filter(
    tokenParameter: TokenClientParam,
    vararg init: TokenParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR,
  )

  fun filter(
    numberParameter: NumberClientParam,
    vararg init: NumberParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR,
  )

  fun filter(
    uriParam: UriClientParam,
    vararg init: UriParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR,
  )

  /**
   * When sorting is applied on a field with repeated values (e.g. [Patient.GIVEN] ), the order is
   * defined by the order of the repeated values in the resource (e.g. [HumanName.given] for
   * [Patient]).
   *
   * If there are two Patients p1 and p2 as follows
   *
   *  ```
   *  {
   *     "resourceType": "Patient",
   *     "id": "p1",
   *     "name": [
   *         {
   *             "family": "Cooper",
   *             "given": [
   *                 "3",
   *                 "1"
   *             ]
   *         }
   *     ]
   * }
   * ```
   *
   * AND
   *
   * ```
   * {
   *     "resourceType": "Patient",
   *     "id": "p2",
   *     "name": [
   *         {
   *             "family": "Cooper",
   *             "given": [
   *                 "2",
   *                 "4"
   *             ]
   *         }
   *     ]
   * }
   * ```
   *
   * Then sorting the patients with their given i.e [Patient.GIVEN] depends on the first given in
   * the first name (i.e **3** and **2**).
   */
  fun sort(parameter: StringClientParam, order: Order)

  /**
   * When sorting is applied on a field with repeated values, the order is defined by the order of
   * for repeated values in the resource.
   *
   * @see sort(parameter: StringClientParam, order: Order) for more details.
   */
  fun sort(parameter: NumberClientParam, order: Order)

  /**
   * When sorting is applied on a field with repeated values, the order is defined by the order of
   * for repeated values in the resource.
   *
   * @see sort(parameter: StringClientParam, order: Order) for more details.
   */
  fun sort(parameter: DateClientParam, order: Order)
}
