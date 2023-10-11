/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.document.dataClasses

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource

/* A data class object which stores an IPS document, the patient it relates to, and a list of
`  titles present in the document */
data class IPSDocument(
  val document: Bundle,
  val titles: ArrayList<Title>,
  val patient: Patient,
)

/* A Title contains a string storing the title itself and a list of resource associated to it */
data class Title(
  val name: String,
  val dataEntries: ArrayList<Resource>,
)

