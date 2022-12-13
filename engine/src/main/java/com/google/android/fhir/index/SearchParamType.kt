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

package com.google.android.fhir.index

enum class SearchParamType(
  val code: String?,
  val system: String?,
  val definition: String?,
  val display: String?
) {
  /** Search parameter SHALL be a number (a whole number, or a decimal). */
  NUMBER(
    "number",
    "http://hl7.org/fhir/search-param-type",
    "Search parameter SHALL be a number (a whole number, or a decimal).",
    "Number"
  ),

  /**
   * Search parameter is on a date/time. The date format is the standard XML format, though other
   * formats may be supported.
   */
  DATE(
    "date",
    "http://hl7.org/fhir/search-param-type",
    "Search parameter is on a date/time. The date format is the standard XML format, though other formats may be supported.",
    "Date/DateTime"
  ),

  /**
   * Search parameter is a simple string, like a name part. Search is case-insensitive and
   * accent-insensitive. May match just the start of a string. String parameters may contain spaces.
   */
  STRING(
    "string",
    "http://hl7.org/fhir/search-param-type",
    "Search parameter is a simple string, like a name part. Search is case-insensitive and accent-insensitive. May match just the start of a string. String parameters may contain spaces.",
    "String"
  ),

  /**
   * Search parameter on a coded element or identifier. May be used to search through the text,
   * display, code and code/codesystem (for codes) and label, system and key (for identifier). Its
   * value is either a string or a pair of namespace and value, separated by a "|", depending on the
   * modifier used.
   */
  TOKEN(
    "token",
    "http://hl7.org/fhir/search-param-type",
    "Search parameter on a coded element or identifier. May be used to search through the text, display, code and code/codesystem (for codes) and label, system and key (for identifier). Its value is either a string or a pair of namespace and value, separated by a \"|\", depending on the modifier used.",
    "Token"
  ),

  /** A reference to another resource (Reference or canonical). */
  REFERENCE(
    "reference",
    "http://hl7.org/fhir/search-param-type",
    "A reference to another resource (Reference or canonical).",
    "Reference"
  ),

  /** A composite search parameter that combines a search on two values together. */
  COMPOSITE(
    "composite",
    "http://hl7.org/fhir/search-param-type",
    "A composite search parameter that combines a search on two values together.",
    "Composite"
  ),

  /** A search parameter that searches on a quantity. */
  QUANTITY(
    "quantity",
    "http://hl7.org/fhir/search-param-type",
    "A search parameter that searches on a quantity.",
    "Quantity"
  ),

  /** A search parameter that searches on a URI (RFC 3986). */
  URI(
    "uri",
    "http://hl7.org/fhir/search-param-type",
    "A search parameter that searches on a URI (RFC 3986).",
    "URI"
  ),

  /** Special logic applies to this parameter per the description of the search parameter. */
  SPECIAL(
    "special",
    "http://hl7.org/fhir/search-param-type",
    "Special logic applies to this parameter per the description of the search parameter.",
    "Special"
  ),

  /** added to help the parsers */
  NULL(null, null, null, null);

  companion object {
    private val codeMapping = SearchParamType.values().associateBy { it.code }
    fun fromCode(codeString: String?) =
      codeMapping[codeString] ?: throw Exception("Unknown SearchParamType code '$codeString'")
  }
}
