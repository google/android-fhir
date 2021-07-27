package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.ProdCharacteristic
import com.google.fhir.r4.core.String

public fun ProdCharacteristic.toHapi(): org.hl7.fhir.r4.model.ProdCharacteristic {
  val hapiValue = org.hl7.fhir.r4.model.ProdCharacteristic()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
  hapiValue.setHeight(height.toHapi())
  hapiValue.setWidth(width.toHapi())
  hapiValue.setDepth(depth.toHapi())
  hapiValue.setWeight(weight.toHapi())
  hapiValue.setNominalVolume(nominalVolume.toHapi())
  hapiValue.setExternalDiameter(externalDiameter.toHapi())
  hapiValue.setShapeElement(shape.toHapi())
  hapiValue.setColor(colorList.map{it.toHapi()})
  hapiValue.setImprint(imprintList.map{it.toHapi()})
  hapiValue.setImage(imageList.map{it.toHapi()})
  hapiValue.setScoring(scoring.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.ProdCharacteristic.toProto(): ProdCharacteristic {
  val protoValue = ProdCharacteristic.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .addAllModifierExtension(modifierExtension.map{it.toProto()})
  .setHeight(height.toProto())
  .setWidth(width.toProto())
  .setDepth(depth.toProto())
  .setWeight(weight.toProto())
  .setNominalVolume(nominalVolume.toProto())
  .setExternalDiameter(externalDiameter.toProto())
  .setShape(shapeElement.toProto())
  .addAllColor(color.map{it.toProto()})
  .addAllImprint(imprint.map{it.toProto()})
  .addAllImage(image.map{it.toProto()})
  .setScoring(scoring.toProto())
  .build()
  return protoValue
}
