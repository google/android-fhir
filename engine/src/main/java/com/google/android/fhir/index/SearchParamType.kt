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

enum class SearchParamType {
  /** Search parameter SHALL be a number (a whole number, or a decimal). */
  NUMBER,

  /**
   * Search parameter is on a date/time. The date format is the standard XML format, though other
   * formats may be supported.
   */
  DATE,

  /**
   * Search parameter is a simple string, like a name part. Search is case-insensitive and
   * accent-insensitive. May match just the start of a string. String parameters may contain spaces.
   */
  STRING,

  /**
   * Search parameter on a coded element or identifier. May be used to search through the text,
   * display, code and code/codesystem (for codes) and label, system and key (for identifier). Its
   * value is either a string or a pair of namespace and value, separated by a "|", depending on the
   * modifier used.
   */
  TOKEN,

  /** A reference to another resource (Reference or canonical). */
  REFERENCE,

  /** A composite search parameter that combines a search on two values together. */
  COMPOSITE,

  /** A search parameter that searches on a quantity. */
  QUANTITY,

  /** A search parameter that searches on a URI (RFC 3986). */
  URI,

  /** Special logic applies to this parameter per the description of the search parameter. */
  SPECIAL,

  /** added to help the parsers */
  NULL;

  fun toCode(): String? {
    return when (this) {
      NUMBER -> "number"
      DATE -> "date"
      STRING -> "string"
      TOKEN -> "token"
      REFERENCE -> "reference"
      COMPOSITE -> "composite"
      QUANTITY -> "quantity"
      URI -> "uri"
      SPECIAL -> "special"
      NULL -> null
    }
  }

  val system: String?
    get() =
      when (this) {
        NUMBER -> "http://hl7.org/fhir/search-param-type"
        DATE -> "http://hl7.org/fhir/search-param-type"
        STRING -> "http://hl7.org/fhir/search-param-type"
        TOKEN -> "http://hl7.org/fhir/search-param-type"
        REFERENCE -> "http://hl7.org/fhir/search-param-type"
        COMPOSITE -> "http://hl7.org/fhir/search-param-type"
        QUANTITY -> "http://hl7.org/fhir/search-param-type"
        URI -> "http://hl7.org/fhir/search-param-type"
        SPECIAL -> "http://hl7.org/fhir/search-param-type"
        NULL -> null
      }
  val definition: String?
    get() {
      return when (this) {
        NUMBER -> "Search parameter SHALL be a number (a whole number, or a decimal)."
        DATE ->
          "Search parameter is on a date/time. The date format is the standard XML format, though other formats may be supported."
        STRING ->
          "Search parameter is a simple string, like a name part. Search is case-insensitive and accent-insensitive. May match just the start of a string. String parameters may contain spaces."
        TOKEN ->
          "Search parameter on a coded element or identifier. May be used to search through the text, display, code and code/codesystem (for codes) and label, system and key (for identifier). Its value is either a string or a pair of namespace and value, separated by a \"|\", depending on the modifier used."
        REFERENCE -> "A reference to another resource (Reference or canonical)."
        COMPOSITE -> "A composite search parameter that combines a search on two values together."
        QUANTITY -> "A search parameter that searches on a quantity."
        URI -> "A search parameter that searches on a URI (RFC 3986)."
        SPECIAL ->
          "Special logic applies to this parameter per the description of the search parameter."
        NULL -> null
      }
    }
  val display: String?
    get() {
      return when (this) {
        NUMBER -> "Number"
        DATE -> "Date/DateTime"
        STRING -> "String"
        TOKEN -> "Token"
        REFERENCE -> "Reference"
        COMPOSITE -> "Composite"
        QUANTITY -> "Quantity"
        URI -> "URI"
        SPECIAL -> "Special"
        NULL -> null
      }
    }

  companion object {
    @Throws(Exception::class)
    fun fromCode(codeString: String?): SearchParamType? {
      if (codeString.isNullOrBlank()) return null
      if ("number" == codeString) return NUMBER
      if ("date" == codeString) return DATE
      if ("string" == codeString) return STRING
      if ("token" == codeString) return TOKEN
      if ("reference" == codeString) return REFERENCE
      if ("composite" == codeString) return COMPOSITE
      if ("quantity" == codeString) return QUANTITY
      if ("uri" == codeString) return URI
      if ("special" == codeString) return SPECIAL
      throw Exception("Unknown SearchParamType code '$codeString'")
    }
  }
}
