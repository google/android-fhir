package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.NamingSystem
import com.google.fhir.r4.core.NamingSystemIdentifierTypeCode
import com.google.fhir.r4.core.NamingSystemTypeCode
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

public object NamingSystemConverter {
  public fun NamingSystem.toHapi(): org.hl7.fhir.r4.model.NamingSystem {
    val hapiValue = org.hl7.fhir.r4.model.NamingSystem()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setKind(org.hl7.fhir.r4.model.NamingSystem.NamingSystemType.valueOf(kind.value.name.replace("_","")))
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map{it.toHapi()})
    hapiValue.setResponsibleElement(responsible.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map{it.toHapi()})
    hapiValue.setJurisdiction(jurisdictionList.map{it.toHapi()})
    hapiValue.setUsageElement(usage.toHapi())
    hapiValue.setUniqueId(uniqueIdList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.NamingSystem.toProto(): NamingSystem {
    val protoValue = NamingSystem.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setStatus(NamingSystem.StatusCode.newBuilder().setValue(PublicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setKind(NamingSystem.KindCode.newBuilder().setValue(NamingSystemTypeCode.Value.valueOf(kind.toCode().replace("-",
        "_").toUpperCase())).build())
    .setDate(dateElement.toProto())
    .setPublisher(publisherElement.toProto())
    .addAllContact(contact.map{it.toProto()})
    .setResponsible(responsibleElement.toProto())
    .setType(type.toProto())
    .setDescription(descriptionElement.toProto())
    .addAllUseContext(useContext.map{it.toProto()})
    .addAllJurisdiction(jurisdiction.map{it.toProto()})
    .setUsage(usageElement.toProto())
    .addAllUniqueId(uniqueId.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.NamingSystem.NamingSystemUniqueIdComponent.toProto():
      NamingSystem.UniqueId {
    val protoValue = NamingSystem.UniqueId.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(NamingSystem.UniqueId.TypeCode.newBuilder().setValue(NamingSystemIdentifierTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setValue(valueElement.toProto())
    .setPreferred(preferredElement.toProto())
    .setComment(commentElement.toProto())
    .setPeriod(period.toProto())
    .build()
    return protoValue
  }

  public fun NamingSystem.UniqueId.toHapi():
      org.hl7.fhir.r4.model.NamingSystem.NamingSystemUniqueIdComponent {
    val hapiValue = org.hl7.fhir.r4.model.NamingSystem.NamingSystemUniqueIdComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(org.hl7.fhir.r4.model.NamingSystem.NamingSystemIdentifierType.valueOf(type.value.name.replace("_","")))
    hapiValue.setValueElement(value.toHapi())
    hapiValue.setPreferredElement(preferred.toHapi())
    hapiValue.setCommentElement(comment.toHapi())
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }
}
