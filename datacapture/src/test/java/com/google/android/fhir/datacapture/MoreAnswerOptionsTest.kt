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

import android.os.Build
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreAnswerOptionsTest {

  @Test
  fun getDisplayString_choiceItemType_answerOptionShouldReturnValueCodingDisplayValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent()
        .setValue(Coding().setCode("test-code").setDisplay("Test Code"))

    assertThat(answerOption.displayString).isEqualTo("Test Code")
  }

  @Test
  fun getDisplayString_choiceItemType_answerOptionShouldReturnValueCodingCodeValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(Coding().setCode("test-code"))

    assertThat(answerOption.displayString).isEqualTo("test-code")
  }

  @Test
  fun getDisplayString_choiceItemType_shouldThrowExceptionForIllegalAnswerOptionValueX() {
    val answerOption = Questionnaire.QuestionnaireItemAnswerOptionComponent()

    assertFailsWith<IllegalArgumentException> { answerOption.displayString }
  }
}
