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
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DeviceMetric
import com.google.fhir.r4.core.DeviceMetric.Calibration
import com.google.fhir.r4.core.DeviceMetricCalibrationStateCode
import com.google.fhir.r4.core.DeviceMetricCalibrationTypeCode
import com.google.fhir.r4.core.DeviceMetricCategoryCode
import com.google.fhir.r4.core.DeviceMetricColorCode
import com.google.fhir.r4.core.DeviceMetricOperationalStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object DeviceMetricConverter {
  @JvmStatic
  fun DeviceMetric.toHapi(): org.hl7.fhir.r4.model.DeviceMetric {
    val hapiValue = org.hl7.fhir.r4.model.DeviceMetric()
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
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasUnit()) {
        hapiValue.unit = unit.toHapi()
    }
    if (hasSource()) {
        hapiValue.source = source.toHapi()
    }
    if (hasParent()) {
        hapiValue.parent = parent.toHapi()
    }
      hapiValue.operationalStatus = org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricOperationalStatus.valueOf(
          operationalStatus.value.name.hapiCodeCheck().replace("_", "")
      )
      hapiValue.color = org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricColor.valueOf(
          color.value.name.hapiCodeCheck().replace("_", "")
      )
      hapiValue.category = org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCategory.valueOf(
          category.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasMeasurementPeriod()) {
        hapiValue.measurementPeriod = measurementPeriod.toHapi()
    }
    if (calibrationCount > 0) {
        hapiValue.calibration = calibrationList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.DeviceMetric.toProto(): DeviceMetric {
    val protoValue = DeviceMetric.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasUnit()) {
        protoValue.unit = unit.toProto()
    }
    if (hasSource()) {
        protoValue.source = source.toProto()
    }
    if (hasParent()) {
        protoValue.parent = parent.toProto()
    }
      protoValue.operationalStatus = DeviceMetric.OperationalStatusCode.newBuilder()
          .setValue(
              DeviceMetricOperationalStatusCode.Value.valueOf(
                  operationalStatus.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
      protoValue.color = DeviceMetric.ColorCode.newBuilder()
          .setValue(
              DeviceMetricColorCode.Value.valueOf(
                  color.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
      protoValue.category = DeviceMetric.CategoryCode.newBuilder()
          .setValue(
              DeviceMetricCategoryCode.Value.valueOf(
                  category.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasMeasurementPeriod()) {
        protoValue.measurementPeriod = measurementPeriod.toProto()
    }
    if (hasCalibration()) {
      protoValue.addAllCalibration(calibration.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationComponent.toProto():
    DeviceMetric.Calibration {
    val protoValue = DeviceMetric.Calibration.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.type = DeviceMetric.Calibration.TypeCode.newBuilder()
          .setValue(
              DeviceMetricCalibrationTypeCode.Value.valueOf(
                  type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
      protoValue.state = DeviceMetric.Calibration.StateCode.newBuilder()
          .setValue(
              DeviceMetricCalibrationStateCode.Value.valueOf(
                  state.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasTime()) {
        protoValue.time = timeElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun DeviceMetric.Calibration.toHapi():
    org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.type = org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
      )
      hapiValue.state = org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationState.valueOf(
          state.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasTime()) {
        hapiValue.timeElement = time.toHapi()
    }
    return hapiValue
  }
}
