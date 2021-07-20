package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CatalogEntry
import com.google.fhir.r4.core.CatalogEntryRelationTypeCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

public object CatalogEntryConverter {
  public fun CatalogEntry.toHapi(): org.hl7.fhir.r4.model.CatalogEntry {
    val hapiValue = org.hl7.fhir.r4.model.CatalogEntry()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setOrderableElement(orderable.toHapi())
    hapiValue.setReferencedItem(referencedItem.toHapi())
    hapiValue.setAdditionalIdentifier(additionalIdentifierList.map{it.toHapi()})
    hapiValue.setClassification(classificationList.map{it.toHapi()})
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setValidityPeriod(validityPeriod.toHapi())
    hapiValue.setValidToElement(validTo.toHapi())
    hapiValue.setLastUpdatedElement(lastUpdated.toHapi())
    hapiValue.setAdditionalCharacteristic(additionalCharacteristicList.map{it.toHapi()})
    hapiValue.setAdditionalClassification(additionalClassificationList.map{it.toHapi()})
    hapiValue.setRelatedEntry(relatedEntryList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.CatalogEntry.toProto(): CatalogEntry {
    val protoValue = CatalogEntry.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setType(type.toProto())
    .setOrderable(orderableElement.toProto())
    .setReferencedItem(referencedItem.toProto())
    .addAllAdditionalIdentifier(additionalIdentifier.map{it.toProto()})
    .addAllClassification(classification.map{it.toProto()})
    .setStatus(CatalogEntry.StatusCode.newBuilder().setValue(PublicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setValidityPeriod(validityPeriod.toProto())
    .setValidTo(validToElement.toProto())
    .setLastUpdated(lastUpdatedElement.toProto())
    .addAllAdditionalCharacteristic(additionalCharacteristic.map{it.toProto()})
    .addAllAdditionalClassification(additionalClassification.map{it.toProto()})
    .addAllRelatedEntry(relatedEntry.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.CatalogEntry.CatalogEntryRelatedEntryComponent.toProto():
      CatalogEntry.RelatedEntry {
    val protoValue = CatalogEntry.RelatedEntry.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setRelationtype(CatalogEntry.RelatedEntry.RelationtypeCode.newBuilder().setValue(CatalogEntryRelationTypeCode.Value.valueOf(relationtype.toCode().replace("-",
        "_").toUpperCase())).build())
    .setItem(item.toProto())
    .build()
    return protoValue
  }

  public fun CatalogEntry.RelatedEntry.toHapi():
      org.hl7.fhir.r4.model.CatalogEntry.CatalogEntryRelatedEntryComponent {
    val hapiValue = org.hl7.fhir.r4.model.CatalogEntry.CatalogEntryRelatedEntryComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setRelationtype(org.hl7.fhir.r4.model.CatalogEntry.CatalogEntryRelationType.valueOf(relationtype.value.name.replace("_","")))
    hapiValue.setItem(item.toHapi())
    return hapiValue
  }
}
