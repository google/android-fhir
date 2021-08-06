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

public object CompositionConverter {
  @JvmStatic
  private fun Composition.RelatesTo.TargetX.compositionRelatesToTargetToHapi(): Type {
    if (this.getIdentifier() != Identifier.newBuilder().defaultInstanceForType) {
      return (this.getIdentifier()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Composition.relatesTo.target[x]")
  }

  @JvmStatic
  private fun Type.compositionRelatesToTargetToProto(): Composition.RelatesTo.TargetX {
    val protoValue = Composition.RelatesTo.TargetX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Identifier) {
      protoValue.setIdentifier(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun Composition.toHapi(): org.hl7.fhir.r4.model.Composition {
    val hapiValue = org.hl7.fhir.r4.model.Composition()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.Composition.CompositionStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setType(type.toHapi())
    hapiValue.setCategory(categoryList.map { it.toHapi() })
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setAuthor(authorList.map { it.toHapi() })
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setConfidentiality(
      org.hl7.fhir.r4.model.Composition.DocumentConfidentiality.valueOf(
        confidentiality.value.name.replace("_", "")
      )
    )
    hapiValue.setAttester(attesterList.map { it.toHapi() })
    hapiValue.setCustodian(custodian.toHapi())
    hapiValue.setRelatesTo(relatesToList.map { it.toHapi() })
    hapiValue.setEvent(eventList.map { it.toHapi() })
    hapiValue.setSection(sectionList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Composition.toProto(): Composition {
    val protoValue =
      Composition.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setIdentifier(identifier.toProto())
        .setStatus(
          Composition.StatusCode.newBuilder()
            .setValue(
              CompositionStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setType(type.toProto())
        .addAllCategory(category.map { it.toProto() })
        .setSubject(subject.toProto())
        .setEncounter(encounter.toProto())
        .setDate(dateElement.toProto())
        .addAllAuthor(author.map { it.toProto() })
        .setTitle(titleElement.toProto())
        .setConfidentiality(
          Composition.ConfidentialityCode.newBuilder()
            .setValue(
              V3ConfidentialityClassificationValueSet.Value.valueOf(
                confidentiality.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .addAllAttester(attester.map { it.toProto() })
        .setCustodian(custodian.toProto())
        .addAllRelatesTo(relatesTo.map { it.toProto() })
        .addAllEvent(event.map { it.toProto() })
        .addAllSection(section.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Composition.CompositionAttesterComponent.toProto():
    Composition.Attester {
    val protoValue =
      Composition.Attester.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setMode(
          Composition.Attester.ModeCode.newBuilder()
            .setValue(
              CompositionAttestationModeCode.Value.valueOf(
                mode.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setTime(timeElement.toProto())
        .setParty(party.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Composition.CompositionRelatesToComponent.toProto():
    Composition.RelatesTo {
    val protoValue =
      Composition.RelatesTo.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(
          Composition.RelatesTo.CodeType.newBuilder()
            .setValue(
              DocumentRelationshipTypeCode.Value.valueOf(
                code.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setTarget(target.compositionRelatesToTargetToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Composition.CompositionEventComponent.toProto():
    Composition.Event {
    val protoValue =
      Composition.Event.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllCode(code.map { it.toProto() })
        .setPeriod(period.toProto())
        .addAllDetail(detail.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Composition.SectionComponent.toProto(): Composition.Section {
    val protoValue =
      Composition.Section.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setTitle(titleElement.toProto())
        .setCode(code.toProto())
        .addAllAuthor(author.map { it.toProto() })
        .setFocus(focus.toProto())
        .setText(text.toProto())
        .setMode(
          Composition.Section.ModeCode.newBuilder()
            .setValue(ListModeCode.Value.valueOf(mode.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .setOrderedBy(orderedBy.toProto())
        .addAllEntry(entry.map { it.toProto() })
        .setEmptyReason(emptyReason.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun Composition.Attester.toHapi():
    org.hl7.fhir.r4.model.Composition.CompositionAttesterComponent {
    val hapiValue = org.hl7.fhir.r4.model.Composition.CompositionAttesterComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setMode(
      org.hl7.fhir.r4.model.Composition.CompositionAttestationMode.valueOf(
        mode.value.name.replace("_", "")
      )
    )
    hapiValue.setTimeElement(time.toHapi())
    hapiValue.setParty(party.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Composition.RelatesTo.toHapi():
    org.hl7.fhir.r4.model.Composition.CompositionRelatesToComponent {
    val hapiValue = org.hl7.fhir.r4.model.Composition.CompositionRelatesToComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(
      org.hl7.fhir.r4.model.Composition.DocumentRelationshipType.valueOf(
        code.value.name.replace("_", "")
      )
    )
    hapiValue.setTarget(target.compositionRelatesToTargetToHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Composition.Event.toHapi():
    org.hl7.fhir.r4.model.Composition.CompositionEventComponent {
    val hapiValue = org.hl7.fhir.r4.model.Composition.CompositionEventComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(codeList.map { it.toHapi() })
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setDetail(detailList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Composition.Section.toHapi(): org.hl7.fhir.r4.model.Composition.SectionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Composition.SectionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setAuthor(authorList.map { it.toHapi() })
    hapiValue.setFocus(focus.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setMode(
      org.hl7.fhir.r4.model.Composition.SectionMode.valueOf(mode.value.name.replace("_", ""))
    )
    hapiValue.setOrderedBy(orderedBy.toHapi())
    hapiValue.setEntry(entryList.map { it.toHapi() })
    hapiValue.setEmptyReason(emptyReason.toHapi())
    return hapiValue
  }
}
