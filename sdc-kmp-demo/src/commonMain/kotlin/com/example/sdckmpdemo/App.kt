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

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.sdckmpdemo.ui.theme.AppTheme
import com.google.fhir.model.r4.Address
import com.google.fhir.model.r4.HumanName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable object PatientListDestination

@Serializable data class PatientDetailDestination(val id: String)

@Serializable object QuestionnaireDestination

@Serializable data class QuestionnaireResponseDestination(val responseJson: String)

@Composable
@Preview
fun App() {
  AppTheme {
    Surface {
      val navController: NavHostController = rememberNavController()
      val viewModel = remember { PatientViewModel() }
      NavHost(navController = navController, startDestination = PatientListDestination) {
        composable<PatientListDestination> {
          PatientList(
            viewModel = viewModel,
            navigateToDetails = { patient ->
              navController.navigate(PatientDetailDestination(patient.id!!))
            },
          )
        }
        composable<PatientDetailDestination> { backStackEntry ->
          PatientDetails(
            viewModel = viewModel,
            id = backStackEntry.toRoute<PatientDetailDestination>().id,
            onBackClick = { navController.popBackStack() },
            navigateToQuestionnaire = { navController.navigate(QuestionnaireDestination) },
          )
        }
        composable<QuestionnaireDestination> {
          QuestionnaireScreen(
            onBackClick = { navController.popBackStack() },
            navigateToResponse = { responseJson ->
              navController.navigate(QuestionnaireResponseDestination(responseJson))
            },
          )
        }
        composable<QuestionnaireResponseDestination> { backStackEntry ->
          QuestionnaireResponseScreen(
            responseJson = backStackEntry.toRoute<QuestionnaireResponseDestination>().responseJson,
            onBackClick = { navController.popBackStack() },
          )
        }
      }
    }
  }
}

val HumanName?.displayInApp: String
  get() = this?.given?.plus(family)?.joinToString(separator = " ") { it?.value.toString() } ?: ""

val List<HumanName?>?.humanNames: String
  get() = this?.joinToString(separator = ", ") { it.displayInApp } ?: ""

val Address?.displayInApp: String
  get() =
    this?.line
      ?.asSequence()
      ?.plus(city)
      ?.plus(state)
      ?.plus(postalCode)
      ?.plus(country)
      ?.map { it?.value }
      ?.joinToString(separator = "\n")
      ?: " "

val List<Address?>?.addresses: String
  get() = this?.joinToString(separator = ", ") { it.displayInApp } ?: ""
