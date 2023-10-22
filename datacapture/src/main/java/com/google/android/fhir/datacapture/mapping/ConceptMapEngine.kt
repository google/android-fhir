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
    val cm =
      context.fetchResource(
        ConceptMap::class.java,
        url,
      )
        ?: throw FHIRException("Unable to find ConceptMap '$url'")
    return if (source.hasSystem()) {
      translateBySystem(
        cm,
        source.system,
        source.code,
      )
    } else {
      translateByJustCode(cm, source.code)
    }
  }

  @Throws(FHIRException::class)
  private fun translateByJustCode(cm: ConceptMap, code: String): Coding? {
    var ct: ConceptMap.SourceElementComponent? = null
    var cg: ConceptMap.ConceptMapGroupComponent? = null
    for (g in cm.group) {
      for (e in g.element) {
        if (code == e!!.code) {
          if (e != null) {
            throw FHIRException(
              "Unable to process translate " +
                code +
                " because multiple candidate matches were found in concept map " +
                cm.url,
            )
          }
          ct = e
          cg = g
        }
      }
    }
    if (ct == null) return null
    var tt: ConceptMap.TargetElementComponent? = null
    for (t in ct.target) {
      if (!t.hasDependsOn() && !t.hasProduct()) {
        if (tt != null) {
          throw FHIRException(
            "Unable to process translate " +
              code +
              " because multiple targets were found in concept map " +
              cm.url,
          )
        }
        tt = t
      }
    }
    if (tt == null) return null
    val cp = CanonicalPair(cg!!.target)
    return Coding().setSystem(cp.url).setVersion(cp.version).setCode(tt.code).setDisplay(tt.display)
  }

  //    private boolean isOkRelationship(ConceptMapRelationship relationship) {
  //        return relationship != null && relationship != ConceptMapRelationship.NOTRELATEDTO;
  //    }
  private fun translateBySystem(cm: ConceptMap, system: String, code: String): Coding {
    throw Error("Not done yet")
  }
}
