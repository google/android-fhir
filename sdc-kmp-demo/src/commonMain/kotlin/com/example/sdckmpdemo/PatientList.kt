/*
 * Copyright 2025 Google LLC
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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.fhir.model.r4.Patient

@Composable
fun PatientList(
  viewModel: PatientViewModel,
  navigateToDetails: (patient: Patient) -> Unit,
  modifier: Modifier = Modifier,
) {
  val patients by viewModel.patients.collectAsState()

  Scaffold(
    topBar = {
      @OptIn(ExperimentalMaterial3Api::class) TopAppBar(title = { Text("Kotlin FHIR Demo") })
    },
  ) { paddingValues ->
    LazyColumn(
      modifier = modifier.fillMaxSize().padding(paddingValues),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
      items(patients) { obj -> PatientListItem(obj = obj, onclick = { navigateToDetails(obj) }) }
    }
  }
}

@Composable
private fun PatientListItem(obj: Patient, onclick: () -> Unit = {}, modifier: Modifier = Modifier) {
  Card(
    onClick = onclick,
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Patient Icon",
        modifier =
          Modifier.size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
      )

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier.fillMaxWidth()) {
        Text(
          text = obj.name.humanNames,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          obj.birthDate?.value.toString(),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = obj.gender?.value.toString(),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}
