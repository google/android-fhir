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
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductPharmaceutical
import com.google.fhir.r4.core.MedicinalProductPharmaceutical.RouteOfAdministration
import com.google.fhir.r4.core.MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

public object MedicinalProductPharmaceuticalConverter {
  @JvmStatic
  public fun MedicinalProductPharmaceutical.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPharmaceutical {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductPharmaceutical()
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
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    if (hasAdministrableDoseForm()) {
      hapiValue.setAdministrableDoseForm(administrableDoseForm.toHapi())
    }
    if (hasUnitOfPresentation()) {
      hapiValue.setUnitOfPresentation(unitOfPresentation.toHapi())
    }
    if (ingredientCount > 0) {
      hapiValue.setIngredient(ingredientList.map { it.toHapi() })
    }
    if (deviceCount > 0) {
      hapiValue.setDevice(deviceList.map { it.toHapi() })
    }
    if (characteristicsCount > 0) {
      hapiValue.setCharacteristics(characteristicsList.map { it.toHapi() })
    }
    if (routeOfAdministrationCount > 0) {
      hapiValue.setRouteOfAdministration(routeOfAdministrationList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.toProto():
    MedicinalProductPharmaceutical {
    val protoValue = MedicinalProductPharmaceutical.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasAdministrableDoseForm()) {
      protoValue.setAdministrableDoseForm(administrableDoseForm.toProto())
    }
    if (hasUnitOfPresentation()) {
      protoValue.setUnitOfPresentation(unitOfPresentation.toProto())
    }
    if (hasIngredient()) {
      protoValue.addAllIngredient(ingredient.map { it.toProto() })
    }
    if (hasDevice()) {
      protoValue.addAllDevice(device.map { it.toProto() })
    }
    if (hasCharacteristics()) {
      protoValue.addAllCharacteristics(characteristics.map { it.toProto() })
    }
    if (hasRouteOfAdministration()) {
      protoValue.addAllRouteOfAdministration(routeOfAdministration.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalCharacteristicsComponent.toProto():
    MedicinalProductPharmaceutical.Characteristics {
    val protoValue =
      MedicinalProductPharmaceutical.Characteristics.newBuilder()
        .setId(String.newBuilder().setValue(id))
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
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationComponent.toProto():
    MedicinalProductPharmaceutical.RouteOfAdministration {
    val protoValue =
      MedicinalProductPharmaceutical.RouteOfAdministration.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    if (hasFirstDose()) {
      protoValue.setFirstDose(firstDose.toProto())
    }
    if (hasMaxSingleDose()) {
      protoValue.setMaxSingleDose(maxSingleDose.toProto())
    }
    if (hasMaxDosePerDay()) {
      protoValue.setMaxDosePerDay(maxDosePerDay.toProto())
    }
    if (hasMaxDosePerTreatmentPeriod()) {
      protoValue.setMaxDosePerTreatmentPeriod(maxDosePerTreatmentPeriod.toProto())
    }
    if (hasMaxTreatmentPeriod()) {
      protoValue.setMaxTreatmentPeriod(maxTreatmentPeriod.toProto())
    }
    if (hasTargetSpecies()) {
      protoValue.addAllTargetSpecies(targetSpecies.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesComponent.toProto():
    MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies {
    val protoValue =
      MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    if (hasWithdrawalPeriod()) {
      protoValue.addAllWithdrawalPeriod(withdrawalPeriod.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesWithdrawalPeriodComponent.toProto():
    MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies.WithdrawalPeriod {
    val protoValue =
      MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies.WithdrawalPeriod
        .newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasTissue()) {
      protoValue.setTissue(tissue.toProto())
    }
    if (hasValue()) {
      protoValue.setValue(value.toProto())
    }
    if (hasSupportingInformation()) {
      protoValue.setSupportingInformation(supportingInformationElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicinalProductPharmaceutical.Characteristics.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalCharacteristicsComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPharmaceutical
        .MedicinalProductPharmaceuticalCharacteristicsComponent()
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
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProductPharmaceutical.RouteOfAdministration.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPharmaceutical
        .MedicinalProductPharmaceuticalRouteOfAdministrationComponent()
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
    if (hasFirstDose()) {
      hapiValue.setFirstDose(firstDose.toHapi())
    }
    if (hasMaxSingleDose()) {
      hapiValue.setMaxSingleDose(maxSingleDose.toHapi())
    }
    if (hasMaxDosePerDay()) {
      hapiValue.setMaxDosePerDay(maxDosePerDay.toHapi())
    }
    if (hasMaxDosePerTreatmentPeriod()) {
      hapiValue.setMaxDosePerTreatmentPeriod(maxDosePerTreatmentPeriod.toHapi())
    }
    if (hasMaxTreatmentPeriod()) {
      hapiValue.setMaxTreatmentPeriod(maxTreatmentPeriod.toHapi())
    }
    if (targetSpeciesCount > 0) {
      hapiValue.setTargetSpecies(targetSpeciesList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPharmaceutical
        .MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesComponent()
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
    if (withdrawalPeriodCount > 0) {
      hapiValue.setWithdrawalPeriod(withdrawalPeriodList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies.WithdrawalPeriod.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesWithdrawalPeriodComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPharmaceutical
        .MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesWithdrawalPeriodComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasTissue()) {
      hapiValue.setTissue(tissue.toHapi())
    }
    if (hasValue()) {
      hapiValue.setValue(value.toHapi())
    }
    if (hasSupportingInformation()) {
      hapiValue.setSupportingInformationElement(supportingInformation.toHapi())
    }
    return hapiValue
  }
}
