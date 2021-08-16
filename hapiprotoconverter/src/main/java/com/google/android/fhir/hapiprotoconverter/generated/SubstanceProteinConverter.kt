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

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SubstanceProtein
import kotlin.jvm.JvmStatic

public object SubstanceProteinConverter {
  @JvmStatic
  public fun SubstanceProtein.toHapi(): org.hl7.fhir.r4.model.SubstanceProtein {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceProtein()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.setMeta(meta.toHapi())
    }
    if (hasImplicitRules()) {
      hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    }
    if (hasText()) {
      hapiValue.setText(text.toHapi())
    }
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSequenceType()) {
      hapiValue.setSequenceType(sequenceType.toHapi())
    }
    if (hasNumberOfSubunits()) {
      hapiValue.setNumberOfSubunitsElement(numberOfSubunits.toHapi())
    }
    if (disulfideLinkageCount > 0) {
      hapiValue.setDisulfideLinkage(disulfideLinkageList.map { it.toHapi() })
    }
    if (subunitCount > 0) {
      hapiValue.setSubunit(subunitList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.SubstanceProtein.toProto(): SubstanceProtein {
    val protoValue = SubstanceProtein.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.setMeta(meta.toProto())
    }
    if (hasImplicitRules()) {
      protoValue.setImplicitRules(implicitRulesElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(text.toProto())
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequenceType()) {
      protoValue.setSequenceType(sequenceType.toProto())
    }
    if (hasNumberOfSubunits()) {
      protoValue.setNumberOfSubunits(numberOfSubunitsElement.toProto())
    }
    if (hasDisulfideLinkage()) {
      protoValue.addAllDisulfideLinkage(disulfideLinkage.map { it.toProto() })
    }
    if (hasSubunit()) {
      protoValue.addAllSubunit(subunit.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceProtein.SubstanceProteinSubunitComponent.toProto():
    SubstanceProtein.Subunit {
    val protoValue = SubstanceProtein.Subunit.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSubunit()) {
      protoValue.setSubunit(subunitElement.toProto())
    }
    if (hasSequence()) {
      protoValue.setSequence(sequenceElement.toProto())
    }
    if (hasLength()) {
      protoValue.setLength(lengthElement.toProto())
    }
    if (hasSequenceAttachment()) {
      protoValue.setSequenceAttachment(sequenceAttachment.toProto())
    }
    if (hasNTerminalModificationId()) {
      protoValue.setNTerminalModificationId(nTerminalModificationId.toProto())
    }
    if (hasNTerminalModification()) {
      protoValue.setNTerminalModification(nTerminalModificationElement.toProto())
    }
    if (hasCTerminalModificationId()) {
      protoValue.setCTerminalModificationId(cTerminalModificationId.toProto())
    }
    if (hasCTerminalModification()) {
      protoValue.setCTerminalModification(cTerminalModificationElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun SubstanceProtein.Subunit.toHapi():
    org.hl7.fhir.r4.model.SubstanceProtein.SubstanceProteinSubunitComponent {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceProtein.SubstanceProteinSubunitComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSubunit()) {
      hapiValue.setSubunitElement(subunit.toHapi())
    }
    if (hasSequence()) {
      hapiValue.setSequenceElement(sequence.toHapi())
    }
    if (hasLength()) {
      hapiValue.setLengthElement(length.toHapi())
    }
    if (hasSequenceAttachment()) {
      hapiValue.setSequenceAttachment(sequenceAttachment.toHapi())
    }
    if (hasNTerminalModificationId()) {
      hapiValue.setNTerminalModificationId(nTerminalModificationId.toHapi())
    }
    if (hasNTerminalModification()) {
      hapiValue.setNTerminalModificationElement(nTerminalModification.toHapi())
    }
    if (hasCTerminalModificationId()) {
      hapiValue.setCTerminalModificationId(cTerminalModificationId.toHapi())
    }
    if (hasCTerminalModification()) {
      hapiValue.setCTerminalModificationElement(cTerminalModification.toHapi())
    }
    return hapiValue
  }
}
