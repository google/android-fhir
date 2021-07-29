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

public object ProdCharacteristicConverter {
  public fun ProdCharacteristic.toHapi(): org.hl7.fhir.r4.model.ProdCharacteristic {
    val hapiValue = org.hl7.fhir.r4.model.ProdCharacteristic()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setHeight(height.toHapi())
    hapiValue.setWidth(width.toHapi())
    hapiValue.setDepth(depth.toHapi())
    hapiValue.setWeight(weight.toHapi())
    hapiValue.setNominalVolume(nominalVolume.toHapi())
    hapiValue.setExternalDiameter(externalDiameter.toHapi())
    hapiValue.setShapeElement(shape.toHapi())
    hapiValue.setColor(colorList.map { it.toHapi() })
    hapiValue.setImprint(imprintList.map { it.toHapi() })
    hapiValue.setImage(imageList.map { it.toHapi() })
    hapiValue.setScoring(scoring.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ProdCharacteristic.toProto(): ProdCharacteristic {
    val protoValue =
      ProdCharacteristic.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setHeight(height.toProto())
        .setWidth(width.toProto())
        .setDepth(depth.toProto())
        .setWeight(weight.toProto())
        .setNominalVolume(nominalVolume.toProto())
        .setExternalDiameter(externalDiameter.toProto())
        .setShape(shapeElement.toProto())
        .addAllColor(color.map { it.toProto() })
        .addAllImprint(imprint.map { it.toProto() })
        .addAllImage(image.map { it.toProto() })
        .setScoring(scoring.toProto())
        .build()
    return protoValue
  }
}
