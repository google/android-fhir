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

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.VisionBaseCode
import com.google.fhir.r4.core.VisionEyesCode
import com.google.fhir.r4.core.VisionPrescription
import com.google.fhir.r4.core.VisionPrescription.LensSpecification
import com.google.fhir.r4.core.VisionPrescription.LensSpecification.Prism
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.SimpleQuantity

public object VisionPrescriptionConverter {
  @JvmStatic
  public fun VisionPrescription.toHapi(): org.hl7.fhir.r4.model.VisionPrescription {
    val hapiValue = org.hl7.fhir.r4.model.VisionPrescription()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.VisionPrescription.VisionStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setCreatedElement(created.toHapi())
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setDateWrittenElement(dateWritten.toHapi())
    hapiValue.setPrescriber(prescriber.toHapi())
    hapiValue.setLensSpecification(lensSpecificationList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.VisionPrescription.toProto(): VisionPrescription {
    val protoValue =
      VisionPrescription.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          VisionPrescription.StatusCode.newBuilder()
            .setValue(
              FinancialResourceStatusCode.Value.valueOf(
                status.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setCreated(createdElement.toProto())
        .setPatient(patient.toProto())
        .setEncounter(encounter.toProto())
        .setDateWritten(dateWrittenElement.toProto())
        .setPrescriber(prescriber.toProto())
        .addAllLensSpecification(lensSpecification.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.VisionPrescription.VisionPrescriptionLensSpecificationComponent.toProto():
    VisionPrescription.LensSpecification {
    val protoValue =
      VisionPrescription.LensSpecification.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setProduct(product.toProto())
        .setEye(
          VisionPrescription.LensSpecification.EyeCode.newBuilder()
            .setValue(VisionEyesCode.Value.valueOf(eye.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .setSphere(sphereElement.toProto())
        .setCylinder(cylinderElement.toProto())
        .setAxis(axisElement.toProto())
        .addAllPrism(prism.map { it.toProto() })
        .setAdd(addElement.toProto())
        .setPower(powerElement.toProto())
        .setBackCurve(backCurveElement.toProto())
        .setDiameter(diameterElement.toProto())
        .setDuration((duration as SimpleQuantity).toProto())
        .setColor(colorElement.toProto())
        .setBrand(brandElement.toProto())
        .addAllNote(note.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.VisionPrescription.PrismComponent.toProto():
    VisionPrescription.LensSpecification.Prism {
    val protoValue =
      VisionPrescription.LensSpecification.Prism.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setAmount(amountElement.toProto())
        .setBase(
          VisionPrescription.LensSpecification.Prism.BaseCode.newBuilder()
            .setValue(VisionBaseCode.Value.valueOf(base.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .build()
    return protoValue
  }

  @JvmStatic
  private fun VisionPrescription.LensSpecification.toHapi():
    org.hl7.fhir.r4.model.VisionPrescription.VisionPrescriptionLensSpecificationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.VisionPrescription.VisionPrescriptionLensSpecificationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setProduct(product.toHapi())
    hapiValue.setEye(
      org.hl7.fhir.r4.model.VisionPrescription.VisionEyes.valueOf(eye.value.name.replace("_", ""))
    )
    hapiValue.setSphereElement(sphere.toHapi())
    hapiValue.setCylinderElement(cylinder.toHapi())
    hapiValue.setAxisElement(axis.toHapi())
    hapiValue.setPrism(prismList.map { it.toHapi() })
    hapiValue.setAddElement(add.toHapi())
    hapiValue.setPowerElement(power.toHapi())
    hapiValue.setBackCurveElement(backCurve.toHapi())
    hapiValue.setDiameterElement(diameter.toHapi())
    hapiValue.setDuration(duration.toHapi())
    hapiValue.setColorElement(color.toHapi())
    hapiValue.setBrandElement(brand.toHapi())
    hapiValue.setNote(noteList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun VisionPrescription.LensSpecification.Prism.toHapi():
    org.hl7.fhir.r4.model.VisionPrescription.PrismComponent {
    val hapiValue = org.hl7.fhir.r4.model.VisionPrescription.PrismComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setAmountElement(amount.toHapi())
    hapiValue.setBase(
      org.hl7.fhir.r4.model.VisionPrescription.VisionBase.valueOf(base.value.name.replace("_", ""))
    )
    return hapiValue
  }
}
