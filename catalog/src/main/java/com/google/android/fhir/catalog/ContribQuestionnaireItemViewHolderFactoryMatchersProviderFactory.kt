/*
 * Copyright 2024 Google LLC
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

import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.QuestionnaireItemViewHolderFactoryMatchersProviderFactory
import com.google.android.fhir.datacapture.contrib.views.locationwidget.LocationGpsCoordinateViewHolderFactory
import com.google.android.fhir.datacapture.contrib.views.locationwidget.LocationWidgetViewHolderFactory

object ContribQuestionnaireItemViewHolderFactoryMatchersProviderFactory :
  QuestionnaireItemViewHolderFactoryMatchersProviderFactory {

  const val LOCATION_WIDGET_PROVIDER = "location-widget-provider"

  override fun get(
    provider: String,
  ): QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider =
    when (provider) {
      LOCATION_WIDGET_PROVIDER ->
        object : QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider() {
          override fun get():
            List<QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher> {
            return listOf(
              QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher(
                factory = LocationGpsCoordinateViewHolderFactory,
                matches = LocationGpsCoordinateViewHolderFactory::matcher,
              ),
              QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher(
                factory = LocationWidgetViewHolderFactory,
                matches = LocationWidgetViewHolderFactory::matcher,
              ),
            )
          }
        }
      else -> throw NotImplementedError()
    }
}
