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
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.AdministrativeGenderCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ObservationDataTypeCode
import com.google.fhir.r4.core.ObservationDefinition
import com.google.fhir.r4.core.ObservationDefinition.QualifiedInterval
import com.google.fhir.r4.core.ObservationRangeCategoryCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

object ObservationDefinitionConverter {
  @JvmStatic
  fun ObservationDefinition.toHapi(): org.hl7.fhir.r4.model.ObservationDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ObservationDefinition()
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
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    permittedDataTypeList.forEach {
      hapiValue.addPermittedDataType(
        org.hl7.fhir.r4.model.ObservationDefinition.ObservationDataType.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasMultipleResultsAllowed()) {
      hapiValue.multipleResultsAllowedElement = multipleResultsAllowed.toHapi()
    }
    if (hasMethod()) {
      hapiValue.method = method.toHapi()
    }
    if (hasPreferredReportName()) {
      hapiValue.preferredReportNameElement = preferredReportName.toHapi()
    }
    if (hasQuantitativeDetails()) {
      hapiValue.quantitativeDetails = quantitativeDetails.toHapi()
    }
    if (qualifiedIntervalCount > 0) {
      hapiValue.qualifiedInterval = qualifiedIntervalList.map { it.toHapi() }
    }
    if (hasValidCodedValueSet()) {
      hapiValue.validCodedValueSet = validCodedValueSet.toHapi()
    }
    if (hasNormalCodedValueSet()) {
      hapiValue.normalCodedValueSet = normalCodedValueSet.toHapi()
    }
    if (hasAbnormalCodedValueSet()) {
      hapiValue.abnormalCodedValueSet = abnormalCodedValueSet.toHapi()
    }
    if (hasCriticalCodedValueSet()) {
      hapiValue.criticalCodedValueSet = criticalCodedValueSet.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.ObservationDefinition.toProto(): ObservationDefinition {
    val protoValue = ObservationDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    protoValue.addAllPermittedDataType(
      permittedDataType.map {
        ObservationDefinition.PermittedDataTypeCode.newBuilder()
          .setValue(
            ObservationDataTypeCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    if (hasMultipleResultsAllowed()) {
      protoValue.multipleResultsAllowed = multipleResultsAllowedElement.toProto()
    }
    if (hasMethod()) {
      protoValue.method = method.toProto()
    }
    if (hasPreferredReportName()) {
      protoValue.preferredReportName = preferredReportNameElement.toProto()
    }
    if (hasQuantitativeDetails()) {
      protoValue.quantitativeDetails = quantitativeDetails.toProto()
    }
    if (hasQualifiedInterval()) {
      protoValue.addAllQualifiedInterval(qualifiedInterval.map { it.toProto() })
    }
    if (hasValidCodedValueSet()) {
      protoValue.validCodedValueSet = validCodedValueSet.toProto()
    }
    if (hasNormalCodedValueSet()) {
      protoValue.normalCodedValueSet = normalCodedValueSet.toProto()
    }
    if (hasAbnormalCodedValueSet()) {
      protoValue.abnormalCodedValueSet = abnormalCodedValueSet.toProto()
    }
    if (hasCriticalCodedValueSet()) {
      protoValue.criticalCodedValueSet = criticalCodedValueSet.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQuantitativeDetailsComponent.toProto():
    ObservationDefinition.QuantitativeDetails {
    val protoValue =
      ObservationDefinition.QuantitativeDetails.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCustomaryUnit()) {
      protoValue.customaryUnit = customaryUnit.toProto()
    }
    if (hasUnit()) {
      protoValue.unit = unit.toProto()
    }
    if (hasConversionFactor()) {
      protoValue.conversionFactor = conversionFactorElement.toProto()
    }
    if (hasDecimalPrecision()) {
      protoValue.decimalPrecision = decimalPrecisionElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQualifiedIntervalComponent.toProto():
    ObservationDefinition.QualifiedInterval {
    val protoValue =
      ObservationDefinition.QualifiedInterval.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.category =
      ObservationDefinition.QualifiedInterval.CategoryCode.newBuilder()
        .setValue(
          ObservationRangeCategoryCode.Value.valueOf(
            category.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasRange()) {
      protoValue.range = range.toProto()
    }
    if (hasContext()) {
      protoValue.context = context.toProto()
    }
    if (hasAppliesTo()) {
      protoValue.addAllAppliesTo(appliesTo.map { it.toProto() })
    }
    protoValue.gender =
      ObservationDefinition.QualifiedInterval.GenderCode.newBuilder()
        .setValue(
          AdministrativeGenderCode.Value.valueOf(
            gender.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasAge()) {
      protoValue.age = age.toProto()
    }
    if (hasGestationalAge()) {
      protoValue.gestationalAge = gestationalAge.toProto()
    }
    if (hasCondition()) {
      protoValue.condition = conditionElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ObservationDefinition.QuantitativeDetails.toHapi():
    org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQuantitativeDetailsComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ObservationDefinition
        .ObservationDefinitionQuantitativeDetailsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCustomaryUnit()) {
      hapiValue.customaryUnit = customaryUnit.toHapi()
    }
    if (hasUnit()) {
      hapiValue.unit = unit.toHapi()
    }
    if (hasConversionFactor()) {
      hapiValue.conversionFactorElement = conversionFactor.toHapi()
    }
    if (hasDecimalPrecision()) {
      hapiValue.decimalPrecisionElement = decimalPrecision.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ObservationDefinition.QualifiedInterval.toHapi():
    org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQualifiedIntervalComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQualifiedIntervalComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.category =
      org.hl7.fhir.r4.model.ObservationDefinition.ObservationRangeCategory.valueOf(
        category.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasRange()) {
      hapiValue.range = range.toHapi()
    }
    if (hasContext()) {
      hapiValue.context = context.toHapi()
    }
    if (appliesToCount > 0) {
      hapiValue.appliesTo = appliesToList.map { it.toHapi() }
    }
    hapiValue.gender =
      Enumerations.AdministrativeGender.valueOf(gender.value.name.hapiCodeCheck().replace("_", ""))
    if (hasAge()) {
      hapiValue.age = age.toHapi()
    }
    if (hasGestationalAge()) {
      hapiValue.gestationalAge = gestationalAge.toHapi()
    }
    if (hasCondition()) {
      hapiValue.conditionElement = condition.toHapi()
    }
    return hapiValue
  }
}
