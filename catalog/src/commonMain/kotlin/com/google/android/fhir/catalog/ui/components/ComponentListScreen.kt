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

package com.google.android.fhir.catalog.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.fhir.catalog.ui.shared.CatalogItemCard
import com.google.android.fhir.catalog.ui.shared.CatalogTopAppBar
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentListScreen(
  viewModel: ComponentListViewModel,
  onComponentClick: (ComponentListViewModel.Component, String) -> Unit,
) {
  Scaffold(
    topBar = { CatalogTopAppBar() },
  ) { padding ->
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier.fillMaxSize().padding(padding),
      contentPadding = PaddingValues(8.dp),
    ) {
      viewModel.viewItemList.forEach { item ->
        when (item) {
          is ComponentListViewModel.ViewItem.HeaderItem -> {
            item(span = { GridItemSpan(2) }) {
              Text(
                text = stringResource(item.header.title),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
              )
            }
          }
          is ComponentListViewModel.ViewItem.ComponentItem -> {
            item {
              val title = stringResource(item.component.text)
              CatalogItemCard(
                icon = item.component.icon,
                text = title,
                onClick = { onComponentClick(item.component, title) },
              )
            }
          }
        }
      }
    }
  }
}
