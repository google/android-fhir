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

public object DeviceMetricConverter {
  public fun DeviceMetric.toHapi(): org.hl7.fhir.r4.model.DeviceMetric {
    val hapiValue = org.hl7.fhir.r4.model.DeviceMetric()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setUnit(unit.toHapi())
    hapiValue.setSource(source.toHapi())
    hapiValue.setParent(parent.toHapi())
    hapiValue.setOperationalStatus(org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricOperationalStatus.valueOf(operationalStatus.value.name.replace("_","")))
    hapiValue.setColor(org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricColor.valueOf(color.value.name.replace("_","")))
    hapiValue.setCategory(org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCategory.valueOf(category.value.name.replace("_","")))
    hapiValue.setMeasurementPeriod(measurementPeriod.toHapi())
    hapiValue.setCalibration(calibrationList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.DeviceMetric.toProto(): DeviceMetric {
    val protoValue = DeviceMetric.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setType(type.toProto())
    .setUnit(unit.toProto())
    .setSource(source.toProto())
    .setParent(parent.toProto())
    .setOperationalStatus(DeviceMetric.OperationalStatusCode.newBuilder().setValue(DeviceMetricOperationalStatusCode.Value.valueOf(operationalStatus.toCode().replace("-",
        "_").toUpperCase())).build())
    .setColor(DeviceMetric.ColorCode.newBuilder().setValue(DeviceMetricColorCode.Value.valueOf(color.toCode().replace("-",
        "_").toUpperCase())).build())
    .setCategory(DeviceMetric.CategoryCode.newBuilder().setValue(DeviceMetricCategoryCode.Value.valueOf(category.toCode().replace("-",
        "_").toUpperCase())).build())
    .setMeasurementPeriod(measurementPeriod.toProto())
    .addAllCalibration(calibration.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationComponent.toProto():
      DeviceMetric.Calibration {
    val protoValue = DeviceMetric.Calibration.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(DeviceMetric.Calibration.TypeCode.newBuilder().setValue(DeviceMetricCalibrationTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setState(DeviceMetric.Calibration.StateCode.newBuilder().setValue(DeviceMetricCalibrationStateCode.Value.valueOf(state.toCode().replace("-",
        "_").toUpperCase())).build())
    .setTime(timeElement.toProto())
    .build()
    return protoValue
  }

  private fun DeviceMetric.Calibration.toHapi():
      org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationType.valueOf(type.value.name.replace("_","")))
    hapiValue.setState(org.hl7.fhir.r4.model.DeviceMetric.DeviceMetricCalibrationState.valueOf(state.value.name.replace("_","")))
    hapiValue.setTimeElement(time.toHapi())
    return hapiValue
  }
}
