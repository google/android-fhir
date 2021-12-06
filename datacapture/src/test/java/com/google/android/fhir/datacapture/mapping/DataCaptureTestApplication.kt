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

package com.google.android.fhir.datacapture.mapping

import android.app.Application
import com.google.android.fhir.datacapture.DataCaptureConfig
import com.google.android.fhir.datacapture.ExternalAnswerValueSetResolver
import com.google.android.fhir.datacapture.QuestionnaireViewModelTest
import org.hl7.fhir.r4.model.Coding

/** Application class when you want to test the DataCaptureConfig.Provider */
class DataCaptureTestApplication : Application(), DataCaptureConfig.Provider {
  override fun getDataCaptureConfiguration() =
    DataCaptureConfig(
      valueSetResolverExternal =
        object : ExternalAnswerValueSetResolver {
          override suspend fun resolve(uri: String): List<Coding> {
            return if (uri == QuestionnaireViewModelTest.CODE_SYSTEM_YES_NO)
              listOf(
                Coding().apply {
                  system = QuestionnaireViewModelTest.CODE_SYSTEM_YES_NO
                  code = "Y"
                  display = "Yes"
                },
                Coding().apply {
                  system = QuestionnaireViewModelTest.CODE_SYSTEM_YES_NO
                  code = "N"
                  display = "No"
                },
                Coding().apply {
                  system = QuestionnaireViewModelTest.CODE_SYSTEM_YES_NO
                  code = "asked-unknown"
                  display = "Don't Know"
                }
              )
            else emptyList()
          }
        }
    )
}
