/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir

object Constant {

  //From db/impl/DatabaseImpl.kt
  const val DEFAULT_DATABASE_NAME = "ResourceDatabase"

  //From sync/SyncData.kt
  const val SORT_KEY = "_sort"
  const val LAST_UPDATED_KEY = "_lastUpdated"
  const val ADDRESS_COUNTRY_KEY = "address-country"
  const val LAST_UPDATED_ASC_VALUE = "_lastUpdated"

  //From index/ResourceIndexer.kt
  /**
   * The FHIR currency code system. See: https://bit.ly/30YB3ML. See:
   * https://www.hl7.org/fhir/valueset-currencies.html.
   */
  const val FHIR_CURRENCY_CODE_SYSTEM = "urn:iso:std:iso:4217"

  //From resource/Resources.kt
  /** The HAPI Fhir package prefix for R4 resources. */
  const val R4_RESOURCE_PACKAGE_PREFIX = "org.hl7.fhir.r4.model."
}