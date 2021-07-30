package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Device
import com.google.fhir.r4.core.Device.DeviceName
import com.google.fhir.r4.core.Device.UdiCarrier
import com.google.fhir.r4.core.DeviceNameTypeCode
import com.google.fhir.r4.core.FHIRDeviceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.UDIEntryTypeCode

public object DeviceConverter {
  public fun Device.toHapi(): org.hl7.fhir.r4.model.Device {
    val hapiValue = org.hl7.fhir.r4.model.Device()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setDefinition(definition.toHapi())
    hapiValue.setUdiCarrier(udiCarrierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Device.FHIRDeviceStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setStatusReason(statusReasonList.map{it.toHapi()})
    hapiValue.setDistinctIdentifierElement(distinctIdentifier.toHapi())
    hapiValue.setManufacturerElement(manufacturer.toHapi())
    hapiValue.setManufactureDateElement(manufactureDate.toHapi())
    hapiValue.setExpirationDateElement(expirationDate.toHapi())
    hapiValue.setLotNumberElement(lotNumber.toHapi())
    hapiValue.setSerialNumberElement(serialNumber.toHapi())
    hapiValue.setDeviceName(deviceNameList.map{it.toHapi()})
    hapiValue.setModelNumberElement(modelNumber.toHapi())
    hapiValue.setPartNumberElement(partNumber.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setSpecialization(specializationList.map{it.toHapi()})
    hapiValue.setVersion(versionList.map{it.toHapi()})
    hapiValue.setProperty(propertyList.map{it.toHapi()})
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setOwner(owner.toHapi())
    hapiValue.setContact(contactList.map{it.toHapi()})
    hapiValue.setLocation(location.toHapi())
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setNote(noteList.map{it.toHapi()})
    hapiValue.setSafety(safetyList.map{it.toHapi()})
    hapiValue.setParent(parent.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Device.toProto(): Device {
    val protoValue = Device.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setDefinition(definition.toProto())
    .addAllUdiCarrier(udiCarrier.map{it.toProto()})
    .setStatus(Device.StatusCode.newBuilder().setValue(FHIRDeviceStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllStatusReason(statusReason.map{it.toProto()})
    .setDistinctIdentifier(distinctIdentifierElement.toProto())
    .setManufacturer(manufacturerElement.toProto())
    .setManufactureDate(manufactureDateElement.toProto())
    .setExpirationDate(expirationDateElement.toProto())
    .setLotNumber(lotNumberElement.toProto())
    .setSerialNumber(serialNumberElement.toProto())
    .addAllDeviceName(deviceName.map{it.toProto()})
    .setModelNumber(modelNumberElement.toProto())
    .setPartNumber(partNumberElement.toProto())
    .setType(type.toProto())
    .addAllSpecialization(specialization.map{it.toProto()})
    .addAllVersion(version.map{it.toProto()})
    .addAllProperty(property.map{it.toProto()})
    .setPatient(patient.toProto())
    .setOwner(owner.toProto())
    .addAllContact(contact.map{it.toProto()})
    .setLocation(location.toProto())
    .setUrl(urlElement.toProto())
    .addAllNote(note.map{it.toProto()})
    .addAllSafety(safety.map{it.toProto()})
    .setParent(parent.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Device.DeviceUdiCarrierComponent.toProto(): Device.UdiCarrier {
    val protoValue = Device.UdiCarrier.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setDeviceIdentifier(deviceIdentifierElement.toProto())
    .setIssuer(issuerElement.toProto())
    .setJurisdiction(jurisdictionElement.toProto())
    .setCarrierAidc(carrierAIDCElement.toProto())
    .setCarrierHrf(carrierHRFElement.toProto())
    .setEntryType(Device.UdiCarrier.EntryTypeCode.newBuilder().setValue(UDIEntryTypeCode.Value.valueOf(entryType.toCode().replace("-",
        "_").toUpperCase())).build())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Device.DeviceDeviceNameComponent.toProto(): Device.DeviceName {
    val protoValue = Device.DeviceName.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setType(Device.DeviceName.TypeCode.newBuilder().setValue(DeviceNameTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Device.DeviceSpecializationComponent.toProto():
      Device.Specialization {
    val protoValue = Device.Specialization.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSystemType(systemType.toProto())
    .setVersion(versionElement.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Device.DeviceVersionComponent.toProto(): Device.Version {
    val protoValue = Device.Version.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setComponent(component.toProto())
    .setValue(valueElement.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Device.DevicePropertyComponent.toProto(): Device.Property {
    val protoValue = Device.Property.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .addAllValueQuantity(valueQuantity.map{it.toProto()})
    .addAllValueCode(valueCode.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun Device.UdiCarrier.toHapi(): org.hl7.fhir.r4.model.Device.DeviceUdiCarrierComponent {
    val hapiValue = org.hl7.fhir.r4.model.Device.DeviceUdiCarrierComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setDeviceIdentifierElement(deviceIdentifier.toHapi())
    hapiValue.setIssuerElement(issuer.toHapi())
    hapiValue.setJurisdictionElement(jurisdiction.toHapi())
    hapiValue.setCarrierAIDCElement(carrierAidc.toHapi())
    hapiValue.setCarrierHRFElement(carrierHrf.toHapi())
    hapiValue.setEntryType(org.hl7.fhir.r4.model.Device.UDIEntryType.valueOf(entryType.value.name.replace("_","")))
    return hapiValue
  }

  private fun Device.DeviceName.toHapi(): org.hl7.fhir.r4.model.Device.DeviceDeviceNameComponent {
    val hapiValue = org.hl7.fhir.r4.model.Device.DeviceDeviceNameComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setType(org.hl7.fhir.r4.model.Device.DeviceNameType.valueOf(type.value.name.replace("_","")))
    return hapiValue
  }

  private fun Device.Specialization.toHapi():
      org.hl7.fhir.r4.model.Device.DeviceSpecializationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Device.DeviceSpecializationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSystemType(systemType.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    return hapiValue
  }

  private fun Device.Version.toHapi(): org.hl7.fhir.r4.model.Device.DeviceVersionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Device.DeviceVersionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setComponent(component.toHapi())
    hapiValue.setValueElement(value.toHapi())
    return hapiValue
  }

  private fun Device.Property.toHapi(): org.hl7.fhir.r4.model.Device.DevicePropertyComponent {
    val hapiValue = org.hl7.fhir.r4.model.Device.DevicePropertyComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setValueQuantity(valueQuantityList.map{it.toHapi()})
    hapiValue.setValueCode(valueCodeList.map{it.toHapi()})
    return hapiValue
  }
}
