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
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
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
import com.google.fhir.r4.core.SubstanceSourceMaterial
import com.google.fhir.r4.core.SubstanceSourceMaterial.Organism
import kotlin.jvm.JvmStatic

public object SubstanceSourceMaterialConverter {
  @JvmStatic
  public fun SubstanceSourceMaterial.toHapi(): org.hl7.fhir.r4.model.SubstanceSourceMaterial {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceSourceMaterial()
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
    if (hasSourceMaterialClass()) {
      hapiValue.setSourceMaterialClass(sourceMaterialClass.toHapi())
    }
    if (hasSourceMaterialType()) {
      hapiValue.setSourceMaterialType(sourceMaterialType.toHapi())
    }
    if (hasSourceMaterialState()) {
      hapiValue.setSourceMaterialState(sourceMaterialState.toHapi())
    }
    if (hasOrganismId()) {
      hapiValue.setOrganismId(organismId.toHapi())
    }
    if (hasOrganismName()) {
      hapiValue.setOrganismNameElement(organismName.toHapi())
    }
    if (parentSubstanceIdCount > 0) {
      hapiValue.setParentSubstanceId(parentSubstanceIdList.map { it.toHapi() })
    }
    if (parentSubstanceNameCount > 0) {
      hapiValue.setParentSubstanceName(parentSubstanceNameList.map { it.toHapi() })
    }
    if (countryOfOriginCount > 0) {
      hapiValue.setCountryOfOrigin(countryOfOriginList.map { it.toHapi() })
    }
    if (geographicalLocationCount > 0) {
      hapiValue.setGeographicalLocation(geographicalLocationList.map { it.toHapi() })
    }
    if (hasDevelopmentStage()) {
      hapiValue.setDevelopmentStage(developmentStage.toHapi())
    }
    if (fractionDescriptionCount > 0) {
      hapiValue.setFractionDescription(fractionDescriptionList.map { it.toHapi() })
    }
    if (hasOrganism()) {
      hapiValue.setOrganism(organism.toHapi())
    }
    if (partDescriptionCount > 0) {
      hapiValue.setPartDescription(partDescriptionList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.toProto(): SubstanceSourceMaterial {
    val protoValue = SubstanceSourceMaterial.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasSourceMaterialClass()) {
      protoValue.setSourceMaterialClass(sourceMaterialClass.toProto())
    }
    if (hasSourceMaterialType()) {
      protoValue.setSourceMaterialType(sourceMaterialType.toProto())
    }
    if (hasSourceMaterialState()) {
      protoValue.setSourceMaterialState(sourceMaterialState.toProto())
    }
    if (hasOrganismId()) {
      protoValue.setOrganismId(organismId.toProto())
    }
    if (hasOrganismName()) {
      protoValue.setOrganismName(organismNameElement.toProto())
    }
    if (hasParentSubstanceId()) {
      protoValue.addAllParentSubstanceId(parentSubstanceId.map { it.toProto() })
    }
    if (hasParentSubstanceName()) {
      protoValue.addAllParentSubstanceName(parentSubstanceName.map { it.toProto() })
    }
    if (hasCountryOfOrigin()) {
      protoValue.addAllCountryOfOrigin(countryOfOrigin.map { it.toProto() })
    }
    if (hasGeographicalLocation()) {
      protoValue.addAllGeographicalLocation(geographicalLocation.map { it.toProto() })
    }
    if (hasDevelopmentStage()) {
      protoValue.setDevelopmentStage(developmentStage.toProto())
    }
    if (hasFractionDescription()) {
      protoValue.addAllFractionDescription(fractionDescription.map { it.toProto() })
    }
    if (hasOrganism()) {
      protoValue.setOrganism(organism.toProto())
    }
    if (hasPartDescription()) {
      protoValue.addAllPartDescription(partDescription.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialFractionDescriptionComponent.toProto():
    SubstanceSourceMaterial.FractionDescription {
    val protoValue =
      SubstanceSourceMaterial.FractionDescription.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasFraction()) {
      protoValue.setFraction(fractionElement.toProto())
    }
    if (hasMaterialType()) {
      protoValue.setMaterialType(materialType.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismComponent.toProto():
    SubstanceSourceMaterial.Organism {
    val protoValue =
      SubstanceSourceMaterial.Organism.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasFamily()) {
      protoValue.setFamily(family.toProto())
    }
    if (hasGenus()) {
      protoValue.setGenus(genus.toProto())
    }
    if (hasSpecies()) {
      protoValue.setSpecies(species.toProto())
    }
    if (hasIntraspecificType()) {
      protoValue.setIntraspecificType(intraspecificType.toProto())
    }
    if (hasIntraspecificDescription()) {
      protoValue.setIntraspecificDescription(intraspecificDescriptionElement.toProto())
    }
    if (hasAuthor()) {
      protoValue.addAllAuthor(author.map { it.toProto() })
    }
    if (hasHybrid()) {
      protoValue.setHybrid(hybrid.toProto())
    }
    if (hasOrganismGeneral()) {
      protoValue.setOrganismGeneral(organismGeneral.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismAuthorComponent.toProto():
    SubstanceSourceMaterial.Organism.Author {
    val protoValue =
      SubstanceSourceMaterial.Organism.Author.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAuthorType()) {
      protoValue.setAuthorType(authorType.toProto())
    }
    if (hasAuthorDescription()) {
      protoValue.setAuthorDescription(authorDescriptionElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismHybridComponent.toProto():
    SubstanceSourceMaterial.Organism.Hybrid {
    val protoValue =
      SubstanceSourceMaterial.Organism.Hybrid.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasMaternalOrganismId()) {
      protoValue.setMaternalOrganismId(maternalOrganismIdElement.toProto())
    }
    if (hasMaternalOrganismName()) {
      protoValue.setMaternalOrganismName(maternalOrganismNameElement.toProto())
    }
    if (hasPaternalOrganismId()) {
      protoValue.setPaternalOrganismId(paternalOrganismIdElement.toProto())
    }
    if (hasPaternalOrganismName()) {
      protoValue.setPaternalOrganismName(paternalOrganismNameElement.toProto())
    }
    if (hasHybridType()) {
      protoValue.setHybridType(hybridType.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismOrganismGeneralComponent.toProto():
    SubstanceSourceMaterial.Organism.OrganismGeneral {
    val protoValue =
      SubstanceSourceMaterial.Organism.OrganismGeneral.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasKingdom()) {
      protoValue.setKingdom(kingdom.toProto())
    }
    if (hasPhylum()) {
      protoValue.setPhylum(phylum.toProto())
    }
    if (hasClass_()) {
      protoValue.setClassValue(class_.toProto())
    }
    if (hasOrder()) {
      protoValue.setOrder(order.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialPartDescriptionComponent.toProto():
    SubstanceSourceMaterial.PartDescription {
    val protoValue =
      SubstanceSourceMaterial.PartDescription.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPart()) {
      protoValue.setPart(part.toProto())
    }
    if (hasPartLocation()) {
      protoValue.setPartLocation(partLocation.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun SubstanceSourceMaterial.FractionDescription.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialFractionDescriptionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial
        .SubstanceSourceMaterialFractionDescriptionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasFraction()) {
      hapiValue.setFractionElement(fraction.toHapi())
    }
    if (hasMaterialType()) {
      hapiValue.setMaterialType(materialType.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSourceMaterial.Organism.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasFamily()) {
      hapiValue.setFamily(family.toHapi())
    }
    if (hasGenus()) {
      hapiValue.setGenus(genus.toHapi())
    }
    if (hasSpecies()) {
      hapiValue.setSpecies(species.toHapi())
    }
    if (hasIntraspecificType()) {
      hapiValue.setIntraspecificType(intraspecificType.toHapi())
    }
    if (hasIntraspecificDescription()) {
      hapiValue.setIntraspecificDescriptionElement(intraspecificDescription.toHapi())
    }
    if (authorCount > 0) {
      hapiValue.setAuthor(authorList.map { it.toHapi() })
    }
    if (hasHybrid()) {
      hapiValue.setHybrid(hybrid.toHapi())
    }
    if (hasOrganismGeneral()) {
      hapiValue.setOrganismGeneral(organismGeneral.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSourceMaterial.Organism.Author.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismAuthorComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismAuthorComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasAuthorType()) {
      hapiValue.setAuthorType(authorType.toHapi())
    }
    if (hasAuthorDescription()) {
      hapiValue.setAuthorDescriptionElement(authorDescription.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSourceMaterial.Organism.Hybrid.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismHybridComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismHybridComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasMaternalOrganismId()) {
      hapiValue.setMaternalOrganismIdElement(maternalOrganismId.toHapi())
    }
    if (hasMaternalOrganismName()) {
      hapiValue.setMaternalOrganismNameElement(maternalOrganismName.toHapi())
    }
    if (hasPaternalOrganismId()) {
      hapiValue.setPaternalOrganismIdElement(paternalOrganismId.toHapi())
    }
    if (hasPaternalOrganismName()) {
      hapiValue.setPaternalOrganismNameElement(paternalOrganismName.toHapi())
    }
    if (hasHybridType()) {
      hapiValue.setHybridType(hybridType.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSourceMaterial.Organism.OrganismGeneral.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismOrganismGeneralComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial
        .SubstanceSourceMaterialOrganismOrganismGeneralComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasKingdom()) {
      hapiValue.setKingdom(kingdom.toHapi())
    }
    if (hasPhylum()) {
      hapiValue.setPhylum(phylum.toHapi())
    }
    if (hasClassValue()) {
      hapiValue.setClass_(classValue.toHapi())
    }
    if (hasOrder()) {
      hapiValue.setOrder(order.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSourceMaterial.PartDescription.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialPartDescriptionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial
        .SubstanceSourceMaterialPartDescriptionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasPart()) {
      hapiValue.setPart(part.toHapi())
    }
    if (hasPartLocation()) {
      hapiValue.setPartLocation(partLocation.toHapi())
    }
    return hapiValue
  }
}
