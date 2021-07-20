package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SubstanceSpecification
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object SubstanceSpecificationConverter {
  public fun SubstanceSpecification.Moiety.AmountX.substanceSpecificationMoietyAmountToHapi():
      Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("SubstanceSpecification.moiety.amount[x]")
  }

  public fun Type.substanceSpecificationMoietyAmountToProto():
      SubstanceSpecification.Moiety.AmountX {
    val protoValue = SubstanceSpecification.Moiety.AmountX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  public
      fun SubstanceSpecification.Property.DefiningSubstanceX.substanceSpecificationPropertyDefiningSubstanceToHapi():
      Type {
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("SubstanceSpecification.property.definingSubstance[x]")
  }

  public fun Type.substanceSpecificationPropertyDefiningSubstanceToProto():
      SubstanceSpecification.Property.DefiningSubstanceX {
    val protoValue = SubstanceSpecification.Property.DefiningSubstanceX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  public fun SubstanceSpecification.Property.AmountX.substanceSpecificationPropertyAmountToHapi():
      Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("SubstanceSpecification.property.amount[x]")
  }

  public fun Type.substanceSpecificationPropertyAmountToProto():
      SubstanceSpecification.Property.AmountX {
    val protoValue = SubstanceSpecification.Property.AmountX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  public
      fun SubstanceSpecification.Relationship.SubstanceX.substanceSpecificationRelationshipSubstanceToHapi():
      Type {
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("SubstanceSpecification.relationship.substance[x]")
  }

  public fun Type.substanceSpecificationRelationshipSubstanceToProto():
      SubstanceSpecification.Relationship.SubstanceX {
    val protoValue = SubstanceSpecification.Relationship.SubstanceX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  public
      fun SubstanceSpecification.Relationship.AmountX.substanceSpecificationRelationshipAmountToHapi():
      Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType ) {
      return (this.getRatio()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("SubstanceSpecification.relationship.amount[x]")
  }

  public fun Type.substanceSpecificationRelationshipAmountToProto():
      SubstanceSpecification.Relationship.AmountX {
    val protoValue = SubstanceSpecification.Relationship.AmountX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  public fun SubstanceSpecification.toHapi(): org.hl7.fhir.r4.model.SubstanceSpecification {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceSpecification()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setStatus(status.toHapi())
    hapiValue.setDomain(domain.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setSource(sourceList.map{it.toHapi()})
    hapiValue.setCommentElement(comment.toHapi())
    hapiValue.setMoiety(moietyList.map{it.toHapi()})
    hapiValue.setProperty(propertyList.map{it.toHapi()})
    hapiValue.setReferenceInformation(referenceInformation.toHapi())
    hapiValue.setStructure(structure.toHapi())
    hapiValue.setCode(codeList.map{it.toHapi()})
    hapiValue.setName(nameList.map{it.toHapi()})
    hapiValue.setMolecularWeight(molecularWeightList.map{it.toHapi()})
    hapiValue.setRelationship(relationshipList.map{it.toHapi()})
    hapiValue.setNucleicAcid(nucleicAcid.toHapi())
    hapiValue.setPolymer(polymer.toHapi())
    hapiValue.setProtein(protein.toHapi())
    hapiValue.setSourceMaterial(sourceMaterial.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.SubstanceSpecification.toProto(): SubstanceSpecification {
    val protoValue = SubstanceSpecification.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setIdentifier(identifier.toProto())
    .setType(type.toProto())
    .setStatus(status.toProto())
    .setDomain(domain.toProto())
    .setDescription(descriptionElement.toProto())
    .addAllSource(source.map{it.toProto()})
    .setComment(commentElement.toProto())
    .addAllMoiety(moiety.map{it.toProto()})
    .addAllProperty(property.map{it.toProto()})
    .setReferenceInformation(referenceInformation.toProto())
    .setStructure(structure.toProto())
    .addAllCode(code.map{it.toProto()})
    .addAllName(name.map{it.toProto()})
    .addAllMolecularWeight(molecularWeight.map{it.toProto()})
    .addAllRelationship(relationship.map{it.toProto()})
    .setNucleicAcid(nucleicAcid.toProto())
    .setPolymer(polymer.toProto())
    .setProtein(protein.toProto())
    .setSourceMaterial(sourceMaterial.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationMoietyComponent.toProto():
      SubstanceSpecification.Moiety {
    val protoValue = SubstanceSpecification.Moiety.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setRole(role.toProto())
    .setIdentifier(identifier.toProto())
    .setName(nameElement.toProto())
    .setStereochemistry(stereochemistry.toProto())
    .setOpticalActivity(opticalActivity.toProto())
    .setMolecularFormula(molecularFormulaElement.toProto())
    .setAmount(amount.substanceSpecificationMoietyAmountToProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationPropertyComponent.toProto():
      SubstanceSpecification.Property {
    val protoValue = SubstanceSpecification.Property.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCategory(category.toProto())
    .setCode(code.toProto())
    .setParameters(parametersElement.toProto())
    .setDefiningSubstance(definingSubstance.substanceSpecificationPropertyDefiningSubstanceToProto())
    .setAmount(amount.substanceSpecificationPropertyAmountToProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureComponent.toProto():
      SubstanceSpecification.Structure {
    val protoValue = SubstanceSpecification.Structure.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setStereochemistry(stereochemistry.toProto())
    .setOpticalActivity(opticalActivity.toProto())
    .setMolecularFormula(molecularFormulaElement.toProto())
    .setMolecularFormulaByMoiety(molecularFormulaByMoietyElement.toProto())
    .addAllIsotope(isotope.map{it.toProto()})
    .setMolecularWeight(molecularWeight.toProto())
    .addAllSource(source.map{it.toProto()})
    .addAllRepresentation(representation.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeComponent.toProto():
      SubstanceSpecification.Structure.Isotope {
    val protoValue = SubstanceSpecification.Structure.Isotope.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setIdentifier(identifier.toProto())
    .setName(name.toProto())
    .setSubstitution(substitution.toProto())
    .setHalfLife(halfLife.toProto())
    .setMolecularWeight(molecularWeight.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeMolecularWeightComponent.toProto():
      SubstanceSpecification.Structure.Isotope.MolecularWeight {
    val protoValue = SubstanceSpecification.Structure.Isotope.MolecularWeight.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setMethod(method.toProto())
    .setType(type.toProto())
    .setAmount(amount.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureRepresentationComponent.toProto():
      SubstanceSpecification.Structure.Representation {
    val protoValue = SubstanceSpecification.Structure.Representation.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setRepresentation(representationElement.toProto())
    .setAttachment(attachment.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationCodeComponent.toProto():
      SubstanceSpecification.Code {
    val protoValue = SubstanceSpecification.Code.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(code.toProto())
    .setStatus(status.toProto())
    .setStatusDate(statusDateElement.toProto())
    .setComment(commentElement.toProto())
    .addAllSource(source.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameComponent.toProto():
      SubstanceSpecification.Name {
    val protoValue = SubstanceSpecification.Name.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setType(type.toProto())
    .setStatus(status.toProto())
    .setPreferred(preferredElement.toProto())
    .addAllLanguage(language.map{it.toProto()})
    .addAllDomain(domain.map{it.toProto()})
    .addAllJurisdiction(jurisdiction.map{it.toProto()})
    .addAllSynonym(synonym.map{it.toProto()})
    .addAllTranslation(translation.map{it.toProto()})
    .addAllOfficial(official.map{it.toProto()})
    .addAllSource(source.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameOfficialComponent.toProto():
      SubstanceSpecification.Name.Official {
    val protoValue = SubstanceSpecification.Name.Official.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setAuthority(authority.toProto())
    .setStatus(status.toProto())
    .setDate(dateElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationRelationshipComponent.toProto():
      SubstanceSpecification.Relationship {
    val protoValue = SubstanceSpecification.Relationship.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSubstance(substance.substanceSpecificationRelationshipSubstanceToProto())
    .setRelationship(relationship.toProto())
    .setIsDefining(isDefiningElement.toProto())
    .setAmount(amount.substanceSpecificationRelationshipAmountToProto())
    .setAmountRatioLowLimit(amountRatioLowLimit.toProto())
    .setAmountType(amountType.toProto())
    .addAllSource(source.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun SubstanceSpecification.Moiety.toHapi():
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationMoietyComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationMoietyComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setRole(role.toHapi())
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setStereochemistry(stereochemistry.toHapi())
    hapiValue.setOpticalActivity(opticalActivity.toHapi())
    hapiValue.setMolecularFormulaElement(molecularFormula.toHapi())
    hapiValue.setAmount(amount.substanceSpecificationMoietyAmountToHapi())
    return hapiValue
  }

  public fun SubstanceSpecification.Property.toHapi():
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationPropertyComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationPropertyComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCategory(category.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setParametersElement(parameters.toHapi())
    hapiValue.setDefiningSubstance(definingSubstance.substanceSpecificationPropertyDefiningSubstanceToHapi())
    hapiValue.setAmount(amount.substanceSpecificationPropertyAmountToHapi())
    return hapiValue
  }

  public fun SubstanceSpecification.Structure.toHapi():
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setStereochemistry(stereochemistry.toHapi())
    hapiValue.setOpticalActivity(opticalActivity.toHapi())
    hapiValue.setMolecularFormulaElement(molecularFormula.toHapi())
    hapiValue.setMolecularFormulaByMoietyElement(molecularFormulaByMoiety.toHapi())
    hapiValue.setIsotope(isotopeList.map{it.toHapi()})
    hapiValue.setMolecularWeight(molecularWeight.toHapi())
    hapiValue.setSource(sourceList.map{it.toHapi()})
    hapiValue.setRepresentation(representationList.map{it.toHapi()})
    return hapiValue
  }

  public fun SubstanceSpecification.Structure.Isotope.toHapi():
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setName(name.toHapi())
    hapiValue.setSubstitution(substitution.toHapi())
    hapiValue.setHalfLife(halfLife.toHapi())
    hapiValue.setMolecularWeight(molecularWeight.toHapi())
    return hapiValue
  }

  public fun SubstanceSpecification.Structure.Isotope.MolecularWeight.toHapi():
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeMolecularWeightComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeMolecularWeightComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setMethod(method.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setAmount(amount.toHapi())
    return hapiValue
  }

  public fun SubstanceSpecification.Structure.Representation.toHapi():
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureRepresentationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureRepresentationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setRepresentationElement(representation.toHapi())
    hapiValue.setAttachment(attachment.toHapi())
    return hapiValue
  }

  public fun SubstanceSpecification.Code.toHapi():
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationCodeComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationCodeComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setStatus(status.toHapi())
    hapiValue.setStatusDateElement(statusDate.toHapi())
    hapiValue.setCommentElement(comment.toHapi())
    hapiValue.setSource(sourceList.map{it.toHapi()})
    return hapiValue
  }

  public fun SubstanceSpecification.Name.toHapi():
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setStatus(status.toHapi())
    hapiValue.setPreferredElement(preferred.toHapi())
    hapiValue.setLanguage(languageList.map{it.toHapi()})
    hapiValue.setDomain(domainList.map{it.toHapi()})
    hapiValue.setJurisdiction(jurisdictionList.map{it.toHapi()})
    hapiValue.setSynonym(synonymList.map{it.toHapi()})
    hapiValue.setTranslation(translationList.map{it.toHapi()})
    hapiValue.setOfficial(officialList.map{it.toHapi()})
    hapiValue.setSource(sourceList.map{it.toHapi()})
    return hapiValue
  }

  public fun SubstanceSpecification.Name.Official.toHapi():
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameOfficialComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameOfficialComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setAuthority(authority.toHapi())
    hapiValue.setStatus(status.toHapi())
    hapiValue.setDateElement(date.toHapi())
    return hapiValue
  }

  public fun SubstanceSpecification.Relationship.toHapi():
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationRelationshipComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationRelationshipComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSubstance(substance.substanceSpecificationRelationshipSubstanceToHapi())
    hapiValue.setRelationship(relationship.toHapi())
    hapiValue.setIsDefiningElement(isDefining.toHapi())
    hapiValue.setAmount(amount.substanceSpecificationRelationshipAmountToHapi())
    hapiValue.setAmountRatioLowLimit(amountRatioLowLimit.toHapi())
    hapiValue.setAmountType(amountType.toHapi())
    hapiValue.setSource(sourceList.map{it.toHapi()})
    return hapiValue
  }
}
