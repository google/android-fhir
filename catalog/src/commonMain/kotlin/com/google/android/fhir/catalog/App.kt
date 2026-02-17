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

package com.google.android.fhir.catalog

import android_fhir.catalog.generated.resources.Res
import android_fhir.catalog.generated.resources.ic_behaviors
import android_fhir.catalog.generated.resources.ic_components
import android_fhir.catalog.generated.resources.ic_layouts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.google.android.fhir.catalog.ui.behaviors.BehaviorListScreen
import com.google.android.fhir.catalog.ui.behaviors.BehaviorListViewModel
import com.google.android.fhir.catalog.ui.components.ComponentListScreen
import com.google.android.fhir.catalog.ui.components.ComponentListViewModel
import com.google.android.fhir.catalog.ui.layouts.LayoutListScreen
import com.google.android.fhir.catalog.ui.layouts.LayoutListViewModel
import com.google.android.fhir.catalog.ui.questionnaire.QuestionnaireResponseScreen
import com.google.android.fhir.catalog.ui.questionnaire.QuestionnaireScreen
import com.google.android.fhir.catalog.ui.questionnaire.QuestionnaireViewModel
import com.google.android.fhir.catalog.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource

sealed class Screen(val route: String, val label: String, val icon: @Composable () -> Unit) {
  object Components :
    Screen(
      "component_list",
      "Components",
      { Icon(painterResource(Res.drawable.ic_components), contentDescription = null) },
    )

  object Layouts :
    Screen(
      "layout_list",
      "Layouts",
      { Icon(painterResource(Res.drawable.ic_layouts), contentDescription = null) },
    )

  object Behaviors :
    Screen(
      "behavior_list",
      "Behaviors",
      { Icon(painterResource(Res.drawable.ic_behaviors), contentDescription = null) },
    )
}

@Composable
fun App() {
  AppTheme {
    Surface {
      val navController: NavHostController = rememberNavController()
      val componentViewModel: ComponentListViewModel = viewModel { ComponentListViewModel() }
      val layoutViewModel: LayoutListViewModel = viewModel { LayoutListViewModel() }
      val behaviorViewModel: BehaviorListViewModel = viewModel { BehaviorListViewModel() }

      var submittedResponseJson by remember { mutableStateOf<String?>(null) }

      val navBackStackEntry by navController.currentBackStackEntryAsState()
      val currentDestination = navBackStackEntry?.destination

      val items = listOf(Screen.Components, Screen.Layouts, Screen.Behaviors)

      Scaffold(
        bottomBar = {
          if (items.any { it.route == currentDestination?.route }) {
            NavigationBar {
              items.forEach { screen ->
                NavigationBarItem(
                  icon = screen.icon,
                  label = { Text(screen.label) },
                  selected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                  onClick = {
                    navController.navigate(screen.route) {
                      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                      launchSingleTop = true
                      restoreState = true
                    }
                  },
                )
              }
            }
          }
        },
      ) { innerPadding ->
        NavHost(
          navController = navController,
          startDestination = Screen.Components.route,
          modifier = Modifier.padding(innerPadding),
        ) {
          composable(Screen.Components.route) {
            ComponentListScreen(
              viewModel = componentViewModel,
              onComponentClick = { component, title ->
                val validationPart =
                  component.questionnaireFileWithValidation?.let { "?validationFile=$it" } ?: ""
                navController.navigate(
                  "questionnaire/${component.questionnaireFile}/$title/false/false/false$validationPart",
                )
              },
            )
          }
          composable(Screen.Layouts.route) {
            LayoutListScreen(
              viewModel = layoutViewModel,
              onLayoutClick = { layout, title ->
                navController.navigate(
                  "questionnaire/${layout.questionnaireFileName}/$title/${layout.showReviewPage}/${layout.showReviewPageFirst}/${layout.isReadOnly}",
                )
              },
            )
          }
          composable(Screen.Behaviors.route) {
            BehaviorListScreen(
              viewModel = behaviorViewModel,
              onBehaviorClick = { behavior, title ->
                navController.navigate(
                  "questionnaire/${behavior.questionnaireFileName}/$title/false/false/false",
                )
              },
            )
          }
          composable(
            route =
              "questionnaire/{fileName}/{title}/{review}/{reviewFirst}/{readOnly}?validationFile={validationFile}",
            arguments =
              listOf(
                navArgument("fileName") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("review") { type = NavType.BoolType },
                navArgument("reviewFirst") { type = NavType.BoolType },
                navArgument("readOnly") { type = NavType.BoolType },
                navArgument("validationFile") {
                  type = NavType.StringType
                  nullable = true
                },
              ),
          ) { backStackEntry ->
            val arguments = backStackEntry.arguments
            val fileName =
              arguments?.read { if (contains("fileName")) getString("fileName") else null } ?: ""
            val title =
              arguments?.read { if (contains("title")) getString("title") else null } ?: ""
            val review =
              arguments?.read { if (contains("review")) getBoolean("review") else null } ?: false
            val reviewFirst =
              arguments?.read { if (contains("reviewFirst")) getBoolean("reviewFirst") else null }
                ?: false
            val readOnly =
              arguments?.read { if (contains("readOnly")) getBoolean("readOnly") else null }
                ?: false
            val validationFile =
              arguments?.read {
                if (contains("validationFile")) getString("validationFile") else null
              }

            QuestionnaireScreen(
              viewModel = viewModel { QuestionnaireViewModel() },
              title = title,
              fileName = fileName,
              validationFileName = validationFile,
              showReviewPage = review,
              showReviewPageFirst = reviewFirst,
              isReadOnly = readOnly,
              onBackClick = { navController.popBackStack() },
              navigateToResponse = { responseJson ->
                submittedResponseJson = responseJson
                navController.navigate("questionnaire_response/$title")
              },
            )
          }
          composable(
            route = "questionnaire_response/{title}",
            arguments = listOf(navArgument("title") { type = NavType.StringType }),
          ) { backStackEntry ->
            val arguments = backStackEntry.arguments
            val title =
              arguments?.read { if (contains("title")) getString("title") else null } ?: ""
            QuestionnaireResponseScreen(
              responseJson = submittedResponseJson ?: "",
              onBackClick = { navController.popBackStack() },
            )
          }
        }
      }
    }
  }
}
