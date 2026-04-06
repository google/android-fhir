/*
 * Copyright 2026 Google LLC
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

package com.google.android.fhir.datacapture.contrib.views.locationwidget

import android_fhir.contrib.locationwidget.generated.resources.Res
import android_fhir.contrib.locationwidget.generated.resources.required_text_and_new_line
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.fhir.datacapture.QuestionnaireItemViewFactoryMatcher
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewFactory
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import org.jetbrains.compose.resources.stringResource

internal const val PRIMARY_GPS_COORDINATE_EXTENSION_URL =
  "https://github.com/google/android-fhir/StructureDefinition/gps-coordinate"
internal const val GPS_COORDINATE_EXTENSION_URL = "gps-coordinate"
internal const val GPS_COORDINATE_EXTENSION_VALUE_LATITUDE = "latitude"
internal const val GPS_COORDINATE_EXTENSION_VALUE_LONGITUDE = "longitude"
internal const val GPS_COORDINATE_EXTENSION_VALUE_ALTITUDE = "altitude"

object LocationDataItemViewFactory : QuestionnaireItemViewFactory {

  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val answer =
      remember(questionnaireViewItem.answers.toString()) {
        questionnaireViewItem.answers
          .singleOrNull()
          ?.value
          ?.asDecimal()
          ?.value
          ?.value
          ?.toStringExpanded()
          ?: ""
      }
    val requiredTextAndNewLineText = stringResource(Res.string.required_text_and_new_line)
    val validationResultMessage =
      remember(questionnaireViewItem.validationResult) {
        when (val validationResult = questionnaireViewItem.validationResult) {
          is Invalid -> {
            if (
              questionnaireViewItem.questionnaireItem.required?.value == true &&
                questionnaireViewItem.questionViewTextConfiguration.showRequiredText
            ) {
              requiredTextAndNewLineText + validationResult.singleStringValidationMessage
            } else {
              validationResult.singleStringValidationMessage
            }
          }
          else -> null
        }
      }
    val questionnaireItemLabel =
      remember(questionnaireViewItem.questionnaireItem) { questionnaireViewItem.questionText }

    var textState by remember(answer) { mutableStateOf(answer) }

    LaunchedEffect(Unit) {
      LocationEventEmitter.locationUpdates.collect { locationData ->
        val gpsCoordinateExtensionValue =
          questionnaireViewItem.questionnaireItem.gpsCoordinateValueString

        val questionnaireResponseAnswerValue =
          when (gpsCoordinateExtensionValue) {
            GPS_COORDINATE_EXTENSION_VALUE_LATITUDE ->
              QuestionnaireResponse.Item.Answer.Value.Decimal(
                value = Decimal(value = BigDecimal.fromDouble(locationData.latitude)),
              )
            GPS_COORDINATE_EXTENSION_VALUE_LONGITUDE ->
              QuestionnaireResponse.Item.Answer.Value.Decimal(
                value = Decimal(value = BigDecimal.fromDouble(locationData.longitude)),
              )
            GPS_COORDINATE_EXTENSION_VALUE_ALTITUDE ->
              locationData.altitude?.let { alt ->
                QuestionnaireResponse.Item.Answer.Value.Decimal(
                  value = Decimal(value = BigDecimal.fromDouble(alt)),
                )
              }
            else -> null
          }

        if (questionnaireResponseAnswerValue != null) {
          questionnaireViewItem.setAnswer(
            QuestionnaireResponse.Item.Answer(value = questionnaireResponseAnswerValue),
          )
        }
      }
    }

    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
            vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
          ),
    ) {
      Header(questionnaireViewItem)

      OutlinedTextField(
        value = textState,
        onValueChange = { /* View shouldn't directly edit coordinate */},
        modifier = Modifier.fillMaxWidth(),
        enabled = false,
        isError = !validationResultMessage.isNullOrBlank(),
        supportingText = { validationResultMessage?.takeIf { it.isNotBlank() }?.let { Text(it) } },
        label = { questionnaireItemLabel?.let { Text(questionnaireItemLabel) } },
      )
    }
  }
}

internal val Questionnaire.Item.gpsCoordinateValueString: String?
  get() =
    extension
      .find { extension ->
        extension.url == PRIMARY_GPS_COORDINATE_EXTENSION_URL ||
          extension.url == GPS_COORDINATE_EXTENSION_URL
      }
      ?.value
      ?.asString()
      ?.value
      ?.value

val LocationDataItemViewFactoryMatcher =
  QuestionnaireItemViewFactoryMatcher(LocationDataItemViewFactory) {
    it.extension.any { extension ->
      extension.url == PRIMARY_GPS_COORDINATE_EXTENSION_URL ||
        extension.url == GPS_COORDINATE_EXTENSION_URL
    }
  }
