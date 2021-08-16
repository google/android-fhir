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

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CompositionStatusCode
import com.google.fhir.r4.core.DocumentReference
import com.google.fhir.r4.core.DocumentReference.RelatesTo
import com.google.fhir.r4.core.DocumentReferenceStatusCode
import com.google.fhir.r4.core.DocumentRelationshipTypeCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

object DocumentReferenceConverter {
  @JvmStatic
  fun DocumentReference.toHapi(): org.hl7.fhir.r4.model.DocumentReference {
    val hapiValue = org.hl7.fhir.r4.model.DocumentReference()
    hapiValue.id = id.value
    if (hasMeta()) {
        hapiValue.meta = meta.toHapi()
    }
    if (hasImplicitRules()) {
        hapiValue.implicitRulesElement = implicitRules.toHapi()
    }
    if (hasText()) {
        hapiValue.text = text.toHapi()
    }
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasMasterIdentifier()) {
        hapiValue.masterIdentifier = masterIdentifier.toHapi()
    }
    if (identifierCount > 0) {
        hapiValue.identifier = identifierList.map { it.toHapi() }
    }
      hapiValue.status = Enumerations.DocumentReferenceStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
      hapiValue.docStatus = org.hl7.fhir.r4.model.DocumentReference.ReferredDocumentStatus.valueOf(
          docStatus.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (categoryCount > 0) {
        hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasSubject()) {
        hapiValue.subject = subject.toHapi()
    }
    if (hasDate()) {
        hapiValue.dateElement = date.toHapi()
    }
    if (authorCount > 0) {
        hapiValue.author = authorList.map { it.toHapi() }
    }
    if (hasAuthenticator()) {
        hapiValue.authenticator = authenticator.toHapi()
    }
    if (hasCustodian()) {
        hapiValue.custodian = custodian.toHapi()
    }
    if (relatesToCount > 0) {
        hapiValue.relatesTo = relatesToList.map { it.toHapi() }
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (securityLabelCount > 0) {
        hapiValue.securityLabel = securityLabelList.map { it.toHapi() }
    }
    if (contentCount > 0) {
        hapiValue.content = contentList.map { it.toHapi() }
    }
    if (hasContext()) {
        hapiValue.context = context.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.DocumentReference.toProto(): DocumentReference {
    val protoValue = DocumentReference.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
        protoValue.meta = meta.toProto()
    }
    if (hasImplicitRules()) {
        protoValue.implicitRules = implicitRulesElement.toProto()
    }
    if (hasText()) {
        protoValue.text = text.toProto()
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasMasterIdentifier()) {
        protoValue.masterIdentifier = masterIdentifier.toProto()
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
      protoValue.status = DocumentReference.StatusCode.newBuilder()
          .setValue(
              DocumentReferenceStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
      protoValue.docStatus = DocumentReference.DocStatusCode.newBuilder()
          .setValue(
              CompositionStatusCode.Value.valueOf(
                  docStatus.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasSubject()) {
        protoValue.subject = subject.toProto()
    }
    if (hasDate()) {
        protoValue.date = dateElement.toProto()
    }
    if (hasAuthor()) {
      protoValue.addAllAuthor(author.map { it.toProto() })
    }
    if (hasAuthenticator()) {
        protoValue.authenticator = authenticator.toProto()
    }
    if (hasCustodian()) {
        protoValue.custodian = custodian.toProto()
    }
    if (hasRelatesTo()) {
      protoValue.addAllRelatesTo(relatesTo.map { it.toProto() })
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasSecurityLabel()) {
      protoValue.addAllSecurityLabel(securityLabel.map { it.toProto() })
    }
    if (hasContent()) {
      protoValue.addAllContent(content.map { it.toProto() })
    }
    if (hasContext()) {
        protoValue.context = context.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceRelatesToComponent.toProto():
    DocumentReference.RelatesTo {
    val protoValue =
      DocumentReference.RelatesTo.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.code = DocumentReference.RelatesTo.CodeType.newBuilder()
          .setValue(
              DocumentRelationshipTypeCode.Value.valueOf(
                  code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasTarget()) {
        protoValue.target = target.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContentComponent.toProto():
    DocumentReference.Content {
    val protoValue = DocumentReference.Content.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAttachment()) {
        protoValue.attachment = attachment.toProto()
    }
    if (hasFormat()) {
        protoValue.format = format.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContextComponent.toProto():
    DocumentReference.Context {
    val protoValue = DocumentReference.Context.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasEncounter()) {
      protoValue.addAllEncounter(encounter.map { it.toProto() })
    }
    if (hasEvent()) {
      protoValue.addAllEvent(event.map { it.toProto() })
    }
    if (hasPeriod()) {
        protoValue.period = period.toProto()
    }
    if (hasFacilityType()) {
        protoValue.facilityType = facilityType.toProto()
    }
    if (hasPracticeSetting()) {
        protoValue.practiceSetting = practiceSetting.toProto()
    }
    if (hasSourcePatientInfo()) {
        protoValue.sourcePatientInfo = sourcePatientInfo.toProto()
    }
    if (hasRelated()) {
      protoValue.addAllRelated(related.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun DocumentReference.RelatesTo.toHapi():
    org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceRelatesToComponent {
    val hapiValue = org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceRelatesToComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.code = org.hl7.fhir.r4.model.DocumentReference.DocumentRelationshipType.valueOf(
          code.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasTarget()) {
        hapiValue.target = target.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun DocumentReference.Content.toHapi():
    org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContentComponent {
    val hapiValue = org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasAttachment()) {
        hapiValue.attachment = attachment.toHapi()
    }
    if (hasFormat()) {
        hapiValue.format = format.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun DocumentReference.Context.toHapi():
    org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContextComponent {
    val hapiValue = org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContextComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (encounterCount > 0) {
        hapiValue.encounter = encounterList.map { it.toHapi() }
    }
    if (eventCount > 0) {
        hapiValue.event = eventList.map { it.toHapi() }
    }
    if (hasPeriod()) {
        hapiValue.period = period.toHapi()
    }
    if (hasFacilityType()) {
        hapiValue.facilityType = facilityType.toHapi()
    }
    if (hasPracticeSetting()) {
        hapiValue.practiceSetting = practiceSetting.toHapi()
    }
    if (hasSourcePatientInfo()) {
        hapiValue.sourcePatientInfo = sourcePatientInfo.toHapi()
    }
    if (relatedCount > 0) {
        hapiValue.related = relatedList.map { it.toHapi() }
    }
    return hapiValue
  }
}
