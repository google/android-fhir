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

package com.google.android.fhir.engine.benchmarks.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.engine.benchmarks.app.data.ResourcesDataProvider
import com.google.android.fhir.engine.benchmarks.app.ui.CrudDetail
import com.google.android.fhir.engine.benchmarks.app.ui.Home
import com.google.android.fhir.engine.benchmarks.app.ui.Screen
import com.google.android.fhir.engine.benchmarks.app.ui.SearchApiDetail
import com.google.android.fhir.engine.benchmarks.app.ui.SyncApiDetail
import com.google.android.fhir.engine.benchmarks.app.ui.theme.AndroidfhirTheme
import kotlin.getValue

class MainActivity : ComponentActivity() {

  @Suppress("unused") private val viewModel: MainViewModel by viewModels()

  private val resourcesDataProvider by lazy {
    val assetManager = this@MainActivity.assets
    val fhirR4JsonParser = FhirContext.forR4Cached().newJsonParser()
    ResourcesDataProvider(assetManager, fhirR4JsonParser)
  }

  private val fhirEngine by lazy { MainApplication.fhirEngine(this@MainActivity) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AndroidfhirTheme { AndroidfhirApp(application, resourcesDataProvider, fhirEngine) }
    }
  }
}

@Composable
fun AndroidfhirApp(
  application: Application,
  resourcesDataProvider: ResourcesDataProvider,
  fhirEngine: FhirEngine,
) {
  val navController = rememberNavController()
  NavHost(navController, startDestination = Screen.HomeScreen) {
    composable<Screen.HomeScreen> {
      Home(
        { navController.navigate(Screen.CRUDDetailScreen) },
        { navController.navigate(Screen.SearchDetailScreen) },
        {
          navController.navigate(
            Screen.SyncDetailScreen,
          )
        },
      )
    }

    composable<Screen.CRUDDetailScreen> {
      val viewModel: CrudApiViewModel =
        viewModel(
          it,
          factory =
            object : ViewModelProvider.Factory {
              override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CrudApiViewModel(resourcesDataProvider, fhirEngine) as T
              }
            },
        )
      CrudDetail(viewModel) { navController.popBackStack() }
    }

    composable<Screen.SearchDetailScreen> {
      val viewModel: SearchApiViewModel =
        viewModel(
          it,
          factory =
            object : ViewModelProvider.Factory {
              override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SearchApiViewModel(resourcesDataProvider, fhirEngine) as T
              }
            },
        )
      SearchApiDetail(viewModel) { navController.popBackStack() }
    }

    composable<Screen.SyncDetailScreen> {
      val viewModel: SyncApiViewModel =
        viewModel(
          it,
          factory =
            object : ViewModelProvider.Factory {
              override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SyncApiViewModel(application, resourcesDataProvider, fhirEngine) as T
              }
            },
        )
      SyncApiDetail(viewModel) { navController.popBackStack() }
    }
  }
}
