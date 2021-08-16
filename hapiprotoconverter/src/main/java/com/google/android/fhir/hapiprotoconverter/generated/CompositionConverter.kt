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
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Composition
import com.google.fhir.r4.core.Composition.Attester
import com.google.fhir.r4.core.Composition.RelatesTo
import com.google.fhir.r4.core.Composition.Section
import com.google.fhir.r4.core.CompositionAttestationModeCode
import com.google.fhir.r4.core.CompositionStatusCode
import com.google.fhir.r4.core.DocumentRelationshipTypeCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Identifier
import com.google.fhir.r4.core.ListModeCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.V3ConfidentialityClassificationValueSet
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Type

object CompositionConverter {
  @JvmStatic
  private fun Composition.RelatesTo.TargetX.compositionRelatesToTargetToHapi(): Type {
    if (this.identifier != Identifier.newBuilder().defaultInstanceForType) {
      return (this.identifier).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Composition.relatesTo.target[x]")
  }

  @JvmStatic
  private fun Type.compositionRelatesToTargetToProto(): Composition.RelatesTo.TargetX {
    val protoValue = Composition.RelatesTo.TargetX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Identifier) {
      protoValue.identifier = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Composition.toHapi(): org.hl7.fhir.r4.model.Composition {
    val hapiValue = org.hl7.fhir.r4.model.Composition()
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
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.Composition.CompositionStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (authorCount > 0) {
      hapiValue.author = authorList.map { it.toHapi() }
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    hapiValue.confidentiality =
      org.hl7.fhir.r4.model.Composition.DocumentConfidentiality.valueOf(
        confidentiality.value.name.hapiCodeCheck().replace("_", "")
      )
    if (attesterCount > 0) {
      hapiValue.attester = attesterList.map { it.toHapi() }
    }
    if (hasCustodian()) {
      hapiValue.custodian = custodian.toHapi()
    }
    if (relatesToCount > 0) {
      hapiValue.relatesTo = relatesToList.map { it.toHapi() }
    }
    if (eventCount > 0) {
      hapiValue.event = eventList.map { it.toHapi() }
    }
    if (sectionCount > 0) {
      hapiValue.section = sectionList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Composition.toProto(): Composition {
    val protoValue = Composition.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.identifier = identifier.toProto()
    }
    protoValue.status =
      Composition.StatusCode.newBuilder()
        .setValue(
          CompositionStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasAuthor()) {
      protoValue.addAllAuthor(author.map { it.toProto() })
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    protoValue.confidentiality =
      Composition.ConfidentialityCode.newBuilder()
        .setValue(
          V3ConfidentialityClassificationValueSet.Value.valueOf(
            confidentiality.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasAttester()) {
      protoValue.addAllAttester(attester.map { it.toProto() })
    }
    if (hasCustodian()) {
      protoValue.custodian = custodian.toProto()
    }
    if (hasRelatesTo()) {
      protoValue.addAllRelatesTo(relatesTo.map { it.toProto() })
    }
    if (hasEvent()) {
      protoValue.addAllEvent(event.map { it.toProto() })
    }
    if (hasSection()) {
      protoValue.addAllSection(section.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Composition.CompositionAttesterComponent.toProto():
    Composition.Attester {
    val protoValue = Composition.Attester.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.mode =
      Composition.Attester.ModeCode.newBuilder()
        .setValue(
          CompositionAttestationModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasTime()) {
      protoValue.time = timeElement.toProto()
    }
    if (hasParty()) {
      protoValue.party = party.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Composition.CompositionRelatesToComponent.toProto():
    Composition.RelatesTo {
    val protoValue = Composition.RelatesTo.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.code =
      Composition.RelatesTo.CodeType.newBuilder()
        .setValue(
          DocumentRelationshipTypeCode.Value.valueOf(
            code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasTarget()) {
      protoValue.target = target.compositionRelatesToTargetToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Composition.CompositionEventComponent.toProto():
    Composition.Event {
    val protoValue = Composition.Event.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    if (hasDetail()) {
      protoValue.addAllDetail(detail.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Composition.SectionComponent.toProto(): Composition.Section {
    val protoValue = Composition.Section.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasAuthor()) {
      protoValue.addAllAuthor(author.map { it.toProto() })
    }
    if (hasFocus()) {
      protoValue.focus = focus.toProto()
    }
    if (hasText()) {
      protoValue.text = text.toProto()
    }
    protoValue.mode =
      Composition.Section.ModeCode.newBuilder()
        .setValue(
          ListModeCode.Value.valueOf(mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
        )
        .build()
    if (hasOrderedBy()) {
      protoValue.orderedBy = orderedBy.toProto()
    }
    if (hasEntry()) {
      protoValue.addAllEntry(entry.map { it.toProto() })
    }
    if (hasEmptyReason()) {
      protoValue.emptyReason = emptyReason.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Composition.Attester.toHapi():
    org.hl7.fhir.r4.model.Composition.CompositionAttesterComponent {
    val hapiValue = org.hl7.fhir.r4.model.Composition.CompositionAttesterComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.mode =
      org.hl7.fhir.r4.model.Composition.CompositionAttestationMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasTime()) {
      hapiValue.timeElement = time.toHapi()
    }
    if (hasParty()) {
      hapiValue.party = party.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Composition.RelatesTo.toHapi():
    org.hl7.fhir.r4.model.Composition.CompositionRelatesToComponent {
    val hapiValue = org.hl7.fhir.r4.model.Composition.CompositionRelatesToComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.code =
      org.hl7.fhir.r4.model.Composition.DocumentRelationshipType.valueOf(
        code.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasTarget()) {
      hapiValue.target = target.compositionRelatesToTargetToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Composition.Event.toHapi():
    org.hl7.fhir.r4.model.Composition.CompositionEventComponent {
    val hapiValue = org.hl7.fhir.r4.model.Composition.CompositionEventComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (codeCount > 0) {
      hapiValue.code = codeList.map { it.toHapi() }
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    if (detailCount > 0) {
      hapiValue.detail = detailList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Composition.Section.toHapi(): org.hl7.fhir.r4.model.Composition.SectionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Composition.SectionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (authorCount > 0) {
      hapiValue.author = authorList.map { it.toHapi() }
    }
    if (hasFocus()) {
      hapiValue.focus = focus.toHapi()
    }
    if (hasText()) {
      hapiValue.text = text.toHapi()
    }
    hapiValue.mode =
      org.hl7.fhir.r4.model.Composition.SectionMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasOrderedBy()) {
      hapiValue.orderedBy = orderedBy.toHapi()
    }
    if (entryCount > 0) {
      hapiValue.entry = entryList.map { it.toHapi() }
    }
    if (hasEmptyReason()) {
      hapiValue.emptyReason = emptyReason.toHapi()
    }
    return hapiValue
  }
}
