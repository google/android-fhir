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

object VerificationResultConverter {
  @JvmStatic
  fun VerificationResult.toHapi(): org.hl7.fhir.r4.model.VerificationResult {
    val hapiValue = org.hl7.fhir.r4.model.VerificationResult()
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
    if (targetCount > 0) {
        hapiValue.target = targetList.map { it.toHapi() }
    }
    if (targetLocationCount > 0) {
        hapiValue.targetLocation = targetLocationList.map { it.toHapi() }
    }
    if (hasNeed()) {
        hapiValue.need = need.toHapi()
    }
      hapiValue.status = org.hl7.fhir.r4.model.VerificationResult.Status.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasStatusDate()) {
        hapiValue.statusDateElement = statusDate.toHapi()
    }
    if (hasValidationType()) {
        hapiValue.validationType = validationType.toHapi()
    }
    if (validationProcessCount > 0) {
        hapiValue.validationProcess = validationProcessList.map { it.toHapi() }
    }
    if (hasFrequency()) {
        hapiValue.frequency = frequency.toHapi()
    }
    if (hasLastPerformed()) {
        hapiValue.lastPerformedElement = lastPerformed.toHapi()
    }
    if (hasNextScheduled()) {
        hapiValue.nextScheduledElement = nextScheduled.toHapi()
    }
    if (hasFailureAction()) {
        hapiValue.failureAction = failureAction.toHapi()
    }
    if (primarySourceCount > 0) {
        hapiValue.primarySource = primarySourceList.map { it.toHapi() }
    }
    if (hasAttestation()) {
        hapiValue.attestation = attestation.toHapi()
    }
    if (validatorCount > 0) {
        hapiValue.validator = validatorList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.VerificationResult.toProto(): VerificationResult {
    val protoValue = VerificationResult.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasTarget()) {
      protoValue.addAllTarget(target.map { it.toProto() })
    }
    if (hasTargetLocation()) {
      protoValue.addAllTargetLocation(targetLocation.map { it.toProto() })
    }
    if (hasNeed()) {
        protoValue.need = need.toProto()
    }
      protoValue.status = VerificationResult.StatusCode.newBuilder()
          .setValue(
              StatusCode.Value.valueOf(status.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
          )
          .build()
    if (hasStatusDate()) {
        protoValue.statusDate = statusDateElement.toProto()
    }
    if (hasValidationType()) {
        protoValue.validationType = validationType.toProto()
    }
    if (hasValidationProcess()) {
      protoValue.addAllValidationProcess(validationProcess.map { it.toProto() })
    }
    if (hasFrequency()) {
        protoValue.frequency = frequency.toProto()
    }
    if (hasLastPerformed()) {
        protoValue.lastPerformed = lastPerformedElement.toProto()
    }
    if (hasNextScheduled()) {
        protoValue.nextScheduled = nextScheduledElement.toProto()
    }
    if (hasFailureAction()) {
        protoValue.failureAction = failureAction.toProto()
    }
    if (hasPrimarySource()) {
      protoValue.addAllPrimarySource(primarySource.map { it.toProto() })
    }
    if (hasAttestation()) {
        protoValue.attestation = attestation.toProto()
    }
    if (hasValidator()) {
      protoValue.addAllValidator(validator.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.VerificationResult.VerificationResultPrimarySourceComponent.toProto():
    VerificationResult.PrimarySource {
    val protoValue =
      VerificationResult.PrimarySource.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasWho()) {
        protoValue.who = who.toProto()
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasCommunicationMethod()) {
      protoValue.addAllCommunicationMethod(communicationMethod.map { it.toProto() })
    }
    if (hasValidationStatus()) {
        protoValue.validationStatus = validationStatus.toProto()
    }
    if (hasValidationDate()) {
        protoValue.validationDate = validationDateElement.toProto()
    }
    if (hasCanPushUpdates()) {
        protoValue.canPushUpdates = canPushUpdates.toProto()
    }
    if (hasPushTypeAvailable()) {
      protoValue.addAllPushTypeAvailable(pushTypeAvailable.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.VerificationResult.VerificationResultAttestationComponent.toProto():
    VerificationResult.Attestation {
    val protoValue =
      VerificationResult.Attestation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasWho()) {
        protoValue.who = who.toProto()
    }
    if (hasOnBehalfOf()) {
        protoValue.onBehalfOf = onBehalfOf.toProto()
    }
    if (hasCommunicationMethod()) {
        protoValue.communicationMethod = communicationMethod.toProto()
    }
    if (hasDate()) {
        protoValue.date = dateElement.toProto()
    }
    if (hasSourceIdentityCertificate()) {
        protoValue.sourceIdentityCertificate = sourceIdentityCertificateElement.toProto()
    }
    if (hasProxyIdentityCertificate()) {
        protoValue.proxyIdentityCertificate = proxyIdentityCertificateElement.toProto()
    }
    if (hasProxySignature()) {
        protoValue.proxySignature = proxySignature.toProto()
    }
    if (hasSourceSignature()) {
        protoValue.sourceSignature = sourceSignature.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.VerificationResult.VerificationResultValidatorComponent.toProto():
    VerificationResult.Validator {
    val protoValue =
      VerificationResult.Validator.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasOrganization()) {
        protoValue.organization = organization.toProto()
    }
    if (hasIdentityCertificate()) {
        protoValue.identityCertificate = identityCertificateElement.toProto()
    }
    if (hasAttestationSignature()) {
        protoValue.attestationSignature = attestationSignature.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun VerificationResult.PrimarySource.toHapi():
    org.hl7.fhir.r4.model.VerificationResult.VerificationResultPrimarySourceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.VerificationResult.VerificationResultPrimarySourceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasWho()) {
        hapiValue.who = who.toHapi()
    }
    if (typeCount > 0) {
        hapiValue.type = typeList.map { it.toHapi() }
    }
    if (communicationMethodCount > 0) {
        hapiValue.communicationMethod = communicationMethodList.map { it.toHapi() }
    }
    if (hasValidationStatus()) {
        hapiValue.validationStatus = validationStatus.toHapi()
    }
    if (hasValidationDate()) {
        hapiValue.validationDateElement = validationDate.toHapi()
    }
    if (hasCanPushUpdates()) {
        hapiValue.canPushUpdates = canPushUpdates.toHapi()
    }
    if (pushTypeAvailableCount > 0) {
        hapiValue.pushTypeAvailable = pushTypeAvailableList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun VerificationResult.Attestation.toHapi():
    org.hl7.fhir.r4.model.VerificationResult.VerificationResultAttestationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.VerificationResult.VerificationResultAttestationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasWho()) {
        hapiValue.who = who.toHapi()
    }
    if (hasOnBehalfOf()) {
        hapiValue.onBehalfOf = onBehalfOf.toHapi()
    }
    if (hasCommunicationMethod()) {
        hapiValue.communicationMethod = communicationMethod.toHapi()
    }
    if (hasDate()) {
        hapiValue.dateElement = date.toHapi()
    }
    if (hasSourceIdentityCertificate()) {
        hapiValue.sourceIdentityCertificateElement = sourceIdentityCertificate.toHapi()
    }
    if (hasProxyIdentityCertificate()) {
        hapiValue.proxyIdentityCertificateElement = proxyIdentityCertificate.toHapi()
    }
    if (hasProxySignature()) {
        hapiValue.proxySignature = proxySignature.toHapi()
    }
    if (hasSourceSignature()) {
        hapiValue.sourceSignature = sourceSignature.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun VerificationResult.Validator.toHapi():
    org.hl7.fhir.r4.model.VerificationResult.VerificationResultValidatorComponent {
    val hapiValue = org.hl7.fhir.r4.model.VerificationResult.VerificationResultValidatorComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasOrganization()) {
        hapiValue.organization = organization.toHapi()
    }
    if (hasIdentityCertificate()) {
        hapiValue.identityCertificateElement = identityCertificate.toHapi()
    }
    if (hasAttestationSignature()) {
        hapiValue.attestationSignature = attestationSignature.toHapi()
    }
    return hapiValue
  }
}
