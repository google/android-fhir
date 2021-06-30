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

package org.fhir.ucum

import org.hl7.fhir.exceptions.FHIRException

/** Needed for HAPI's FHIRPathEngine. See https://github.com/hapifhir/hapi-fhir/issues/2443. */
class UcumException : FHIRException {
  constructor() {}
  constructor(message: String?, cause: Throwable?) : super(message, cause) {}
  constructor(message: String?) : super(message) {}
  constructor(cause: Throwable?) : super(cause) {}
}
