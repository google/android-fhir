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

public object DeviceMetricConverter {
  @JvmStatic
  public fun DeviceMetric.toHapi(): org.hl7.fhir.r4.model.DeviceMetric {
    val hapiValue = org.hl7.fhir.r4.model.DeviceMetric()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.setMeta(meta.toHapi())
    }
    if (hasImplicitRules()) {
      hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    }
    if (hasText()) {
      hapiValue.setText(text.toHapi())
    }
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasUnit()) {
      hapiValue.setUnit(unit.toHapi())
    }
    if (hasSource()) {
      hapiValue.setSource(source.toHapi())
    }
    if (hasParent()) {
      hapiValue.setParent(parent.toHapi())
    }
    hapiValue.setOperationalStatus(
      org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricOperationalStatus.valueOf(
        operationalStatus.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setColor(
      org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricColor.valueOf(
        color.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setCategory(
      org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCategory.valueOf(
        category.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasMeasurementPeriod()) {
      hapiValue.setMeasurementPeriod(measurementPeriod.toHapi())
    }
    if (calibrationCount > 0) {
      hapiValue.setCalibration(calibrationList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.DeviceMetric.toProto(): DeviceMetric {
    val protoValue = DeviceMetric.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.setMeta(meta.toProto())
    }
    if (hasImplicitRules()) {
      protoValue.setImplicitRules(implicitRulesElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(text.toProto())
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
      protoValue.setType(type.toProto())
    }
    if (hasUnit()) {
      protoValue.setUnit(unit.toProto())
    }
    if (hasSource()) {
      protoValue.setSource(source.toProto())
    }
    if (hasParent()) {
      protoValue.setParent(parent.toProto())
    }
    protoValue.setOperationalStatus(
      DeviceMetric.OperationalStatusCode.newBuilder()
        .setValue(
          DeviceMetricOperationalStatusCode.Value.valueOf(
            operationalStatus.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.setColor(
      DeviceMetric.ColorCode.newBuilder()
        .setValue(
          DeviceMetricColorCode.Value.valueOf(
            color.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.setCategory(
      DeviceMetric.CategoryCode.newBuilder()
        .setValue(
          DeviceMetricCategoryCode.Value.valueOf(
            category.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasMeasurementPeriod()) {
      protoValue.setMeasurementPeriod(measurementPeriod.toProto())
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
    protoValue.setType(
      DeviceMetric.Calibration.TypeCode.newBuilder()
        .setValue(
          DeviceMetricCalibrationTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.setState(
      DeviceMetric.Calibration.StateCode.newBuilder()
        .setValue(
          DeviceMetricCalibrationStateCode.Value.valueOf(
            state.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasTime()) {
      protoValue.setTime(timeElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun DeviceMetric.Calibration.toHapi():
    org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setType(
      org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setState(
      org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationState.valueOf(
        state.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasTime()) {
      hapiValue.setTimeElement(time.toHapi())
    }
    return hapiValue
  }
}
