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

public object DocumentReferenceConverter {
  @JvmStatic
  public fun DocumentReference.toHapi(): org.hl7.fhir.r4.model.DocumentReference {
    val hapiValue = org.hl7.fhir.r4.model.DocumentReference()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setMasterIdentifier(masterIdentifier.toHapi())
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      Enumerations.DocumentReferenceStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setDocStatus(
      org.hl7.fhir.r4.model.DocumentReference.ReferredDocumentStatus.valueOf(
        docStatus.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setType(type.toHapi())
    hapiValue.setCategory(categoryList.map { it.toHapi() })
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setAuthor(authorList.map { it.toHapi() })
    hapiValue.setAuthenticator(authenticator.toHapi())
    hapiValue.setCustodian(custodian.toHapi())
    hapiValue.setRelatesTo(relatesToList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setSecurityLabel(securityLabelList.map { it.toHapi() })
    hapiValue.setContent(contentList.map { it.toHapi() })
    hapiValue.setContext(context.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.DocumentReference.toProto(): DocumentReference {
    val protoValue =
      DocumentReference.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setMasterIdentifier(masterIdentifier.toProto())
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          DocumentReference.StatusCode.newBuilder()
            .setValue(
              DocumentReferenceStatusCode.Value.valueOf(
                status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setDocStatus(
          DocumentReference.DocStatusCode.newBuilder()
            .setValue(
              CompositionStatusCode.Value.valueOf(
                docStatus.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setType(type.toProto())
        .addAllCategory(category.map { it.toProto() })
        .setSubject(subject.toProto())
        .setDate(dateElement.toProto())
        .addAllAuthor(author.map { it.toProto() })
        .setAuthenticator(authenticator.toProto())
        .setCustodian(custodian.toProto())
        .addAllRelatesTo(relatesTo.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllSecurityLabel(securityLabel.map { it.toProto() })
        .addAllContent(content.map { it.toProto() })
        .setContext(context.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceRelatesToComponent.toProto():
    DocumentReference.RelatesTo {
    val protoValue =
      DocumentReference.RelatesTo.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(
          DocumentReference.RelatesTo.CodeType.newBuilder()
            .setValue(
              DocumentRelationshipTypeCode.Value.valueOf(
                code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setTarget(target.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContentComponent.toProto():
    DocumentReference.Content {
    val protoValue =
      DocumentReference.Content.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setAttachment(attachment.toProto())
        .setFormat(format.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContextComponent.toProto():
    DocumentReference.Context {
    val protoValue =
      DocumentReference.Context.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllEncounter(encounter.map { it.toProto() })
        .addAllEvent(event.map { it.toProto() })
        .setPeriod(period.toProto())
        .setFacilityType(facilityType.toProto())
        .setPracticeSetting(practiceSetting.toProto())
        .setSourcePatientInfo(sourcePatientInfo.toProto())
        .addAllRelated(related.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun DocumentReference.RelatesTo.toHapi():
    org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceRelatesToComponent {
    val hapiValue = org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceRelatesToComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(
      org.hl7.fhir.r4.model.DocumentReference.DocumentRelationshipType.valueOf(
        code.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setTarget(target.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun DocumentReference.Content.toHapi():
    org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContentComponent {
    val hapiValue = org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContentComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setAttachment(attachment.toHapi())
    hapiValue.setFormat(format.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun DocumentReference.Context.toHapi():
    org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContextComponent {
    val hapiValue = org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContextComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setEncounter(encounterList.map { it.toHapi() })
    hapiValue.setEvent(eventList.map { it.toHapi() })
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setFacilityType(facilityType.toHapi())
    hapiValue.setPracticeSetting(practiceSetting.toHapi())
    hapiValue.setSourcePatientInfo(sourcePatientInfo.toHapi())
    hapiValue.setRelated(relatedList.map { it.toHapi() })
    return hapiValue
  }
}
