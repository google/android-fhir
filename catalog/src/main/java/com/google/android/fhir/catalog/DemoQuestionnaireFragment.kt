/*
 * Copyright 2023 Google LLC
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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
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
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class DemoQuestionnaireFragment : Fragment() {
  private val viewModel: DemoQuestionnaireViewModel by viewModels()
  private val args: DemoQuestionnaireFragmentArgs by navArgs()
  private var isErrorState = false
  private lateinit var infoCard: MaterialCardView
  private lateinit var infoCardHeader: TextView
  private lateinit var infoCardText: TextView

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
    infoCard = view.findViewById(R.id.infoCard)
    when (args.questionnaireTitleKey) {
      getString(R.string.behavior_name_skip_logic) -> {
        infoCardHeader = view.findViewById(R.id.infoCardHeader)
        infoCardHeader.text = args.questionnaireTitleKey
        infoCardText = view.findViewById(R.id.infoCardText)
        infoCardText.text = getString(R.string.behavior_name_skip_logic_info)
        infoCard.visibility = View.VISIBLE
      }
      getString(R.string.behavior_name_calculated_expression) -> {
        infoCardHeader = view.findViewById(R.id.infoCardHeader)
        infoCardHeader.text = args.questionnaireTitleKey
        infoCardText = view.findViewById(R.id.infoCardText)
        infoCardText.text = getString(R.string.behavior_name_calculated_expression_info)
        infoCard.visibility = View.VISIBLE
      }
      else -> infoCard.visibility = View.GONE
    }
    setFragmentResultListener(REQUEST_ERROR_KEY) { _, bundle ->
      isErrorState = bundle.getBoolean(BUNDLE_ERROR_KEY)
      replaceQuestionnaireFragmentWithQuestionnaireJson()
    }
    childFragmentManager.setFragmentResultListener(SUBMIT_REQUEST_KEY, viewLifecycleOwner) { _, _ ->
      onSubmitQuestionnaireClick()
    }
    if (savedInstanceState == null) {
      addQuestionnaireFragment()
    }
    (activity as? MainActivity)?.showOpenQuestionnaireMenu(false)
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
      com.google.android.fhir.datacapture.R.id.submit_questionnaire -> {
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
    getMenu()?.let { inflater.inflate(it, menu) }
  }

  private fun setUpActionBar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
    }
    (requireActivity() as MainActivity).setActionBar(args.questionnaireTitleKey, Gravity.TOP)
    setHasOptionsMenu(true)
  }

  private fun addQuestionnaireFragment() {
    viewLifecycleOwner.lifecycleScope.launch {
      if (childFragmentManager.findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) == null) {
        childFragmentManager.commit {
          setReorderingAllowed(true)
          val questionnaireFragment =
            QuestionnaireFragment.builder()
              .apply { setQuestionnaire(args.questionnaireJsonStringKey!!) }
              .build()
          add(R.id.container, questionnaireFragment, QUESTIONNAIRE_FRAGMENT_TAG)
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
    if (args.questionnaireWithValidationJsonStringKey.isNullOrEmpty()) {
      return
    }
    viewLifecycleOwner.lifecycleScope.launch {
      val questionnaireJsonString =
        if (isErrorState) {
          args.questionnaireWithValidationJsonStringKey!!
        } else {
          args.questionnaireJsonStringKey!!
        }
      childFragmentManager.commit {
        setReorderingAllowed(true)
        replace(
          R.id.container,
          QuestionnaireFragment.builder().setQuestionnaire(questionnaireJsonString).build(),
          QUESTIONNAIRE_FRAGMENT_TAG
        )
      }
    }
  }

  private fun getThemeId(): Int {
    return when (args.workflow) {
      WorkflowType.DEFAULT -> R.style.Theme_Androidfhir_DefaultLayout
      WorkflowType.COMPONENT,
      WorkflowType.BEHAVIOR -> R.style.Theme_Androidfhir_Component
      WorkflowType.PAGINATED -> R.style.Theme_Androidfhir_PaginatedLayout
    }
  }

  private fun getMenu(): Int? {
    return when (args.workflow) {
      WorkflowType.COMPONENT -> R.menu.component_menu
      else -> null
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
  }
}

enum class WorkflowType {
  COMPONENT,
  DEFAULT,
  PAGINATED,
  BEHAVIOR
}
