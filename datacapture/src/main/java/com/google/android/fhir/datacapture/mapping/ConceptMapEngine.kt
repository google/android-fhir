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

package com.google.android.fhir.datacapture.mapping

import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ConceptMap
import org.hl7.fhir.utilities.CanonicalPair

class ConceptMapEngine(private val context: IWorkerContext) {
  @Throws(FHIRException::class)
  fun translate(source: Coding, url: String): Coding? {
    val conceptMap =
      context.fetchResource(
        ConceptMap::class.java,
        url,
      )
        ?: throw FHIRException("Unable to find ConceptMap '$url'")
    return if (source.hasSystem()) {
      translateBySystem(
        conceptMap,
        source.system,
        source.code,
      )
    } else {
      translateByJustCode(conceptMap, source.code)
    }
  }

  @Throws(FHIRException::class)
  private fun translateByJustCode(conceptMap: ConceptMap, code: String): Coding? {
    var sourceElementComponent: ConceptMap.SourceElementComponent? = null
    var conceptMapGroupComponent: ConceptMap.ConceptMapGroupComponent? = null
    for (group in conceptMap.group) {
      for (element in group.element) {
        if (element != null) {
          if (code == element.code) {
            throw FHIRException(
              "Unable to process translate " +
                code +
                " because multiple candidate matches were found in concept map " +
                conceptMap.url,
            )
          }
          sourceElementComponent = element
          conceptMapGroupComponent = group
        }
      }
    }
    sourceElementComponent ?: return null
    var targetElementComponent: ConceptMap.TargetElementComponent? = null
    sourceElementComponent.target
      .singleOrNull { !it.hasDependsOn() && !it.hasProduct() }
      ?.let { targetElementComponent = it }
    targetElementComponent ?: return null
    val canonicalPair = CanonicalPair(conceptMapGroupComponent!!.target)
    return Coding()
      .setSystem(canonicalPair.url)
      .setVersion(canonicalPair.version)
      .setCode(targetElementComponent?.code)
      .setDisplay(targetElementComponent?.display)
  }

  private fun translateBySystem(conceptMap: ConceptMap, system: String, code: String): Coding {
    throw Error("Not done yet")
  }
}
