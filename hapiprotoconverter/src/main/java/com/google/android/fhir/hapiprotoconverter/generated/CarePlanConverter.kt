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

object CarePlanConverter {
  private fun CarePlan.Activity.Detail.ScheduledX.carePlanActivityDetailScheduledToHapi(): Type {
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CarePlan.activity.detail.scheduled[x]")
  }

  private fun Type.carePlanActivityDetailScheduledToProto(): CarePlan.Activity.Detail.ScheduledX {
    val protoValue = CarePlan.Activity.Detail.ScheduledX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.timing = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  private fun CarePlan.Activity.Detail.ProductX.carePlanActivityDetailProductToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CarePlan.activity.detail.product[x]")
  }

  private fun Type.carePlanActivityDetailProductToProto(): CarePlan.Activity.Detail.ProductX {
    val protoValue = CarePlan.Activity.Detail.ProductX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  fun CarePlan.toHapi(): org.hl7.fhir.r4.model.CarePlan {
    val hapiValue = org.hl7.fhir.r4.model.CarePlan()
    hapiValue.id = id.value
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
    if (instantiatesCanonicalCount > 0) {
      hapiValue.instantiatesCanonical = instantiatesCanonicalList.map { it.toHapi() }
    }
    if (instantiatesUriCount > 0) {
      hapiValue.instantiatesUri = instantiatesUriList.map { it.toHapi() }
    }
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (replacesCount > 0) {
      hapiValue.replaces = replacesList.map { it.toHapi() }
    }
    if (partOfCount > 0) {
      hapiValue.partOf = partOfList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.CarePlan.CarePlanStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.intent =
      org.hl7.fhir.r4.model.CarePlan.CarePlanIntent.valueOf(
        intent.value.name.hapiCodeCheck().replace("_", "")
      )
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    if (hasCreated()) {
      hapiValue.createdElement = created.toHapi()
    }
    if (hasAuthor()) {
      hapiValue.author = author.toHapi()
    }
    if (contributorCount > 0) {
      hapiValue.contributor = contributorList.map { it.toHapi() }
    }
    if (careTeamCount > 0) {
      hapiValue.careTeam = careTeamList.map { it.toHapi() }
    }
    if (addressesCount > 0) {
      hapiValue.addresses = addressesList.map { it.toHapi() }
    }
    if (supportingInfoCount > 0) {
      hapiValue.supportingInfo = supportingInfoList.map { it.toHapi() }
    }
    if (goalCount > 0) {
      hapiValue.goal = goalList.map { it.toHapi() }
    }
    if (activityCount > 0) {
      hapiValue.activity = activityList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.CarePlan.toProto(): CarePlan {
    val protoValue = CarePlan.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasInstantiatesCanonical()) {
      protoValue.addAllInstantiatesCanonical(instantiatesCanonical.map { it.toProto() })
    }
    if (hasInstantiatesUri()) {
      protoValue.addAllInstantiatesUri(instantiatesUri.map { it.toProto() })
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasReplaces()) {
      protoValue.addAllReplaces(replaces.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
    protoValue.status =
      CarePlan.StatusCode.newBuilder()
        .setValue(
          RequestStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.intent =
      CarePlan.IntentCode.newBuilder()
        .setValue(
          CarePlanIntentValueSet.Value.valueOf(
            intent.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    if (hasCreated()) {
      protoValue.created = createdElement.toProto()
    }
    if (hasAuthor()) {
      protoValue.author = author.toProto()
    }
    if (hasContributor()) {
      protoValue.addAllContributor(contributor.map { it.toProto() })
    }
    if (hasCareTeam()) {
      protoValue.addAllCareTeam(careTeam.map { it.toProto() })
    }
    if (hasAddresses()) {
      protoValue.addAllAddresses(addresses.map { it.toProto() })
    }
    if (hasSupportingInfo()) {
      protoValue.addAllSupportingInfo(supportingInfo.map { it.toProto() })
    }
    if (hasGoal()) {
      protoValue.addAllGoal(goal.map { it.toProto() })
    }
    if (hasActivity()) {
      protoValue.addAllActivity(activity.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent.toProto():
    CarePlan.Activity {
    val protoValue = CarePlan.Activity.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasOutcomeCodeableConcept()) {
      protoValue.addAllOutcomeCodeableConcept(outcomeCodeableConcept.map { it.toProto() })
    }
    if (hasOutcomeReference()) {
      protoValue.addAllOutcomeReference(outcomeReference.map { it.toProto() })
    }
    if (hasProgress()) {
      protoValue.addAllProgress(progress.map { it.toProto() })
    }
    if (hasReference()) {
      protoValue.reference = reference.toProto()
    }
    if (hasDetail()) {
      protoValue.detail = detail.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CarePlan.CarePlanActivityDetailComponent.toProto():
    CarePlan.Activity.Detail {
    val protoValue = CarePlan.Activity.Detail.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.kind =
      CarePlan.Activity.Detail.KindCode.newBuilder()
        .setValue(
          CarePlanActivityKindValueSet.Value.valueOf(
            kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasInstantiatesCanonical()) {
      protoValue.addAllInstantiatesCanonical(instantiatesCanonical.map { it.toProto() })
    }
    if (hasInstantiatesUri()) {
      protoValue.addAllInstantiatesUri(instantiatesUri.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasGoal()) {
      protoValue.addAllGoal(goal.map { it.toProto() })
    }
    protoValue.status =
      CarePlan.Activity.Detail.StatusCode.newBuilder()
        .setValue(
          CarePlanActivityStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasStatusReason()) {
      protoValue.statusReason = statusReason.toProto()
    }
    if (hasDoNotPerform()) {
      protoValue.doNotPerform = doNotPerformElement.toProto()
    }
    if (hasScheduled()) {
      protoValue.scheduled = scheduled.carePlanActivityDetailScheduledToProto()
    }
    if (hasLocation()) {
      protoValue.location = location.toProto()
    }
    if (hasPerformer()) {
      protoValue.addAllPerformer(performer.map { it.toProto() })
    }
    if (hasProduct()) {
      protoValue.product = product.carePlanActivityDetailProductToProto()
    }
    if (hasDailyAmount()) {
      protoValue.dailyAmount = (dailyAmount as SimpleQuantity).toProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    return protoValue.build()
  }

  private fun CarePlan.Activity.toHapi(): org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent {
    val hapiValue = org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (outcomeCodeableConceptCount > 0) {
      hapiValue.outcomeCodeableConcept = outcomeCodeableConceptList.map { it.toHapi() }
    }
    if (outcomeReferenceCount > 0) {
      hapiValue.outcomeReference = outcomeReferenceList.map { it.toHapi() }
    }
    if (progressCount > 0) {
      hapiValue.progress = progressList.map { it.toHapi() }
    }
    if (hasReference()) {
      hapiValue.reference = reference.toHapi()
    }
    if (hasDetail()) {
      hapiValue.detail = detail.toHapi()
    }
    return hapiValue
  }

  private fun CarePlan.Activity.Detail.toHapi():
    org.hl7.fhir.r4.model.CarePlan.CarePlanActivityDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.CarePlan.CarePlanActivityDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.kind =
      org.hl7.fhir.r4.model.CarePlan.CarePlanActivityKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
    if (instantiatesCanonicalCount > 0) {
      hapiValue.instantiatesCanonical = instantiatesCanonicalList.map { it.toHapi() }
    }
    if (instantiatesUriCount > 0) {
      hapiValue.instantiatesUri = instantiatesUriList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (goalCount > 0) {
      hapiValue.goal = goalList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.CarePlan.CarePlanActivityStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasStatusReason()) {
      hapiValue.statusReason = statusReason.toHapi()
    }
    if (hasDoNotPerform()) {
      hapiValue.doNotPerformElement = doNotPerform.toHapi()
    }
    if (hasScheduled()) {
      hapiValue.scheduled = scheduled.carePlanActivityDetailScheduledToHapi()
    }
    if (hasLocation()) {
      hapiValue.location = location.toHapi()
    }
    if (performerCount > 0) {
      hapiValue.performer = performerList.map { it.toHapi() }
    }
    if (hasProduct()) {
      hapiValue.product = product.carePlanActivityDetailProductToHapi()
    }
    if (hasDailyAmount()) {
      hapiValue.dailyAmount = dailyAmount.toHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    return hapiValue
  }
}
