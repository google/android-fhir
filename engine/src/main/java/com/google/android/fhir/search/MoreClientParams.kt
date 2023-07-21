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

package com.google.android.fhir.search

import ca.uhn.fhir.rest.gclient.DateClientParam

internal const val LOCAL_LAST_UPDATED = "local_lastUpdated"
internal const val LAST_UPDATED = "_lastUpdated"

/** Resource Date Parameter to search using local last updated date. */
val LOCAL_LAST_UPDATED_PARAM = DateClientParam(LOCAL_LAST_UPDATED)

/** Resource Date Parameter to search using remote last updated date. */
val LAST_UPDATED_PARAM = DateClientParam(LAST_UPDATED)
