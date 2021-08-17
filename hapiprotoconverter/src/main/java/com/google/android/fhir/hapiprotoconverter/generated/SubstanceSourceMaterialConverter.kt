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

object SubstanceSourceMaterialConverter {
  fun SubstanceSourceMaterial.toHapi(): org.hl7.fhir.r4.model.SubstanceSourceMaterial {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceSourceMaterial()
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
    if (hasSourceMaterialClass()) {
      hapiValue.sourceMaterialClass = sourceMaterialClass.toHapi()
    }
    if (hasSourceMaterialType()) {
      hapiValue.sourceMaterialType = sourceMaterialType.toHapi()
    }
    if (hasSourceMaterialState()) {
      hapiValue.sourceMaterialState = sourceMaterialState.toHapi()
    }
    if (hasOrganismId()) {
      hapiValue.organismId = organismId.toHapi()
    }
    if (hasOrganismName()) {
      hapiValue.organismNameElement = organismName.toHapi()
    }
    if (parentSubstanceIdCount > 0) {
      hapiValue.parentSubstanceId = parentSubstanceIdList.map { it.toHapi() }
    }
    if (parentSubstanceNameCount > 0) {
      hapiValue.parentSubstanceName = parentSubstanceNameList.map { it.toHapi() }
    }
    if (countryOfOriginCount > 0) {
      hapiValue.countryOfOrigin = countryOfOriginList.map { it.toHapi() }
    }
    if (geographicalLocationCount > 0) {
      hapiValue.geographicalLocation = geographicalLocationList.map { it.toHapi() }
    }
    if (hasDevelopmentStage()) {
      hapiValue.developmentStage = developmentStage.toHapi()
    }
    if (fractionDescriptionCount > 0) {
      hapiValue.fractionDescription = fractionDescriptionList.map { it.toHapi() }
    }
    if (hasOrganism()) {
      hapiValue.organism = organism.toHapi()
    }
    if (partDescriptionCount > 0) {
      hapiValue.partDescription = partDescriptionList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.toProto(): SubstanceSourceMaterial {
    val protoValue = SubstanceSourceMaterial.newBuilder()
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
    if (hasSourceMaterialClass()) {
      protoValue.sourceMaterialClass = sourceMaterialClass.toProto()
    }
    if (hasSourceMaterialType()) {
      protoValue.sourceMaterialType = sourceMaterialType.toProto()
    }
    if (hasSourceMaterialState()) {
      protoValue.sourceMaterialState = sourceMaterialState.toProto()
    }
    if (hasOrganismId()) {
      protoValue.organismId = organismId.toProto()
    }
    if (hasOrganismName()) {
      protoValue.organismName = organismNameElement.toProto()
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
      protoValue.developmentStage = developmentStage.toProto()
    }
    if (hasFractionDescription()) {
      protoValue.addAllFractionDescription(fractionDescription.map { it.toProto() })
    }
    if (hasOrganism()) {
      protoValue.organism = organism.toProto()
    }
    if (hasPartDescription()) {
      protoValue.addAllPartDescription(partDescription.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialFractionDescriptionComponent.toProto():
    SubstanceSourceMaterial.FractionDescription {
    val protoValue = SubstanceSourceMaterial.FractionDescription.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasFraction()) {
      protoValue.fraction = fractionElement.toProto()
    }
    if (hasMaterialType()) {
      protoValue.materialType = materialType.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismComponent.toProto():
    SubstanceSourceMaterial.Organism {
    val protoValue = SubstanceSourceMaterial.Organism.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasFamily()) {
      protoValue.family = family.toProto()
    }
    if (hasGenus()) {
      protoValue.genus = genus.toProto()
    }
    if (hasSpecies()) {
      protoValue.species = species.toProto()
    }
    if (hasIntraspecificType()) {
      protoValue.intraspecificType = intraspecificType.toProto()
    }
    if (hasIntraspecificDescription()) {
      protoValue.intraspecificDescription = intraspecificDescriptionElement.toProto()
    }
    if (hasAuthor()) {
      protoValue.addAllAuthor(author.map { it.toProto() })
    }
    if (hasHybrid()) {
      protoValue.hybrid = hybrid.toProto()
    }
    if (hasOrganismGeneral()) {
      protoValue.organismGeneral = organismGeneral.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismAuthorComponent.toProto():
    SubstanceSourceMaterial.Organism.Author {
    val protoValue = SubstanceSourceMaterial.Organism.Author.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAuthorType()) {
      protoValue.authorType = authorType.toProto()
    }
    if (hasAuthorDescription()) {
      protoValue.authorDescription = authorDescriptionElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismHybridComponent.toProto():
    SubstanceSourceMaterial.Organism.Hybrid {
    val protoValue = SubstanceSourceMaterial.Organism.Hybrid.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasMaternalOrganismId()) {
      protoValue.maternalOrganismId = maternalOrganismIdElement.toProto()
    }
    if (hasMaternalOrganismName()) {
      protoValue.maternalOrganismName = maternalOrganismNameElement.toProto()
    }
    if (hasPaternalOrganismId()) {
      protoValue.paternalOrganismId = paternalOrganismIdElement.toProto()
    }
    if (hasPaternalOrganismName()) {
      protoValue.paternalOrganismName = paternalOrganismNameElement.toProto()
    }
    if (hasHybridType()) {
      protoValue.hybridType = hybridType.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismOrganismGeneralComponent.toProto():
    SubstanceSourceMaterial.Organism.OrganismGeneral {
    val protoValue = SubstanceSourceMaterial.Organism.OrganismGeneral.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasKingdom()) {
      protoValue.kingdom = kingdom.toProto()
    }
    if (hasPhylum()) {
      protoValue.phylum = phylum.toProto()
    }
    if (hasClass_()) {
      protoValue.classValue = class_.toProto()
    }
    if (hasOrder()) {
      protoValue.order = order.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialPartDescriptionComponent.toProto():
    SubstanceSourceMaterial.PartDescription {
    val protoValue = SubstanceSourceMaterial.PartDescription.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPart()) {
      protoValue.part = part.toProto()
    }
    if (hasPartLocation()) {
      protoValue.partLocation = partLocation.toProto()
    }
    return protoValue.build()
  }

  private fun SubstanceSourceMaterial.FractionDescription.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialFractionDescriptionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial
        .SubstanceSourceMaterialFractionDescriptionComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasFraction()) {
      hapiValue.fractionElement = fraction.toHapi()
    }
    if (hasMaterialType()) {
      hapiValue.materialType = materialType.toHapi()
    }
    return hapiValue
  }

  private fun SubstanceSourceMaterial.Organism.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasFamily()) {
      hapiValue.family = family.toHapi()
    }
    if (hasGenus()) {
      hapiValue.genus = genus.toHapi()
    }
    if (hasSpecies()) {
      hapiValue.species = species.toHapi()
    }
    if (hasIntraspecificType()) {
      hapiValue.intraspecificType = intraspecificType.toHapi()
    }
    if (hasIntraspecificDescription()) {
      hapiValue.intraspecificDescriptionElement = intraspecificDescription.toHapi()
    }
    if (authorCount > 0) {
      hapiValue.author = authorList.map { it.toHapi() }
    }
    if (hasHybrid()) {
      hapiValue.hybrid = hybrid.toHapi()
    }
    if (hasOrganismGeneral()) {
      hapiValue.organismGeneral = organismGeneral.toHapi()
    }
    return hapiValue
  }

  private fun SubstanceSourceMaterial.Organism.Author.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismAuthorComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismAuthorComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasAuthorType()) {
      hapiValue.authorType = authorType.toHapi()
    }
    if (hasAuthorDescription()) {
      hapiValue.authorDescriptionElement = authorDescription.toHapi()
    }
    return hapiValue
  }

  private fun SubstanceSourceMaterial.Organism.Hybrid.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismHybridComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismHybridComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasMaternalOrganismId()) {
      hapiValue.maternalOrganismIdElement = maternalOrganismId.toHapi()
    }
    if (hasMaternalOrganismName()) {
      hapiValue.maternalOrganismNameElement = maternalOrganismName.toHapi()
    }
    if (hasPaternalOrganismId()) {
      hapiValue.paternalOrganismIdElement = paternalOrganismId.toHapi()
    }
    if (hasPaternalOrganismName()) {
      hapiValue.paternalOrganismNameElement = paternalOrganismName.toHapi()
    }
    if (hasHybridType()) {
      hapiValue.hybridType = hybridType.toHapi()
    }
    return hapiValue
  }

  private fun SubstanceSourceMaterial.Organism.OrganismGeneral.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismOrganismGeneralComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial
        .SubstanceSourceMaterialOrganismOrganismGeneralComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasKingdom()) {
      hapiValue.kingdom = kingdom.toHapi()
    }
    if (hasPhylum()) {
      hapiValue.phylum = phylum.toHapi()
    }
    if (hasClassValue()) {
      hapiValue.class_ = classValue.toHapi()
    }
    if (hasOrder()) {
      hapiValue.order = order.toHapi()
    }
    return hapiValue
  }

  private fun SubstanceSourceMaterial.PartDescription.toHapi():
    org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialPartDescriptionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSourceMaterial
        .SubstanceSourceMaterialPartDescriptionComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasPart()) {
      hapiValue.part = part.toHapi()
    }
    if (hasPartLocation()) {
      hapiValue.partLocation = partLocation.toHapi()
    }
    return hapiValue
  }
}
