package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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

public object SubstanceSourceMaterialConverter {
  public fun SubstanceSourceMaterial.toHapi(): org.hl7.fhir.r4.model.SubstanceSourceMaterial {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceSourceMaterial()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSourceMaterialClass(sourceMaterialClass.toHapi())
    hapiValue.setSourceMaterialType(sourceMaterialType.toHapi())
    hapiValue.setSourceMaterialState(sourceMaterialState.toHapi())
    hapiValue.setOrganismId(organismId.toHapi())
    hapiValue.setOrganismNameElement(organismName.toHapi())
    hapiValue.setParentSubstanceId(parentSubstanceIdList.map{it.toHapi()})
    hapiValue.setParentSubstanceName(parentSubstanceNameList.map{it.toHapi()})
    hapiValue.setCountryOfOrigin(countryOfOriginList.map{it.toHapi()})
    hapiValue.setGeographicalLocation(geographicalLocationList.map{it.toHapi()})
    hapiValue.setDevelopmentStage(developmentStage.toHapi())
    hapiValue.setFractionDescription(fractionDescriptionList.map{it.toHapi()})
    hapiValue.setOrganism(organism.toHapi())
    hapiValue.setPartDescription(partDescriptionList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.toProto(): SubstanceSourceMaterial {
    val protoValue = SubstanceSourceMaterial.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSourceMaterialClass(sourceMaterialClass.toProto())
    .setSourceMaterialType(sourceMaterialType.toProto())
    .setSourceMaterialState(sourceMaterialState.toProto())
    .setOrganismId(organismId.toProto())
    .setOrganismName(organismNameElement.toProto())
    .addAllParentSubstanceId(parentSubstanceId.map{it.toProto()})
    .addAllParentSubstanceName(parentSubstanceName.map{it.toProto()})
    .addAllCountryOfOrigin(countryOfOrigin.map{it.toProto()})
    .addAllGeographicalLocation(geographicalLocation.map{it.toProto()})
    .setDevelopmentStage(developmentStage.toProto())
    .addAllFractionDescription(fractionDescription.map{it.toProto()})
    .setOrganism(organism.toProto())
    .addAllPartDescription(partDescription.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialFractionDescriptionComponent.toProto():
      SubstanceSourceMaterial.FractionDescription {
    val protoValue = SubstanceSourceMaterial.FractionDescription.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setFraction(fractionElement.toProto())
    .setMaterialType(materialType.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismComponent.toProto():
      SubstanceSourceMaterial.Organism {
    val protoValue = SubstanceSourceMaterial.Organism.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setFamily(family.toProto())
    .setGenus(genus.toProto())
    .setSpecies(species.toProto())
    .setIntraspecificType(intraspecificType.toProto())
    .setIntraspecificDescription(intraspecificDescriptionElement.toProto())
    .addAllAuthor(author.map{it.toProto()})
    .setHybrid(hybrid.toProto())
    .setOrganismGeneral(organismGeneral.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismAuthorComponent.toProto():
      SubstanceSourceMaterial.Organism.Author {
    val protoValue = SubstanceSourceMaterial.Organism.Author.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setAuthorType(authorType.toProto())
    .setAuthorDescription(authorDescriptionElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismHybridComponent.toProto():
      SubstanceSourceMaterial.Organism.Hybrid {
    val protoValue = SubstanceSourceMaterial.Organism.Hybrid.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setMaternalOrganismId(maternalOrganismIdElement.toProto())
    .setMaternalOrganismName(maternalOrganismNameElement.toProto())
    .setPaternalOrganismId(paternalOrganismIdElement.toProto())
    .setPaternalOrganismName(paternalOrganismNameElement.toProto())
    .setHybridType(hybridType.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismOrganismGeneralComponent.toProto():
      SubstanceSourceMaterial.Organism.OrganismGeneral {
    val protoValue = SubstanceSourceMaterial.Organism.OrganismGeneral.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setKingdom(kingdom.toProto())
    .setPhylum(phylum.toProto())
    .setClass(class.toProto())
    .setOrder(order.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialPartDescriptionComponent.toProto():
      SubstanceSourceMaterial.PartDescription {
    val protoValue = SubstanceSourceMaterial.PartDescription.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setPart(part.toProto())
    .setPartLocation(partLocation.toProto())
    .build()
    return protoValue
  }

  public fun SubstanceSourceMaterial.FractionDescription.toHapi():
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialFractionDescriptionComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialFractionDescriptionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setFractionElement(fraction.toHapi())
    hapiValue.setMaterialType(materialType.toHapi())
    return hapiValue
  }

  public fun SubstanceSourceMaterial.Organism.toHapi():
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setFamily(family.toHapi())
    hapiValue.setGenus(genus.toHapi())
    hapiValue.setSpecies(species.toHapi())
    hapiValue.setIntraspecificType(intraspecificType.toHapi())
    hapiValue.setIntraspecificDescriptionElement(intraspecificDescription.toHapi())
    hapiValue.setAuthor(authorList.map{it.toHapi()})
    hapiValue.setHybrid(hybrid.toHapi())
    hapiValue.setOrganismGeneral(organismGeneral.toHapi())
    return hapiValue
  }

  public fun SubstanceSourceMaterial.Organism.Author.toHapi():
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismAuthorComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismAuthorComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setAuthorType(authorType.toHapi())
    hapiValue.setAuthorDescriptionElement(authorDescription.toHapi())
    return hapiValue
  }

  public fun SubstanceSourceMaterial.Organism.Hybrid.toHapi():
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismHybridComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismHybridComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setMaternalOrganismIdElement(maternalOrganismId.toHapi())
    hapiValue.setMaternalOrganismNameElement(maternalOrganismName.toHapi())
    hapiValue.setPaternalOrganismIdElement(paternalOrganismId.toHapi())
    hapiValue.setPaternalOrganismNameElement(paternalOrganismName.toHapi())
    hapiValue.setHybridType(hybridType.toHapi())
    return hapiValue
  }

  public fun SubstanceSourceMaterial.Organism.OrganismGeneral.toHapi():
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismOrganismGeneralComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialOrganismOrganismGeneralComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setKingdom(kingdom.toHapi())
    hapiValue.setPhylum(phylum.toHapi())
    hapiValue.setClass(class.toHapi())
    hapiValue.setOrder(order.toHapi())
    return hapiValue
  }

  public fun SubstanceSourceMaterial.PartDescription.toHapi():
      org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialPartDescriptionComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSourceMaterial.SubstanceSourceMaterialPartDescriptionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setPart(part.toHapi())
    hapiValue.setPartLocation(partLocation.toHapi())
    return hapiValue
  }
}
