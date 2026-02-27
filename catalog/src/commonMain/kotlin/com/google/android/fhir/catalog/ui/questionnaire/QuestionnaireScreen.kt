/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir.catalog.ui.questionnaire

import android_fhir.catalog.generated.resources.Res
import android_fhir.catalog.generated.resources.behavior_name_calculated_expression
import android_fhir.catalog.generated.resources.behavior_name_calculated_expression_info
import android_fhir.catalog.generated.resources.behavior_name_skip_logic
import android_fhir.catalog.generated.resources.behavior_name_skip_logic_info
import android_fhir.catalog.generated.resources.ic_info_24
import android_fhir.catalog.generated.resources.loading
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.fhir.catalog.ui.questionnaire.components.ErrorStateToggleAction
import com.google.android.fhir.catalog.ui.questionnaire.components.ValidationErrorDialog
import com.google.android.fhir.datacapture.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
  viewModel: QuestionnaireViewModel,
  title: String,
  fileName: String,
  validationFileName: String? = null,
  showReviewPage: Boolean = false,
  showReviewPageFirst: Boolean = false,
  isReadOnly: Boolean = false,
  onBackClick: () -> Unit,
  navigateToResponse: (String) -> Unit,
) {
  var isErrorState by remember { mutableStateOf(false) }
  var questionnaireJson by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()

  val skipLogicTitle = stringResource(Res.string.behavior_name_skip_logic)
  val calculatedExpressionTitle = stringResource(Res.string.behavior_name_calculated_expression)

  val launchContextMap = remember {
    mapOf("patient" to "{\"resourceType\":\"Patient\",\"id\":\"P1\"}")
  }

  LaunchedEffect(fileName, validationFileName, isErrorState) {
    val fileToLoad =
      if (isErrorState && validationFileName != null) validationFileName else fileName
    if (fileToLoad.isNotEmpty()) {
      questionnaireJson = viewModel.getQuestionnaire(fileToLoad)
    }
  }

  Scaffold(
    topBar = {
      Column {
        TopAppBar(
          title = { Text(title) },
          navigationIcon = {
            IconButton(onClick = onBackClick) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          },
          actions = {
            if (validationFileName != null) {
              ErrorStateToggleAction(
                isErrorState = isErrorState,
                onToggle = { isErrorState = it },
              )
            }
          },
        )
      }
    },
  ) { paddingValues ->
    Surface(
      modifier = Modifier.fillMaxSize().padding(paddingValues),
      color = Color(0xFFF5F5F5),
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        if (title == skipLogicTitle || title == calculatedExpressionTitle) {
          InfoCard(
            title = title,
            info =
              if (title == skipLogicTitle) {
                stringResource(Res.string.behavior_name_skip_logic_info)
              } else {
                stringResource(Res.string.behavior_name_calculated_expression_info)
              },
          )
        }

        Box(modifier = Modifier.weight(1f)) {
          var showValidationDialog by remember { mutableStateOf(false) }
          var invalidFields by remember { mutableStateOf<List<String>>(emptyList()) }
          var pendingResponse by remember { mutableStateOf<QuestionnaireResponse?>(null) }

          if (showValidationDialog) {
            ValidationErrorDialog(
              invalidFields = invalidFields,
              onDismiss = { showValidationDialog = false },
              onFixQuestions = { showValidationDialog = false },
              onSubmitAnyway = {
                showValidationDialog = false
                pendingResponse?.let {
                  val responseJson = viewModel.getQuestionnaireResponseJson(it)
                  navigateToResponse(responseJson)
                }
              },
            )
          }

          questionnaireJson?.let { json ->
            Questionnaire(
              questionnaireJson = json,
              questionnaireLaunchContextMap = launchContextMap,
              showSubmitButton = true,
              showReviewPage = showReviewPage,
              showReviewPageFirst = showReviewPageFirst,
              isReadOnly = isReadOnly,
              showCancelButton = false,
              showRequiredText = isErrorState,
              showOptionalText = isErrorState,
              onSubmit = { getResponse ->
                scope.launch {
                  val response = getResponse()
                  val errors = viewModel.validateAndGetErrors(json, response)

                  if (errors.isNotEmpty()) {
                    invalidFields = errors
                    pendingResponse = response
                    showValidationDialog = true
                  } else {
                    val responseJson = viewModel.getQuestionnaireResponseJson(response)
                    navigateToResponse(responseJson)
                  }
                }
              },
              onCancel = {},
            )
          }
            ?: run { Text(stringResource(Res.string.loading), modifier = Modifier.padding(16.dp)) }
        }
      }
    }
  }
}

@Composable
fun InfoCard(title: String, info: String) {
  Card(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
      ),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          painter = painterResource(Res.drawable.ic_info_24),
          contentDescription = null,
          modifier = Modifier.size(20.dp),
          tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
      }
      Text(
        text = info,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 8.dp),
        color = MaterialTheme.colorScheme.onSecondaryContainer,
      )
    }
  }
}
