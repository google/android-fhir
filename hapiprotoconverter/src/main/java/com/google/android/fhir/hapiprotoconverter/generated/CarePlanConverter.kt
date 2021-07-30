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
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CarePlan
import com.google.fhir.r4.core.CarePlan.Activity
import com.google.fhir.r4.core.CarePlan.Activity.Detail
import com.google.fhir.r4.core.CarePlanActivityKindValueSet
import com.google.fhir.r4.core.CarePlanActivityStatusCode
import com.google.fhir.r4.core.CarePlanIntentValueSet
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RequestStatusCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object CarePlanConverter {
  private fun CarePlan.Activity.Detail.ScheduledX.carePlanActivityDetailScheduledToHapi(): Type {
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CarePlan.activity.detail.scheduled[x]")
  }

  private fun Type.carePlanActivityDetailScheduledToProto(): CarePlan.Activity.Detail.ScheduledX {
    val protoValue = CarePlan.Activity.Detail.ScheduledX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  private fun CarePlan.Activity.Detail.ProductX.carePlanActivityDetailProductToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CarePlan.activity.detail.product[x]")
  }

  private fun Type.carePlanActivityDetailProductToProto(): CarePlan.Activity.Detail.ProductX {
    val protoValue = CarePlan.Activity.Detail.ProductX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun CarePlan.toHapi(): org.hl7.fhir.r4.model.CarePlan {
    val hapiValue = org.hl7.fhir.r4.model.CarePlan()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setInstantiatesCanonical(instantiatesCanonicalList.map { it.toHapi() })
    hapiValue.setInstantiatesUri(instantiatesUriList.map { it.toHapi() })
    hapiValue.setBasedOn(basedOnList.map { it.toHapi() })
    hapiValue.setReplaces(replacesList.map { it.toHapi() })
    hapiValue.setPartOf(partOfList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.CarePlan.CarePlanStatus.valueOf(status.value.name.replace("_", ""))
    )
    hapiValue.setIntent(
      org.hl7.fhir.r4.model.CarePlan.CarePlanIntent.valueOf(intent.value.name.replace("_", ""))
    )
    hapiValue.setCategory(categoryList.map { it.toHapi() })
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setCreatedElement(created.toHapi())
    hapiValue.setAuthor(author.toHapi())
    hapiValue.setContributor(contributorList.map { it.toHapi() })
    hapiValue.setCareTeam(careTeamList.map { it.toHapi() })
    hapiValue.setAddresses(addressesList.map { it.toHapi() })
    hapiValue.setSupportingInfo(supportingInfoList.map { it.toHapi() })
    hapiValue.setGoal(goalList.map { it.toHapi() })
    hapiValue.setActivity(activityList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.CarePlan.toProto(): CarePlan {
    val protoValue =
      CarePlan.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllInstantiatesCanonical(instantiatesCanonical.map { it.toProto() })
        .addAllInstantiatesUri(instantiatesUri.map { it.toProto() })
        .addAllBasedOn(basedOn.map { it.toProto() })
        .addAllReplaces(replaces.map { it.toProto() })
        .addAllPartOf(partOf.map { it.toProto() })
        .setStatus(
          CarePlan.StatusCode.newBuilder()
            .setValue(
              RequestStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setIntent(
          CarePlan.IntentCode.newBuilder()
            .setValue(
              CarePlanIntentValueSet.Value.valueOf(intent.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .addAllCategory(category.map { it.toProto() })
        .setTitle(titleElement.toProto())
        .setDescription(descriptionElement.toProto())
        .setSubject(subject.toProto())
        .setEncounter(encounter.toProto())
        .setPeriod(period.toProto())
        .setCreated(createdElement.toProto())
        .setAuthor(author.toProto())
        .addAllContributor(contributor.map { it.toProto() })
        .addAllCareTeam(careTeam.map { it.toProto() })
        .addAllAddresses(addresses.map { it.toProto() })
        .addAllSupportingInfo(supportingInfo.map { it.toProto() })
        .addAllGoal(goal.map { it.toProto() })
        .addAllActivity(activity.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent.toProto():
    CarePlan.Activity {
    val protoValue =
      CarePlan.Activity.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllOutcomeCodeableConcept(outcomeCodeableConcept.map { it.toProto() })
        .addAllOutcomeReference(outcomeReference.map { it.toProto() })
        .addAllProgress(progress.map { it.toProto() })
        .setReference(reference.toProto())
        .setDetail(detail.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.CarePlan.CarePlanActivityDetailComponent.toProto():
    CarePlan.Activity.Detail {
    val protoValue =
      CarePlan.Activity.Detail.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setKind(
          CarePlan.Activity.Detail.KindCode.newBuilder()
            .setValue(
              CarePlanActivityKindValueSet.Value.valueOf(
                kind.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .addAllInstantiatesCanonical(instantiatesCanonical.map { it.toProto() })
        .addAllInstantiatesUri(instantiatesUri.map { it.toProto() })
        .setCode(code.toProto())
        .addAllReasonCode(reasonCode.map { it.toProto() })
        .addAllReasonReference(reasonReference.map { it.toProto() })
        .addAllGoal(goal.map { it.toProto() })
        .setStatus(
          CarePlan.Activity.Detail.StatusCode.newBuilder()
            .setValue(
              CarePlanActivityStatusCode.Value.valueOf(
                status.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setStatusReason(statusReason.toProto())
        .setDoNotPerform(doNotPerformElement.toProto())
        .setScheduled(scheduled.carePlanActivityDetailScheduledToProto())
        .setLocation(location.toProto())
        .addAllPerformer(performer.map { it.toProto() })
        .setProduct(product.carePlanActivityDetailProductToProto())
        .setDailyAmount((dailyAmount as SimpleQuantity).toProto())
        .setQuantity((quantity as SimpleQuantity).toProto())
        .setDescription(descriptionElement.toProto())
        .build()
    return protoValue
  }

  private fun CarePlan.Activity.toHapi(): org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent {
    val hapiValue = org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setOutcomeCodeableConcept(outcomeCodeableConceptList.map { it.toHapi() })
    hapiValue.setOutcomeReference(outcomeReferenceList.map { it.toHapi() })
    hapiValue.setProgress(progressList.map { it.toHapi() })
    hapiValue.setReference(reference.toHapi())
    hapiValue.setDetail(detail.toHapi())
    return hapiValue
  }

  private fun CarePlan.Activity.Detail.toHapi():
    org.hl7.fhir.r4.model.CarePlan.CarePlanActivityDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.CarePlan.CarePlanActivityDetailComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setKind(
      org.hl7.fhir.r4.model.CarePlan.CarePlanActivityKind.valueOf(kind.value.name.replace("_", ""))
    )
    hapiValue.setInstantiatesCanonical(instantiatesCanonicalList.map { it.toHapi() })
    hapiValue.setInstantiatesUri(instantiatesUriList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    hapiValue.setReasonReference(reasonReferenceList.map { it.toHapi() })
    hapiValue.setGoal(goalList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.CarePlan.CarePlanActivityStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setStatusReason(statusReason.toHapi())
    hapiValue.setDoNotPerformElement(doNotPerform.toHapi())
    hapiValue.setScheduled(scheduled.carePlanActivityDetailScheduledToHapi())
    hapiValue.setLocation(location.toHapi())
    hapiValue.setPerformer(performerList.map { it.toHapi() })
    hapiValue.setProduct(product.carePlanActivityDetailProductToHapi())
    hapiValue.setDailyAmount(dailyAmount.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    return hapiValue
  }
}
