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

package com.google.android.fhir.catalog.ui.behaviors

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.fhir.catalog.ui.shared.CatalogItemCard
import com.google.android.fhir.catalog.ui.shared.CatalogTopAppBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun BehaviorListScreen(
  viewModel: BehaviorListViewModel,
  onBehaviorClick: (BehaviorListViewModel.Behavior, String) -> Unit,
) {
  Scaffold(
    topBar = { CatalogTopAppBar() },
  ) { padding ->
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier.fillMaxSize().padding(padding),
      contentPadding = PaddingValues(8.dp),
    ) {
      items(viewModel.getBehaviorList()) { behavior ->
        val title = stringResource(behavior.text)
        CatalogItemCard(
          icon = behavior.icon,
          text = title,
          onClick = { onBehaviorClick(behavior, title) },
        )
      }
    }
  }
}
