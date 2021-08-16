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
import com.google.fhir.r4.core.SubstancePolymer.MonomerSet
import com.google.fhir.r4.core.SubstancePolymer.Repeat
import com.google.fhir.r4.core.SubstancePolymer.Repeat.RepeatUnit
import kotlin.jvm.JvmStatic

object SubstancePolymerConverter {
  @JvmStatic
  fun SubstancePolymer.toHapi(): org.hl7.fhir.r4.model.SubstancePolymer {
    val hapiValue = org.hl7.fhir.r4.model.SubstancePolymer()
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
    if (hasClassValue()) {
        hapiValue.class_ = classValue.toHapi()
    }
    if (hasGeometry()) {
        hapiValue.geometry = geometry.toHapi()
    }
    if (copolymerConnectivityCount > 0) {
        hapiValue.copolymerConnectivity = copolymerConnectivityList.map { it.toHapi() }
    }
    if (modificationCount > 0) {
        hapiValue.modification = modificationList.map { it.toHapi() }
    }
    if (monomerSetCount > 0) {
        hapiValue.monomerSet = monomerSetList.map { it.toHapi() }
    }
    if (repeatCount > 0) {
        hapiValue.repeat = repeatList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.SubstancePolymer.toProto(): SubstancePolymer {
    val protoValue = SubstancePolymer.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasClass_()) {
        protoValue.classValue = class_.toProto()
    }
    if (hasGeometry()) {
        protoValue.geometry = geometry.toProto()
    }
    if (hasCopolymerConnectivity()) {
      protoValue.addAllCopolymerConnectivity(copolymerConnectivity.map { it.toProto() })
    }
    if (hasModification()) {
      protoValue.addAllModification(modification.map { it.toProto() })
    }
    if (hasMonomerSet()) {
      protoValue.addAllMonomerSet(monomerSet.map { it.toProto() })
    }
    if (hasRepeat()) {
      protoValue.addAllRepeat(repeat.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetComponent.toProto():
    SubstancePolymer.MonomerSet {
    val protoValue =
      SubstancePolymer.MonomerSet.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRatioType()) {
        protoValue.ratioType = ratioType.toProto()
    }
    if (hasStartingMaterial()) {
      protoValue.addAllStartingMaterial(startingMaterial.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetStartingMaterialComponent.toProto():
    SubstancePolymer.MonomerSet.StartingMaterial {
    val protoValue =
      SubstancePolymer.MonomerSet.StartingMaterial.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasMaterial()) {
        protoValue.material = material.toProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasIsDefining()) {
        protoValue.isDefining = isDefiningElement.toProto()
    }
    if (hasAmount()) {
        protoValue.amount = amount.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatComponent.toProto():
    SubstancePolymer.Repeat {
    val protoValue = SubstancePolymer.Repeat.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasNumberOfUnits()) {
        protoValue.numberOfUnits = numberOfUnitsElement.toProto()
    }
    if (hasAverageMolecularFormula()) {
        protoValue.averageMolecularFormula = averageMolecularFormulaElement.toProto()
    }
    if (hasRepeatUnitAmountType()) {
        protoValue.repeatUnitAmountType = repeatUnitAmountType.toProto()
    }
    if (hasRepeatUnit()) {
      protoValue.addAllRepeatUnit(repeatUnit.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitComponent.toProto():
    SubstancePolymer.Repeat.RepeatUnit {
    val protoValue =
      SubstancePolymer.Repeat.RepeatUnit.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasOrientationOfPolymerisation()) {
        protoValue.orientationOfPolymerisation = orientationOfPolymerisation.toProto()
    }
    if (hasRepeatUnit()) {
        protoValue.repeatUnit = repeatUnitElement.toProto()
    }
    if (hasAmount()) {
        protoValue.amount = amount.toProto()
    }
    if (hasDegreeOfPolymerisation()) {
      protoValue.addAllDegreeOfPolymerisation(degreeOfPolymerisation.map { it.toProto() })
    }
    if (hasStructuralRepresentation()) {
      protoValue.addAllStructuralRepresentation(structuralRepresentation.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitDegreeOfPolymerisationComponent.toProto():
    SubstancePolymer.Repeat.RepeatUnit.DegreeOfPolymerisation {
    val protoValue =
      SubstancePolymer.Repeat.RepeatUnit.DegreeOfPolymerisation.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDegree()) {
        protoValue.degree = degree.toProto()
    }
    if (hasAmount()) {
        protoValue.amount = amount.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitStructuralRepresentationComponent.toProto():
    SubstancePolymer.Repeat.RepeatUnit.StructuralRepresentation {
    val protoValue =
      SubstancePolymer.Repeat.RepeatUnit.StructuralRepresentation.newBuilder()
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

  @JvmStatic
  private fun SubstancePolymer.MonomerSet.toHapi():
    org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetComponent {
    val hapiValue = org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasRatioType()) {
        hapiValue.ratioType = ratioType.toHapi()
    }
    if (startingMaterialCount > 0) {
        hapiValue.startingMaterial = startingMaterialList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstancePolymer.MonomerSet.StartingMaterial.toHapi():
    org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetStartingMaterialComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerMonomerSetStartingMaterialComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasMaterial()) {
        hapiValue.material = material.toHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasIsDefining()) {
        hapiValue.isDefiningElement = isDefining.toHapi()
    }
    if (hasAmount()) {
        hapiValue.amount = amount.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstancePolymer.Repeat.toHapi():
    org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatComponent {
    val hapiValue = org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasNumberOfUnits()) {
        hapiValue.numberOfUnitsElement = numberOfUnits.toHapi()
    }
    if (hasAverageMolecularFormula()) {
        hapiValue.averageMolecularFormulaElement = averageMolecularFormula.toHapi()
    }
    if (hasRepeatUnitAmountType()) {
        hapiValue.repeatUnitAmountType = repeatUnitAmountType.toHapi()
    }
    if (repeatUnitCount > 0) {
        hapiValue.repeatUnit = repeatUnitList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstancePolymer.Repeat.RepeatUnit.toHapi():
    org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasOrientationOfPolymerisation()) {
        hapiValue.orientationOfPolymerisation = orientationOfPolymerisation.toHapi()
    }
    if (hasRepeatUnit()) {
        hapiValue.repeatUnitElement = repeatUnit.toHapi()
    }
    if (hasAmount()) {
        hapiValue.amount = amount.toHapi()
    }
    if (degreeOfPolymerisationCount > 0) {
        hapiValue.degreeOfPolymerisation = degreeOfPolymerisationList.map { it.toHapi() }
    }
    if (structuralRepresentationCount > 0) {
        hapiValue.structuralRepresentation = structuralRepresentationList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstancePolymer.Repeat.RepeatUnit.DegreeOfPolymerisation.toHapi():
    org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitDegreeOfPolymerisationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstancePolymer
        .SubstancePolymerRepeatRepeatUnitDegreeOfPolymerisationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDegree()) {
        hapiValue.degree = degree.toHapi()
    }
    if (hasAmount()) {
        hapiValue.amount = amount.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun SubstancePolymer.Repeat.RepeatUnit.StructuralRepresentation.toHapi():
    org.hl7.fhir.r4.model.SubstancePolymer.SubstancePolymerRepeatRepeatUnitStructuralRepresentationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SubstancePolymer
        .SubstancePolymerRepeatRepeatUnitStructuralRepresentationComponent()
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
}
