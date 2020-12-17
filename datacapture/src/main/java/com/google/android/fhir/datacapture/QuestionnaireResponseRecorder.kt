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

package com.google.android.fhir.datacapture

import org.hl7.fhir.r4.model.DateType

interface QuestionnaireResponseRecorder {
    /**
     * Records an answer of [Boolean] type to the question with [linkId]. This will overwrite any
     * previous answer to the same question.
     */
    fun recordAnswer(linkId: String, answer: Boolean)

    /**
     * Records an answer of [String] type to the question with [linkId]. This will overwrite any
     * previous answer to the same question.
     */
    fun recordAnswer(linkId: String, answer: String)

    /**
     * Records an answer of [DateType] to the question with [linkId]. This will overwrite any
     * previous answer to the same question.
     */
    fun recordAnswer(linkId: String, year: Int, month: Int, dayOfMonth: Int)
}
