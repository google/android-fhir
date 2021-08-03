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
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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

public object MeasureReportConverter {
  @JvmStatic
  public fun MeasureReport.toHapi(): org.hl7.fhir.r4.model.MeasureReport {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.MeasureReport.MeasureReportStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setType(
      org.hl7.fhir.r4.model.MeasureReport.MeasureReportType.valueOf(
        type.value.name.replace("_", "")
      )
    )
    hapiValue.setMeasureElement(measure.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setReporter(reporter.toHapi())
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setImprovementNotation(improvementNotation.toHapi())
    hapiValue.setGroup(groupList.map { it.toHapi() })
    hapiValue.setEvaluatedResource(evaluatedResourceList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MeasureReport.toProto(): MeasureReport {
    val protoValue =
      MeasureReport.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setLanguage(languageElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          MeasureReport.StatusCode.newBuilder()
            .setValue(
              MeasureReportStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setType(
          MeasureReport.TypeCode.newBuilder()
            .setValue(
              MeasureReportTypeCode.Value.valueOf(type.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setMeasure(measureElement.toProto())
        .setSubject(subject.toProto())
        .setDate(dateElement.toProto())
        .setReporter(reporter.toProto())
        .setPeriod(period.toProto())
        .setImprovementNotation(improvementNotation.toProto())
        .addAllGroup(group.map { it.toProto() })
        .addAllEvaluatedResource(evaluatedResource.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupComponent.toProto():
    MeasureReport.Group {
    val protoValue =
      MeasureReport.Group.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .addAllPopulation(population.map { it.toProto() })
        .setMeasureScore(measureScore.toProto())
        .addAllStratifier(stratifier.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupPopulationComponent.toProto():
    MeasureReport.Group.Population {
    val protoValue =
      MeasureReport.Group.Population.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setCount(countElement.toProto())
        .setSubjectResults(subjectResults.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupStratifierComponent.toProto():
    MeasureReport.Group.Stratifier {
    val protoValue =
      MeasureReport.Group.Stratifier.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllCode(code.map { it.toProto() })
        .addAllStratum(stratum.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponent.toProto():
    MeasureReport.Group.Stratifier.StratifierGroup {
    val protoValue =
      MeasureReport.Group.Stratifier.StratifierGroup.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setValue(value.toProto())
        .addAllComponent(component.map { it.toProto() })
        .addAllPopulation(population.map { it.toProto() })
        .setMeasureScore(measureScore.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponentComponent.toProto():
    MeasureReport.Group.Stratifier.StratifierGroup.Component {
    val protoValue =
      MeasureReport.Group.Stratifier.StratifierGroup.Component.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setValue(value.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MeasureReport.StratifierGroupPopulationComponent.toProto():
    MeasureReport.Group.Stratifier.StratifierGroup.StratifierGroupPopulation {
    val protoValue =
      MeasureReport.Group.Stratifier.StratifierGroup.StratifierGroupPopulation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setCount(countElement.toProto())
        .setSubjectResults(subjectResults.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun MeasureReport.Group.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setPopulation(populationList.map { it.toHapi() })
    hapiValue.setMeasureScore(measureScore.toHapi())
    hapiValue.setStratifier(stratifierList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun MeasureReport.Group.Population.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupPopulationComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupPopulationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setCountElement(count.toHapi())
    hapiValue.setSubjectResults(subjectResults.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MeasureReport.Group.Stratifier.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupStratifierComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupStratifierComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(codeList.map { it.toHapi() })
    hapiValue.setStratum(stratumList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun MeasureReport.Group.Stratifier.StratifierGroup.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setValue(value.toHapi())
    hapiValue.setComponent(componentList.map { it.toHapi() })
    hapiValue.setPopulation(populationList.map { it.toHapi() })
    hapiValue.setMeasureScore(measureScore.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MeasureReport.Group.Stratifier.StratifierGroup.Component.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponentComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.StratifierGroupComponentComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setValue(value.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MeasureReport.Group.Stratifier.StratifierGroup.StratifierGroupPopulation.toHapi():
    org.hl7.fhir.r4.model.MeasureReport.StratifierGroupPopulationComponent {
    val hapiValue = org.hl7.fhir.r4.model.MeasureReport.StratifierGroupPopulationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setCountElement(count.toHapi())
    hapiValue.setSubjectResults(subjectResults.toHapi())
    return hapiValue
  }
}
