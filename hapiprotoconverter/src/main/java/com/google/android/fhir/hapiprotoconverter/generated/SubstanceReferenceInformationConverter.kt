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

public object SubstanceReferenceInformationConverter {
  private
      fun SubstanceReferenceInformation.Target.AmountX.substanceReferenceInformationTargetAmountToHapi():
      Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for SubstanceReferenceInformation.target.amount[x]")
  }

  private fun Type.substanceReferenceInformationTargetAmountToProto():
      SubstanceReferenceInformation.Target.AmountX {
    val protoValue = SubstanceReferenceInformation.Target.AmountX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  public fun SubstanceReferenceInformation.toHapi():
      org.hl7.fhir.r4.model.SubstanceReferenceInformation {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceReferenceInformation()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCommentElement(comment.toHapi())
    hapiValue.setGene(geneList.map{it.toHapi()})
    hapiValue.setGeneElement(geneElementList.map{it.toHapi()})
    hapiValue.setClassification(classificationList.map{it.toHapi()})
    hapiValue.setTarget(targetList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.SubstanceReferenceInformation.toProto():
      SubstanceReferenceInformation {
    val protoValue = SubstanceReferenceInformation.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setComment(commentElement.toProto())
    .addAllGene(gene.map{it.toProto()})
    .addAllGeneElement(geneElement.map{it.toProto()})
    .addAllClassification(classification.map{it.toProto()})
    .addAllTarget(target.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationGeneComponent.toProto():
      SubstanceReferenceInformation.Gene {
    val protoValue = SubstanceReferenceInformation.Gene.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setGeneSequenceOrigin(geneSequenceOrigin.toProto())
    .setGene(gene.toProto())
    .addAllSource(source.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationGeneElementComponent.toProto():
      SubstanceReferenceInformation.GeneElement {
    val protoValue = SubstanceReferenceInformation.GeneElement.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setElement(element.toProto())
    .addAllSource(source.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationClassificationComponent.toProto():
      SubstanceReferenceInformation.Classification {
    val protoValue = SubstanceReferenceInformation.Classification.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setDomain(domain.toProto())
    .setClassification(classification.toProto())
    .addAllSubtype(subtype.map{it.toProto()})
    .addAllSource(source.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationTargetComponent.toProto():
      SubstanceReferenceInformation.Target {
    val protoValue = SubstanceReferenceInformation.Target.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setTarget(target.toProto())
    .setType(type.toProto())
    .setInteraction(interaction.toProto())
    .setOrganism(organism.toProto())
    .setOrganismType(organismType.toProto())
    .setAmount(amount.substanceReferenceInformationTargetAmountToProto())
    .setAmountType(amountType.toProto())
    .addAllSource(source.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun SubstanceReferenceInformation.Gene.toHapi():
      org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationGeneComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationGeneComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setGeneSequenceOrigin(geneSequenceOrigin.toHapi())
    hapiValue.setGene(gene.toHapi())
    hapiValue.setSource(sourceList.map{it.toHapi()})
    return hapiValue
  }

  private fun SubstanceReferenceInformation.GeneElement.toHapi():
      org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationGeneElementComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationGeneElementComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setElement(element.toHapi())
    hapiValue.setSource(sourceList.map{it.toHapi()})
    return hapiValue
  }

  private fun SubstanceReferenceInformation.Classification.toHapi():
      org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationClassificationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationClassificationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setDomain(domain.toHapi())
    hapiValue.setClassification(classification.toHapi())
    hapiValue.setSubtype(subtypeList.map{it.toHapi()})
    hapiValue.setSource(sourceList.map{it.toHapi()})
    return hapiValue
  }

  private fun SubstanceReferenceInformation.Target.toHapi():
      org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationTargetComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceReferenceInformation.SubstanceReferenceInformationTargetComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setTarget(target.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setInteraction(interaction.toHapi())
    hapiValue.setOrganism(organism.toHapi())
    hapiValue.setOrganismType(organismType.toHapi())
    hapiValue.setAmount(amount.substanceReferenceInformationTargetAmountToHapi())
    hapiValue.setAmountType(amountType.toHapi())
    hapiValue.setSource(sourceList.map{it.toHapi()})
    return hapiValue
  }
}
