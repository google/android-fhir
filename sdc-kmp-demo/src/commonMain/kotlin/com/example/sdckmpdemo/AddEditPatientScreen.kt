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

package com.example.sdckmpdemo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.fhir.model.r4.ContactPoint
import com.google.fhir.model.r4.Date
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.HumanName
import com.google.fhir.model.r4.Patient
import com.google.fhir.model.r4.terminologies.AdministrativeGender
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPatientScreen(
  viewModel: PatientViewModel,
  patientId: String?,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val patients by viewModel.patients.collectAsState()
  val existingPatient = patientId?.let { id -> patients.firstOrNull { it.id == id } }
  val isEdit = existingPatient != null

  var givenName by remember { mutableStateOf("") }
  var familyName by remember { mutableStateOf("") }
  var gender by remember { mutableStateOf("male") }
  var birthDate by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }

  LaunchedEffect(existingPatient) {
    existingPatient?.let { p ->
      givenName = p.name.firstOrNull()?.given?.firstOrNull()?.value ?: ""
      familyName = p.name.firstOrNull()?.family?.value ?: ""
      gender = p.gender?.value?.name?.lowercase() ?: "male"
      birthDate = p.birthDate?.value?.toString() ?: ""
      phone = p.telecom.firstOrNull()?.value?.value ?: ""
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(if (isEdit) "Edit Patient" else "Add Patient") },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { paddingValues ->
    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 16.dp)
          .verticalScroll(rememberScrollState()),
    ) {
      OutlinedTextField(
        value = givenName,
        onValueChange = { givenName = it },
        label = { Text("Given Name") },
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = familyName,
        onValueChange = { familyName = it },
        label = { Text("Family Name") },
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(modifier = Modifier.height(8.dp))

      var expanded by remember { mutableStateOf(false) }
      val genderOptions = listOf("male", "female", "other", "unknown")
      ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
      ) {
        OutlinedTextField(
          value = gender,
          onValueChange = {},
          readOnly = true,
          label = { Text("Gender") },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
          modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
          genderOptions.forEach { option ->
            DropdownMenuItem(
              text = { Text(option.replaceFirstChar { it.uppercase() }) },
              onClick = {
                gender = option
                expanded = false
              },
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = birthDate,
        onValueChange = { birthDate = it },
        label = { Text("Birth Date (YYYY-MM-DD)") },
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = phone,
        onValueChange = { phone = it },
        label = { Text("Phone") },
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          val patient = buildPatient(existingPatient, givenName, familyName, gender, birthDate, phone)
          if (isEdit) {
            viewModel.updatePatient(patient)
          } else {
            viewModel.createPatient(patient)
          }
          onBackClick()
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = givenName.isNotBlank() || familyName.isNotBlank(),
      ) {
        Text(if (isEdit) "Save Changes" else "Create Patient")
      }
    }
  }
}

@OptIn(ExperimentalUuidApi::class)
private fun buildPatient(
  existing: Patient?,
  givenName: String,
  familyName: String,
  gender: String,
  birthDate: String,
  phone: String,
): Patient {
  val id = existing?.id ?: Uuid.random().toString()
  val genderEnum =
    when (gender) {
      "male" -> AdministrativeGender.Male
      "female" -> AdministrativeGender.Female
      "other" -> AdministrativeGender.Other
      else -> AdministrativeGender.Unknown
    }
  return Patient(
    id = id,
    name =
      listOf(
        HumanName(
          given = listOf(com.google.fhir.model.r4.String(value = givenName)),
          family = com.google.fhir.model.r4.String(value = familyName),
        ),
      ),
    gender = Enumeration(value = genderEnum),
    birthDate =
      birthDate.takeIf { it.isNotBlank() }?.let {
        Date(value = FhirDate.fromString(it))
      },
    telecom =
      phone.takeIf { it.isNotBlank() }?.let {
        listOf(
          ContactPoint(
            system = Enumeration(value = ContactPoint.ContactPointSystem.Phone),
            value = com.google.fhir.model.r4.String(value = it),
          ),
        )
      }
        ?: emptyList(),
  )
}
