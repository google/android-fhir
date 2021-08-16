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

public object ProdCharacteristicConverter {
  @JvmStatic
  public fun ProdCharacteristic.toHapi(): org.hl7.fhir.r4.model.ProdCharacteristic {
    val hapiValue = org.hl7.fhir.r4.model.ProdCharacteristic()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasHeight()) {
      hapiValue.setHeight(height.toHapi())
    }
    if (hasWidth()) {
      hapiValue.setWidth(width.toHapi())
    }
    if (hasDepth()) {
      hapiValue.setDepth(depth.toHapi())
    }
    if (hasWeight()) {
      hapiValue.setWeight(weight.toHapi())
    }
    if (hasNominalVolume()) {
      hapiValue.setNominalVolume(nominalVolume.toHapi())
    }
    if (hasExternalDiameter()) {
      hapiValue.setExternalDiameter(externalDiameter.toHapi())
    }
    if (hasShape()) {
      hapiValue.setShapeElement(shape.toHapi())
    }
    if (colorCount > 0) {
      hapiValue.setColor(colorList.map { it.toHapi() })
    }
    if (imprintCount > 0) {
      hapiValue.setImprint(imprintList.map { it.toHapi() })
    }
    if (imageCount > 0) {
      hapiValue.setImage(imageList.map { it.toHapi() })
    }
    if (hasScoring()) {
      hapiValue.setScoring(scoring.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ProdCharacteristic.toProto(): ProdCharacteristic {
    val protoValue = ProdCharacteristic.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasHeight()) {
      protoValue.setHeight(height.toProto())
    }
    if (hasWidth()) {
      protoValue.setWidth(width.toProto())
    }
    if (hasDepth()) {
      protoValue.setDepth(depth.toProto())
    }
    if (hasWeight()) {
      protoValue.setWeight(weight.toProto())
    }
    if (hasNominalVolume()) {
      protoValue.setNominalVolume(nominalVolume.toProto())
    }
    if (hasExternalDiameter()) {
      protoValue.setExternalDiameter(externalDiameter.toProto())
    }
    if (hasShape()) {
      protoValue.setShape(shapeElement.toProto())
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
      protoValue.setScoring(scoring.toProto())
    }
    return protoValue.build()
  }
}
