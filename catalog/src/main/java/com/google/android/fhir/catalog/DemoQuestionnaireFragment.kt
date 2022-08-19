/*
 * Copyright 2022 Google LLC
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

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.fhir.catalog.ModalBottomSheetFragment.Companion.BUNDLE_ERROR_KEY
import com.google.android.fhir.catalog.ModalBottomSheetFragment.Companion.REQUEST_ERROR_KEY
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.SUBMIT_REQUEST_KEY
import kotlinx.coroutines.launch

class DemoQuestionnaireFragment : Fragment() {
  private val viewModel: DemoQuestionnaireViewModel by viewModels()
  private val args: DemoQuestionnaireFragmentArgs by navArgs()
  private var isErrorState = false

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    requireContext().setTheme(getThemeId())
    return inflater.inflate(R.layout.fragment_demo_questionnaire, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setFragmentResultListener(REQUEST_ERROR_KEY) { _, bundle ->
      isErrorState = bundle.getBoolean(BUNDLE_ERROR_KEY)
      replaceQuestionnaireFragmentWithQuestionnaireJson()
    }
    childFragmentManager.setFragmentResultListener(SUBMIT_REQUEST_KEY, viewLifecycleOwner) { _, _ ->
      onSubmitQuestionnaireClick()
    }
    updateArguments()
    if (savedInstanceState == null) {
      addQuestionnaireFragment()
    }
  }

  override fun onResume() {
    super.onResume()
    (requireActivity() as MainActivity).showBottomNavigationView(View.GONE)
    setUpActionBar()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      // TODO https://github.com/google/android-fhir/issues/1088
      R.id.submit_questionnaire -> {
        onSubmitQuestionnaireClick()
        true
      }
      R.id.error_menu -> {
        launchModalBottomSheetFragment()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(getMenu(), menu)
  }

  private fun setUpActionBar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
    }
    (requireActivity() as MainActivity).setActionBar(args.questionnaireTitleKey, Gravity.TOP)
    setHasOptionsMenu(true)
  }

  private fun updateArguments() {
    requireArguments().putString(QUESTIONNAIRE_FILE_PATH_KEY, args.questionnaireFilePathKey)
    requireArguments()
      .putString(
        QUESTIONNAIRE_FILE_WITH_VALIDATION_PATH_KEY,
        args.questionnaireFileWithValidationPathKey
      )
  }

  private fun addQuestionnaireFragment() {
    viewLifecycleOwner.lifecycleScope.launch {
      if (childFragmentManager.findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) == null) {
        childFragmentManager.commit {
          setReorderingAllowed(true)
          add<QuestionnaireFragment>(
            R.id.container,
            tag = QUESTIONNAIRE_FRAGMENT_TAG,
            args =
              bundleOf(
                QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to
                  viewModel.getQuestionnaireJson()
              )
          )
        }
      }
    }
  }

  /**
   * Replaces existing [QuestionnaireFragment] with questionnaire json as per [isErrorState] value.
   * If isErrorState is true then existing fragment get replaced with questionnaire json which shows
   * error.
   */
  private fun replaceQuestionnaireFragmentWithQuestionnaireJson() {
    // TODO: remove check once all files are added
    if (args.questionnaireFileWithValidationPathKey.isNullOrEmpty()) {
      return
    }
    viewLifecycleOwner.lifecycleScope.launch {
      val questionnaireJsonString =
        if (isErrorState) {
          viewModel.getQuestionnaireWithValidationJson()
        } else {
          viewModel.getQuestionnaireJson()
        }
      childFragmentManager.commit {
        setReorderingAllowed(true)
        replace<QuestionnaireFragment>(
          R.id.container,
          tag = QUESTIONNAIRE_FRAGMENT_TAG,
          args =
            bundleOf(
              QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJsonString
            )
        )
      }
    }
  }

  private fun getThemeId(): Int {
    return when (args.workflow) {
      WorkflowType.DEFAULT -> R.style.Theme_Androidfhir_DefaultLayout
      WorkflowType.COMPONENT -> R.style.Theme_Androidfhir_Component
      WorkflowType.PAGINATED -> R.style.Theme_Androidfhir_PaginatedLayout
    }
  }

  private fun getMenu(): Int {
    return when (args.workflow) {
      WorkflowType.DEFAULT, WorkflowType.PAGINATED -> R.menu.layout_menu
      WorkflowType.COMPONENT -> R.menu.component_menu
    }
  }

  private fun onSubmitQuestionnaireClick() {
    val questionnaireFragment =
      childFragmentManager.findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) as QuestionnaireFragment
    launchQuestionnaireResponseFragment(
      viewModel.getQuestionnaireResponseJson(questionnaireFragment.getQuestionnaireResponse())
    )
  }

  private fun launchQuestionnaireResponseFragment(response: String) {
    findNavController()
      .navigate(
        DemoQuestionnaireFragmentDirections
          .actionGalleryQuestionnaireFragmentToQuestionnaireResponseFragment(response)
      )
  }

  private fun launchModalBottomSheetFragment() {
    findNavController()
      .navigate(
        DemoQuestionnaireFragmentDirections.actionGalleryQuestionnaireFragmentToModalBottomSheet(
          isErrorState
        )
      )
  }

  companion object {
    const val QUESTIONNAIRE_FRAGMENT_TAG = "questionnaire-fragment-tag"
    const val QUESTIONNAIRE_FILE_PATH_KEY = "questionnaire-file-path-key"
    const val QUESTIONNAIRE_FILE_WITH_VALIDATION_PATH_KEY =
      "questionnaire-file-with-validation-path-key"
  }
}

enum class WorkflowType {
  COMPONENT,
  DEFAULT,
  PAGINATED
}
