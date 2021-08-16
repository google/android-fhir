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
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.BiologicallyDerivedProduct
import com.google.fhir.r4.core.BiologicallyDerivedProduct.Collection
import com.google.fhir.r4.core.BiologicallyDerivedProduct.Manipulation
import com.google.fhir.r4.core.BiologicallyDerivedProduct.Processing
import com.google.fhir.r4.core.BiologicallyDerivedProduct.Storage
import com.google.fhir.r4.core.BiologicallyDerivedProductCategoryCode
import com.google.fhir.r4.core.BiologicallyDerivedProductStatusCode
import com.google.fhir.r4.core.BiologicallyDerivedProductStorageScaleCode
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

object BiologicallyDerivedProductConverter {
  @JvmStatic
  private fun BiologicallyDerivedProduct.Collection.CollectedX.biologicallyDerivedProductCollectionCollectedToHapi():
    Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for BiologicallyDerivedProduct.collection.collected[x]"
    )
  }

  @JvmStatic
  private fun Type.biologicallyDerivedProductCollectionCollectedToProto():
    BiologicallyDerivedProduct.Collection.CollectedX {
    val protoValue = BiologicallyDerivedProduct.Collection.CollectedX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun BiologicallyDerivedProduct.Processing.TimeX.biologicallyDerivedProductProcessingTimeToHapi():
    Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for BiologicallyDerivedProduct.processing.time[x]")
  }

  @JvmStatic
  private fun Type.biologicallyDerivedProductProcessingTimeToProto():
    BiologicallyDerivedProduct.Processing.TimeX {
    val protoValue = BiologicallyDerivedProduct.Processing.TimeX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun BiologicallyDerivedProduct.Manipulation.TimeX.biologicallyDerivedProductManipulationTimeToHapi():
    Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for BiologicallyDerivedProduct.manipulation.time[x]"
    )
  }

  @JvmStatic
  private fun Type.biologicallyDerivedProductManipulationTimeToProto():
    BiologicallyDerivedProduct.Manipulation.TimeX {
    val protoValue = BiologicallyDerivedProduct.Manipulation.TimeX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun BiologicallyDerivedProduct.toHapi(): org.hl7.fhir.r4.model.BiologicallyDerivedProduct {
    val hapiValue = org.hl7.fhir.r4.model.BiologicallyDerivedProduct()
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    hapiValue.productCategory =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductCategory.valueOf(
        productCategory.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasProductCode()) {
      hapiValue.productCode = productCode.toHapi()
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (requestCount > 0) {
      hapiValue.request = requestList.map { it.toHapi() }
    }
    if (hasQuantity()) {
      hapiValue.quantityElement = quantity.toHapi()
    }
    if (parentCount > 0) {
      hapiValue.parent = parentList.map { it.toHapi() }
    }
    if (hasCollection()) {
      hapiValue.collection = collection.toHapi()
    }
    if (processingCount > 0) {
      hapiValue.processing = processingList.map { it.toHapi() }
    }
    if (hasManipulation()) {
      hapiValue.manipulation = manipulation.toHapi()
    }
    if (storageCount > 0) {
      hapiValue.storage = storageList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.BiologicallyDerivedProduct.toProto(): BiologicallyDerivedProduct {
    val protoValue = BiologicallyDerivedProduct.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    protoValue.productCategory =
      BiologicallyDerivedProduct.ProductCategoryCode.newBuilder()
        .setValue(
          BiologicallyDerivedProductCategoryCode.Value.valueOf(
            productCategory.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasProductCode()) {
      protoValue.productCode = productCode.toProto()
    }
    protoValue.status =
      BiologicallyDerivedProduct.StatusCode.newBuilder()
        .setValue(
          BiologicallyDerivedProductStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasRequest()) {
      protoValue.addAllRequest(request.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.quantity = quantityElement.toProto()
    }
    if (hasParent()) {
      protoValue.addAllParent(parent.map { it.toProto() })
    }
    if (hasCollection()) {
      protoValue.collection = collection.toProto()
    }
    if (hasProcessing()) {
      protoValue.addAllProcessing(processing.map { it.toProto() })
    }
    if (hasManipulation()) {
      protoValue.manipulation = manipulation.toProto()
    }
    if (hasStorage()) {
      protoValue.addAllStorage(storage.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductCollectionComponent.toProto():
    BiologicallyDerivedProduct.Collection {
    val protoValue =
      BiologicallyDerivedProduct.Collection.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCollector()) {
      protoValue.collector = collector.toProto()
    }
    if (hasSource()) {
      protoValue.source = source.toProto()
    }
    if (hasCollected()) {
      protoValue.collected = collected.biologicallyDerivedProductCollectionCollectedToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductProcessingComponent.toProto():
    BiologicallyDerivedProduct.Processing {
    val protoValue =
      BiologicallyDerivedProduct.Processing.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasProcedure()) {
      protoValue.procedure = procedure.toProto()
    }
    if (hasAdditive()) {
      protoValue.additive = additive.toProto()
    }
    if (hasTime()) {
      protoValue.time = time.biologicallyDerivedProductProcessingTimeToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductManipulationComponent.toProto():
    BiologicallyDerivedProduct.Manipulation {
    val protoValue =
      BiologicallyDerivedProduct.Manipulation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasTime()) {
      protoValue.time = time.biologicallyDerivedProductManipulationTimeToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductStorageComponent.toProto():
    BiologicallyDerivedProduct.Storage {
    val protoValue =
      BiologicallyDerivedProduct.Storage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasTemperature()) {
      protoValue.temperature = temperatureElement.toProto()
    }
    protoValue.scale =
      BiologicallyDerivedProduct.Storage.ScaleCode.newBuilder()
        .setValue(
          BiologicallyDerivedProductStorageScaleCode.Value.valueOf(
            scale.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasDuration()) {
      protoValue.duration = duration.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun BiologicallyDerivedProduct.Collection.toHapi():
    org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductCollectionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct
        .BiologicallyDerivedProductCollectionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCollector()) {
      hapiValue.collector = collector.toHapi()
    }
    if (hasSource()) {
      hapiValue.source = source.toHapi()
    }
    if (hasCollected()) {
      hapiValue.collected = collected.biologicallyDerivedProductCollectionCollectedToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun BiologicallyDerivedProduct.Processing.toHapi():
    org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductProcessingComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct
        .BiologicallyDerivedProductProcessingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasProcedure()) {
      hapiValue.procedure = procedure.toHapi()
    }
    if (hasAdditive()) {
      hapiValue.additive = additive.toHapi()
    }
    if (hasTime()) {
      hapiValue.time = time.biologicallyDerivedProductProcessingTimeToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun BiologicallyDerivedProduct.Manipulation.toHapi():
    org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductManipulationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct
        .BiologicallyDerivedProductManipulationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasTime()) {
      hapiValue.time = time.biologicallyDerivedProductManipulationTimeToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun BiologicallyDerivedProduct.Storage.toHapi():
    org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductStorageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductStorageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasTemperature()) {
      hapiValue.temperatureElement = temperature.toHapi()
    }
    hapiValue.scale =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductStorageScale
        .valueOf(scale.value.name.hapiCodeCheck().replace("_", ""))
    if (hasDuration()) {
      hapiValue.duration = duration.toHapi()
    }
    return hapiValue
  }
}
