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
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object SubstanceSpecificationConverter {
  private fun SubstanceSpecification.Moiety.AmountX.substanceSpecificationMoietyAmountToHapi():
    Type {
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SubstanceSpecification.moiety.amount[x]")
  }

  private fun Type.substanceSpecificationMoietyAmountToProto():
    SubstanceSpecification.Moiety.AmountX {
    val protoValue = SubstanceSpecification.Moiety.AmountX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  private fun SubstanceSpecification.Property.DefiningSubstanceX.substanceSpecificationPropertyDefiningSubstanceToHapi():
    Type {
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for SubstanceSpecification.property.definingSubstance[x]"
    )
  }

  private fun Type.substanceSpecificationPropertyDefiningSubstanceToProto():
    SubstanceSpecification.Property.DefiningSubstanceX {
    val protoValue = SubstanceSpecification.Property.DefiningSubstanceX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  private fun SubstanceSpecification.Property.AmountX.substanceSpecificationPropertyAmountToHapi():
    Type {
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SubstanceSpecification.property.amount[x]")
  }

  private fun Type.substanceSpecificationPropertyAmountToProto():
    SubstanceSpecification.Property.AmountX {
    val protoValue = SubstanceSpecification.Property.AmountX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  private fun SubstanceSpecification.Relationship.SubstanceX.substanceSpecificationRelationshipSubstanceToHapi():
    Type {
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for SubstanceSpecification.relationship.substance[x]"
    )
  }

  private fun Type.substanceSpecificationRelationshipSubstanceToProto():
    SubstanceSpecification.Relationship.SubstanceX {
    val protoValue = SubstanceSpecification.Relationship.SubstanceX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  private fun SubstanceSpecification.Relationship.AmountX.substanceSpecificationRelationshipAmountToHapi():
    Type {
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.ratio != Ratio.newBuilder().defaultInstanceForType) {
      return (this.ratio).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SubstanceSpecification.relationship.amount[x]")
  }

  private fun Type.substanceSpecificationRelationshipAmountToProto():
    SubstanceSpecification.Relationship.AmountX {
    val protoValue = SubstanceSpecification.Relationship.AmountX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.ratio = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  fun SubstanceSpecification.toHapi(): org.hl7.fhir.r4.model.SubstanceSpecification {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceSpecification()
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
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status = status.toHapi()
    }
    if (hasDomain()) {
      hapiValue.domain = domain.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (sourceCount > 0) {
      hapiValue.source = sourceList.map { it.toHapi() }
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    if (moietyCount > 0) {
      hapiValue.moiety = moietyList.map { it.toHapi() }
    }
    if (propertyCount > 0) {
      hapiValue.property = propertyList.map { it.toHapi() }
    }
    if (hasReferenceInformation()) {
      hapiValue.referenceInformation = referenceInformation.toHapi()
    }
    if (hasStructure()) {
      hapiValue.structure = structure.toHapi()
    }
    if (codeCount > 0) {
      hapiValue.code = codeList.map { it.toHapi() }
    }
    if (nameCount > 0) {
      hapiValue.name = nameList.map { it.toHapi() }
    }
    if (relationshipCount > 0) {
      hapiValue.relationship = relationshipList.map { it.toHapi() }
    }
    if (hasNucleicAcid()) {
      hapiValue.nucleicAcid = nucleicAcid.toHapi()
    }
    if (hasPolymer()) {
      hapiValue.polymer = polymer.toHapi()
    }
    if (hasProtein()) {
      hapiValue.protein = protein.toHapi()
    }
    if (hasSourceMaterial()) {
      hapiValue.sourceMaterial = sourceMaterial.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.SubstanceSpecification.toProto(): SubstanceSpecification {
    val protoValue = SubstanceSpecification.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasStatus()) {
      protoValue.status = status.toProto()
    }
    if (hasDomain()) {
      protoValue.domain = domain.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    if (hasMoiety()) {
      protoValue.addAllMoiety(moiety.map { it.toProto() })
    }
    if (hasProperty()) {
      protoValue.addAllProperty(property.map { it.toProto() })
    }
    if (hasReferenceInformation()) {
      protoValue.referenceInformation = referenceInformation.toProto()
    }
    if (hasStructure()) {
      protoValue.structure = structure.toProto()
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
      protoValue.nucleicAcid = nucleicAcid.toProto()
    }
    if (hasPolymer()) {
      protoValue.polymer = polymer.toProto()
    }
    if (hasProtein()) {
      protoValue.protein = protein.toProto()
    }
    if (hasSourceMaterial()) {
      protoValue.sourceMaterial = sourceMaterial.toProto()
    }
    return protoValue.build()
  }

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
      protoValue.role = role.toProto()
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasStereochemistry()) {
      protoValue.stereochemistry = stereochemistry.toProto()
    }
    if (hasOpticalActivity()) {
      protoValue.opticalActivity = opticalActivity.toProto()
    }
    if (hasMolecularFormula()) {
      protoValue.molecularFormula = molecularFormulaElement.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.substanceSpecificationMoietyAmountToProto()
    }
    return protoValue.build()
  }

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
      protoValue.category = category.toProto()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasParameters()) {
      protoValue.parameters = parametersElement.toProto()
    }
    if (hasDefiningSubstance()) {
      protoValue.definingSubstance =
        definingSubstance.substanceSpecificationPropertyDefiningSubstanceToProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.substanceSpecificationPropertyAmountToProto()
    }
    return protoValue.build()
  }

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
      protoValue.stereochemistry = stereochemistry.toProto()
    }
    if (hasOpticalActivity()) {
      protoValue.opticalActivity = opticalActivity.toProto()
    }
    if (hasMolecularFormula()) {
      protoValue.molecularFormula = molecularFormulaElement.toProto()
    }
    if (hasMolecularFormulaByMoiety()) {
      protoValue.molecularFormulaByMoiety = molecularFormulaByMoietyElement.toProto()
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
      protoValue.identifier = identifier.toProto()
    }
    if (hasName()) {
      protoValue.name = name.toProto()
    }
    if (hasSubstitution()) {
      protoValue.substitution = substitution.toProto()
    }
    if (hasHalfLife()) {
      protoValue.halfLife = halfLife.toProto()
    }
    if (hasMolecularWeight()) {
      protoValue.molecularWeight = molecularWeight.toProto()
    }
    return protoValue.build()
  }

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
      protoValue.method = method.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.toProto()
    }
    return protoValue.build()
  }

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
      protoValue.type = type.toProto()
    }
    if (hasRepresentation()) {
      protoValue.representation = representationElement.toProto()
    }
    if (hasAttachment()) {
      protoValue.attachment = attachment.toProto()
    }
    return protoValue.build()
  }

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
      protoValue.code = code.toProto()
    }
    if (hasStatus()) {
      protoValue.status = status.toProto()
    }
    if (hasStatusDate()) {
      protoValue.statusDate = statusDateElement.toProto()
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    return protoValue.build()
  }

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
      protoValue.name = nameElement.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasStatus()) {
      protoValue.status = status.toProto()
    }
    if (hasPreferred()) {
      protoValue.preferred = preferredElement.toProto()
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
      protoValue.authority = authority.toProto()
    }
    if (hasStatus()) {
      protoValue.status = status.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    return protoValue.build()
  }

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
      protoValue.substance = substance.substanceSpecificationRelationshipSubstanceToProto()
    }
    if (hasRelationship()) {
      protoValue.relationship = relationship.toProto()
    }
    if (hasIsDefining()) {
      protoValue.isDefining = isDefiningElement.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.substanceSpecificationRelationshipAmountToProto()
    }
    if (hasAmountRatioLowLimit()) {
      protoValue.amountRatioLowLimit = amountRatioLowLimit.toProto()
    }
    if (hasAmountType()) {
      protoValue.amountType = amountType.toProto()
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun SubstanceSpecification.Moiety.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationMoietyComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationMoietyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasRole()) {
      hapiValue.role = role.toHapi()
    }
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasStereochemistry()) {
      hapiValue.stereochemistry = stereochemistry.toHapi()
    }
    if (hasOpticalActivity()) {
      hapiValue.opticalActivity = opticalActivity.toHapi()
    }
    if (hasMolecularFormula()) {
      hapiValue.molecularFormulaElement = molecularFormula.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.substanceSpecificationMoietyAmountToHapi()
    }
    return hapiValue
  }

  private fun SubstanceSpecification.Property.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationPropertyComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationPropertyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasParameters()) {
      hapiValue.parametersElement = parameters.toHapi()
    }
    if (hasDefiningSubstance()) {
      hapiValue.definingSubstance =
        definingSubstance.substanceSpecificationPropertyDefiningSubstanceToHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.substanceSpecificationPropertyAmountToHapi()
    }
    return hapiValue
  }

  private fun SubstanceSpecification.Structure.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasStereochemistry()) {
      hapiValue.stereochemistry = stereochemistry.toHapi()
    }
    if (hasOpticalActivity()) {
      hapiValue.opticalActivity = opticalActivity.toHapi()
    }
    if (hasMolecularFormula()) {
      hapiValue.molecularFormulaElement = molecularFormula.toHapi()
    }
    if (hasMolecularFormulaByMoiety()) {
      hapiValue.molecularFormulaByMoietyElement = molecularFormulaByMoiety.toHapi()
    }
    if (isotopeCount > 0) {
      hapiValue.isotope = isotopeList.map { it.toHapi() }
    }
    if (sourceCount > 0) {
      hapiValue.source = sourceList.map { it.toHapi() }
    }
    if (representationCount > 0) {
      hapiValue.representation = representationList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun SubstanceSpecification.Structure.Isotope.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeComponent()
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
      hapiValue.name = name.toHapi()
    }
    if (hasSubstitution()) {
      hapiValue.substitution = substitution.toHapi()
    }
    if (hasHalfLife()) {
      hapiValue.halfLife = halfLife.toHapi()
    }
    if (hasMolecularWeight()) {
      hapiValue.molecularWeight = molecularWeight.toHapi()
    }
    return hapiValue
  }

  private fun SubstanceSpecification.Structure.Isotope.MolecularWeight.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureIsotopeMolecularWeightComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification
        .SubstanceSpecificationStructureIsotopeMolecularWeightComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasMethod()) {
      hapiValue.method = method.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.toHapi()
    }
    return hapiValue
  }

  private fun SubstanceSpecification.Structure.Representation.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationStructureRepresentationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification
        .SubstanceSpecificationStructureRepresentationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasRepresentation()) {
      hapiValue.representationElement = representation.toHapi()
    }
    if (hasAttachment()) {
      hapiValue.attachment = attachment.toHapi()
    }
    return hapiValue
  }

  private fun SubstanceSpecification.CodeType.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationCodeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationCodeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status = status.toHapi()
    }
    if (hasStatusDate()) {
      hapiValue.statusDateElement = statusDate.toHapi()
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    if (sourceCount > 0) {
      hapiValue.source = sourceList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun SubstanceSpecification.Name.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status = status.toHapi()
    }
    if (hasPreferred()) {
      hapiValue.preferredElement = preferred.toHapi()
    }
    if (languageCount > 0) {
      hapiValue.language = languageList.map { it.toHapi() }
    }
    if (domainCount > 0) {
      hapiValue.domain = domainList.map { it.toHapi() }
    }
    if (jurisdictionCount > 0) {
      hapiValue.jurisdiction = jurisdictionList.map { it.toHapi() }
    }
    if (officialCount > 0) {
      hapiValue.official = officialList.map { it.toHapi() }
    }
    if (sourceCount > 0) {
      hapiValue.source = sourceList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun SubstanceSpecification.Name.Official.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameOfficialComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationNameOfficialComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasAuthority()) {
      hapiValue.authority = authority.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status = status.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    return hapiValue
  }

  private fun SubstanceSpecification.Relationship.toHapi():
    org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationRelationshipComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceSpecification.SubstanceSpecificationRelationshipComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSubstance()) {
      hapiValue.substance = substance.substanceSpecificationRelationshipSubstanceToHapi()
    }
    if (hasRelationship()) {
      hapiValue.relationship = relationship.toHapi()
    }
    if (hasIsDefining()) {
      hapiValue.isDefiningElement = isDefining.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.substanceSpecificationRelationshipAmountToHapi()
    }
    if (hasAmountRatioLowLimit()) {
      hapiValue.amountRatioLowLimit = amountRatioLowLimit.toHapi()
    }
    if (hasAmountType()) {
      hapiValue.amountType = amountType.toHapi()
    }
    if (sourceCount > 0) {
      hapiValue.source = sourceList.map { it.toHapi() }
    }
    return hapiValue
  }
}
