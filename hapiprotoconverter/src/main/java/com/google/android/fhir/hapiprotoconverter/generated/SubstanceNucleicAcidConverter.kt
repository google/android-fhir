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
import com.google.fhir.r4.core.SubstanceNucleicAcid
import com.google.fhir.r4.core.SubstanceNucleicAcid.Subunit

object SubstanceNucleicAcidConverter {
  fun SubstanceNucleicAcid.toHapi(): org.hl7.fhir.r4.model.SubstanceNucleicAcid {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceNucleicAcid()
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
    if (hasSequenceType()) {
      hapiValue.sequenceType = sequenceType.toHapi()
    }
    if (hasNumberOfSubunits()) {
      hapiValue.numberOfSubunitsElement = numberOfSubunits.toHapi()
    }
    if (hasAreaOfHybridisation()) {
      hapiValue.areaOfHybridisationElement = areaOfHybridisation.toHapi()
    }
    if (hasOligoNucleotideType()) {
      hapiValue.oligoNucleotideType = oligoNucleotideType.toHapi()
    }
    if (subunitCount > 0) {
      hapiValue.subunit = subunitList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.SubstanceNucleicAcid.toProto(): SubstanceNucleicAcid {
    val protoValue = SubstanceNucleicAcid.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasSequenceType()) {
      protoValue.sequenceType = sequenceType.toProto()
    }
    if (hasNumberOfSubunits()) {
      protoValue.numberOfSubunits = numberOfSubunitsElement.toProto()
    }
    if (hasAreaOfHybridisation()) {
      protoValue.areaOfHybridisation = areaOfHybridisationElement.toProto()
    }
    if (hasOligoNucleotideType()) {
      protoValue.oligoNucleotideType = oligoNucleotideType.toProto()
    }
    if (hasSubunit()) {
      protoValue.addAllSubunit(subunit.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitComponent.toProto():
    SubstanceNucleicAcid.Subunit {
    val protoValue =
      SubstanceNucleicAcid.Subunit.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSubunit()) {
      protoValue.subunit = subunitElement.toProto()
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasLength()) {
      protoValue.length = lengthElement.toProto()
    }
    if (hasSequenceAttachment()) {
      protoValue.sequenceAttachment = sequenceAttachment.toProto()
    }
    if (hasFivePrime()) {
      protoValue.fivePrime = fivePrime.toProto()
    }
    if (hasThreePrime()) {
      protoValue.threePrime = threePrime.toProto()
    }
    if (hasLinkage()) {
      protoValue.addAllLinkage(linkage.map { it.toProto() })
    }
    if (hasSugar()) {
      protoValue.addAllSugar(sugar.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitLinkageComponent.toProto():
    SubstanceNucleicAcid.Subunit.Linkage {
    val protoValue =
      SubstanceNucleicAcid.Subunit.Linkage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasConnectivity()) {
      protoValue.connectivity = connectivityElement.toProto()
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasResidueSite()) {
      protoValue.residueSite = residueSiteElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitSugarComponent.toProto():
    SubstanceNucleicAcid.Subunit.Sugar {
    val protoValue =
      SubstanceNucleicAcid.Subunit.Sugar.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasResidueSite()) {
      protoValue.residueSite = residueSiteElement.toProto()
    }
    return protoValue.build()
  }

  private fun SubstanceNucleicAcid.Subunit.toHapi():
    org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSubunit()) {
      hapiValue.subunitElement = subunit.toHapi()
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (hasLength()) {
      hapiValue.lengthElement = length.toHapi()
    }
    if (hasSequenceAttachment()) {
      hapiValue.sequenceAttachment = sequenceAttachment.toHapi()
    }
    if (hasFivePrime()) {
      hapiValue.fivePrime = fivePrime.toHapi()
    }
    if (hasThreePrime()) {
      hapiValue.threePrime = threePrime.toHapi()
    }
    if (linkageCount > 0) {
      hapiValue.linkage = linkageList.map { it.toHapi() }
    }
    if (sugarCount > 0) {
      hapiValue.sugar = sugarList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun SubstanceNucleicAcid.Subunit.Linkage.toHapi():
    org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitLinkageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitLinkageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasConnectivity()) {
      hapiValue.connectivityElement = connectivity.toHapi()
    }
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasResidueSite()) {
      hapiValue.residueSiteElement = residueSite.toHapi()
    }
    return hapiValue
  }

  private fun SubstanceNucleicAcid.Subunit.Sugar.toHapi():
    org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitSugarComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitSugarComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasResidueSite()) {
      hapiValue.residueSiteElement = residueSite.toHapi()
    }
    return hapiValue
  }
}
