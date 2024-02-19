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
import android.graphics.Bitmap
import com.google.android.fhir.datacapture.DataCaptureConfig.Provider
import com.google.android.fhir.datacapture.QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher
import com.google.android.fhir.datacapture.QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.utilities.npm.NpmPackage

/**
 * The App developers may provide the [DataCaptureConfig] for the DataCapture library by
 * implementing [Provider] interface in the [Application] class. The library would load and cache
 * the configuration by calling [Provider.getDataCaptureConfig].
 *
 * NOTE: App developers should make sure that [Provider.getDataCaptureConfig] provides a constant
 * [DataCaptureConfig] throughout the lifecycle of the application.
 */
data class DataCaptureConfig(
  /**
   * An [ExternalAnswerValueSetResolver] may be set to provide answer options dynamically for
   * `choice` and `open-choice` type questions.
   */
  var valueSetResolverExternal: ExternalAnswerValueSetResolver? = null,

  /**
   * A [NpmPackage] may be set by the client for Structure-Map based Resource Extraction.
   *
   * The loading and extraction of a [NpmPackage] may take multiple seconds, so the client app
   * should try to include the smallest [NpmPackage] possible that contains only the resources
   * needed by [StructureMap]s used by the client app.
   */
  var npmPackage: NpmPackage? = null,

  /**
   * A [XFhirQueryResolver] may be set by the client to resolve x-fhir-query for the library. See
   * https://build.fhir.org/ig/HL7/sdc/expressions.html#fhirquery for more details.
   */
  var xFhirQueryResolver: XFhirQueryResolver? = null,

  /** Resolves a URL to the media binary content. */
  var urlResolver: UrlResolver? = null,

  /**
   * A [QuestionnaireItemViewHolderFactoryMatchersProviderFactory] may be set by the client to
   * provide [QuestionnaireItemViewHolderFactoryMatcher]s to add custom questionnaire components or
   * override the behaviour of existing components in the sdc.
   */
  var questionnaireItemViewHolderFactoryMatchersProviderFactory:
    QuestionnaireItemViewHolderFactoryMatchersProviderFactory? =
    null
) {

  internal val simpleWorkerContext: SimpleWorkerContext by lazy {
    if (npmPackage == null) SimpleWorkerContext() else SimpleWorkerContext.fromPackage(npmPackage)
  }

  /**
   * A class that can provide the [DataCaptureConfig] for the Structured Data Capture Library. To do
   * this, implement the [DataCaptureConfig.Provider] interface on your [Application] class. You
   * should provide the same configuration throughout the lifecycle of your application. The library
   * may cache the configuration and different configurations will be ignored.
   */
  interface Provider {
    fun getDataCaptureConfig(): DataCaptureConfig
  }
}

/**
 * Resolves external answer value sets not defined in the questionnaire's `contained` element. This
 * allows the library to render answer options to `choice` and `open-choice` type questions more
 * dynamically.
 *
 * NOTE: The result of the resolution may be cached to improve performance. In other words, the
 * resolver may be called only once after which the same answer value set may be used multiple times
 * in the UI to populate answer options.
 */
interface ExternalAnswerValueSetResolver {
  suspend fun resolve(uri: String): List<Coding>
}

/**
 * Resolves resources based on the provided xFhir query. This allows the library to resolve
 * x-fhir-query answer expressions.
 *
 * NOTE: The result of the resolution may be cached to improve performance. In other words, the
 * resolver may be called only once after which the Resources may be used multiple times in the UI.
 */
fun interface XFhirQueryResolver {
  suspend fun resolve(xFhirQuery: String): List<Resource>
}

/**
 * Resolves media content based on the provided URL, allowing the library to render media content in
 * its UI.
 */
interface UrlResolver {
  suspend fun resolveBitmapUrl(url: String): Bitmap?
}

/**
 * Factory to create [QuestionnaireItemViewHolderFactoryMatchersProvider] for the
 * [QuestionnaireFragment] to provide [List] of [QuestionnaireItemViewHolderFactoryMatcher]. The
 * developers may provide the factory to the library via [DataCaptureConfig] to add custom
 * questionnaire components or override the behaviour of existing components in the sdc.
 *
 * See the
 * [developer guide](https://github.com/google/android-fhir/wiki/SDCL:-Customize-how-a-Questionnaire-is-displayed#custom-questionnaire-components)
 * for more information.
 */
fun interface QuestionnaireItemViewHolderFactoryMatchersProviderFactory {
  fun get(provider: String): QuestionnaireItemViewHolderFactoryMatchersProvider
}
