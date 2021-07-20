package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.fhir.r4.core.ObservationRangeCategoryCode
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

public object ObservationDefinitionConverter {
  public fun ObservationDefinition.toHapi(): org.hl7.fhir.r4.model.ObservationDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ObservationDefinition()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCategory(categoryList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    permittedDataTypeList.map{hapiValue.addPermittedDataType(org.hl7.fhir.r4.model.ObservationDefinition.ObservationDataType.valueOf(it.value.name.replace("_","")))}
    hapiValue.setMultipleResultsAllowedElement(multipleResultsAllowed.toHapi())
    hapiValue.setMethod(method.toHapi())
    hapiValue.setPreferredReportNameElement(preferredReportName.toHapi())
    hapiValue.setQuantitativeDetails(quantitativeDetails.toHapi())
    hapiValue.setQualifiedInterval(qualifiedIntervalList.map{it.toHapi()})
    hapiValue.setValidCodedValueSet(validCodedValueSet.toHapi())
    hapiValue.setNormalCodedValueSet(normalCodedValueSet.toHapi())
    hapiValue.setAbnormalCodedValueSet(abnormalCodedValueSet.toHapi())
    hapiValue.setCriticalCodedValueSet(criticalCodedValueSet.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ObservationDefinition.toProto(): ObservationDefinition {
    val protoValue = ObservationDefinition.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllCategory(category.map{it.toProto()})
    .setCode(code.toProto())
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllPermittedDataType(permittedDataType.map{ObservationDefinition.PermittedDataTypeCode.newBuilder().setValue(ObservationDataTypeCode.Value.valueOf(it.value.toCode().replace("-",
        "_").toUpperCase())).build()})
    .setMultipleResultsAllowed(multipleResultsAllowedElement.toProto())
    .setMethod(method.toProto())
    .setPreferredReportName(preferredReportNameElement.toProto())
    .setQuantitativeDetails(quantitativeDetails.toProto())
    .addAllQualifiedInterval(qualifiedInterval.map{it.toProto()})
    .setValidCodedValueSet(validCodedValueSet.toProto())
    .setNormalCodedValueSet(normalCodedValueSet.toProto())
    .setAbnormalCodedValueSet(abnormalCodedValueSet.toProto())
    .setCriticalCodedValueSet(criticalCodedValueSet.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQuantitativeDetailsComponent.toProto():
      ObservationDefinition.QuantitativeDetails {
    val protoValue = ObservationDefinition.QuantitativeDetails.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCustomaryUnit(customaryUnit.toProto())
    .setUnit(unit.toProto())
    .setConversionFactor(conversionFactorElement.toProto())
    .setDecimalPrecision(decimalPrecisionElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQualifiedIntervalComponent.toProto():
      ObservationDefinition.QualifiedInterval {
    val protoValue = ObservationDefinition.QualifiedInterval.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCategory(ObservationDefinition.QualifiedInterval.CategoryCode.newBuilder().setValue(ObservationRangeCategoryCode.Value.valueOf(category.toCode().replace("-",
        "_").toUpperCase())).build())
    .setRange(range.toProto())
    .setContext(context.toProto())
    .addAllAppliesTo(appliesTo.map{it.toProto()})
    .setGender(ObservationDefinition.QualifiedInterval.GenderCode.newBuilder().setValue(AdministrativeGenderCode.Value.valueOf(gender.toCode().replace("-",
        "_").toUpperCase())).build())
    .setAge(age.toProto())
    .setGestationalAge(gestationalAge.toProto())
    .setCondition(conditionElement.toProto())
    .build()
    return protoValue
  }

  public fun ObservationDefinition.QuantitativeDetails.toHapi():
      org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQuantitativeDetailsComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQuantitativeDetailsComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCustomaryUnit(customaryUnit.toHapi())
    hapiValue.setUnit(unit.toHapi())
    hapiValue.setConversionFactorElement(conversionFactor.toHapi())
    hapiValue.setDecimalPrecisionElement(decimalPrecision.toHapi())
    return hapiValue
  }

  public fun ObservationDefinition.QualifiedInterval.toHapi():
      org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQualifiedIntervalComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ObservationDefinition.ObservationDefinitionQualifiedIntervalComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCategory(org.hl7.fhir.r4.model.ObservationDefinition.ObservationRangeCategory.valueOf(category.value.name.replace("_","")))
    hapiValue.setRange(range.toHapi())
    hapiValue.setContext(context.toHapi())
    hapiValue.setAppliesTo(appliesToList.map{it.toHapi()})
    hapiValue.setGender(Enumerations.AdministrativeGender.valueOf(gender.value.name.replace("_","")))
    hapiValue.setAge(age.toHapi())
    hapiValue.setGestationalAge(gestationalAge.toHapi())
    hapiValue.setConditionElement(condition.toHapi())
    return hapiValue
  }
}
