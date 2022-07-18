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

package com.google.android.fhir.datacapture

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.datacapture.enablement.EnablementEvaluator
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseItemValidator
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator.checkQuestionnaireResponse
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IBaseDatatype
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.ValueSet
import org.hl7.fhir.r4.utils.FHIRPathEngine
import timber.log.Timber

internal class QuestionnaireViewModel(application: Application, state: SavedStateHandle) :
  AndroidViewModel(application) {

  private val parser: IParser by lazy { FhirContext.forCached(FhirVersionEnum.R4).newJsonParser() }

  /** The current questionnaire as questions are being answered. */
  internal val questionnaire: Questionnaire

  init {
    questionnaire =
      when {
        state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_URI) -> {
          if (state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING)) {
            Timber.w(
              "Both EXTRA_QUESTIONNAIRE_JSON_URI & EXTRA_QUESTIONNAIRE_JSON_STRING are provided. " +
                "EXTRA_QUESTIONNAIRE_JSON_URI takes precedence."
            )
          }
          val uri: Uri = state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_URI]!!
          parser.parseResource(application.contentResolver.openInputStream(uri)) as Questionnaire
        }
        state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING) -> {
          val questionnaireJson: String =
            state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING]!!
          parser.parseResource(questionnaireJson) as Questionnaire
        }
        else ->
          error(
            "Neither EXTRA_QUESTIONNAIRE_JSON_URI nor EXTRA_QUESTIONNAIRE_JSON_STRING is supplied."
          )
      }
  }

  /** The current questionnaire response as questions are being answered. */
  private val questionnaireResponse: QuestionnaireResponse

  /**
   * Contains [QuestionnaireResponse.QuestionnaireResponseItemComponent]s that have been modified by
   * the user. [QuestionnaireResponse.QuestionnaireResponseItemComponent]s that have not been
   * modified by the user will not be validated. This is to avoid spamming the user with a sea of
   * validation errors when the questionnaire is loaded initially.
   */
  private val modifiedQuestionnaireResponseItemSet =
    mutableSetOf<QuestionnaireResponse.QuestionnaireResponseItemComponent>()

  init {
    when {
      state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI) -> {
        if (state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING)) {
          Timber.w(
            "Both EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI & EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING are provided. " +
              "EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI takes precedence."
          )
        }
        val uri: Uri = state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI]!!
        questionnaireResponse =
          parser.parseResource(application.contentResolver.openInputStream(uri)) as
            QuestionnaireResponse
        checkQuestionnaireResponse(questionnaire, questionnaireResponse)
      }
      state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING) -> {
        val questionnaireResponseJson: String =
          state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING]!!
        questionnaireResponse =
          parser.parseResource(questionnaireResponseJson) as QuestionnaireResponse
        checkQuestionnaireResponse(questionnaire, questionnaireResponse)
      }
      else -> {
        questionnaireResponse =
          QuestionnaireResponse().apply {
            questionnaire = this@QuestionnaireViewModel.questionnaire.url
          }
        // Retain the hierarchy and order of items within the questionnaire as specified in the
        // standard. See https://www.hl7.org/fhir/questionnaireresponse.html#notes.
        questionnaire.item.forEach {
          questionnaireResponse.addItem(it.createQuestionnaireResponseItem())
        }
      }
    }
  }


  /** Map from link IDs to questionnaire response items. */
  private val linkIdToQuestionnaireResponseItemMap =
    createLinkIdToQuestionnaireResponseItemMap(questionnaireResponse.item)

  /** Map from linkIDs to questionnaire items path */
  private val linkIdToQuestionnaireItemPathMap =
    createLinkIdToQuestionnaireItemPathMap(questionnaire.item)

  /** Map from questionnaire items path to Variables */
  internal val pathToVariableMap = createPathToVariableMap(questionnaire.item, questionnaire)

  /** Map from link IDs to questionnaire items. */
  private val linkIdToQuestionnaireItemMap = createLinkIdToQuestionnaireItemMap(questionnaire.item)

  /** Map from variables to DependentOfs */
  private var variablesToDependentsMap: MutableMap<String, List<String>?> = mutableMapOf()

  /** Tracks modifications in order to update the UI. */
  private val modificationCount = MutableStateFlow(0)

  private val fhirPathEngine: FHIRPathEngine =
    with(FhirContext.forCached(FhirVersionEnum.R4)) {
      FHIRPathEngine(HapiWorkerContext(this, this.validationSupport)).apply {
        hostServices = FHIRPathEngineHostServices
      }
    }

  /**
   * Callback function to update the view model after the answer(s) to a question have been changed.
   * This is passed to the [QuestionnaireItemViewItem] in its constructor so that it can invoke this
   * callback function after the UI widget has updated the answer(s).
   *
   * This function updates the [QuestionnaireResponse] held in memory using the answer(s) provided
   * by the UI. Subsequently it should also trigger the recalculation of any relevant expressions,
   * enablement states, and validation results throughout the questionnaire.
   *
   * This callback function has 3 params:
   * - the reference to the [Questionnaire.QuestionnaireItemComponent] in the [Questionnaire]
   * - the reference to the [QuestionnaireResponse.QuestionnaireResponseItemComponent] in the
   * [QuestionnaireResponse]
   * - a [List] of [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] which are the
   * new answers to the question.
   */

  private val answersChangedCallback:
    (
      Questionnaire.QuestionnaireItemComponent,
      QuestionnaireResponse.QuestionnaireResponseItemComponent,
      List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>) -> Unit =
      { questionnaireItem, questionnaireResponseItem, answers ->
    questionnaireResponseItem.answer = answers.toList()
    if (questionnaireItem.hasNestedItemsWithinAnswers) {
      questionnaireResponseItem.addNestedItemsToAnswer(questionnaireItem)
    }

    modifiedQuestionnaireResponseItemSet.add(questionnaireResponseItem)
    modificationCount.update { it + 1 }
  }

  /**
   * * Function to update dependent variable based on predicate, predicate could be linkId or
   * variable name
   */
  private fun updateDependentVariables(
    predicate: String,
    isLinkId: Boolean = false,
    variablePath: String? = null
  ) {
    variablesToDependentsMap
      .filterValues { it -> !it?.find { it == predicate }.isNullOrEmpty() }
      .keys
      .filter {
        if (!isLinkId) {
          isDescendant(variablePath!!, it.substringBeforeLast("."))
        } else {
          true
        }
      }
      .forEach { key ->
        val path = key.substringBeforeLast(".")
        val variableId = key.substringAfterLast(".")
        pathToVariableMap[path]?.find { it.expression.name == variableId }.also { variable ->
          calculateVariable(
            variable?.expression!!,
            path,
            linkIdToQuestionnaireResponseItemMap[variable.questionnaireItemLinkId]
          )
          updateDependentVariables(variableId, false, path)
        }
      }
  }

  /**
   * Function to calculate Variables based on variable extensions added at root and item level in
   * the Questionnaire
   */
  private fun calculateVariables() {
    questionnaire.variableExpressions.forEach { variableExpression ->
      val path = ROOT_VARIABLES
      calculateVariable(variableExpression, path)
    }

    // Filter out questionnaire items having Variable extension and calculate Items variables
    linkIdToQuestionnaireItemMap.values
      .filter { !it.variableExpressions.isNullOrEmpty() }
      .forEach { questionnaireItem ->
        linkIdToQuestionnaireResponseItemMap[questionnaireItem.linkId]?.let {
          questionnaireResponseItem ->
          questionnaireItem.variableExpressions.forEach { variableExpression ->
            val path = linkIdToQuestionnaireItemPathMap[questionnaireResponseItem.linkId]
            path?.let { calculateVariable(variableExpression, it, questionnaireResponseItem) }
          }
        }
      }
  }
  /** Function to calculate Variable with provided expression, path and questionnaireResponseItem */
  private fun calculateVariable(
    expression: Expression,
    path: String,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent? = null
  ) {
    val evaluatedValue = evaluateVariables(fhirPathEngine, expression, questionnaireResponseItem)
    pathToVariableMap[path]?.let { updateVariable(it, expression, evaluatedValue) }
  }

  /**
   * A function to update the value of a variable If the variable with same id doesn't exists, add
   * the new variable If the variable with the same id already exists, update the value of variable
   */
  private fun updateVariable(
    variables: List<Variable>,
    expression: Expression,
    evaluatedValue: Any?
  ) {
    variables.find { it.expression.name == expression.name }.also { variable ->
      if (evaluatedValue != null) variable?.value = evaluatedValue as Type
      else variable?.value = evaluatedValue
    }
  }

  /** A function to evaluate variable expression using FHIRPathEngine */
  private fun evaluateVariables(
    fhirPathEngine: FHIRPathEngine,
    expression: Expression,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent? = null
  ) =
    try {
      require(expression.hasLanguage() && expression.language == "text/fhirpath") {
        "Unsupported expression language, language should be text/fhirpath"
      }

      fhirPathEngine
        .evaluate(
          findVariables(expression, questionnaireResponseItem),
          questionnaireResponse,
          null,
          null,
          expression.expression
        )
        .firstOrNull()
    } catch (exception: FHIRException) {
      Timber.w("Could not evaluate expression with FHIRPathEngine", exception)
      null
    }

  /**
   * A function to find the values of variables if they already exist in the respective scope. For
   * root level variables, find only root level variables. For item level variables, find at root
   * level, all the ancestors of current item and current item itself
   */
  private fun findVariables(
    expression: Expression,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent? = null
  ): Map<String, Any> {
    val map = mutableMapOf<String, Base>()

    // check root level variables
    if (pathToVariableMap.containsKey(ROOT_VARIABLES)) {
      val rootVariables = pathToVariableMap[ROOT_VARIABLES]
      rootVariables?.forEach {
        if (it.expression.name != expression.name) {
          it.value?.let { value -> map[it.expression.name] = value as Type }
        }
      }
    }

    questionnaireResponseItem?.let {
      // check variables in ancestors including item itself
      var path = linkIdToQuestionnaireItemPathMap[questionnaireResponseItem.linkId]
      for ((key, _) in pathToVariableMap) {
        if (path?.contains(key) == true) {
          val variables = pathToVariableMap[key]
          variables?.forEach {
            if (it.expression.name != expression.name) {
              it.value?.let { value -> map[it.expression.name] = value as Type }
            }
          }
        }
      }
    }

    return map
  }

  internal val pageFlow = MutableStateFlow(questionnaire.getInitialPagination())

  private val answerValueSetMap =
    mutableMapOf<String, List<Questionnaire.QuestionnaireItemAnswerOptionComponent>>()

  /**
   * Returns current [QuestionnaireResponse] captured by the UI which includes answers of enabled
   * questions.
   */
  fun getQuestionnaireResponse(): QuestionnaireResponse {
    return questionnaireResponse.copy().apply {
      item = getEnabledResponseItems(this@QuestionnaireViewModel.questionnaire.item, item)
    }
  }

  internal fun goToPreviousPage() {
    pageFlow.value = pageFlow.value!!.previousPage()
  }

  internal fun goToNextPage() {
    pageFlow.value = pageFlow.value!!.nextPage()
  }

  /** [QuestionnaireState] to be displayed in the UI. */
  internal val questionnaireStateFlow: Flow<QuestionnaireState> =
    modificationCount
      .combine(pageFlow) { _, pagination ->
        getQuestionnaireState(
          questionnaireItemList = questionnaire.item,
          questionnaireResponseItemList = questionnaireResponse.item,
          pagination = pagination,
          modificationCount = modificationCount.value
        )
      }
      .stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        initialValue =
          getQuestionnaireState(
            questionnaireItemList = questionnaire.item,
            questionnaireResponseItemList = questionnaireResponse.item,
            pagination = questionnaire.getInitialPagination(),
            modificationCount = 0
          )
      )
      .also {
        findDependents()
        calculateVariables()
      }

  /**
   * * Function to find dependents of a variable, For example: variable X having expression = "%Y +
   * %Z", so Y and Z are the variables on which the variable X would depend on
   */
  private fun findDependents() {
    for ((key, value) in pathToVariableMap) {
      value?.forEach { variable ->
        // parse the expression, find the variables and linkIds on which this variable depends on
        variablesToDependentsMap["$key.${variable.expression.name}"] =
          findDependentOf(variable.expression.expression)
      }
    }
  }

  /** Function to find DependentOfs based on linkId and variable names in the given expresion */
  private fun findDependentOf(expression: String) =
    buildList {
      val variableRegex = Regex("[%]([A-Za-z0-9\\-]{1,64})")
      val variableMatches = variableRegex.findAll(expression)

      addAll(
        variableMatches.map { it.groupValues[1] }.toList().filterNot {
          it == "resource" || it == "rootResource"
        }
      )

      val linkIdRegex = Regex("[l][i][n][k][I][d]\\s*[=]\\s*[']([\\r\\n\\t\\S]+)[']")
      val linkIdMatches = linkIdRegex.findAll(expression)

      addAll(linkIdMatches.map { it.groupValues[1] }.toList())
    }
      .takeIf { it.isNotEmpty() }

  private fun isDescendant(parentPath: String, childPath: String): Boolean {
    val pattern = "\\b${parentPath}\\b".toRegex()
    return if (parentPath == ROOT_VARIABLES) true else pattern.containsMatchIn(childPath)
  }

  @PublishedApi
  internal suspend fun resolveAnswerValueSet(
    uri: String
  ): List<Questionnaire.QuestionnaireItemAnswerOptionComponent> {
    // If cache hit, return it
    if (answerValueSetMap.contains(uri)) {
      return answerValueSetMap[uri]!!
    }

    val options =
      if (uri.startsWith("#")) {
        questionnaire.contained
          .firstOrNull { resource ->
            resource.id.equals(uri) &&
              resource.resourceType == ResourceType.ValueSet &&
              (resource as ValueSet).hasExpansion()
          }
          ?.let { resource ->
            val valueSet = resource as ValueSet
            valueSet.expansion.contains.filterNot { it.abstract || it.inactive }.map { component ->
              Questionnaire.QuestionnaireItemAnswerOptionComponent(
                Coding(component.system, component.code, component.display)
              )
            }
          }
      } else {
        // Ask the client to provide the answers from an external expanded Valueset.
        DataCapture.getConfiguration(getApplication())
          .valueSetResolverExternal
          ?.resolve(uri)
          ?.map { coding -> Questionnaire.QuestionnaireItemAnswerOptionComponent(coding.copy()) }
      }
        ?: emptyList()
    // save it so that we avoid have cache misses.
    answerValueSetMap[uri] = options
    return options
  }

  private fun createLinkIdToQuestionnaireResponseItemMap(
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  ): MutableMap<String, QuestionnaireResponse.QuestionnaireResponseItemComponent> {
    val linkIdToQuestionnaireResponseItemMap =
      questionnaireResponseItemList.map { it.linkId to it }.toMap().toMutableMap()
    for (item in questionnaireResponseItemList) {
      linkIdToQuestionnaireResponseItemMap.putAll(
        createLinkIdToQuestionnaireResponseItemMap(item.item)
      )
      item.answer.forEach {
        linkIdToQuestionnaireResponseItemMap.putAll(
          createLinkIdToQuestionnaireResponseItemMap(it.item)
        )
      }
    }
    return linkIdToQuestionnaireResponseItemMap
  }

  /** Function to create a map from linkId to questionnaireItem path */
  private fun createLinkIdToQuestionnaireItemPathMap(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    pathPrefix: String = ""
  ): MutableMap<String, String> {

    val linkIdToQuestionnaireItemPathMap =
      questionnaireItemList
        .associate { it.linkId to if (pathPrefix == "") it.linkId else "$pathPrefix.${it.linkId}" }
        .toMutableMap()

    for (item in questionnaireItemList) {
      linkIdToQuestionnaireItemPathMap.putAll(
        createLinkIdToQuestionnaireItemPathMap(
          item.item,
          if (pathPrefix == "") item.linkId else "$pathPrefix.${item.linkId}"
        )
      )
    }
    return linkIdToQuestionnaireItemPathMap
  }

  /** * Function to create a map from questionnaireItem path to variables */
  private fun createPathToVariableMap(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaire: Questionnaire? = null
  ): java.util.LinkedHashMap<String, List<Variable>?> {

    val pathToVariableMap =
      questionnaireItemList.associate { questionnaireItem ->
        linkIdToQuestionnaireItemPathMap[questionnaireItem.linkId]!! to
          buildList {
            questionnaireItem.variableExpressions.forEach { variableExpression ->
              add(Variable(variableExpression, null, questionnaireItem.linkId))
            }
          }
            .takeIf { it.isNotEmpty() }
      } as
        LinkedHashMap

    questionnaire?.let { questionnaire ->
      val rootVariables =
        buildList {
          questionnaire.variableExpressions.forEach { variableExpression ->
            add(Variable(variableExpression, null, null))
          }
        }
          .takeIf { it.isNotEmpty() }
      rootVariables?.let { pathToVariableMap.put(ROOT_VARIABLES, it) }
    }

    for (item in questionnaireItemList) {
      pathToVariableMap.putAll(createPathToVariableMap(item.item))
    }
    return pathToVariableMap
  }

  private fun createLinkIdToQuestionnaireItemMap(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>
  ): Map<String, Questionnaire.QuestionnaireItemComponent> {
    val linkIdToQuestionnaireItemMap =
      questionnaireItemList.map { it.linkId to it }.toMap().toMutableMap()
    for (item in questionnaireItemList) {
      linkIdToQuestionnaireItemMap.putAll(createLinkIdToQuestionnaireItemMap(item.item))
    }
    return linkIdToQuestionnaireItemMap
  }

  /**
   * Traverses through the list of questionnaire items, the list of questionnaire response items and
   * the list of items in the questionnaire response answer list and populates
   * [questionnaireStateFlow] with matching pairs of questionnaire item and questionnaire response
   * item.
   *
   * The traverse is carried out in the two lists in tandem. The two lists should be structurally
   * identical.
   */
  private fun getQuestionnaireState(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
    pagination: QuestionnairePagination?,
    modificationCount: Int,
  ): QuestionnaireState {
    var responseIndex = 0
    val items: List<QuestionnaireItemViewItem> =
      questionnaireItemList
        .asSequence()
        .flatMapIndexed { index, questionnaireItem ->
          var questionnaireResponseItem = questionnaireItem.createQuestionnaireResponseItem()

          // If there is an enabled questionnaire response available then we use that. Or else we
          // just use an empty questionnaireResponse Item
          if (responseIndex < questionnaireResponseItemList.size &&
              questionnaireItem.linkId == questionnaireResponseItem.linkId
          ) {
            questionnaireResponseItem = questionnaireResponseItemList[responseIndex]
            responseIndex += 1
          }
          // if the questionnaire is paginated and we're currently working through the paginated
          // groups, make sure that only the current page gets set
          if (pagination != null && pagination.currentPageIndex != index) {
            return@flatMapIndexed emptyList()
          }

          val enabled =
            EnablementEvaluator.evaluate(questionnaireItem, questionnaireResponse) { linkId ->
              getQuestionnaireResponseItem(linkId)
            }

          if (!enabled || questionnaireItem.isHidden) {
            return@flatMapIndexed emptyList()
          }

          val validationResult =
            if (modifiedQuestionnaireResponseItemSet.contains(questionnaireResponseItem)) {
              QuestionnaireResponseItemValidator.validate(
                questionnaireItem,
                questionnaireResponseItem.answer,
                this@QuestionnaireViewModel.getApplication()
              )
            } else {
              ValidationResult(true, listOf())
            }

          listOf(
            QuestionnaireItemViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = validationResult,
              answersChangedCallback = answersChangedCallback,
              resolveAnswerValueSet = { resolveAnswerValueSet(it) },
            )
          ) +
            getQuestionnaireState(
                // Nested display item is subtitle text for parent questionnaire item if data type
                // is not group.
                // If nested display item is identified as subtitle text, then do not create
                // questionnaire state for it.
                questionnaireItemList =
                  when (questionnaireItem.type) {
                    Questionnaire.QuestionnaireItemType.GROUP -> questionnaireItem.item
                    else ->
                      questionnaireItem.item.filterNot {
                        it.type == Questionnaire.QuestionnaireItemType.DISPLAY
                      }
                  },
                questionnaireResponseItemList =
                  if (questionnaireResponseItem.answer.isEmpty()) {
                    questionnaireResponseItem.item
                  } else {
                    questionnaireResponseItem.answer.first().item
                  },
                // we're now dealing with nested items, so pagination is no longer a concern
                pagination = null,
                modificationCount = modificationCount,
              )
              .items
        }
        .toList()
    return QuestionnaireState(
      items = items,
      pagination = pagination,
      modificationCount = modificationCount
    )
  }

  private fun getEnabledResponseItems(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
  ): List<QuestionnaireResponse.QuestionnaireResponseItemComponent> {
    return questionnaireItemList
      .asSequence()
      .zip(questionnaireResponseItemList.asSequence())
      .filter { (questionnaireItem, _) ->
        EnablementEvaluator.evaluate(questionnaireItem, questionnaireResponse) { linkId ->
          getQuestionnaireResponseItem(linkId) ?: return@evaluate null
        }
      }
      .map { (questionnaireItem, questionnaireResponseItem) ->
        // Nested group items
        questionnaireResponseItem.item =
          getEnabledResponseItems(questionnaireItem.item, questionnaireResponseItem.item)
        // Nested question items
        questionnaireResponseItem.answer.forEach {
          it.item = getEnabledResponseItems(questionnaireItem.item, it.item)
        }
        questionnaireResponseItem
      }
      .toList()
  }

  /**
   * Checks if this questionnaire uses pagination via the "page" extension.
   *
   * If any one group has a "page" extension, it is assumed that the whole questionnaire is a
   * well-formed, paginated questionnaire (eg, each top-level group should be its own page).
   *
   * If this questionnaire uses pagination, returns the [QuestionnairePagination] that you would see
   * when first opening this questionnaire. Otherwise, returns `null`.
   */
  private fun Questionnaire.getInitialPagination(): QuestionnairePagination? {
    val usesPagination =
      item.any { item ->
        item.extension.any { extension ->
          (extension.value as? CodeableConcept)?.coding?.any { coding -> coding.code == "page" } ==
            true
        }
      }
    return if (usesPagination) {
      QuestionnairePagination(
        currentPageIndex = 0,
        lastPageIndex = item.size - 1,
      )
    } else {
      null
    }
  }

  /**
   * Returns a [QuestionnaireResponse.QuestionnaireResponseItemComponent] with the given `linkId`.
   */
  private fun getQuestionnaireResponseItem(
    linkId: String
  ): QuestionnaireResponse.QuestionnaireResponseItemComponent? {
    return questionnaireResponse
      .item
      .map { it.getQuestionnaireResponseItemComponent(linkId) }
      .firstOrNull()
  }

  /** Returns the current item or any descendant with the given `linkId`. */
  private fun QuestionnaireResponse.QuestionnaireResponseItemComponent.getQuestionnaireResponseItemComponent(
    linkId: String
  ): QuestionnaireResponse.QuestionnaireResponseItemComponent? {
    if (this.linkId == linkId) {
      return this
    }

    val child = item.firstOrNull { it.linkId == linkId }
    if (child != null) {
      return child
    }

    return null
  }
}

/** Questionnaire state for the Fragment to consume. */
internal data class QuestionnaireState(
  /** The items that should be currently-rendered into the Fragment. */
  val items: List<QuestionnaireItemViewItem>,
  /** The pagination state of the questionnaire. If `null`, the questionnaire is not paginated. */
  val pagination: QuestionnairePagination?,
  /** Tracks modifications in order to update the UI. */
  val modificationCount: Int,
)

internal data class QuestionnairePagination(
  val currentPageIndex: Int,
  val lastPageIndex: Int,
)

internal val QuestionnairePagination.hasPreviousPage: Boolean
  get() = currentPageIndex > 0
internal val QuestionnairePagination.hasNextPage: Boolean
  get() = currentPageIndex < lastPageIndex

internal fun QuestionnairePagination.previousPage(): QuestionnairePagination {
  check(hasPreviousPage) { "Can't call previousPage() if hasPreviousPage is false ($this)" }
  return copy(currentPageIndex = currentPageIndex - 1)
}

internal fun QuestionnairePagination.nextPage(): QuestionnairePagination {
  check(hasNextPage) { "Can't call nextPage() if hasNextPage is false ($this)" }
  return copy(currentPageIndex = currentPageIndex + 1)
}

/** A class for Variables defined at root and item level in the Questionnaire */
data class Variable(
  val expression: Expression,
  var value: IBaseDatatype? = null,
  val questionnaireItemLinkId: String? = null
)

internal const val ROOT_VARIABLES = "/"
