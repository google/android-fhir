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

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SignatureConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SignatureConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.StatusCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.VerificationResult
import kotlin.jvm.JvmStatic

public object VerificationResultConverter {
  @JvmStatic
  public fun VerificationResult.toHapi(): org.hl7.fhir.r4.model.VerificationResult {
    val hapiValue = org.hl7.fhir.r4.model.VerificationResult()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setTarget(targetList.map { it.toHapi() })
    hapiValue.setTargetLocation(targetLocationList.map { it.toHapi() })
    hapiValue.setNeed(need.toHapi())
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.VerificationResult.Status.valueOf(
        status
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setStatusDateElement(statusDate.toHapi())
    hapiValue.setValidationType(validationType.toHapi())
    hapiValue.setValidationProcess(validationProcessList.map { it.toHapi() })
    hapiValue.setFrequency(frequency.toHapi())
    hapiValue.setLastPerformedElement(lastPerformed.toHapi())
    hapiValue.setNextScheduledElement(nextScheduled.toHapi())
    hapiValue.setFailureAction(failureAction.toHapi())
    hapiValue.setPrimarySource(primarySourceList.map { it.toHapi() })
    hapiValue.setAttestation(attestation.toHapi())
    hapiValue.setValidator(validatorList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.VerificationResult.toProto(): VerificationResult {
    val protoValue =
      VerificationResult.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllTarget(target.map { it.toProto() })
        .addAllTargetLocation(targetLocation.map { it.toProto() })
        .setNeed(need.toProto())
        .setStatus(
          VerificationResult.StatusCode.newBuilder()
            .setValue(
              StatusCode.Value.valueOf(
                status
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setStatusDate(statusDateElement.toProto())
        .setValidationType(validationType.toProto())
        .addAllValidationProcess(validationProcess.map { it.toProto() })
        .setFrequency(frequency.toProto())
        .setLastPerformed(lastPerformedElement.toProto())
        .setNextScheduled(nextScheduledElement.toProto())
        .setFailureAction(failureAction.toProto())
        .addAllPrimarySource(primarySource.map { it.toProto() })
        .setAttestation(attestation.toProto())
        .addAllValidator(validator.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.VerificationResult.VerificationResultPrimarySourceComponent.toProto():
    VerificationResult.PrimarySource {
    val protoValue =
      VerificationResult.PrimarySource.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setWho(who.toProto())
        .addAllType(type.map { it.toProto() })
        .addAllCommunicationMethod(communicationMethod.map { it.toProto() })
        .setValidationStatus(validationStatus.toProto())
        .setValidationDate(validationDateElement.toProto())
        .setCanPushUpdates(canPushUpdates.toProto())
        .addAllPushTypeAvailable(pushTypeAvailable.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.VerificationResult.VerificationResultAttestationComponent.toProto():
    VerificationResult.Attestation {
    val protoValue =
      VerificationResult.Attestation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setWho(who.toProto())
        .setOnBehalfOf(onBehalfOf.toProto())
        .setCommunicationMethod(communicationMethod.toProto())
        .setDate(dateElement.toProto())
        .setSourceIdentityCertificate(sourceIdentityCertificateElement.toProto())
        .setProxyIdentityCertificate(proxyIdentityCertificateElement.toProto())
        .setProxySignature(proxySignature.toProto())
        .setSourceSignature(sourceSignature.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.VerificationResult.VerificationResultValidatorComponent.toProto():
    VerificationResult.Validator {
    val protoValue =
      VerificationResult.Validator.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setOrganization(organization.toProto())
        .setIdentityCertificate(identityCertificateElement.toProto())
        .setAttestationSignature(attestationSignature.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun VerificationResult.PrimarySource.toHapi():
    org.hl7.fhir.r4.model.VerificationResult.VerificationResultPrimarySourceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.VerificationResult.VerificationResultPrimarySourceComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setWho(who.toHapi())
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setCommunicationMethod(communicationMethodList.map { it.toHapi() })
    hapiValue.setValidationStatus(validationStatus.toHapi())
    hapiValue.setValidationDateElement(validationDate.toHapi())
    hapiValue.setCanPushUpdates(canPushUpdates.toHapi())
    hapiValue.setPushTypeAvailable(pushTypeAvailableList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun VerificationResult.Attestation.toHapi():
    org.hl7.fhir.r4.model.VerificationResult.VerificationResultAttestationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.VerificationResult.VerificationResultAttestationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setWho(who.toHapi())
    hapiValue.setOnBehalfOf(onBehalfOf.toHapi())
    hapiValue.setCommunicationMethod(communicationMethod.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setSourceIdentityCertificateElement(sourceIdentityCertificate.toHapi())
    hapiValue.setProxyIdentityCertificateElement(proxyIdentityCertificate.toHapi())
    hapiValue.setProxySignature(proxySignature.toHapi())
    hapiValue.setSourceSignature(sourceSignature.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun VerificationResult.Validator.toHapi():
    org.hl7.fhir.r4.model.VerificationResult.VerificationResultValidatorComponent {
    val hapiValue = org.hl7.fhir.r4.model.VerificationResult.VerificationResultValidatorComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setOrganization(organization.toHapi())
    hapiValue.setIdentityCertificateElement(identityCertificate.toHapi())
    hapiValue.setAttestationSignature(attestationSignature.toHapi())
    return hapiValue
  }
}
