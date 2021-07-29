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
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object BiologicallyDerivedProductConverter {
  private fun BiologicallyDerivedProduct.Collection.CollectedX.biologicallyDerivedProductCollectionCollectedToHapi():
    Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for BiologicallyDerivedProduct.collection.collected[x]"
    )
  }

  private fun Type.biologicallyDerivedProductCollectionCollectedToProto():
    BiologicallyDerivedProduct.Collection.CollectedX {
    val protoValue = BiologicallyDerivedProduct.Collection.CollectedX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  private fun BiologicallyDerivedProduct.Processing.TimeX.biologicallyDerivedProductProcessingTimeToHapi():
    Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for BiologicallyDerivedProduct.processing.time[x]")
  }

  private fun Type.biologicallyDerivedProductProcessingTimeToProto():
    BiologicallyDerivedProduct.Processing.TimeX {
    val protoValue = BiologicallyDerivedProduct.Processing.TimeX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  private fun BiologicallyDerivedProduct.Manipulation.TimeX.biologicallyDerivedProductManipulationTimeToHapi():
    Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for BiologicallyDerivedProduct.manipulation.time[x]"
    )
  }

  private fun Type.biologicallyDerivedProductManipulationTimeToProto():
    BiologicallyDerivedProduct.Manipulation.TimeX {
    val protoValue = BiologicallyDerivedProduct.Manipulation.TimeX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  public fun BiologicallyDerivedProduct.toHapi(): org.hl7.fhir.r4.model.BiologicallyDerivedProduct {
    val hapiValue = org.hl7.fhir.r4.model.BiologicallyDerivedProduct()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setProductCategory(
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductCategory.valueOf(
        productCategory.value.name.replace("_", "")
      )
    )
    hapiValue.setProductCode(productCode.toHapi())
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setRequest(requestList.map { it.toHapi() })
    hapiValue.setQuantityElement(quantity.toHapi())
    hapiValue.setParent(parentList.map { it.toHapi() })
    hapiValue.setCollection(collection.toHapi())
    hapiValue.setProcessing(processingList.map { it.toHapi() })
    hapiValue.setManipulation(manipulation.toHapi())
    hapiValue.setStorage(storageList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.BiologicallyDerivedProduct.toProto():
    BiologicallyDerivedProduct {
    val protoValue =
      BiologicallyDerivedProduct.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setProductCategory(
          BiologicallyDerivedProduct.ProductCategoryCode.newBuilder()
            .setValue(
              BiologicallyDerivedProductCategoryCode.Value.valueOf(
                productCategory.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setProductCode(productCode.toProto())
        .setStatus(
          BiologicallyDerivedProduct.StatusCode.newBuilder()
            .setValue(
              BiologicallyDerivedProductStatusCode.Value.valueOf(
                status.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .addAllRequest(request.map { it.toProto() })
        .setQuantity(quantityElement.toProto())
        .addAllParent(parent.map { it.toProto() })
        .setCollection(collection.toProto())
        .addAllProcessing(processing.map { it.toProto() })
        .setManipulation(manipulation.toProto())
        .addAllStorage(storage.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductCollectionComponent.toProto():
    BiologicallyDerivedProduct.Collection {
    val protoValue =
      BiologicallyDerivedProduct.Collection.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCollector(collector.toProto())
        .setSource(source.toProto())
        .setCollected(collected.biologicallyDerivedProductCollectionCollectedToProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductProcessingComponent.toProto():
    BiologicallyDerivedProduct.Processing {
    val protoValue =
      BiologicallyDerivedProduct.Processing.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setProcedure(procedure.toProto())
        .setAdditive(additive.toProto())
        .setTime(time.biologicallyDerivedProductProcessingTimeToProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductManipulationComponent.toProto():
    BiologicallyDerivedProduct.Manipulation {
    val protoValue =
      BiologicallyDerivedProduct.Manipulation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setTime(time.biologicallyDerivedProductManipulationTimeToProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductStorageComponent.toProto():
    BiologicallyDerivedProduct.Storage {
    val protoValue =
      BiologicallyDerivedProduct.Storage.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setTemperature(temperatureElement.toProto())
        .setScale(
          BiologicallyDerivedProduct.Storage.ScaleCode.newBuilder()
            .setValue(
              BiologicallyDerivedProductStorageScaleCode.Value.valueOf(
                scale.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setDuration(duration.toProto())
        .build()
    return protoValue
  }

  private fun BiologicallyDerivedProduct.Collection.toHapi():
    org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductCollectionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct
        .BiologicallyDerivedProductCollectionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCollector(collector.toHapi())
    hapiValue.setSource(source.toHapi())
    hapiValue.setCollected(collected.biologicallyDerivedProductCollectionCollectedToHapi())
    return hapiValue
  }

  private fun BiologicallyDerivedProduct.Processing.toHapi():
    org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductProcessingComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct
        .BiologicallyDerivedProductProcessingComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setProcedure(procedure.toHapi())
    hapiValue.setAdditive(additive.toHapi())
    hapiValue.setTime(time.biologicallyDerivedProductProcessingTimeToHapi())
    return hapiValue
  }

  private fun BiologicallyDerivedProduct.Manipulation.toHapi():
    org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductManipulationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct
        .BiologicallyDerivedProductManipulationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setTime(time.biologicallyDerivedProductManipulationTimeToHapi())
    return hapiValue
  }

  private fun BiologicallyDerivedProduct.Storage.toHapi():
    org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductStorageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductStorageComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setTemperatureElement(temperature.toHapi())
    hapiValue.setScale(
      org.hl7.fhir.r4.model.BiologicallyDerivedProduct.BiologicallyDerivedProductStorageScale
        .valueOf(scale.value.name.replace("_", ""))
    )
    hapiValue.setDuration(duration.toHapi())
    return hapiValue
  }
}
