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

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
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
import com.google.fhir.r4.core.CatalogEntry.RelatedEntry
import com.google.fhir.r4.core.CatalogEntryRelationTypeCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

public object CatalogEntryConverter {
  @JvmStatic
  public fun CatalogEntry.toHapi(): org.hl7.fhir.r4.model.CatalogEntry {
    val hapiValue = org.hl7.fhir.r4.model.CatalogEntry()
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
    if (hasOrderable()) {
      hapiValue.setOrderableElement(orderable.toHapi())
    }
    if (hasReferencedItem()) {
      hapiValue.setReferencedItem(referencedItem.toHapi())
    }
    if (additionalIdentifierCount > 0) {
      hapiValue.setAdditionalIdentifier(additionalIdentifierList.map { it.toHapi() })
    }
    if (classificationCount > 0) {
      hapiValue.setClassification(classificationList.map { it.toHapi() })
    }
    hapiValue.setStatus(
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasValidityPeriod()) {
      hapiValue.setValidityPeriod(validityPeriod.toHapi())
    }
    if (hasValidTo()) {
      hapiValue.setValidToElement(validTo.toHapi())
    }
    if (hasLastUpdated()) {
      hapiValue.setLastUpdatedElement(lastUpdated.toHapi())
    }
    if (additionalCharacteristicCount > 0) {
      hapiValue.setAdditionalCharacteristic(additionalCharacteristicList.map { it.toHapi() })
    }
    if (additionalClassificationCount > 0) {
      hapiValue.setAdditionalClassification(additionalClassificationList.map { it.toHapi() })
    }
    if (relatedEntryCount > 0) {
      hapiValue.setRelatedEntry(relatedEntryList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.CatalogEntry.toProto(): CatalogEntry {
    val protoValue = CatalogEntry.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasOrderable()) {
      protoValue.setOrderable(orderableElement.toProto())
    }
    if (hasReferencedItem()) {
      protoValue.setReferencedItem(referencedItem.toProto())
    }
    if (hasAdditionalIdentifier()) {
      protoValue.addAllAdditionalIdentifier(additionalIdentifier.map { it.toProto() })
    }
    if (hasClassification()) {
      protoValue.addAllClassification(classification.map { it.toProto() })
    }
    protoValue.setStatus(
      CatalogEntry.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasValidityPeriod()) {
      protoValue.setValidityPeriod(validityPeriod.toProto())
    }
    if (hasValidTo()) {
      protoValue.setValidTo(validToElement.toProto())
    }
    if (hasLastUpdated()) {
      protoValue.setLastUpdated(lastUpdatedElement.toProto())
    }
    if (hasAdditionalCharacteristic()) {
      protoValue.addAllAdditionalCharacteristic(additionalCharacteristic.map { it.toProto() })
    }
    if (hasAdditionalClassification()) {
      protoValue.addAllAdditionalClassification(additionalClassification.map { it.toProto() })
    }
    if (hasRelatedEntry()) {
      protoValue.addAllRelatedEntry(relatedEntry.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CatalogEntry.CatalogEntryRelatedEntryComponent.toProto():
    CatalogEntry.RelatedEntry {
    val protoValue = CatalogEntry.RelatedEntry.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.setRelationtype(
      CatalogEntry.RelatedEntry.RelationtypeCode.newBuilder()
        .setValue(
          CatalogEntryRelationTypeCode.Value.valueOf(
            relationtype.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasItem()) {
      protoValue.setItem(item.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CatalogEntry.RelatedEntry.toHapi():
    org.hl7.fhir.r4.model.CatalogEntry.CatalogEntryRelatedEntryComponent {
    val hapiValue = org.hl7.fhir.r4.model.CatalogEntry.CatalogEntryRelatedEntryComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setRelationtype(
      org.hl7.fhir.r4.model.CatalogEntry.CatalogEntryRelationType.valueOf(
        relationtype.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasItem()) {
      hapiValue.setItem(item.toHapi())
    }
    return hapiValue
  }
}
