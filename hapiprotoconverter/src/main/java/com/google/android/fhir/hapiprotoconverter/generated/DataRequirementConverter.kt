package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DataRequirement
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Duration
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.SortDirectionCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

private fun DataRequirement.SubjectX.dataRequirementSubjectToHapi(): Type {
  if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
    return (this.getCodeableConcept()).toHapi()
  }
  if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
    return (this.getReference()).toHapi()
  }
  throw IllegalArgumentException("Invalid Type for DataRequirement.subject[x]")
}

private fun Type.dataRequirementSubjectToProto(): DataRequirement.SubjectX {
  val protoValue = DataRequirement.SubjectX.newBuilder()
  if (this is org.hl7.fhir.r4.model.CodeableConcept) {
    protoValue.setCodeableConcept(this.toProto())
  }
  if (this is org.hl7.fhir.r4.model.Reference) {
    protoValue.setReference(this.toProto())
  }
  return protoValue.build()
}

private fun DataRequirement.DateFilter.ValueX.dataRequirementDateFilterValueToHapi(): Type {
  if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
    return (this.getDateTime()).toHapi()
  }
  if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
    return (this.getPeriod()).toHapi()
  }
  if (this.getDuration() != Duration.newBuilder().defaultInstanceForType ) {
    return (this.getDuration()).toHapi()
  }
  throw IllegalArgumentException("Invalid Type for DataRequirement.dateFilter.value[x]")
}

private fun Type.dataRequirementDateFilterValueToProto(): DataRequirement.DateFilter.ValueX {
  val protoValue = DataRequirement.DateFilter.ValueX.newBuilder()
  if (this is DateTimeType) {
    protoValue.setDateTime(this.toProto())
  }
  if (this is org.hl7.fhir.r4.model.Period) {
    protoValue.setPeriod(this.toProto())
  }
  if (this is org.hl7.fhir.r4.model.Duration) {
    protoValue.setDuration(this.toProto())
  }
  return protoValue.build()
}

public fun DataRequirement.toHapi(): org.hl7.fhir.r4.model.DataRequirement {
  val hapiValue = org.hl7.fhir.r4.model.DataRequirement()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  //hapiValue.setTypeElement(type.toHapi())
  hapiValue.setProfile(profileList.map{it.toHapi()})
  hapiValue.setSubject(subject.dataRequirementSubjectToHapi())
  hapiValue.setMustSupport(mustSupportList.map{it.toHapi()})
  hapiValue.setCodeFilter(codeFilterList.map{it.toHapi()})
  hapiValue.setDateFilter(dateFilterList.map{it.toHapi()})
  hapiValue.setLimitElement(limit.toHapi())
  hapiValue.setSort(sortList.map{it.toHapi()})
  return hapiValue
}

public fun org.hl7.fhir.r4.model.DataRequirement.toProto(): DataRequirement {
  val protoValue = DataRequirement.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  //.setType(typeElement.toProto())
  .addAllProfile(profile.map{it.toProto()})
  .setSubject(subject.dataRequirementSubjectToProto())
  .addAllMustSupport(mustSupport.map{it.toProto()})
  .addAllCodeFilter(codeFilter.map{it.toProto()})
  .addAllDateFilter(dateFilter.map{it.toProto()})
  .setLimit(limitElement.toProto())
  .addAllSort(sort.map{it.toProto()})
  .build()
  return protoValue
}

private fun org.hl7.fhir.r4.model.DataRequirement.DataRequirementCodeFilterComponent.toProto():
    DataRequirement.CodeFilter {
  val protoValue = DataRequirement.CodeFilter.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setPath(pathElement.toProto())
  .setSearchParam(searchParamElement.toProto())
  .setValueSet(valueSetElement.toProto())
  .addAllCode(code.map{it.toProto()})
  .build()
  return protoValue
}

private fun org.hl7.fhir.r4.model.DataRequirement.DataRequirementDateFilterComponent.toProto():
    DataRequirement.DateFilter {
  val protoValue = DataRequirement.DateFilter.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setPath(pathElement.toProto())
  .setSearchParam(searchParamElement.toProto())
  .setValue(value.dataRequirementDateFilterValueToProto())
  .build()
  return protoValue
}

private fun org.hl7.fhir.r4.model.DataRequirement.DataRequirementSortComponent.toProto():
    DataRequirement.Sort {
  val protoValue = DataRequirement.Sort.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setPath(pathElement.toProto())
  .setDirection(DataRequirement.Sort.DirectionCode.newBuilder().setValue(SortDirectionCode.Value.valueOf(direction.toCode().replace("-",
      "_").toUpperCase())).build())
  .build()
  return protoValue
}

private fun DataRequirement.CodeFilter.toHapi():
    org.hl7.fhir.r4.model.DataRequirement.DataRequirementCodeFilterComponent {
  val hapiValue = org.hl7.fhir.r4.model.DataRequirement.DataRequirementCodeFilterComponent()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setPathElement(path.toHapi())
  hapiValue.setSearchParamElement(searchParam.toHapi())
  hapiValue.setValueSetElement(valueSet.toHapi())
  hapiValue.setCode(codeList.map{it.toHapi()})
  return hapiValue
}

private fun DataRequirement.DateFilter.toHapi():
    org.hl7.fhir.r4.model.DataRequirement.DataRequirementDateFilterComponent {
  val hapiValue = org.hl7.fhir.r4.model.DataRequirement.DataRequirementDateFilterComponent()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setPathElement(path.toHapi())
  hapiValue.setSearchParamElement(searchParam.toHapi())
  hapiValue.setValue(value.dataRequirementDateFilterValueToHapi())
  return hapiValue
}

private fun DataRequirement.Sort.toHapi():
    org.hl7.fhir.r4.model.DataRequirement.DataRequirementSortComponent {
  val hapiValue = org.hl7.fhir.r4.model.DataRequirement.DataRequirementSortComponent()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setPathElement(path.toHapi())
  hapiValue.setDirection(org.hl7.fhir.r4.model.DataRequirement.SortDirection.valueOf(direction.value.name.replace("_","")))
  return hapiValue
}
