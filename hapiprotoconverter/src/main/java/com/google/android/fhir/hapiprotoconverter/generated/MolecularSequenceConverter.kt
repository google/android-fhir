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
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MolecularSequence
import com.google.fhir.r4.core.MolecularSequence.Quality
import com.google.fhir.r4.core.MolecularSequence.ReferenceSeq
import com.google.fhir.r4.core.MolecularSequence.Repository
import com.google.fhir.r4.core.MolecularSequence.StructureVariant
import com.google.fhir.r4.core.OrientationTypeCode
import com.google.fhir.r4.core.QualityTypeCode
import com.google.fhir.r4.core.RepositoryTypeCode
import com.google.fhir.r4.core.SequenceTypeCode
import com.google.fhir.r4.core.StrandTypeCode
import com.google.fhir.r4.core.String

public object MolecularSequenceConverter {
  public fun MolecularSequence.toHapi(): org.hl7.fhir.r4.model.MolecularSequence {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setType(org.hl7.fhir.r4.model.MolecularSequence.SequenceType.valueOf(type.value.name.replace("_","")))
    hapiValue.setCoordinateSystemElement(coordinateSystem.toHapi())
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setSpecimen(specimen.toHapi())
    hapiValue.setDevice(device.toHapi())
    hapiValue.setPerformer(performer.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setReferenceSeq(referenceSeq.toHapi())
    hapiValue.setVariant(variantList.map{it.toHapi()})
    hapiValue.setObservedSeqElement(observedSeq.toHapi())
    hapiValue.setQuality(qualityList.map{it.toHapi()})
    hapiValue.setReadCoverageElement(readCoverage.toHapi())
    hapiValue.setRepository(repositoryList.map{it.toHapi()})
    hapiValue.setPointer(pointerList.map{it.toHapi()})
    hapiValue.setStructureVariant(structureVariantList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MolecularSequence.toProto(): MolecularSequence {
    val protoValue = MolecularSequence.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setType(MolecularSequence.TypeCode.newBuilder().setValue(SequenceTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setCoordinateSystem(coordinateSystemElement.toProto())
    .setPatient(patient.toProto())
    .setSpecimen(specimen.toProto())
    .setDevice(device.toProto())
    .setPerformer(performer.toProto())
    .setQuantity(quantity.toProto())
    .setReferenceSeq(referenceSeq.toProto())
    .addAllVariant(variant.map{it.toProto()})
    .setObservedSeq(observedSeqElement.toProto())
    .addAllQuality(quality.map{it.toProto()})
    .setReadCoverage(readCoverageElement.toProto())
    .addAllRepository(repository.map{it.toProto()})
    .addAllPointer(pointer.map{it.toProto()})
    .addAllStructureVariant(structureVariant.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceReferenceSeqComponent.toProto():
      MolecularSequence.ReferenceSeq {
    val protoValue = MolecularSequence.ReferenceSeq.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setChromosome(chromosome.toProto())
    .setGenomeBuild(genomeBuildElement.toProto())
    .setOrientation(MolecularSequence.ReferenceSeq.OrientationCode.newBuilder().setValue(OrientationTypeCode.Value.valueOf(orientation.toCode().replace("-",
        "_").toUpperCase())).build())
    .setReferenceSeqId(referenceSeqId.toProto())
    .setReferenceSeqPointer(referenceSeqPointer.toProto())
    .setReferenceSeqString(referenceSeqStringElement.toProto())
    .setStrand(MolecularSequence.ReferenceSeq.StrandCode.newBuilder().setValue(StrandTypeCode.Value.valueOf(strand.toCode().replace("-",
        "_").toUpperCase())).build())
    .setWindowStart(windowStartElement.toProto())
    .setWindowEnd(windowEndElement.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceVariantComponent.toProto():
      MolecularSequence.Variant {
    val protoValue = MolecularSequence.Variant.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setStart(startElement.toProto())
    .setEnd(endElement.toProto())
    .setObservedAllele(observedAlleleElement.toProto())
    .setReferenceAllele(referenceAlleleElement.toProto())
    .setCigar(cigarElement.toProto())
    .setVariantPointer(variantPointer.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityComponent.toProto():
      MolecularSequence.Quality {
    val protoValue = MolecularSequence.Quality.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(MolecularSequence.Quality.TypeCode.newBuilder().setValue(QualityTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setStandardSequence(standardSequence.toProto())
    .setStart(startElement.toProto())
    .setEnd(endElement.toProto())
    .setScore(score.toProto())
    .setMethod(method.toProto())
    .setTruthTp(truthTPElement.toProto())
    .setQueryTp(queryTPElement.toProto())
    .setTruthFn(truthFNElement.toProto())
    .setQueryFp(queryFPElement.toProto())
    .setGtFp(gtFPElement.toProto())
    .setPrecision(precisionElement.toProto())
    .setRecall(recallElement.toProto())
    .setFScore(fScoreElement.toProto())
    .setRoc(roc.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityRocComponent.toProto():
      MolecularSequence.Quality.Roc {
    val protoValue = MolecularSequence.Quality.Roc.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllScore(score.map{it.toProto()})
    .addAllNumTp(numTP.map{it.toProto()})
    .addAllNumFp(numFP.map{it.toProto()})
    .addAllNumFn(numFN.map{it.toProto()})
    .addAllPrecision(precision.map{it.toProto()})
    .addAllSensitivity(sensitivity.map{it.toProto()})
    .addAllFMeasure(fMeasure.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceRepositoryComponent.toProto():
      MolecularSequence.Repository {
    val protoValue = MolecularSequence.Repository.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(MolecularSequence.Repository.TypeCode.newBuilder().setValue(RepositoryTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setUrl(urlElement.toProto())
    .setName(nameElement.toProto())
    .setDatasetId(datasetIdElement.toProto())
    .setVariantsetId(variantsetIdElement.toProto())
    .setReadsetId(readsetIdElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantComponent.toProto():
      MolecularSequence.StructureVariant {
    val protoValue = MolecularSequence.StructureVariant.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setVariantType(variantType.toProto())
    .setExact(exactElement.toProto())
    .setLength(lengthElement.toProto())
    .setOuter(outer.toProto())
    .setInner(inner.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantOuterComponent.toProto():
      MolecularSequence.StructureVariant.Outer {
    val protoValue = MolecularSequence.StructureVariant.Outer.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setStart(startElement.toProto())
    .setEnd(endElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantInnerComponent.toProto():
      MolecularSequence.StructureVariant.Inner {
    val protoValue = MolecularSequence.StructureVariant.Inner.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setStart(startElement.toProto())
    .setEnd(endElement.toProto())
    .build()
    return protoValue
  }

  private fun MolecularSequence.ReferenceSeq.toHapi():
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceReferenceSeqComponent {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceReferenceSeqComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setChromosome(chromosome.toHapi())
    hapiValue.setGenomeBuildElement(genomeBuild.toHapi())
    hapiValue.setOrientation(org.hl7.fhir.r4.model.MolecularSequence.OrientationType.valueOf(orientation.value.name.replace("_","")))
    hapiValue.setReferenceSeqId(referenceSeqId.toHapi())
    hapiValue.setReferenceSeqPointer(referenceSeqPointer.toHapi())
    hapiValue.setReferenceSeqStringElement(referenceSeqString.toHapi())
    hapiValue.setStrand(org.hl7.fhir.r4.model.MolecularSequence.StrandType.valueOf(strand.value.name.replace("_","")))
    hapiValue.setWindowStartElement(windowStart.toHapi())
    hapiValue.setWindowEndElement(windowEnd.toHapi())
    return hapiValue
  }

  private fun MolecularSequence.Variant.toHapi():
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceVariantComponent {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceVariantComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setStartElement(start.toHapi())
    hapiValue.setEndElement(end.toHapi())
    hapiValue.setObservedAlleleElement(observedAllele.toHapi())
    hapiValue.setReferenceAlleleElement(referenceAllele.toHapi())
    hapiValue.setCigarElement(cigar.toHapi())
    hapiValue.setVariantPointer(variantPointer.toHapi())
    return hapiValue
  }

  private fun MolecularSequence.Quality.toHapi():
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityComponent {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(org.hl7.fhir.r4.model.MolecularSequence.QualityType.valueOf(type.value.name.replace("_","")))
    hapiValue.setStandardSequence(standardSequence.toHapi())
    hapiValue.setStartElement(start.toHapi())
    hapiValue.setEndElement(end.toHapi())
    hapiValue.setScore(score.toHapi())
    hapiValue.setMethod(method.toHapi())
    hapiValue.setTruthTPElement(truthTp.toHapi())
    hapiValue.setQueryTPElement(queryTp.toHapi())
    hapiValue.setTruthFNElement(truthFn.toHapi())
    hapiValue.setQueryFPElement(queryFp.toHapi())
    hapiValue.setGtFPElement(gtFp.toHapi())
    hapiValue.setPrecisionElement(precision.toHapi())
    hapiValue.setRecallElement(recall.toHapi())
    hapiValue.setFScoreElement(fScore.toHapi())
    hapiValue.setRoc(roc.toHapi())
    return hapiValue
  }

  private fun MolecularSequence.Quality.Roc.toHapi():
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityRocComponent {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityRocComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setScore(scoreList.map{it.toHapi()})
    hapiValue.setNumTP(numTpList.map{it.toHapi()})
    hapiValue.setNumFP(numFpList.map{it.toHapi()})
    hapiValue.setNumFN(numFnList.map{it.toHapi()})
    hapiValue.setPrecision(precisionList.map{it.toHapi()})
    hapiValue.setSensitivity(sensitivityList.map{it.toHapi()})
    hapiValue.setFMeasure(fMeasureList.map{it.toHapi()})
    return hapiValue
  }

  private fun MolecularSequence.Repository.toHapi():
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceRepositoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceRepositoryComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(org.hl7.fhir.r4.model.MolecularSequence.RepositoryType.valueOf(type.value.name.replace("_","")))
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDatasetIdElement(datasetId.toHapi())
    hapiValue.setVariantsetIdElement(variantsetId.toHapi())
    hapiValue.setReadsetIdElement(readsetId.toHapi())
    return hapiValue
  }

  private fun MolecularSequence.StructureVariant.toHapi():
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setVariantType(variantType.toHapi())
    hapiValue.setExactElement(exact.toHapi())
    hapiValue.setLengthElement(length.toHapi())
    hapiValue.setOuter(outer.toHapi())
    hapiValue.setInner(inner.toHapi())
    return hapiValue
  }

  private fun MolecularSequence.StructureVariant.Outer.toHapi():
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantOuterComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantOuterComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setStartElement(start.toHapi())
    hapiValue.setEndElement(end.toHapi())
    return hapiValue
  }

  private fun MolecularSequence.StructureVariant.Inner.toHapi():
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantInnerComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantInnerComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setStartElement(start.toHapi())
    hapiValue.setEndElement(end.toHapi())
    return hapiValue
  }
}
