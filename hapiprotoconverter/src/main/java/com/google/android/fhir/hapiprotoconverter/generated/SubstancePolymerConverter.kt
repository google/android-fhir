package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SubstanceAmountConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SubstanceAmountConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SubstancePolymer

public object SubstancePolymerConverter {
  public fun SubstancePolymer.toHapi(): org.hl7.fhir.r4.model.SubstancePolymer {
    val hapiValue = org.hl7.fhir.r4.model.SubstancePolymer()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setClass(class.toHapi())
    hapiValue.setGeometry(geometry.toHapi())
    hapiValue.setCopolymerConnectivity(copolymerConnectivityList.map{it.toHapi()})
    hapiValue.setModification(modificationList.map{it.toHapi()})
    hapiValue.setMonomerSet(monomerSetList.map{it.toHapi()})
    hapiValue.setRepeat(repeatList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.SubstancePolymer.toProto(): SubstancePolymer {
    val protoValue = SubstancePolymer.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setClass(class.toProto())
    .setGeometry(geometry.toProto())
    .addAllCopolymerConnectivity(copolymerConnectivity.map{it.toProto()})
    .addAllModification(modification.map{it.toProto()})
    .addAllMonomerSet(monomerSet.map{it.toProto()})
    .addAllRepeat(repeat.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetComponent.toProto():
      SubstancePolymer.MonomerSet {
    val protoValue = SubstancePolymer.MonomerSet.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setRatioType(ratioType.toProto())
    .addAllStartingMaterial(startingMaterial.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetStartingMaterialComponent.toProto():
      SubstancePolymer.MonomerSet.StartingMaterial {
    val protoValue = SubstancePolymer.MonomerSet.StartingMaterial.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setMaterial(material.toProto())
    .setType(type.toProto())
    .setIsDefining(isDefiningElement.toProto())
    .setAmount(amount.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatComponent.toProto():
      SubstancePolymer.Repeat {
    val protoValue = SubstancePolymer.Repeat.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setNumberOfUnits(numberOfUnitsElement.toProto())
    .setAverageMolecularFormula(averageMolecularFormulaElement.toProto())
    .setRepeatUnitAmountType(repeatUnitAmountType.toProto())
    .addAllRepeatUnit(repeatUnit.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitComponent.toProto():
      SubstancePolymer.Repeat.RepeatUnit {
    val protoValue = SubstancePolymer.Repeat.RepeatUnit.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setOrientationOfPolymerisation(orientationOfPolymerisation.toProto())
    .setRepeatUnit(repeatUnitElement.toProto())
    .setAmount(amount.toProto())
    .addAllDegreeOfPolymerisation(degreeOfPolymerisation.map{it.toProto()})
    .addAllStructuralRepresentation(structuralRepresentation.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitDegreeOfPolymerisationComponent.toProto():
      SubstancePolymer.Repeat.RepeatUnit.DegreeOfPolymerisation {
    val protoValue = SubstancePolymer.Repeat.RepeatUnit.DegreeOfPolymerisation.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setDegree(degree.toProto())
    .setAmount(amount.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitStructuralRepresentationComponent.toProto():
      SubstancePolymer.Repeat.RepeatUnit.StructuralRepresentation {
    val protoValue = SubstancePolymer.Repeat.RepeatUnit.StructuralRepresentation.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setRepresentation(representationElement.toProto())
    .setAttachment(attachment.toProto())
    .build()
    return protoValue
  }

  public fun SubstancePolymer.MonomerSet.toHapi():
      org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetComponent {
    val hapiValue = org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setRatioType(ratioType.toHapi())
    hapiValue.setStartingMaterial(startingMaterialList.map{it.toHapi()})
    return hapiValue
  }

  public fun SubstancePolymer.MonomerSet.StartingMaterial.toHapi():
      org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetStartingMaterialComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetStartingMaterialComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setMaterial(material.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setIsDefiningElement(isDefining.toHapi())
    hapiValue.setAmount(amount.toHapi())
    return hapiValue
  }

  public fun SubstancePolymer.Repeat.toHapi():
      org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatComponent {
    val hapiValue = org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNumberOfUnitsElement(numberOfUnits.toHapi())
    hapiValue.setAverageMolecularFormulaElement(averageMolecularFormula.toHapi())
    hapiValue.setRepeatUnitAmountType(repeatUnitAmountType.toHapi())
    hapiValue.setRepeatUnit(repeatUnitList.map{it.toHapi()})
    return hapiValue
  }

  public fun SubstancePolymer.Repeat.RepeatUnit.toHapi():
      org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setOrientationOfPolymerisation(orientationOfPolymerisation.toHapi())
    hapiValue.setRepeatUnitElement(repeatUnit.toHapi())
    hapiValue.setAmount(amount.toHapi())
    hapiValue.setDegreeOfPolymerisation(degreeOfPolymerisationList.map{it.toHapi()})
    hapiValue.setStructuralRepresentation(structuralRepresentationList.map{it.toHapi()})
    return hapiValue
  }

  public fun SubstancePolymer.Repeat.RepeatUnit.DegreeOfPolymerisation.toHapi():
      org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitDegreeOfPolymerisationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitDegreeOfPolymerisationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setDegree(degree.toHapi())
    hapiValue.setAmount(amount.toHapi())
    return hapiValue
  }

  public fun SubstancePolymer.Repeat.RepeatUnit.StructuralRepresentation.toHapi():
      org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitStructuralRepresentationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitStructuralRepresentationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setRepresentationElement(representation.toHapi())
    hapiValue.setAttachment(attachment.toHapi())
    return hapiValue
  }
}
