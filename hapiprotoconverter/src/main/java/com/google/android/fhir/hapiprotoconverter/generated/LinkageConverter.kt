package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Linkage
import com.google.fhir.r4.core.Linkage.Item
import com.google.fhir.r4.core.LinkageTypeCode
import com.google.fhir.r4.core.String

public object LinkageConverter {
  public fun Linkage.toHapi(): org.hl7.fhir.r4.model.Linkage {
    val hapiValue = org.hl7.fhir.r4.model.Linkage()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setActiveElement(active.toHapi())
    hapiValue.setAuthor(author.toHapi())
    hapiValue.setItem(itemList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Linkage.toProto(): Linkage {
    val protoValue = Linkage.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setActive(activeElement.toProto())
    .setAuthor(author.toProto())
    .addAllItem(item.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Linkage.LinkageItemComponent.toProto(): Linkage.Item {
    val protoValue = Linkage.Item.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(Linkage.Item.TypeCode.newBuilder().setValue(LinkageTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setResource(resource.toProto())
    .build()
    return protoValue
  }

  private fun Linkage.Item.toHapi(): org.hl7.fhir.r4.model.Linkage.LinkageItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.Linkage.LinkageItemComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(org.hl7.fhir.r4.model.Linkage.LinkageType.valueOf(type.value.name.replace("_","")))
    hapiValue.setResource(resource.toHapi())
    return hapiValue
  }
}
