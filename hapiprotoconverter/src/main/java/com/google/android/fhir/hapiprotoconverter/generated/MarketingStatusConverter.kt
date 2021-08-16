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
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.fhir.r4.core.MarketingStatus
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

public object MarketingStatusConverter {
  @JvmStatic
  public fun MarketingStatus.toHapi(): org.hl7.fhir.r4.model.MarketingStatus {
    val hapiValue = org.hl7.fhir.r4.model.MarketingStatus()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCountry()) {
      hapiValue.setCountry(country.toHapi())
    }
    if (hasJurisdiction()) {
      hapiValue.setJurisdiction(jurisdiction.toHapi())
    }
    if (hasStatus()) {
      hapiValue.setStatus(status.toHapi())
    }
    if (hasDateRange()) {
      hapiValue.setDateRange(dateRange.toHapi())
    }
    if (hasRestoreDate()) {
      hapiValue.setRestoreDateElement(restoreDate.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MarketingStatus.toProto(): MarketingStatus {
    val protoValue = MarketingStatus.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCountry()) {
      protoValue.setCountry(country.toProto())
    }
    if (hasJurisdiction()) {
      protoValue.setJurisdiction(jurisdiction.toProto())
    }
    if (hasStatus()) {
      protoValue.setStatus(status.toProto())
    }
    if (hasDateRange()) {
      protoValue.setDateRange(dateRange.toProto())
    }
    if (hasRestoreDate()) {
      protoValue.setRestoreDate(restoreDateElement.toProto())
    }
    return protoValue.build()
  }
}
