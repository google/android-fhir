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
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductAuthorization
import com.google.fhir.r4.core.MedicinalProductAuthorization.Procedure
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object MedicinalProductAuthorizationConverter {
  @JvmStatic
  private fun MedicinalProductAuthorization.Procedure.DateX.medicinalProductAuthorizationProcedureDateToHapi():
    Type {
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicinalProductAuthorization.procedure.date[x]"
    )
  }

  @JvmStatic
  private fun Type.medicinalProductAuthorizationProcedureDateToProto():
    MedicinalProductAuthorization.Procedure.DateX {
    val protoValue = MedicinalProductAuthorization.Procedure.DateX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun MedicinalProductAuthorization.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductAuthorization {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductAuthorization()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setCountry(countryList.map { it.toHapi() })
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    hapiValue.setStatus(status.toHapi())
    hapiValue.setStatusDateElement(statusDate.toHapi())
    hapiValue.setRestoreDateElement(restoreDate.toHapi())
    hapiValue.setValidityPeriod(validityPeriod.toHapi())
    hapiValue.setDataExclusivityPeriod(dataExclusivityPeriod.toHapi())
    hapiValue.setDateOfFirstAuthorizationElement(dateOfFirstAuthorization.toHapi())
    hapiValue.setInternationalBirthDateElement(internationalBirthDate.toHapi())
    hapiValue.setLegalBasis(legalBasis.toHapi())
    hapiValue.setJurisdictionalAuthorization(jurisdictionalAuthorizationList.map { it.toHapi() })
    hapiValue.setHolder(holder.toHapi())
    hapiValue.setRegulator(regulator.toHapi())
    hapiValue.setProcedure(procedure.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicinalProductAuthorization.toProto():
    MedicinalProductAuthorization {
    val protoValue =
      MedicinalProductAuthorization.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setSubject(subject.toProto())
        .addAllCountry(country.map { it.toProto() })
        .addAllJurisdiction(jurisdiction.map { it.toProto() })
        .setStatus(status.toProto())
        .setStatusDate(statusDateElement.toProto())
        .setRestoreDate(restoreDateElement.toProto())
        .setValidityPeriod(validityPeriod.toProto())
        .setDataExclusivityPeriod(dataExclusivityPeriod.toProto())
        .setDateOfFirstAuthorization(dateOfFirstAuthorizationElement.toProto())
        .setInternationalBirthDate(internationalBirthDateElement.toProto())
        .setLegalBasis(legalBasis.toProto())
        .addAllJurisdictionalAuthorization(jurisdictionalAuthorization.map { it.toProto() })
        .setHolder(holder.toProto())
        .setRegulator(regulator.toProto())
        .setProcedure(procedure.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationJurisdictionalAuthorizationComponent.toProto():
    MedicinalProductAuthorization.JurisdictionalAuthorization {
    val protoValue =
      MedicinalProductAuthorization.JurisdictionalAuthorization.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setCountry(country.toProto())
        .addAllJurisdiction(jurisdiction.map { it.toProto() })
        .setLegalStatusOfSupply(legalStatusOfSupply.toProto())
        .setValidityPeriod(validityPeriod.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationProcedureComponent.toProto():
    MedicinalProductAuthorization.Procedure {
    val protoValue =
      MedicinalProductAuthorization.Procedure.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setIdentifier(identifier.toProto())
        .setType(type.toProto())
        .setDate(date.medicinalProductAuthorizationProcedureDateToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun MedicinalProductAuthorization.JurisdictionalAuthorization.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationJurisdictionalAuthorizationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductAuthorization
        .MedicinalProductAuthorizationJurisdictionalAuthorizationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setCountry(country.toHapi())
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    hapiValue.setLegalStatusOfSupply(legalStatusOfSupply.toHapi())
    hapiValue.setValidityPeriod(validityPeriod.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProductAuthorization.Procedure.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationProcedureComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductAuthorization
        .MedicinalProductAuthorizationProcedureComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setDate(date.medicinalProductAuthorizationProcedureDateToHapi())
    return hapiValue
  }
}
