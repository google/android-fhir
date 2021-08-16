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
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
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
import com.google.fhir.r4.core.SubstanceSpecification.Moiety
import com.google.fhir.r4.core.SubstanceSpecification.Name
import com.google.fhir.r4.core.SubstanceSpecification.Property
import com.google.fhir.r4.core.SubstanceSpecification.Relationship
import com.google.fhir.r4.core.SubstanceSpecification.Structure
import com.google.fhir.r4.core.SubstanceSpecification.Structure.Isotope
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object SubstanceSpecificationConverter {
  @JvmStatic
  private fun SubstanceSpecification.Moiety.AmountX.substanceSpecificationMoietyAmountToHapi():
    Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SubstanceSpecification.moiety.amount[x]")
  }

  @JvmStatic
  private fun Type.substanceSpecificationMoietyAmountToProto():
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

  @JvmStatic
  private fun SubstanceSpecification.Property.DefiningSubstanceX.substanceSpecificationPropertyDefiningSubstanceToHapi():
    Type {
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for SubstanceSpecification.property.definingSubstance[x]"
    )
  }

  @JvmStatic
  private fun Type.substanceSpecificationPropertyDefiningSubstanceToProto():
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

  @JvmStatic
  private fun SubstanceSpecification.Property.AmountX.substanceSpecificationPropertyAmountToHapi():
    Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SubstanceSpecification.property.amount[x]")
  }

  @JvmStatic
  private fun Type.substanceSpecificationPropertyAmountToProto():
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

  @JvmStatic
  private fun SubstanceSpecification.Relationship.SubstanceX.substanceSpecificationRelationshipSubstanceToHapi():
    Type {
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for SubstanceSpecification.relationship.substance[x]"
    )
  }

  @JvmStatic
  private fun Type.substanceSpecificationRelationshipSubstanceToProto():
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

  @JvmStatic
  private fun SubstanceSpecification.Relationship.AmountX.substanceSpecificationRelationshipAmountToHapi():
    Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType) {
      return (this.getRatio()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SubstanceSpecification.relationship.amount[x]")
  }

  @JvmStatic
  private fun Type.substanceSpecificationRelationshipAmountToProto():
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

  @JvmStatic
  public fun SubstanceSpecification.toHapi(): org.hl7.fhir.r4.model.SubstanceSpecification {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceSpecification()
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
    if (hasIdentifier()) {
      hapiValue.setIdentifier(identifier.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasStatus()) {
      hapiValue.setStatus(status.toHapi())
    }
    if (hasDomain()) {
      hapiValue.setDomain(domain.toHapi())
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (sourceCount > 0) {
      hapiValue.setSource(sourceList.map { it.toHapi() })
    }
    if (hasComment()) {
      hapiValue.setCommentElement(comment.toHapi())
    }
    if (moietyCount > 0) {
      hapiValue.setMoiety(moietyList.map { it.toHapi() })
    }
    if (propertyCount > 0) {
      hapiValue.setProperty(propertyList.map { it.toHapi() })
    }
    if (hasReferenceInformation()) {
      hapiValue.setReferenceInformation(referenceInformation.toHapi())
    }
    if (hasStructure()) {
      hapiValue.setStructure(structure.toHapi())
    }
    if (codeCount > 0) {
      hapiValue.setCode(codeList.map { it.toHapi() })
    }
    if (nameCount > 0) {
      hapiValue.setName(nameList.map { it.toHapi() })
    }
    if (relationshipCount > 0) {
      hapiValue.setRelationship(relationshipList.map { it.toHapi() })
    }
    if (hasNucleicAcid()) {
      hapiValue.setNucleicAcid(nucleicAcid.toHapi())
    }
    if (hasPolymer()) {
      hapiValue.setPolymer(polymer.toHapi())
    }
    if (hasProtein()) {
      hapiValue.setProtein(protein.toHapi())
    }
    if (hasSourceMaterial()) {
      hapiValue.setSourceMaterial(sourceMaterial.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.SubstanceSpecification.toProto(): SubstanceSpecification {
    val protoValue = SubstanceSpecification.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasIdentifier()) {
      protoValue.setIdentifier(identifier.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasStatus()) {
      protoValue.setStatus(status.toProto())
    }
    if (hasDomain()) {
      protoValue.setDomain(domain.toProto())
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    if (hasComment()) {
      protoValue.setComment(commentElement.toProto())
    }
    if (hasMoiety()) {
      protoValue.addAllMoiety(moiety.map { it.toProto() })
    }
    if (hasProperty()) {
      protoValue.addAllProperty(property.map { it.toProto() })
    }
    if (hasReferenceInformation()) {
      protoValue.setReferenceInformation(referenceInformation.toProto())
    }
    if (hasStructure()) {
      protoValue.setStructure(structure.toProto())
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.addAllName(name.map { it.toProto() })
    }
    if (hasRelationship()) {
      protoValue.addAllRelationship(relationship.map { it.toProto() })
    }
    if (hasNucleicAcid()) {
      protoValue.setNucleicAcid(nucleicAcid.toProto())
    }
    if (hasPolymer()) {
      protoValue.setPolymer(polymer.toProto())
    }
    if (hasProtein()) {
      protoValue.setProtein(protein.toProto())
    }
    if (hasSourceMaterial()) {
      protoValue.setSourceMaterial(sourceMaterial.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationMoietyComponent.toProto():
    SubstanceSpecification.Moiety {
    val protoValue =
      SubstanceSpecification.Moiety.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRole()) {
      protoValue.setRole(role.toProto())
    }
    if (hasIdentifier()) {
      protoValue.setIdentifier(identifier.toProto())
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasStereochemistry()) {
      protoValue.setStereochemistry(stereochemistry.toProto())
    }
    if (hasOpticalActivity()) {
      protoValue.setOpticalActivity(opticalActivity.toProto())
    }
    if (hasMolecularFormula()) {
      protoValue.setMolecularFormula(molecularFormulaElement.toProto())
    }
    if (hasAmount()) {
      protoValue.setAmount(amount.substanceSpecificationMoietyAmountToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationPropertyComponent.toProto():
    SubstanceSpecification.Property {
    val protoValue =
      SubstanceSpecification.Property.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCategory()) {
      protoValue.setCategory(category.toProto())
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    if (hasParameters()) {
      protoValue.setParameters(parametersElement.toProto())
    }
    if (hasDefiningSubstance()) {
      protoValue.setDefiningSubstance(
        definingSubstance.substanceSpecificationPropertyDefiningSubstanceToProto()
      )
    }
    if (hasAmount()) {
      protoValue.setAmount(amount.substanceSpecificationPropertyAmountToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureComponent.toProto():
    SubstanceSpecification.Structure {
    val protoValue =
      SubstanceSpecification.Structure.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasStereochemistry()) {
      protoValue.setStereochemistry(stereochemistry.toProto())
    }
    if (hasOpticalActivity()) {
      protoValue.setOpticalActivity(opticalActivity.toProto())
    }
    if (hasMolecularFormula()) {
      protoValue.setMolecularFormula(molecularFormulaElement.toProto())
    }
    if (hasMolecularFormulaByMoiety()) {
      protoValue.setMolecularFormulaByMoiety(molecularFormulaByMoietyElement.toProto())
    }
    if (hasIsotope()) {
      protoValue.addAllIsotope(isotope.map { it.toProto() })
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    if (hasRepresentation()) {
      protoValue.addAllRepresentation(representation.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeComponent.toProto():
    SubstanceSpecification.Structure.Isotope {
    val protoValue =
      SubstanceSpecification.Structure.Isotope.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.setIdentifier(identifier.toProto())
    }
    if (hasName()) {
      protoValue.setName(name.toProto())
    }
    if (hasSubstitution()) {
      protoValue.setSubstitution(substitution.toProto())
    }
    if (hasHalfLife()) {
      protoValue.setHalfLife(halfLife.toProto())
    }
    if (hasMolecularWeight()) {
      protoValue.setMolecularWeight(molecularWeight.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeMolecularWeightComponent.toProto():
    SubstanceSpecification.Structure.Isotope.MolecularWeight {
    val protoValue =
      SubstanceSpecification.Structure.Isotope.MolecularWeight.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasMethod()) {
      protoValue.setMethod(method.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasAmount()) {
      protoValue.setAmount(amount.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureRepresentationComponent.toProto():
    SubstanceSpecification.Structure.Representation {
    val protoValue =
      SubstanceSpecification.Structure.Representation.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasRepresentation()) {
      protoValue.setRepresentation(representationElement.toProto())
    }
    if (hasAttachment()) {
      protoValue.setAttachment(attachment.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationCodeComponent.toProto():
    SubstanceSpecification.CodeType {
    val protoValue =
      SubstanceSpecification.CodeType.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    if (hasStatus()) {
      protoValue.setStatus(status.toProto())
    }
    if (hasStatusDate()) {
      protoValue.setStatusDate(statusDateElement.toProto())
    }
    if (hasComment()) {
      protoValue.setComment(commentElement.toProto())
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameComponent.toProto():
    SubstanceSpecification.Name {
    val protoValue =
      SubstanceSpecification.Name.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasStatus()) {
      protoValue.setStatus(status.toProto())
    }
    if (hasPreferred()) {
      protoValue.setPreferred(preferredElement.toProto())
    }
    if (hasLanguage()) {
      protoValue.addAllLanguage(language.map { it.toProto() })
    }
    if (hasDomain()) {
      protoValue.addAllDomain(domain.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasOfficial()) {
      protoValue.addAllOfficial(official.map { it.toProto() })
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameOfficialComponent.toProto():
    SubstanceSpecification.Name.Official {
    val protoValue =
      SubstanceSpecification.Name.Official.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAuthority()) {
      protoValue.setAuthority(authority.toProto())
    }
    if (hasStatus()) {
      protoValue.setStatus(status.toProto())
    }
    if (hasDate()) {
      protoValue.setDate(dateElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationRelationshipComponent.toProto():
    SubstanceSpecification.Relationship {
    val protoValue =
      SubstanceSpecification.Relationship.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSubstance()) {
      protoValue.setSubstance(substance.substanceSpecificationRelationshipSubstanceToProto())
    }
    if (hasRelationship()) {
      protoValue.setRelationship(relationship.toProto())
    }
    if (hasIsDefining()) {
      protoValue.setIsDefining(isDefiningElement.toProto())
    }
    if (hasAmount()) {
      protoValue.setAmount(amount.substanceSpecificationRelationshipAmountToProto())
    }
    if (hasAmountRatioLowLimit()) {
      protoValue.setAmountRatioLowLimit(amountRatioLowLimit.toProto())
    }
    if (hasAmountType()) {
      protoValue.setAmountType(amountType.toProto())
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun SubstanceSpecification.Moiety.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationMoietyComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationMoietyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasRole()) {
      hapiValue.setRole(role.toHapi())
    }
    if (hasIdentifier()) {
      hapiValue.setIdentifier(identifier.toHapi())
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasStereochemistry()) {
      hapiValue.setStereochemistry(stereochemistry.toHapi())
    }
    if (hasOpticalActivity()) {
      hapiValue.setOpticalActivity(opticalActivity.toHapi())
    }
    if (hasMolecularFormula()) {
      hapiValue.setMolecularFormulaElement(molecularFormula.toHapi())
    }
    if (hasAmount()) {
      hapiValue.setAmount(amount.substanceSpecificationMoietyAmountToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSpecification.Property.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationPropertyComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationPropertyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCategory()) {
      hapiValue.setCategory(category.toHapi())
    }
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    if (hasParameters()) {
      hapiValue.setParametersElement(parameters.toHapi())
    }
    if (hasDefiningSubstance()) {
      hapiValue.setDefiningSubstance(
        definingSubstance.substanceSpecificationPropertyDefiningSubstanceToHapi()
      )
    }
    if (hasAmount()) {
      hapiValue.setAmount(amount.substanceSpecificationPropertyAmountToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSpecification.Structure.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasStereochemistry()) {
      hapiValue.setStereochemistry(stereochemistry.toHapi())
    }
    if (hasOpticalActivity()) {
      hapiValue.setOpticalActivity(opticalActivity.toHapi())
    }
    if (hasMolecularFormula()) {
      hapiValue.setMolecularFormulaElement(molecularFormula.toHapi())
    }
    if (hasMolecularFormulaByMoiety()) {
      hapiValue.setMolecularFormulaByMoietyElement(molecularFormulaByMoiety.toHapi())
    }
    if (isotopeCount > 0) {
      hapiValue.setIsotope(isotopeList.map { it.toHapi() })
    }
    if (sourceCount > 0) {
      hapiValue.setSource(sourceList.map { it.toHapi() })
    }
    if (representationCount > 0) {
      hapiValue.setRepresentation(representationList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSpecification.Structure.Isotope.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasIdentifier()) {
      hapiValue.setIdentifier(identifier.toHapi())
    }
    if (hasName()) {
      hapiValue.setName(name.toHapi())
    }
    if (hasSubstitution()) {
      hapiValue.setSubstitution(substitution.toHapi())
    }
    if (hasHalfLife()) {
      hapiValue.setHalfLife(halfLife.toHapi())
    }
    if (hasMolecularWeight()) {
      hapiValue.setMolecularWeight(molecularWeight.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSpecification.Structure.Isotope.MolecularWeight.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeMolecularWeightComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification
        .SubstanceSpecificationStructureIsotopeMolecularWeightComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasMethod()) {
      hapiValue.setMethod(method.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasAmount()) {
      hapiValue.setAmount(amount.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSpecification.Structure.Representation.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureRepresentationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification
        .SubstanceSpecificationStructureRepresentationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasRepresentation()) {
      hapiValue.setRepresentationElement(representation.toHapi())
    }
    if (hasAttachment()) {
      hapiValue.setAttachment(attachment.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSpecification.CodeType.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationCodeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationCodeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    if (hasStatus()) {
      hapiValue.setStatus(status.toHapi())
    }
    if (hasStatusDate()) {
      hapiValue.setStatusDateElement(statusDate.toHapi())
    }
    if (hasComment()) {
      hapiValue.setCommentElement(comment.toHapi())
    }
    if (sourceCount > 0) {
      hapiValue.setSource(sourceList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSpecification.Name.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasStatus()) {
      hapiValue.setStatus(status.toHapi())
    }
    if (hasPreferred()) {
      hapiValue.setPreferredElement(preferred.toHapi())
    }
    if (languageCount > 0) {
      hapiValue.setLanguage(languageList.map { it.toHapi() })
    }
    if (domainCount > 0) {
      hapiValue.setDomain(domainList.map { it.toHapi() })
    }
    if (jurisdictionCount > 0) {
      hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    }
    if (officialCount > 0) {
      hapiValue.setOfficial(officialList.map { it.toHapi() })
    }
    if (sourceCount > 0) {
      hapiValue.setSource(sourceList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSpecification.Name.Official.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameOfficialComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameOfficialComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasAuthority()) {
      hapiValue.setAuthority(authority.toHapi())
    }
    if (hasStatus()) {
      hapiValue.setStatus(status.toHapi())
    }
    if (hasDate()) {
      hapiValue.setDateElement(date.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstanceSpecification.Relationship.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationRelationshipComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationRelationshipComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSubstance()) {
      hapiValue.setSubstance(substance.substanceSpecificationRelationshipSubstanceToHapi())
    }
    if (hasRelationship()) {
      hapiValue.setRelationship(relationship.toHapi())
    }
    if (hasIsDefining()) {
      hapiValue.setIsDefiningElement(isDefining.toHapi())
    }
    if (hasAmount()) {
      hapiValue.setAmount(amount.substanceSpecificationRelationshipAmountToHapi())
    }
    if (hasAmountRatioLowLimit()) {
      hapiValue.setAmountRatioLowLimit(amountRatioLowLimit.toHapi())
    }
    if (hasAmountType()) {
      hapiValue.setAmountType(amountType.toHapi())
    }
    if (sourceCount > 0) {
      hapiValue.setSource(sourceList.map { it.toHapi() })
    }
    return hapiValue
  }
}
