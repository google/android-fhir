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

package com.google.android.fhir.hapiprotoconverter

internal val primitiveTypeList =
  listOf(
    "base64Binary",
    "boolean",
    "canonical",
    "code",
    "date",
    "dateTime",
    "decimal",
    "id",
    "instant",
    "integer",
    "markdown",
    "oid",
    "positiveInt",
    "string",
    "time",
    "unsignedInt",
    "uri",
    "url",
    "uuid",
    //      "xhtml"
    )

// package that contains all converters
internal const val converterPackage = "com.google.android.fhir.hapiprotoconverter.generated"
// template for when the max value of an element is 1 ( in protos )
internal const val singleMethodTemplate = ".set%L"
// template for when the max value of an element is > 1 ( in protos )
internal const val multipleMethodTemplate = ".addAll%L"

// TODO handle these
internal val ignoreValueSet =
  listOf(
    "http://hl7.org/fhir/ValueSet/languages",
    "http://hl7.org/fhir/ValueSet/expression-language",
  )

// proto package that contains the structures
internal const val protoPackage = "com.google.fhir.r4.core"
// hapi package that contains the structures
internal const val hapiPackage = "org.hl7.fhir.r4.model"
// uri that specifies if a valueset is in the the common Enumerations Class
internal const val uriBindingName =
  "http://hl7.org/fhir/StructureDefinition/elementdefinition-bindingName"
internal const val FHIRPATH_TYPE_PREFIX = "http://hl7.org/fhirpath/"
internal const val FHIR_TYPE_EXTENSION_URL =
  "http://hl7.org/fhir/StructureDefinition/structuredefinition-fhir-type"
