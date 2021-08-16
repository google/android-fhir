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

import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MeasureReport
import com.google.fhir.r4.core.MeasureReport.Group
import com.google.fhir.r4.core.MeasureReport.Group.Stratifier
import com.google.fhir.r4.core.MeasureReport.Group.Stratifier.StratifierGroup
import com.google.fhir.r4.core.MeasureReportStatusCode
import com.google.fhir.r4.core.MeasureReportTypeCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object MeasureReportConverter {
  @JvmStatic
  fun MeasureReport.toHapi(): org.hl7.fhir.r4.model.MeasureReport {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport()
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
    if (identifierCount > 0) {
        hapiValue.identifier = identifierList.map { it.toHapi() }
    }
      hapiValue.status = org.hl7.fhir.r4.model.MeasureReport.MeasureReportStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
      hapiValue.type = org.hl7.fhir.r4.model.MeasureReport.MeasureReportType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasMeasure()) {
        hapiValue.measureElement = measure.toHapi()
    }
    if (hasSubject()) {
        hapiValue.subject = subject.toHapi()
    }
    if (hasDate()) {
        hapiValue.dateElement = date.toHapi()
    }
    if (hasReporter()) {
        hapiValue.reporter = reporter.toHapi()
    }
    if (hasPeriod()) {
        hapiValue.period = period.toHapi()
    }
    if (hasImprovementNotation()) {
        hapiValue.improvementNotation = improvementNotation.toHapi()
    }
    if (groupCount > 0) {
        hapiValue.group = groupList.map { it.toHapi() }
    }
    if (evaluatedResourceCount > 0) {
        hapiValue.evaluatedResource = evaluatedResourceList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.MeasureReport.toProto(): MeasureReport {
    val protoValue = MeasureReport.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.status = MeasureReport.StatusCode.newBuilder()
          .setValue(
              MeasureReportStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
      protoValue.type = MeasureReport.TypeCode.newBuilder()
          .setValue(
              MeasureReportTypeCode.Value.valueOf(
                  type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasMeasure()) {
        protoValue.measure = measureElement.toProto()
    }
    if (hasSubject()) {
        protoValue.subject = subject.toProto()
    }
    if (hasDate()) {
        protoValue.date = dateElement.toProto()
    }
    if (hasReporter()) {
        protoValue.reporter = reporter.toProto()
    }
    if (hasPeriod()) {
        protoValue.period = period.toProto()
    }
    if (hasImprovementNotation()) {
        protoValue.improvementNotation = improvementNotation.toProto()
    }
    if (hasGroup()) {
      protoValue.addAllGroup(group.map { it.toProto() })
    }
    if (hasEvaluatedResource()) {
      protoValue.addAllEvaluatedResource(evaluatedResource.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupComponent.toProto():
    MeasureReport.Group {
    val protoValue = MeasureReport.Group.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
        protoValue.code = code.toProto()
    }
    if (hasPopulation()) {
      protoValue.addAllPopulation(population.map { it.toProto() })
    }
    if (hasMeasureScore()) {
        protoValue.measureScore = measureScore.toProto()
    }
    if (hasStratifier()) {
      protoValue.addAllStratifier(stratifier.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupPopulationComponent.toProto():
    MeasureReport.Group.Population {
    val protoValue =
      MeasureReport.Group.Population.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
        protoValue.code = code.toProto()
    }
    if (hasCount()) {
        protoValue.count = countElement.toProto()
    }
    if (hasSubjectResults()) {
        protoValue.subjectResults = subjectResults.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupStratifierComponent.toProto():
    MeasureReport.Group.Stratifier {
    val protoValue =
      MeasureReport.Group.Stratifier.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasStratum()) {
      protoValue.addAllStratum(stratum.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponent.toProto():
    MeasureReport.Group.Stratifier.StratifierGroup {
    val protoValue =
      MeasureReport.Group.Stratifier.StratifierGroup.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasValue()) {
        protoValue.value = value.toProto()
    }
    if (hasComponent()) {
      protoValue.addAllComponent(component.map { it.toProto() })
    }
    if (hasPopulation()) {
      protoValue.addAllPopulation(population.map { it.toProto() })
    }
    if (hasMeasureScore()) {
        protoValue.measureScore = measureScore.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponentComponent.toProto():
    MeasureReport.Group.Stratifier.StratifierGroup.Component {
    val protoValue =
      MeasureReport.Group.Stratifier.StratifierGroup.Component.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
        protoValue.code = code.toProto()
    }
    if (hasValue()) {
        protoValue.value = value.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.StratifierGroupPopulationComponent.toProto():
    MeasureReport.Group.Stratifier.StratifierGroup.StratifierGroupPopulation {
    val protoValue =
      MeasureReport.Group.Stratifier.StratifierGroup.StratifierGroupPopulation.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
        protoValue.code = code.toProto()
    }
    if (hasCount()) {
        protoValue.count = countElement.toProto()
    }
    if (hasSubjectResults()) {
        protoValue.subjectResults = subjectResults.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MeasureReport.Group.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
        hapiValue.code = code.toHapi()
    }
    if (populationCount > 0) {
        hapiValue.population = populationList.map { it.toHapi() }
    }
    if (hasMeasureScore()) {
        hapiValue.measureScore = measureScore.toHapi()
    }
    if (stratifierCount > 0) {
        hapiValue.stratifier = stratifierList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun MeasureReport.Group.Population.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupPopulationComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupPopulationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
        hapiValue.code = code.toHapi()
    }
    if (hasCount()) {
        hapiValue.countElement = count.toHapi()
    }
    if (hasSubjectResults()) {
        hapiValue.subjectResults = subjectResults.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun MeasureReport.Group.Stratifier.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupStratifierComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupStratifierComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (codeCount > 0) {
        hapiValue.code = codeList.map { it.toHapi() }
    }
    if (stratumCount > 0) {
        hapiValue.stratum = stratumList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun MeasureReport.Group.Stratifier.StratifierGroup.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasValue()) {
        hapiValue.value = value.toHapi()
    }
    if (componentCount > 0) {
        hapiValue.component = componentList.map { it.toHapi() }
    }
    if (populationCount > 0) {
        hapiValue.population = populationList.map { it.toHapi() }
    }
    if (hasMeasureScore()) {
        hapiValue.measureScore = measureScore.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun MeasureReport.Group.Stratifier.StratifierGroup.Component.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponentComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
        hapiValue.code = code.toHapi()
    }
    if (hasValue()) {
        hapiValue.value = value.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun MeasureReport.Group.Stratifier.StratifierGroup.StratifierGroupPopulation.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.StratifierGroupPopulationComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.StratifierGroupPopulationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
        hapiValue.code = code.toHapi()
    }
    if (hasCount()) {
        hapiValue.countElement = count.toHapi()
    }
    if (hasSubjectResults()) {
        hapiValue.subjectResults = subjectResults.toHapi()
    }
    return hapiValue
  }
}
