package com.example.sdckmpdemo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import android_fhir.sdc_kmp_demo.generated.resources.Res
import android_fhir.sdc_kmp_demo.generated.resources.back
import android_fhir.sdc_kmp_demo.generated.resources.label_address
import android_fhir.sdc_kmp_demo.generated.resources.label_date_of_birth
import android_fhir.sdc_kmp_demo.generated.resources.label_gender
import android_fhir.sdc_kmp_demo.generated.resources.label_marital_status
import android_fhir.sdc_kmp_demo.generated.resources.label_telecom
import org.jetbrains.compose.resources.stringResource

@Composable
fun PatientDetails(
    viewModel: PatientViewModel,
    id: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val patients by viewModel.patients.collectAsState()
  val patient = patients.first() { it.id == id }

  Scaffold(
    topBar = {
      @OptIn(ExperimentalMaterial3Api::class)
      TopAppBar(
        title = { Text(patient.name.humanNames) },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.back))
          }
        },
      )
    }
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      ) {
        Column(Modifier.padding(16.dp)) {
          LabeledInfo(stringResource(Res.string.label_gender), patient.gender?.value.toString())
          LabeledInfo(
            stringResource(Res.string.label_date_of_birth),
            patient.birthDate?.value.toString(),
          )
          LabeledInfo(
            stringResource(Res.string.label_marital_status),
            patient.maritalStatus?.text?.value ?: "",
          )
          LabeledInfo(
            stringResource(Res.string.label_telecom),
            patient.telecom.firstOrNull()?.value?.value ?: "",
          )
          LabeledInfoMultiLine(stringResource(Res.string.label_address), patient.address.addresses)
        }
      }
    }
  }
}

@Composable
private fun LabeledInfo(label: String, data: String, modifier: Modifier = Modifier) {
  Column(modifier.padding(vertical = 4.dp)) {
    Text(
      buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("$label: ") }
        append(data)
      }
    )
  }
}

@Composable
private fun LabeledInfoMultiLine(label: String, data: String, modifier: Modifier = Modifier) {
  Column(modifier.padding(vertical = 4.dp)) {
    Text(
      buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("$label: ") }
      }
    )
    Text(data)
  }
}
