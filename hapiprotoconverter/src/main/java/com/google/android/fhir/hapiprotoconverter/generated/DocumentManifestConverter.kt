package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DocumentManifest
import com.google.fhir.r4.core.DocumentReferenceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

public object DocumentManifestConverter {
  public fun DocumentManifest.toHapi(): org.hl7.fhir.r4.model.DocumentManifest {
    val hapiValue = org.hl7.fhir.r4.model.DocumentManifest()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setMasterIdentifier(masterIdentifier.toHapi())
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(Enumerations.DocumentReferenceStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setType(type.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setCreatedElement(created.toHapi())
    hapiValue.setAuthor(authorList.map{it.toHapi()})
    hapiValue.setRecipient(recipientList.map{it.toHapi()})
    hapiValue.setSourceElement(source.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setContent(contentList.map{it.toHapi()})
    hapiValue.setRelated(relatedList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.DocumentManifest.toProto(): DocumentManifest {
    val protoValue = DocumentManifest.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setMasterIdentifier(masterIdentifier.toProto())
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(DocumentManifest.StatusCode.newBuilder().setValue(DocumentReferenceStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setType(type.toProto())
    .setSubject(subject.toProto())
    .setCreated(createdElement.toProto())
    .addAllAuthor(author.map{it.toProto()})
    .addAllRecipient(recipient.map{it.toProto()})
    .setSource(sourceElement.toProto())
    .setDescription(descriptionElement.toProto())
    .addAllContent(content.map{it.toProto()})
    .addAllRelated(related.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.DocumentManifest.DocumentManifestRelatedComponent.toProto():
      DocumentManifest.Related {
    val protoValue = DocumentManifest.Related.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setIdentifier(identifier.toProto())
    .setRef(ref.toProto())
    .build()
    return protoValue
  }

  private fun DocumentManifest.Related.toHapi():
      org.hl7.fhir.r4.model.DocumentManifest.DocumentManifestRelatedComponent {
    val hapiValue = org.hl7.fhir.r4.model.DocumentManifest.DocumentManifestRelatedComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setRef(ref.toHapi())
    return hapiValue
  }
}
