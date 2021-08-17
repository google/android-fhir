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

object MolecularSequenceConverter {
  fun MolecularSequence.toHapi(): org.hl7.fhir.r4.model.MolecularSequence {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence()
    if (hasId()) {
      hapiValue.id = id.value
    }
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type =
        org.hl7.fhir.r4.model.MolecularSequence.SequenceType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasCoordinateSystem()) {
      hapiValue.coordinateSystemElement = coordinateSystem.toHapi()
    }
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (hasSpecimen()) {
      hapiValue.specimen = specimen.toHapi()
    }
    if (hasDevice()) {
      hapiValue.device = device.toHapi()
    }
    if (hasPerformer()) {
      hapiValue.performer = performer.toHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasReferenceSeq()) {
      hapiValue.referenceSeq = referenceSeq.toHapi()
    }
    if (variantCount > 0) {
      hapiValue.variant = variantList.map { it.toHapi() }
    }
    if (hasObservedSeq()) {
      hapiValue.observedSeqElement = observedSeq.toHapi()
    }
    if (qualityCount > 0) {
      hapiValue.quality = qualityList.map { it.toHapi() }
    }
    if (hasReadCoverage()) {
      hapiValue.readCoverageElement = readCoverage.toHapi()
    }
    if (repositoryCount > 0) {
      hapiValue.repository = repositoryList.map { it.toHapi() }
    }
    if (pointerCount > 0) {
      hapiValue.pointer = pointerList.map { it.toHapi() }
    }
    if (structureVariantCount > 0) {
      hapiValue.structureVariant = structureVariantList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.MolecularSequence.toProto(): MolecularSequence {
    val protoValue = MolecularSequence.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
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
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type =
        MolecularSequence.TypeCode.newBuilder()
          .setValue(
            SequenceTypeCode.Value.valueOf(
              type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasCoordinateSystem()) {
      protoValue.coordinateSystem = coordinateSystemElement.toProto()
    }
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasSpecimen()) {
      protoValue.specimen = specimen.toProto()
    }
    if (hasDevice()) {
      protoValue.device = device.toProto()
    }
    if (hasPerformer()) {
      protoValue.performer = performer.toProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = quantity.toProto()
    }
    if (hasReferenceSeq()) {
      protoValue.referenceSeq = referenceSeq.toProto()
    }
    if (hasVariant()) {
      protoValue.addAllVariant(variant.map { it.toProto() })
    }
    if (hasObservedSeq()) {
      protoValue.observedSeq = observedSeqElement.toProto()
    }
    if (hasQuality()) {
      protoValue.addAllQuality(quality.map { it.toProto() })
    }
    if (hasReadCoverage()) {
      protoValue.readCoverage = readCoverageElement.toProto()
    }
    if (hasRepository()) {
      protoValue.addAllRepository(repository.map { it.toProto() })
    }
    if (hasPointer()) {
      protoValue.addAllPointer(pointer.map { it.toProto() })
    }
    if (hasStructureVariant()) {
      protoValue.addAllStructureVariant(structureVariant.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceReferenceSeqComponent.toProto():
    MolecularSequence.ReferenceSeq {
    val protoValue = MolecularSequence.ReferenceSeq.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasChromosome()) {
      protoValue.chromosome = chromosome.toProto()
    }
    if (hasGenomeBuild()) {
      protoValue.genomeBuild = genomeBuildElement.toProto()
    }
    if (hasOrientation()) {
      protoValue.orientation =
        MolecularSequence.ReferenceSeq.OrientationCode.newBuilder()
          .setValue(
            OrientationTypeCode.Value.valueOf(
              orientation.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasReferenceSeqId()) {
      protoValue.referenceSeqId = referenceSeqId.toProto()
    }
    if (hasReferenceSeqPointer()) {
      protoValue.referenceSeqPointer = referenceSeqPointer.toProto()
    }
    if (hasReferenceSeqString()) {
      protoValue.referenceSeqString = referenceSeqStringElement.toProto()
    }
    if (hasStrand()) {
      protoValue.strand =
        MolecularSequence.ReferenceSeq.StrandCode.newBuilder()
          .setValue(
            StrandTypeCode.Value.valueOf(
              strand.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasWindowStart()) {
      protoValue.windowStart = windowStartElement.toProto()
    }
    if (hasWindowEnd()) {
      protoValue.windowEnd = windowEndElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceVariantComponent.toProto():
    MolecularSequence.Variant {
    val protoValue = MolecularSequence.Variant.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasStart()) {
      protoValue.start = startElement.toProto()
    }
    if (hasEnd()) {
      protoValue.end = endElement.toProto()
    }
    if (hasObservedAllele()) {
      protoValue.observedAllele = observedAlleleElement.toProto()
    }
    if (hasReferenceAllele()) {
      protoValue.referenceAllele = referenceAlleleElement.toProto()
    }
    if (hasCigar()) {
      protoValue.cigar = cigarElement.toProto()
    }
    if (hasVariantPointer()) {
      protoValue.variantPointer = variantPointer.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityComponent.toProto():
    MolecularSequence.Quality {
    val protoValue = MolecularSequence.Quality.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type =
        MolecularSequence.Quality.TypeCode.newBuilder()
          .setValue(
            QualityTypeCode.Value.valueOf(
              type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasStandardSequence()) {
      protoValue.standardSequence = standardSequence.toProto()
    }
    if (hasStart()) {
      protoValue.start = startElement.toProto()
    }
    if (hasEnd()) {
      protoValue.end = endElement.toProto()
    }
    if (hasScore()) {
      protoValue.score = score.toProto()
    }
    if (hasMethod()) {
      protoValue.method = method.toProto()
    }
    if (hasTruthTP()) {
      protoValue.truthTp = truthTPElement.toProto()
    }
    if (hasQueryTP()) {
      protoValue.queryTp = queryTPElement.toProto()
    }
    if (hasTruthFN()) {
      protoValue.truthFn = truthFNElement.toProto()
    }
    if (hasQueryFP()) {
      protoValue.queryFp = queryFPElement.toProto()
    }
    if (hasGtFP()) {
      protoValue.gtFp = gtFPElement.toProto()
    }
    if (hasPrecision()) {
      protoValue.precision = precisionElement.toProto()
    }
    if (hasRecall()) {
      protoValue.recall = recallElement.toProto()
    }
    if (hasFScore()) {
      protoValue.fScore = fScoreElement.toProto()
    }
    if (hasRoc()) {
      protoValue.roc = roc.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityRocComponent.toProto():
    MolecularSequence.Quality.Roc {
    val protoValue = MolecularSequence.Quality.Roc.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasScore()) {
      protoValue.addAllScore(score.map { it.toProto() })
    }
    if (hasNumTP()) {
      protoValue.addAllNumTp(numTP.map { it.toProto() })
    }
    if (hasNumFP()) {
      protoValue.addAllNumFp(numFP.map { it.toProto() })
    }
    if (hasNumFN()) {
      protoValue.addAllNumFn(numFN.map { it.toProto() })
    }
    if (hasPrecision()) {
      protoValue.addAllPrecision(precision.map { it.toProto() })
    }
    if (hasSensitivity()) {
      protoValue.addAllSensitivity(sensitivity.map { it.toProto() })
    }
    if (hasFMeasure()) {
      protoValue.addAllFMeasure(fMeasure.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceRepositoryComponent.toProto():
    MolecularSequence.Repository {
    val protoValue = MolecularSequence.Repository.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type =
        MolecularSequence.Repository.TypeCode.newBuilder()
          .setValue(
            RepositoryTypeCode.Value.valueOf(
              type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasDatasetId()) {
      protoValue.datasetId = datasetIdElement.toProto()
    }
    if (hasVariantsetId()) {
      protoValue.variantsetId = variantsetIdElement.toProto()
    }
    if (hasReadsetId()) {
      protoValue.readsetId = readsetIdElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantComponent.toProto():
    MolecularSequence.StructureVariant {
    val protoValue = MolecularSequence.StructureVariant.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasVariantType()) {
      protoValue.variantType = variantType.toProto()
    }
    if (hasExact()) {
      protoValue.exact = exactElement.toProto()
    }
    if (hasLength()) {
      protoValue.length = lengthElement.toProto()
    }
    if (hasOuter()) {
      protoValue.outer = outer.toProto()
    }
    if (hasInner()) {
      protoValue.inner = inner.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantOuterComponent.toProto():
    MolecularSequence.StructureVariant.Outer {
    val protoValue = MolecularSequence.StructureVariant.Outer.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasStart()) {
      protoValue.start = startElement.toProto()
    }
    if (hasEnd()) {
      protoValue.end = endElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantInnerComponent.toProto():
    MolecularSequence.StructureVariant.Inner {
    val protoValue = MolecularSequence.StructureVariant.Inner.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasStart()) {
      protoValue.start = startElement.toProto()
    }
    if (hasEnd()) {
      protoValue.end = endElement.toProto()
    }
    return protoValue.build()
  }

  private fun MolecularSequence.ReferenceSeq.toHapi():
    org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceReferenceSeqComponent {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceReferenceSeqComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasChromosome()) {
      hapiValue.chromosome = chromosome.toHapi()
    }
    if (hasGenomeBuild()) {
      hapiValue.genomeBuildElement = genomeBuild.toHapi()
    }
    if (hasOrientation()) {
      hapiValue.orientation =
        org.hl7.fhir.r4.model.MolecularSequence.OrientationType.valueOf(
          orientation.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasReferenceSeqId()) {
      hapiValue.referenceSeqId = referenceSeqId.toHapi()
    }
    if (hasReferenceSeqPointer()) {
      hapiValue.referenceSeqPointer = referenceSeqPointer.toHapi()
    }
    if (hasReferenceSeqString()) {
      hapiValue.referenceSeqStringElement = referenceSeqString.toHapi()
    }
    if (hasStrand()) {
      hapiValue.strand =
        org.hl7.fhir.r4.model.MolecularSequence.StrandType.valueOf(
          strand.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasWindowStart()) {
      hapiValue.windowStartElement = windowStart.toHapi()
    }
    if (hasWindowEnd()) {
      hapiValue.windowEndElement = windowEnd.toHapi()
    }
    return hapiValue
  }

  private fun MolecularSequence.Variant.toHapi():
    org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceVariantComponent {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceVariantComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasStart()) {
      hapiValue.startElement = start.toHapi()
    }
    if (hasEnd()) {
      hapiValue.endElement = end.toHapi()
    }
    if (hasObservedAllele()) {
      hapiValue.observedAlleleElement = observedAllele.toHapi()
    }
    if (hasReferenceAllele()) {
      hapiValue.referenceAlleleElement = referenceAllele.toHapi()
    }
    if (hasCigar()) {
      hapiValue.cigarElement = cigar.toHapi()
    }
    if (hasVariantPointer()) {
      hapiValue.variantPointer = variantPointer.toHapi()
    }
    return hapiValue
  }

  private fun MolecularSequence.Quality.toHapi():
    org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityComponent {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type =
        org.hl7.fhir.r4.model.MolecularSequence.QualityType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasStandardSequence()) {
      hapiValue.standardSequence = standardSequence.toHapi()
    }
    if (hasStart()) {
      hapiValue.startElement = start.toHapi()
    }
    if (hasEnd()) {
      hapiValue.endElement = end.toHapi()
    }
    if (hasScore()) {
      hapiValue.score = score.toHapi()
    }
    if (hasMethod()) {
      hapiValue.method = method.toHapi()
    }
    if (hasTruthTp()) {
      hapiValue.truthTPElement = truthTp.toHapi()
    }
    if (hasQueryTp()) {
      hapiValue.queryTPElement = queryTp.toHapi()
    }
    if (hasTruthFn()) {
      hapiValue.truthFNElement = truthFn.toHapi()
    }
    if (hasQueryFp()) {
      hapiValue.queryFPElement = queryFp.toHapi()
    }
    if (hasGtFp()) {
      hapiValue.gtFPElement = gtFp.toHapi()
    }
    if (hasPrecision()) {
      hapiValue.precisionElement = precision.toHapi()
    }
    if (hasRecall()) {
      hapiValue.recallElement = recall.toHapi()
    }
    if (hasFScore()) {
      hapiValue.fScoreElement = fScore.toHapi()
    }
    if (hasRoc()) {
      hapiValue.roc = roc.toHapi()
    }
    return hapiValue
  }

  private fun MolecularSequence.Quality.Roc.toHapi():
    org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityRocComponent {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceQualityRocComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (scoreCount > 0) {
      hapiValue.score = scoreList.map { it.toHapi() }
    }
    if (numTpCount > 0) {
      hapiValue.numTP = numTpList.map { it.toHapi() }
    }
    if (numFpCount > 0) {
      hapiValue.numFP = numFpList.map { it.toHapi() }
    }
    if (numFnCount > 0) {
      hapiValue.numFN = numFnList.map { it.toHapi() }
    }
    if (precisionCount > 0) {
      hapiValue.precision = precisionList.map { it.toHapi() }
    }
    if (sensitivityCount > 0) {
      hapiValue.sensitivity = sensitivityList.map { it.toHapi() }
    }
    if (fMeasureCount > 0) {
      hapiValue.fMeasure = fMeasureList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun MolecularSequence.Repository.toHapi():
    org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceRepositoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceRepositoryComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type =
        org.hl7.fhir.r4.model.MolecularSequence.RepositoryType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasDatasetId()) {
      hapiValue.datasetIdElement = datasetId.toHapi()
    }
    if (hasVariantsetId()) {
      hapiValue.variantsetIdElement = variantsetId.toHapi()
    }
    if (hasReadsetId()) {
      hapiValue.readsetIdElement = readsetId.toHapi()
    }
    return hapiValue
  }

  private fun MolecularSequence.StructureVariant.toHapi():
    org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasVariantType()) {
      hapiValue.variantType = variantType.toHapi()
    }
    if (hasExact()) {
      hapiValue.exactElement = exact.toHapi()
    }
    if (hasLength()) {
      hapiValue.lengthElement = length.toHapi()
    }
    if (hasOuter()) {
      hapiValue.outer = outer.toHapi()
    }
    if (hasInner()) {
      hapiValue.inner = inner.toHapi()
    }
    return hapiValue
  }

  private fun MolecularSequence.StructureVariant.Outer.toHapi():
    org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantOuterComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantOuterComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasStart()) {
      hapiValue.startElement = start.toHapi()
    }
    if (hasEnd()) {
      hapiValue.endElement = end.toHapi()
    }
    return hapiValue
  }

  private fun MolecularSequence.StructureVariant.Inner.toHapi():
    org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantInnerComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceStructureVariantInnerComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasStart()) {
      hapiValue.startElement = start.toHapi()
    }
    if (hasEnd()) {
      hapiValue.endElement = end.toHapi()
    }
    return hapiValue
  }
}
