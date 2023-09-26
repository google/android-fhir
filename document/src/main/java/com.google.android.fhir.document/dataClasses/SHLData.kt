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

import java.io.Serializable

/* This data class holds all the information stored in a SHL.
If the P flag is present, a passcode is needed to decode the data */
data class SHLData(
  var fullLink: String,
  var shl: String,
  var manifestUrl: String,
  var key: String,
  var label: String,
  var flag: String,
  var exp: String,
  var v: String,
  var ipsDoc: IPSDocument,
) : Serializable {

  constructor() : this("", "", "", "", "", "", "", "", IPSDocument())

  constructor(fullLink: String) : this(fullLink, "", "", "", "", "", "", "", IPSDocument())

  constructor(doc: IPSDocument) : this("", "", "", "", "", "", "", "", doc)
}
