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
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCountry(country.toHapi())
    hapiValue.setJurisdiction(jurisdiction.toHapi())
    hapiValue.setStatus(status.toHapi())
    hapiValue.setDateRange(dateRange.toHapi())
    hapiValue.setRestoreDateElement(restoreDate.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MarketingStatus.toProto(): MarketingStatus {
    val protoValue =
      MarketingStatus.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCountry(country.toProto())
        .setJurisdiction(jurisdiction.toProto())
        .setStatus(status.toProto())
        .setDateRange(dateRange.toProto())
        .setRestoreDate(restoreDateElement.toProto())
        .build()
    return protoValue
  }
}
