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

package com.google.android.fhir.engine.benchmarks.app.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.fhir.engine.benchmarks.app.R
import com.google.android.fhir.engine.benchmarks.app.ui.theme.AndroidfhirTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
  navigateToCrudScreen: () -> Unit,
  navigateToSearchScreen: () -> Unit,
  navigateToSyncScreen: () -> Unit,
) {
  Scaffold(
    topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    modifier = Modifier.fillMaxSize().semantics { testTagsAsResourceId = true },
  ) { innerPadding ->
    Box(
      Modifier.padding(innerPadding).padding(horizontal = 16.dp),
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionCard(
          title = "CRUD",
          imageId = R.drawable.ic_outline_database_24,
          modifier = Modifier.testTag("crudBenchmarkSection"),
          onActionClick = navigateToCrudScreen,
        )

        ActionCard(
          title = "Search API",
          imageId = R.drawable.ic_home_search,
          modifier = Modifier.testTag("searchBenchmarkSection"),
          onActionClick = navigateToSearchScreen,
        )

        ActionCard(
          title = "Sync API",
          imageId = R.drawable.ic_home_sync,
          modifier = Modifier.testTag("syncBenchmarkSection"),
          onActionClick = navigateToSyncScreen,
        )
      }
    }
  }
}

@Composable
fun ActionCard(
  title: String,
  @DrawableRes imageId: Int,
  modifier: Modifier = Modifier,
  onActionClick: () -> Unit,
) {
  Card(
    onClick = onActionClick,
    shape = RoundedCornerShape(10.dp),
    colors =
      CardDefaults.cardColors(
        containerColor = Color.White,
      ),
    modifier = modifier.fillMaxWidth(),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(30.dp)) {
      Image(
        painterResource(imageId),
        contentDescription = title,
        modifier = Modifier.size(36.dp, 36.dp),
      )
      Spacer(Modifier.width(8.dp))
      Text(
        text = title,
        maxLines = 1,
        style = MaterialTheme.typography.titleLarge,
        fontSize = 20.sp,
      )
    }
  }
}

@Composable
@Preview
fun HomePreview() {
  Home({}, {}, {})
}

@Preview(showBackground = true)
@Composable
fun ActionCardPreview() {
  AndroidfhirTheme { ActionCard("Android", R.drawable.ic_outline_database_24) {} }
}
