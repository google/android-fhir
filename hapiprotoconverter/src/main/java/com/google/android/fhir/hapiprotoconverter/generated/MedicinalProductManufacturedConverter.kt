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

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ProdCharacteristicConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ProdCharacteristicConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductManufactured
import kotlin.jvm.JvmStatic

public object MedicinalProductManufacturedConverter {
  @JvmStatic
  public fun MedicinalProductManufactured.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductManufactured {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductManufactured()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setManufacturedDoseForm(manufacturedDoseForm.toHapi())
    hapiValue.setUnitOfPresentation(unitOfPresentation.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setManufacturer(manufacturerList.map { it.toHapi() })
    hapiValue.setIngredient(ingredientList.map { it.toHapi() })
    hapiValue.setPhysicalCharacteristics(physicalCharacteristics.toHapi())
    hapiValue.setOtherCharacteristics(otherCharacteristicsList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicinalProductManufactured.toProto():
    MedicinalProductManufactured {
    val protoValue =
      MedicinalProductManufactured.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setLanguage(languageElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setManufacturedDoseForm(manufacturedDoseForm.toProto())
        .setUnitOfPresentation(unitOfPresentation.toProto())
        .setQuantity(quantity.toProto())
        .addAllManufacturer(manufacturer.map { it.toProto() })
        .addAllIngredient(ingredient.map { it.toProto() })
        .setPhysicalCharacteristics(physicalCharacteristics.toProto())
        .addAllOtherCharacteristics(otherCharacteristics.map { it.toProto() })
        .build()
    return protoValue
  }
}
