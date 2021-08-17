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
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SubstanceReferenceInformation
import com.google.fhir.r4.core.SubstanceReferenceInformation.Target
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object SubstanceReferenceInformationConverter {
  private fun SubstanceReferenceInformation.Target.AmountX.substanceReferenceInformationTargetAmountToHapi():
    Type {
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for SubstanceReferenceInformation.target.amount[x]"
    )
  }

  private fun Type.substanceReferenceInformationTargetAmountToProto():
    SubstanceReferenceInformation.Target.AmountX {
    val protoValue = SubstanceReferenceInformation.Target.AmountX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  fun SubstanceReferenceInformation.toHapi(): org.hl7.fhir.r4.model.SubstanceReferenceInformation {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceReferenceInformation()
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
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    if (geneCount > 0) {
      hapiValue.gene = geneList.map { it.toHapi() }
    }
    if (geneElementCount > 0) {
      hapiValue.geneElement = geneElementList.map { it.toHapi() }
    }
    if (classificationCount > 0) {
      hapiValue.classification = classificationList.map { it.toHapi() }
    }
    if (targetCount > 0) {
      hapiValue.target = targetList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.SubstanceReferenceInformation.toProto(): SubstanceReferenceInformation {
    val protoValue = SubstanceReferenceInformation.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    if (hasGene()) {
      protoValue.addAllGene(gene.map { it.toProto() })
    }
    if (hasGeneElement()) {
      protoValue.addAllGeneElement(geneElement.map { it.toProto() })
    }
    if (hasClassification()) {
      protoValue.addAllClassification(classification.map { it.toProto() })
    }
    if (hasTarget()) {
      protoValue.addAllTarget(target.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationGeneComponent.toProto():
    SubstanceReferenceInformation.Gene {
    val protoValue =
      SubstanceReferenceInformation.Gene.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasGeneSequenceOrigin()) {
      protoValue.geneSequenceOrigin = geneSequenceOrigin.toProto()
    }
    if (hasGene()) {
      protoValue.gene = gene.toProto()
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationGeneElementComponent.toProto():
    SubstanceReferenceInformation.GeneElement {
    val protoValue =
      SubstanceReferenceInformation.GeneElement.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasElement()) {
      protoValue.element = element.toProto()
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationClassificationComponent.toProto():
    SubstanceReferenceInformation.Classification {
    val protoValue =
      SubstanceReferenceInformation.Classification.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDomain()) {
      protoValue.domain = domain.toProto()
    }
    if (hasClassification()) {
      protoValue.classification = classification.toProto()
    }
    if (hasSubtype()) {
      protoValue.addAllSubtype(subtype.map { it.toProto() })
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationTargetComponent.toProto():
    SubstanceReferenceInformation.Target {
    val protoValue =
      SubstanceReferenceInformation.Target.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasTarget()) {
      protoValue.target = target.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasInteraction()) {
      protoValue.interaction = interaction.toProto()
    }
    if (hasOrganism()) {
      protoValue.organism = organism.toProto()
    }
    if (hasOrganismType()) {
      protoValue.organismType = organismType.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.substanceReferenceInformationTargetAmountToProto()
    }
    if (hasAmountType()) {
      protoValue.amountType = amountType.toProto()
    }
    if (hasSource()) {
      protoValue.addAllSource(source.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun SubstanceReferenceInformation.Gene.toHapi():
    org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationGeneComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceReferenceInformation
        .SubstanceReferenceInformationGeneComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasGeneSequenceOrigin()) {
      hapiValue.geneSequenceOrigin = geneSequenceOrigin.toHapi()
    }
    if (hasGene()) {
      hapiValue.gene = gene.toHapi()
    }
    if (sourceCount > 0) {
      hapiValue.source = sourceList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun SubstanceReferenceInformation.GeneElement.toHapi():
    org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationGeneElementComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceReferenceInformation
        .SubstanceReferenceInformationGeneElementComponent()
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
    if (hasElement()) {
      hapiValue.element = element.toHapi()
    }
    if (sourceCount > 0) {
      hapiValue.source = sourceList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun SubstanceReferenceInformation.Classification.toHapi():
    org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationClassificationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceReferenceInformation
        .SubstanceReferenceInformationClassificationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDomain()) {
      hapiValue.domain = domain.toHapi()
    }
    if (hasClassification()) {
      hapiValue.classification = classification.toHapi()
    }
    if (subtypeCount > 0) {
      hapiValue.subtype = subtypeList.map { it.toHapi() }
    }
    if (sourceCount > 0) {
      hapiValue.source = sourceList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun SubstanceReferenceInformation.Target.toHapi():
    org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationTargetComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstanceReferenceInformation
        .SubstanceReferenceInformationTargetComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasTarget()) {
      hapiValue.target = target.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasInteraction()) {
      hapiValue.interaction = interaction.toHapi()
    }
    if (hasOrganism()) {
      hapiValue.organism = organism.toHapi()
    }
    if (hasOrganismType()) {
      hapiValue.organismType = organismType.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.substanceReferenceInformationTargetAmountToHapi()
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
