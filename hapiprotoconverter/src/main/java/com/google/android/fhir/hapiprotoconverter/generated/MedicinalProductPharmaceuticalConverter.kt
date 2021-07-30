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

public object MedicinalProductPharmaceuticalConverter {
  public fun MedicinalProductPharmaceutical.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPharmaceutical {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductPharmaceutical()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setAdministrableDoseForm(administrableDoseForm.toHapi())
    hapiValue.setUnitOfPresentation(unitOfPresentation.toHapi())
    hapiValue.setIngredient(ingredientList.map { it.toHapi() })
    hapiValue.setDevice(deviceList.map { it.toHapi() })
    hapiValue.setCharacteristics(characteristicsList.map { it.toHapi() })
    hapiValue.setRouteOfAdministration(routeOfAdministrationList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.toProto():
    MedicinalProductPharmaceutical {
    val protoValue =
      MedicinalProductPharmaceutical.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setAdministrableDoseForm(administrableDoseForm.toProto())
        .setUnitOfPresentation(unitOfPresentation.toProto())
        .addAllIngredient(ingredient.map { it.toProto() })
        .addAllDevice(device.map { it.toProto() })
        .addAllCharacteristics(characteristics.map { it.toProto() })
        .addAllRouteOfAdministration(routeOfAdministration.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalCharacteristicsComponent.toProto():
    MedicinalProductPharmaceutical.Characteristics {
    val protoValue =
      MedicinalProductPharmaceutical.Characteristics.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setStatus(status.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationComponent.toProto():
    MedicinalProductPharmaceutical.RouteOfAdministration {
    val protoValue =
      MedicinalProductPharmaceutical.RouteOfAdministration.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setFirstDose(firstDose.toProto())
        .setMaxSingleDose(maxSingleDose.toProto())
        .setMaxDosePerDay(maxDosePerDay.toProto())
        .setMaxDosePerTreatmentPeriod(maxDosePerTreatmentPeriod.toProto())
        .setMaxTreatmentPeriod(maxTreatmentPeriod.toProto())
        .addAllTargetSpecies(targetSpecies.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesComponent.toProto():
    MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies {
    val protoValue =
      MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .addAllWithdrawalPeriod(withdrawalPeriod.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesWithdrawalPeriodComponent.toProto():
    MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies.WithdrawalPeriod {
    val protoValue =
      MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies.WithdrawalPeriod
        .newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setTissue(tissue.toProto())
        .setValue(value.toProto())
        .setSupportingInformation(supportingInformationElement.toProto())
        .build()
    return protoValue
  }

  private fun MedicinalProductPharmaceutical.Characteristics.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalCharacteristicsComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPharmaceutical
        .MedicinalProductPharmaceuticalCharacteristicsComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setStatus(status.toHapi())
    return hapiValue
  }

  private fun MedicinalProductPharmaceutical.RouteOfAdministration.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPharmaceutical
        .MedicinalProductPharmaceuticalRouteOfAdministrationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setFirstDose(firstDose.toHapi())
    hapiValue.setMaxSingleDose(maxSingleDose.toHapi())
    hapiValue.setMaxDosePerDay(maxDosePerDay.toHapi())
    hapiValue.setMaxDosePerTreatmentPeriod(maxDosePerTreatmentPeriod.toHapi())
    hapiValue.setMaxTreatmentPeriod(maxTreatmentPeriod.toHapi())
    hapiValue.setTargetSpecies(targetSpeciesList.map { it.toHapi() })
    return hapiValue
  }

  private fun MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPharmaceutical
        .MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setWithdrawalPeriod(withdrawalPeriodList.map { it.toHapi() })
    return hapiValue
  }

  private fun MedicinalProductPharmaceutical.RouteOfAdministration.TargetSpecies.WithdrawalPeriod.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPharmaceutical.MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesWithdrawalPeriodComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPharmaceutical
        .MedicinalProductPharmaceuticalRouteOfAdministrationTargetSpeciesWithdrawalPeriodComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setTissue(tissue.toHapi())
    hapiValue.setValue(value.toHapi())
    hapiValue.setSupportingInformationElement(supportingInformation.toHapi())
    return hapiValue
  }
}
