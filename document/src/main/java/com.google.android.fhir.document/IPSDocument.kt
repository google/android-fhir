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

package com.google.android.fhir.document

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource

/**
 * Represents an International Patient Summary (IPS) document, associating it with a specific
 * patient and containing a list of titles present in the document. For detailed specifications, see
 * [Official IPS Implementation Guide](https://build.fhir.org/ig/HL7/fhir-ips/index.html).
 *
 * This class serves as a developer-friendly, in-memory representation of an IPS document, allowing
 * for easier manipulation and interaction with its components compared to a raw FHIR Composition
 * resource.
 *
 * @property document The FHIR Bundle itself, which contains the IPS document
 * @property titles A list of titles of the sections present in the document.
 * @property patient The FHIR Patient resource associated with the IPS document.
 */
data class IPSDocument(
  val document: Bundle,
  val titles: ArrayList<Title>,
  val patient: Patient,
)

/**
 * Represents a title, which corresponds to a section present in the IPS document.
 *
 * @property name The string storing the title of the section itself. Examples: "Allergies and
 *   Intolerances", "Immunizations", etc...
 * @property dataEntries A list of FHIR resources which are present in the section. For example, if
 *   the title is "Allergies and Intolerances", all the patient's current allergies and/or
 *   intolerances will be listed here.
 */
data class Title(
  val name: String,
  val dataEntries: ArrayList<Resource>,
)
