package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Group
import com.google.fhir.r4.core.GroupTypeCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Type

public object GroupConverter {
  public fun Group.Characteristic.ValueX.groupCharacteristicValueToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Group.characteristic.value[x]")
  }

  public fun Type.groupCharacteristicValueToProto(): Group.Characteristic.ValueX {
    val protoValue = Group.Characteristic.ValueX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun Group.toHapi(): org.hl7.fhir.r4.model.Group {
    val hapiValue = org.hl7.fhir.r4.model.Group()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setActiveElement(active.toHapi())
    hapiValue.setType(org.hl7.fhir.r4.model.Group.GroupType.valueOf(type.value.name.replace("_","")))
    hapiValue.setActualElement(actual.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setQuantityElement(quantity.toHapi())
    hapiValue.setManagingEntity(managingEntity.toHapi())
    hapiValue.setCharacteristic(characteristicList.map{it.toHapi()})
    hapiValue.setMember(memberList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Group.toProto(): Group {
    val protoValue = Group.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setActive(activeElement.toProto())
    .setType(Group.TypeCode.newBuilder().setValue(GroupTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setActual(actualElement.toProto())
    .setCode(code.toProto())
    .setName(nameElement.toProto())
    .setQuantity(quantityElement.toProto())
    .setManagingEntity(managingEntity.toProto())
    .addAllCharacteristic(characteristic.map{it.toProto()})
    .addAllMember(member.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Group.GroupCharacteristicComponent.toProto():
      Group.Characteristic {
    val protoValue = Group.Characteristic.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(code.toProto())
    .setValue(value.groupCharacteristicValueToProto())
    .setExclude(excludeElement.toProto())
    .setPeriod(period.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Group.GroupMemberComponent.toProto(): Group.Member {
    val protoValue = Group.Member.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setEntity(entity.toProto())
    .setPeriod(period.toProto())
    .setInactive(inactiveElement.toProto())
    .build()
    return protoValue
  }

  public fun Group.Characteristic.toHapi():
      org.hl7.fhir.r4.model.Group.GroupCharacteristicComponent {
    val hapiValue = org.hl7.fhir.r4.model.Group.GroupCharacteristicComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setValue(value.groupCharacteristicValueToHapi())
    hapiValue.setExcludeElement(exclude.toHapi())
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }

  public fun Group.Member.toHapi(): org.hl7.fhir.r4.model.Group.GroupMemberComponent {
    val hapiValue = org.hl7.fhir.r4.model.Group.GroupMemberComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setEntity(entity.toHapi())
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setInactiveElement(inactive.toHapi())
    return hapiValue
  }
}
