package com.example.sdckmpdemo

import android_fhir.sdc_kmp_demo.generated.resources.Res
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.fhir.datacapture.QuestionnaireUI
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var questionnaireJson by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()

  // Load questionnaire JSON from resources
  LaunchedEffect(Unit) {
    questionnaireJson = Res.readBytes("files/sample-questionnaire.json").decodeToString()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Fill Questionnaire") },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        }
      )
    }
  ) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      questionnaireJson?.let { json ->
        QuestionnaireUI(
          questionnaireJson = json,
          showSubmitButton = true,
          showCancelButton = true,
          onSubmit = { response ->
            // Log the response and navigate back
            println("Questionnaire submitted: $response")
            scope.launch {
              onBackClick()
            }
          },
          onCancel = {
            // Navigate back on cancel
            onBackClick()
          }
        )
      } ?: run {
        Text("Loading questionnaire...")
      }
    }
  }
}
