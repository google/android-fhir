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

import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.TestReport
import com.google.fhir.r4.core.TestReport.Participant
import com.google.fhir.r4.core.TestReport.Setup
import com.google.fhir.r4.core.TestReport.Setup.SetupAction
import com.google.fhir.r4.core.TestReport.Setup.SetupAction.Assert
import com.google.fhir.r4.core.TestReport.Setup.SetupAction.Operation
import com.google.fhir.r4.core.TestReport.Teardown
import com.google.fhir.r4.core.TestReport.Test
import com.google.fhir.r4.core.TestReportActionResultCode
import com.google.fhir.r4.core.TestReportParticipantTypeCode
import com.google.fhir.r4.core.TestReportResultCode
import com.google.fhir.r4.core.TestReportStatusCode

public object TestReportConverter {
  public fun TestReport.toHapi(): org.hl7.fhir.r4.model.TestReport {
    val hapiValue = org.hl7.fhir.r4.model.TestReport()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.TestReport.TestReportStatus.valueOf(status.value.name.replace("_", ""))
    )
    hapiValue.setTestScript(testScript.toHapi())
    hapiValue.setResult(
      org.hl7.fhir.r4.model.TestReport.TestReportResult.valueOf(result.value.name.replace("_", ""))
    )
    hapiValue.setScoreElement(score.toHapi())
    hapiValue.setTesterElement(tester.toHapi())
    hapiValue.setIssuedElement(issued.toHapi())
    hapiValue.setParticipant(participantList.map { it.toHapi() })
    hapiValue.setSetup(setup.toHapi())
    hapiValue.setTest(testList.map { it.toHapi() })
    hapiValue.setTeardown(teardown.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.TestReport.toProto(): TestReport {
    val protoValue =
      TestReport.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setIdentifier(identifier.toProto())
        .setName(nameElement.toProto())
        .setStatus(
          TestReport.StatusCode.newBuilder()
            .setValue(
              TestReportStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setTestScript(testScript.toProto())
        .setResult(
          TestReport.ResultCode.newBuilder()
            .setValue(
              TestReportResultCode.Value.valueOf(result.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setScore(scoreElement.toProto())
        .setTester(testerElement.toProto())
        .setIssued(issuedElement.toProto())
        .addAllParticipant(participant.map { it.toProto() })
        .setSetup(setup.toProto())
        .addAllTest(test.map { it.toProto() })
        .setTeardown(teardown.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.TestReport.TestReportParticipantComponent.toProto():
    TestReport.Participant {
    val protoValue =
      TestReport.Participant.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(
          TestReport.Participant.TypeCode.newBuilder()
            .setValue(
              TestReportParticipantTypeCode.Value.valueOf(
                type.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setUri(uriElement.toProto())
        .setDisplay(displayElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.TestReport.TestReportSetupComponent.toProto():
    TestReport.Setup {
    val protoValue =
      TestReport.Setup.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllAction(action.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.TestReport.SetupActionComponent.toProto():
    TestReport.Setup.SetupAction {
    val protoValue =
      TestReport.Setup.SetupAction.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setOperation(operation.toProto())
        .setAssertValue(assert.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.TestReport.SetupActionOperationComponent.toProto():
    TestReport.Setup.SetupAction.Operation {
    val protoValue =
      TestReport.Setup.SetupAction.Operation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setResult(
          TestReport.Setup.SetupAction.Operation.ResultCode.newBuilder()
            .setValue(
              TestReportActionResultCode.Value.valueOf(
                result.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setMessage(messageElement.toProto())
        .setDetail(detailElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.TestReport.SetupActionAssertComponent.toProto():
    TestReport.Setup.SetupAction.Assert {
    val protoValue =
      TestReport.Setup.SetupAction.Assert.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setResult(
          TestReport.Setup.SetupAction.Assert.ResultCode.newBuilder()
            .setValue(
              TestReportActionResultCode.Value.valueOf(
                result.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setMessage(messageElement.toProto())
        .setDetail(detailElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.TestReport.TestReportTestComponent.toProto(): TestReport.Test {
    val protoValue =
      TestReport.Test.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setDescription(descriptionElement.toProto())
        .addAllAction(action.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.TestReport.TestActionComponent.toProto():
    TestReport.Test.TestAction {
    val protoValue =
      TestReport.Test.TestAction.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.TestReport.TestReportTeardownComponent.toProto():
    TestReport.Teardown {
    val protoValue =
      TestReport.Teardown.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllAction(action.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.TestReport.TeardownActionComponent.toProto():
    TestReport.Teardown.TeardownAction {
    val protoValue =
      TestReport.Teardown.TeardownAction.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun TestReport.Participant.toHapi():
    org.hl7.fhir.r4.model.TestReport.TestReportParticipantComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TestReportParticipantComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(
      org.hl7.fhir.r4.model.TestReport.TestReportParticipantType.valueOf(
        type.value.name.replace("_", "")
      )
    )
    hapiValue.setUriElement(uri.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    return hapiValue
  }

  private fun TestReport.Setup.toHapi(): org.hl7.fhir.r4.model.TestReport.TestReportSetupComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TestReportSetupComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setAction(actionList.map { it.toHapi() })
    return hapiValue
  }

  private fun TestReport.Setup.SetupAction.toHapi():
    org.hl7.fhir.r4.model.TestReport.SetupActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.SetupActionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setOperation(operation.toHapi())
    hapiValue.setAssert(assertValue.toHapi())
    return hapiValue
  }

  private fun TestReport.Setup.SetupAction.Operation.toHapi():
    org.hl7.fhir.r4.model.TestReport.SetupActionOperationComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.SetupActionOperationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setResult(
      org.hl7.fhir.r4.model.TestReport.TestReportActionResult.valueOf(
        result.value.name.replace("_", "")
      )
    )
    hapiValue.setMessageElement(message.toHapi())
    hapiValue.setDetailElement(detail.toHapi())
    return hapiValue
  }

  private fun TestReport.Setup.SetupAction.Assert.toHapi():
    org.hl7.fhir.r4.model.TestReport.SetupActionAssertComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.SetupActionAssertComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setResult(
      org.hl7.fhir.r4.model.TestReport.TestReportActionResult.valueOf(
        result.value.name.replace("_", "")
      )
    )
    hapiValue.setMessageElement(message.toHapi())
    hapiValue.setDetailElement(detail.toHapi())
    return hapiValue
  }

  private fun TestReport.Test.toHapi(): org.hl7.fhir.r4.model.TestReport.TestReportTestComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TestReportTestComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setAction(actionList.map { it.toHapi() })
    return hapiValue
  }

  private fun TestReport.Test.TestAction.toHapi():
    org.hl7.fhir.r4.model.TestReport.TestActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TestActionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    return hapiValue
  }

  private fun TestReport.Teardown.toHapi():
    org.hl7.fhir.r4.model.TestReport.TestReportTeardownComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TestReportTeardownComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setAction(actionList.map { it.toHapi() })
    return hapiValue
  }

  private fun TestReport.Teardown.TeardownAction.toHapi():
    org.hl7.fhir.r4.model.TestReport.TeardownActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TeardownActionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    return hapiValue
  }
}
