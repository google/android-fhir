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

public object ObservationDefinitionConverter {
  @JvmStatic
  public fun ObservationDefinition.toHapi(): org.hl7.fhir.r4.model.ObservationDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ObservationDefinition()
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
    if (categoryCount > 0) {
      hapiValue.setCategory(categoryList.map { it.toHapi() })
    }
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    permittedDataTypeList.forEach {
      hapiValue.addPermittedDataType(
        org.hl7.fhir.r4.model.ObservationDefinition.ObservationDataType.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasMultipleResultsAllowed()) {
      hapiValue.setMultipleResultsAllowedElement(multipleResultsAllowed.toHapi())
    }
    if (hasMethod()) {
      hapiValue.setMethod(method.toHapi())
    }
    if (hasPreferredReportName()) {
      hapiValue.setPreferredReportNameElement(preferredReportName.toHapi())
    }
    if (hasQuantitativeDetails()) {
      hapiValue.setQuantitativeDetails(quantitativeDetails.toHapi())
    }
    if (qualifiedIntervalCount > 0) {
      hapiValue.setQualifiedInterval(qualifiedIntervalList.map { it.toHapi() })
    }
    if (hasValidCodedValueSet()) {
      hapiValue.setValidCodedValueSet(validCodedValueSet.toHapi())
    }
    if (hasNormalCodedValueSet()) {
      hapiValue.setNormalCodedValueSet(normalCodedValueSet.toHapi())
    }
    if (hasAbnormalCodedValueSet()) {
      hapiValue.setAbnormalCodedValueSet(abnormalCodedValueSet.toHapi())
    }
    if (hasCriticalCodedValueSet()) {
      hapiValue.setCriticalCodedValueSet(criticalCodedValueSet.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ObservationDefinition.toProto(): ObservationDefinition {
    val protoValue = ObservationDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
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
      protoValue.setMultipleResultsAllowed(multipleResultsAllowedElement.toProto())
    }
    if (hasMethod()) {
      protoValue.setMethod(method.toProto())
    }
    if (hasPreferredReportName()) {
      protoValue.setPreferredReportName(preferredReportNameElement.toProto())
    }
    if (hasQuantitativeDetails()) {
      protoValue.setQuantitativeDetails(quantitativeDetails.toProto())
    }
    if (hasQualifiedInterval()) {
      protoValue.addAllQualifiedInterval(qualifiedInterval.map { it.toProto() })
    }
    if (hasValidCodedValueSet()) {
      protoValue.setValidCodedValueSet(validCodedValueSet.toProto())
    }
    if (hasNormalCodedValueSet()) {
      protoValue.setNormalCodedValueSet(normalCodedValueSet.toProto())
    }
    if (hasAbnormalCodedValueSet()) {
      protoValue.setAbnormalCodedValueSet(abnormalCodedValueSet.toProto())
    }
    if (hasCriticalCodedValueSet()) {
      protoValue.setCriticalCodedValueSet(criticalCodedValueSet.toProto())
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
      protoValue.setCustomaryUnit(customaryUnit.toProto())
    }
    if (hasUnit()) {
      protoValue.setUnit(unit.toProto())
    }
    if (hasConversionFactor()) {
      protoValue.setConversionFactor(conversionFactorElement.toProto())
    }
    if (hasDecimalPrecision()) {
      protoValue.setDecimalPrecision(decimalPrecisionElement.toProto())
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
    protoValue.setCategory(
      ObservationDefinition.QualifiedInterval.CategoryCode.newBuilder()
        .setValue(
          ObservationRangeCategoryCode.Value.valueOf(
            category.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasRange()) {
      protoValue.setRange(range.toProto())
    }
    if (hasContext()) {
      protoValue.setContext(context.toProto())
    }
    if (hasAppliesTo()) {
      protoValue.addAllAppliesTo(appliesTo.map { it.toProto() })
    }
    protoValue.setGender(
      ObservationDefinition.QualifiedInterval.GenderCode.newBuilder()
        .setValue(
          AdministrativeGenderCode.Value.valueOf(
            gender.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasAge()) {
      protoValue.setAge(age.toProto())
    }
    if (hasGestationalAge()) {
      protoValue.setGestationalAge(gestationalAge.toProto())
    }
    if (hasCondition()) {
      protoValue.setCondition(conditionElement.toProto())
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
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCustomaryUnit()) {
      hapiValue.setCustomaryUnit(customaryUnit.toHapi())
    }
    if (hasUnit()) {
      hapiValue.setUnit(unit.toHapi())
    }
    if (hasConversionFactor()) {
      hapiValue.setConversionFactorElement(conversionFactor.toHapi())
    }
    if (hasDecimalPrecision()) {
      hapiValue.setDecimalPrecisionElement(decimalPrecision.toHapi())
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
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setCategory(
      org.hl7.fhir.r4.model.ObservationDefinition.ObservationRangeCategory.valueOf(
        category.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasRange()) {
      hapiValue.setRange(range.toHapi())
    }
    if (hasContext()) {
      hapiValue.setContext(context.toHapi())
    }
    if (appliesToCount > 0) {
      hapiValue.setAppliesTo(appliesToList.map { it.toHapi() })
    }
    hapiValue.setGender(
      Enumerations.AdministrativeGender.valueOf(gender.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasAge()) {
      hapiValue.setAge(age.toHapi())
    }
    if (hasGestationalAge()) {
      hapiValue.setGestationalAge(gestationalAge.toHapi())
    }
    if (hasCondition()) {
      hapiValue.setConditionElement(condition.toHapi())
    }
    return hapiValue
  }
}
