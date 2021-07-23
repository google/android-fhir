package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.AuditEvent
import com.google.fhir.r4.core.AuditEventActionCode
import com.google.fhir.r4.core.AuditEventAgentNetworkTypeCode
import com.google.fhir.r4.core.AuditEventOutcomeCode
import com.google.fhir.r4.core.Base64Binary
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Base64BinaryType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object AuditEventConverter {
  public fun AuditEvent.Entity.Detail.ValueX.auditEventEntityDetailValueToHapi(): Type {
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getBase64Binary() != Base64Binary.newBuilder().defaultInstanceForType ) {
      return (this.getBase64Binary()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for AuditEvent.entity.detail.value[x]")
  }

  public fun Type.auditEventEntityDetailValueToProto(): AuditEvent.Entity.Detail.ValueX {
    val protoValue = AuditEvent.Entity.Detail.ValueX.newBuilder()
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is Base64BinaryType) {
      protoValue.setBase64Binary(this.toProto())
    }
    return protoValue.build()
  }

  public fun AuditEvent.toHapi(): org.hl7.fhir.r4.model.AuditEvent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setSubtype(subtypeList.map{it.toHapi()})
    hapiValue.setAction(org.hl7.fhir.r4.model.AuditEvent.AuditEventAction.valueOf(action.value.name.replace("_","")))
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setRecordedElement(recorded.toHapi())
    hapiValue.setOutcome(org.hl7.fhir.r4.model.AuditEvent.AuditEventOutcome.valueOf(outcome.value.name.replace("_","")))
    hapiValue.setOutcomeDescElement(outcomeDesc.toHapi())
    hapiValue.setPurposeOfEvent(purposeOfEventList.map{it.toHapi()})
    hapiValue.setAgent(agentList.map{it.toHapi()})
    hapiValue.setSource(source.toHapi())
    hapiValue.setEntity(entityList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.AuditEvent.toProto(): AuditEvent {
    val protoValue = AuditEvent.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .addAllSubtype(subtype.map{it.toProto()})
    .setAction(AuditEvent.ActionCode.newBuilder().setValue(AuditEventActionCode.Value.valueOf(action.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPeriod(period.toProto())
    .setRecorded(recordedElement.toProto())
    .setOutcome(AuditEvent.OutcomeCode.newBuilder().setValue(AuditEventOutcomeCode.Value.valueOf(outcome.toCode().replace("-",
        "_").toUpperCase())).build())
    .setOutcomeDesc(outcomeDescElement.toProto())
    .addAllPurposeOfEvent(purposeOfEvent.map{it.toProto()})
    .addAllAgent(agent.map{it.toProto()})
    .setSource(source.toProto())
    .addAllEntity(entity.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent.toProto(): AuditEvent.Agent {
    val protoValue = AuditEvent.Agent.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .addAllRole(role.map{it.toProto()})
    .setWho(who.toProto())
    .setAltId(altIdElement.toProto())
    .setName(nameElement.toProto())
    .setRequestor(requestorElement.toProto())
    .setLocation(location.toProto())
    .addAllPolicy(policy.map{it.toProto()})
    .setMedia(media.toProto())
    .setNetwork(network.toProto())
    .addAllPurposeOfUse(purposeOfUse.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkComponent.toProto():
      AuditEvent.Agent.Network {
    val protoValue = AuditEvent.Agent.Network.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setAddress(addressElement.toProto())
    .setType(AuditEvent.Agent.Network.TypeCode.newBuilder().setValue(AuditEventAgentNetworkTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent.toProto():
      AuditEvent.Source {
    val protoValue = AuditEvent.Source.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSite(siteElement.toProto())
    .setObserver(observer.toProto())
    .addAllType(type.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent.toProto():
      AuditEvent.Entity {
    val protoValue = AuditEvent.Entity.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setWhat(what.toProto())
    .setType(type.toProto())
    .setRole(role.toProto())
    .setLifecycle(lifecycle.toProto())
    .addAllSecurityLabel(securityLabel.map{it.toProto()})
    .setName(nameElement.toProto())
    .setDescription(descriptionElement.toProto())
    .setQuery(queryElement.toProto())
    .addAllDetail(detail.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityDetailComponent.toProto():
      AuditEvent.Entity.Detail {
    val protoValue = AuditEvent.Entity.Detail.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(typeElement.toProto())
    .setValue(value.auditEventEntityDetailValueToProto())
    .build()
    return protoValue
  }

  public fun AuditEvent.Agent.toHapi(): org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setRole(roleList.map{it.toHapi()})
    hapiValue.setWho(who.toHapi())
    hapiValue.setAltIdElement(altId.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setRequestorElement(requestor.toHapi())
    hapiValue.setLocation(location.toHapi())
    hapiValue.setPolicy(policyList.map{it.toHapi()})
    hapiValue.setMedia(media.toHapi())
    hapiValue.setNetwork(network.toHapi())
    hapiValue.setPurposeOfUse(purposeOfUseList.map{it.toHapi()})
    return hapiValue
  }

  public fun AuditEvent.Agent.Network.toHapi():
      org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setAddressElement(address.toHapi())
    hapiValue.setType(org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkType.valueOf(type.value.name.replace("_","")))
    return hapiValue
  }

  public fun AuditEvent.Source.toHapi():
      org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSiteElement(site.toHapi())
    hapiValue.setObserver(observer.toHapi())
    hapiValue.setType(typeList.map{it.toHapi()})
    return hapiValue
  }

  public fun AuditEvent.Entity.toHapi():
      org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setWhat(what.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setRole(role.toHapi())
    hapiValue.setLifecycle(lifecycle.toHapi())
    hapiValue.setSecurityLabel(securityLabelList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setQueryElement(query.toHapi())
    hapiValue.setDetail(detailList.map{it.toHapi()})
    return hapiValue
  }

  public fun AuditEvent.Entity.Detail.toHapi():
      org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityDetailComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setTypeElement(type.toHapi())
    hapiValue.setValue(value.auditEventEntityDetailValueToHapi())
    return hapiValue
  }
}
