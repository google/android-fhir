/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ImagingStudy
import com.google.fhir.r4.core.ImagingStudy.Series
import com.google.fhir.r4.core.ImagingStudyStatusCode
import com.google.fhir.r4.core.String

object ImagingStudyConverter {
  fun ImagingStudy.toHapi(): org.hl7.fhir.r4.model.ImagingStudy {
    val hapiValue = org.hl7.fhir.r4.model.ImagingStudy()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (hasMeta()) {
      hapiValue.meta = meta.toHapi()
    }
    if (hasImplicitRules()) {
      hapiValue.implicitRulesElement = implicitRules.toHapi()
    }
    if (hasText()) {
      hapiValue.text = text.toHapi()
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.ImagingStudy.ImagingStudyStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (modalityCount > 0) {
      hapiValue.modality = modalityList.map { it.toHapi() }
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasStarted()) {
      hapiValue.startedElement = started.toHapi()
    }
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (hasReferrer()) {
      hapiValue.referrer = referrer.toHapi()
    }
    if (interpreterCount > 0) {
      hapiValue.interpreter = interpreterList.map { it.toHapi() }
    }
    if (endpointCount > 0) {
      hapiValue.endpoint = endpointList.map { it.toHapi() }
    }
    if (hasNumberOfSeries()) {
      hapiValue.numberOfSeriesElement = numberOfSeries.toHapi()
    }
    if (hasNumberOfInstances()) {
      hapiValue.numberOfInstancesElement = numberOfInstances.toHapi()
    }
    if (hasProcedureReference()) {
      hapiValue.procedureReference = procedureReference.toHapi()
    }
    if (procedureCodeCount > 0) {
      hapiValue.procedureCode = procedureCodeList.map { it.toHapi() }
    }
    if (hasLocation()) {
      hapiValue.location = location.toHapi()
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (seriesCount > 0) {
      hapiValue.series = seriesList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.ImagingStudy.toProto(): ImagingStudy {
    val protoValue = ImagingStudy.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
    if (hasMeta()) {
      protoValue.meta = meta.toProto()
    }
    if (hasImplicitRules()) {
      protoValue.implicitRules = implicitRulesElement.toProto()
    }
    if (hasText()) {
      protoValue.text = text.toProto()
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        ImagingStudy.StatusCode.newBuilder()
          .setValue(
            ImagingStudyStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasModality()) {
      protoValue.addAllModality(modality.map { it.toProto() })
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasStarted()) {
      protoValue.started = startedElement.toProto()
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasReferrer()) {
      protoValue.referrer = referrer.toProto()
    }
    if (hasInterpreter()) {
      protoValue.addAllInterpreter(interpreter.map { it.toProto() })
    }
    if (hasEndpoint()) {
      protoValue.addAllEndpoint(endpoint.map { it.toProto() })
    }
    if (hasNumberOfSeries()) {
      protoValue.numberOfSeries = numberOfSeriesElement.toProto()
    }
    if (hasNumberOfInstances()) {
      protoValue.numberOfInstances = numberOfInstancesElement.toProto()
    }
    if (hasProcedureReference()) {
      protoValue.procedureReference = procedureReference.toProto()
    }
    if (hasProcedureCode()) {
      protoValue.addAllProcedureCode(procedureCode.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.location = location.toProto()
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasSeries()) {
      protoValue.addAllSeries(series.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesComponent.toProto():
    ImagingStudy.Series {
    val protoValue = ImagingStudy.Series.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasUid()) {
      protoValue.uid = uidElement.toProto()
    }
    if (hasNumber()) {
      protoValue.number = numberElement.toProto()
    }
    if (hasModality()) {
      protoValue.modality = modality.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasNumberOfInstances()) {
      protoValue.numberOfInstances = numberOfInstancesElement.toProto()
    }
    if (hasEndpoint()) {
      protoValue.addAllEndpoint(endpoint.map { it.toProto() })
    }
    if (hasBodySite()) {
      protoValue.bodySite = bodySite.toProto()
    }
    if (hasLaterality()) {
      protoValue.laterality = laterality.toProto()
    }
    if (hasSpecimen()) {
      protoValue.addAllSpecimen(specimen.map { it.toProto() })
    }
    if (hasStarted()) {
      protoValue.started = startedElement.toProto()
    }
    if (hasPerformer()) {
      protoValue.addAllPerformer(performer.map { it.toProto() })
    }
    if (hasInstance()) {
      protoValue.addAllInstance(instance.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesPerformerComponent.toProto():
    ImagingStudy.Series.Performer {
    val protoValue = ImagingStudy.Series.Performer.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasFunction()) {
      protoValue.function = function.toProto()
    }
    if (hasActor()) {
      protoValue.actor = actor.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesInstanceComponent.toProto():
    ImagingStudy.Series.Instance {
    val protoValue = ImagingStudy.Series.Instance.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasUid()) {
      protoValue.uid = uidElement.toProto()
    }
    if (hasSopClass()) {
      protoValue.sopClass = sopClass.toProto()
    }
    if (hasNumber()) {
      protoValue.number = numberElement.toProto()
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    return protoValue.build()
  }

  private fun ImagingStudy.Series.toHapi():
    org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasUid()) {
      hapiValue.uidElement = uid.toHapi()
    }
    if (hasNumber()) {
      hapiValue.numberElement = number.toHapi()
    }
    if (hasModality()) {
      hapiValue.modality = modality.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasNumberOfInstances()) {
      hapiValue.numberOfInstancesElement = numberOfInstances.toHapi()
    }
    if (endpointCount > 0) {
      hapiValue.endpoint = endpointList.map { it.toHapi() }
    }
    if (hasBodySite()) {
      hapiValue.bodySite = bodySite.toHapi()
    }
    if (hasLaterality()) {
      hapiValue.laterality = laterality.toHapi()
    }
    if (specimenCount > 0) {
      hapiValue.specimen = specimenList.map { it.toHapi() }
    }
    if (hasStarted()) {
      hapiValue.startedElement = started.toHapi()
    }
    if (performerCount > 0) {
      hapiValue.performer = performerList.map { it.toHapi() }
    }
    if (instanceCount > 0) {
      hapiValue.instance = instanceList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun ImagingStudy.Series.Performer.toHapi():
    org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesPerformerComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesPerformerComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasFunction()) {
      hapiValue.function = function.toHapi()
    }
    if (hasActor()) {
      hapiValue.actor = actor.toHapi()
    }
    return hapiValue
  }

  private fun ImagingStudy.Series.Instance.toHapi():
    org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesInstanceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesInstanceComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasUid()) {
      hapiValue.uidElement = uid.toHapi()
    }
    if (hasSopClass()) {
      hapiValue.sopClass = sopClass.toHapi()
    }
    if (hasNumber()) {
      hapiValue.numberElement = number.toHapi()
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    return hapiValue
  }
}
