/*
 * Copyright 2021 Google LLC
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

import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem

/**
 * Responsible for holding current Page strategy that can changed/set dynamically on runtime by the
 * Questionnaire
 */
class QuestionnairePageChangeEventContext {
  /*
   * Default Implementation provided by the SDK
   * */
  private var pageChangeStrategy: PageChangeStrategy = DefaultPageChangeStrategy()

  fun setStrategy(pageChangeStrategy: PageChangeStrategy) {
    this.pageChangeStrategy = pageChangeStrategy
  }

  /**
   * This function will be called in the QuestionnaireViewModel goToNextPage and if returns true
   * then pageFlow.value will be changed.
   */
  fun pageNextEvent(list: List<QuestionnaireItemViewItem>): Boolean {
    return pageChangeStrategy.shouldGoToNextPage(list)
  }

  /*
   * This function will be called in the QuestionnaireViewModel goToPreviousPage and if returns true then pageFlow.value will be changed.
   * */
  fun pagePreviousEvent(list: List<QuestionnaireItemViewItem>): Boolean {
    return pageChangeStrategy.shouldGoToPreviousPage(list)
  }
}

/*
 * Responsible giving support methods for Questionnaire Page switching
 *  */
interface PageChangeStrategy {
  /*
   * when User taps on previous page of the Questionnaire
   * */
  fun shouldGoToPreviousPage(list: List<QuestionnaireItemViewItem>): Boolean

  /*
   * when User taps on next page of the Questionnaire
   * */
  fun shouldGoToNextPage(list: List<QuestionnaireItemViewItem>): Boolean
}
