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
import kotlin.jvm.JvmStatic

public object ImagingStudyConverter {
  @JvmStatic
  public fun ImagingStudy.toHapi(): org.hl7.fhir.r4.model.ImagingStudy {
    val hapiValue = org.hl7.fhir.r4.model.ImagingStudy()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.ImagingStudy.ImagingStudyStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setModality(modalityList.map { it.toHapi() })
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setStartedElement(started.toHapi())
    hapiValue.setBasedOn(basedOnList.map { it.toHapi() })
    hapiValue.setReferrer(referrer.toHapi())
    hapiValue.setInterpreter(interpreterList.map { it.toHapi() })
    hapiValue.setEndpoint(endpointList.map { it.toHapi() })
    hapiValue.setNumberOfSeriesElement(numberOfSeries.toHapi())
    hapiValue.setNumberOfInstancesElement(numberOfInstances.toHapi())
    hapiValue.setProcedureReference(procedureReference.toHapi())
    hapiValue.setProcedureCode(procedureCodeList.map { it.toHapi() })
    hapiValue.setLocation(location.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    hapiValue.setReasonReference(reasonReferenceList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setSeries(seriesList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ImagingStudy.toProto(): ImagingStudy {
    val protoValue =
      ImagingStudy.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          ImagingStudy.StatusCode.newBuilder()
            .setValue(
              ImagingStudyStatusCode.Value.valueOf(
                status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .addAllModality(modality.map { it.toProto() })
        .setSubject(subject.toProto())
        .setEncounter(encounter.toProto())
        .setStarted(startedElement.toProto())
        .addAllBasedOn(basedOn.map { it.toProto() })
        .setReferrer(referrer.toProto())
        .addAllInterpreter(interpreter.map { it.toProto() })
        .addAllEndpoint(endpoint.map { it.toProto() })
        .setNumberOfSeries(numberOfSeriesElement.toProto())
        .setNumberOfInstances(numberOfInstancesElement.toProto())
        .setProcedureReference(procedureReference.toProto())
        .addAllProcedureCode(procedureCode.map { it.toProto() })
        .setLocation(location.toProto())
        .addAllReasonCode(reasonCode.map { it.toProto() })
        .addAllReasonReference(reasonReference.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllSeries(series.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesComponent.toProto():
    ImagingStudy.Series {
    val protoValue =
      ImagingStudy.Series.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUid(uidElement.toProto())
        .setNumber(numberElement.toProto())
        .setModality(modality.toProto())
        .setDescription(descriptionElement.toProto())
        .setNumberOfInstances(numberOfInstancesElement.toProto())
        .addAllEndpoint(endpoint.map { it.toProto() })
        .setBodySite(bodySite.toProto())
        .setLaterality(laterality.toProto())
        .addAllSpecimen(specimen.map { it.toProto() })
        .setStarted(startedElement.toProto())
        .addAllPerformer(performer.map { it.toProto() })
        .addAllInstance(instance.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesPerformerComponent.toProto():
    ImagingStudy.Series.Performer {
    val protoValue =
      ImagingStudy.Series.Performer.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setFunction(function.toProto())
        .setActor(actor.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesInstanceComponent.toProto():
    ImagingStudy.Series.Instance {
    val protoValue =
      ImagingStudy.Series.Instance.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUid(uidElement.toProto())
        .setSopClass(sopClass.toProto())
        .setNumber(numberElement.toProto())
        .setTitle(titleElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun ImagingStudy.Series.toHapi():
    org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUidElement(uid.toHapi())
    hapiValue.setNumberElement(number.toHapi())
    hapiValue.setModality(modality.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setNumberOfInstancesElement(numberOfInstances.toHapi())
    hapiValue.setEndpoint(endpointList.map { it.toHapi() })
    hapiValue.setBodySite(bodySite.toHapi())
    hapiValue.setLaterality(laterality.toHapi())
    hapiValue.setSpecimen(specimenList.map { it.toHapi() })
    hapiValue.setStartedElement(started.toHapi())
    hapiValue.setPerformer(performerList.map { it.toHapi() })
    hapiValue.setInstance(instanceList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun ImagingStudy.Series.Performer.toHapi():
    org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesPerformerComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesPerformerComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setFunction(function.toHapi())
    hapiValue.setActor(actor.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun ImagingStudy.Series.Instance.toHapi():
    org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesInstanceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesInstanceComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUidElement(uid.toHapi())
    hapiValue.setSopClass(sopClass.toHapi())
    hapiValue.setNumberElement(number.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    return hapiValue
  }
}
