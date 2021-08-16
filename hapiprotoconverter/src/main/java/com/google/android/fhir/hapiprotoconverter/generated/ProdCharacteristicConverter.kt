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
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.ProdCharacteristic
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object ProdCharacteristicConverter {
  @JvmStatic
  fun ProdCharacteristic.toHapi(): org.hl7.fhir.r4.model.ProdCharacteristic {
    val hapiValue = org.hl7.fhir.r4.model.ProdCharacteristic()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasHeight()) {
      hapiValue.height = height.toHapi()
    }
    if (hasWidth()) {
      hapiValue.width = width.toHapi()
    }
    if (hasDepth()) {
      hapiValue.depth = depth.toHapi()
    }
    if (hasWeight()) {
      hapiValue.weight = weight.toHapi()
    }
    if (hasNominalVolume()) {
      hapiValue.nominalVolume = nominalVolume.toHapi()
    }
    if (hasExternalDiameter()) {
      hapiValue.externalDiameter = externalDiameter.toHapi()
    }
    if (hasShape()) {
      hapiValue.shapeElement = shape.toHapi()
    }
    if (colorCount > 0) {
      hapiValue.color = colorList.map { it.toHapi() }
    }
    if (imprintCount > 0) {
      hapiValue.imprint = imprintList.map { it.toHapi() }
    }
    if (imageCount > 0) {
      hapiValue.image = imageList.map { it.toHapi() }
    }
    if (hasScoring()) {
      hapiValue.scoring = scoring.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.ProdCharacteristic.toProto(): ProdCharacteristic {
    val protoValue = ProdCharacteristic.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasHeight()) {
      protoValue.height = height.toProto()
    }
    if (hasWidth()) {
      protoValue.width = width.toProto()
    }
    if (hasDepth()) {
      protoValue.depth = depth.toProto()
    }
    if (hasWeight()) {
      protoValue.weight = weight.toProto()
    }
    if (hasNominalVolume()) {
      protoValue.nominalVolume = nominalVolume.toProto()
    }
    if (hasExternalDiameter()) {
      protoValue.externalDiameter = externalDiameter.toProto()
    }
    if (hasShape()) {
      protoValue.shape = shapeElement.toProto()
    }
    if (hasColor()) {
      protoValue.addAllColor(color.map { it.toProto() })
    }
    if (hasImprint()) {
      protoValue.addAllImprint(imprint.map { it.toProto() })
    }
    if (hasImage()) {
      protoValue.addAllImage(image.map { it.toProto() })
    }
    if (hasScoring()) {
      protoValue.scoring = scoring.toProto()
    }
    return protoValue.build()
  }
}
