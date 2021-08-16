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
import kotlin.jvm.JvmStatic

object TestReportConverter {
  @JvmStatic
  fun TestReport.toHapi(): org.hl7.fhir.r4.model.TestReport {
    val hapiValue = org.hl7.fhir.r4.model.TestReport()
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
    if (hasIdentifier()) {
        hapiValue.identifier = identifier.toHapi()
    }
    if (hasName()) {
        hapiValue.nameElement = name.toHapi()
    }
      hapiValue.status = org.hl7.fhir.r4.model.TestReport.TestReportStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasTestScript()) {
        hapiValue.testScript = testScript.toHapi()
    }
      hapiValue.result = org.hl7.fhir.r4.model.TestReport.TestReportResult.valueOf(
          result.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasScore()) {
        hapiValue.scoreElement = score.toHapi()
    }
    if (hasTester()) {
        hapiValue.testerElement = tester.toHapi()
    }
    if (hasIssued()) {
        hapiValue.issuedElement = issued.toHapi()
    }
    if (participantCount > 0) {
        hapiValue.participant = participantList.map { it.toHapi() }
    }
    if (hasSetup()) {
        hapiValue.setup = setup.toHapi()
    }
    if (testCount > 0) {
        hapiValue.test = testList.map { it.toHapi() }
    }
    if (hasTeardown()) {
        hapiValue.teardown = teardown.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.TestReport.toProto(): TestReport {
    val protoValue = TestReport.newBuilder().setId(Id.newBuilder().setValue(id))
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
        protoValue.identifier = identifier.toProto()
    }
    if (hasName()) {
        protoValue.name = nameElement.toProto()
    }
      protoValue.status = TestReport.StatusCode.newBuilder()
          .setValue(
              TestReportStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasTestScript()) {
        protoValue.testScript = testScript.toProto()
    }
      protoValue.result = TestReport.ResultCode.newBuilder()
          .setValue(
              TestReportResultCode.Value.valueOf(
                  result.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasScore()) {
        protoValue.score = scoreElement.toProto()
    }
    if (hasTester()) {
        protoValue.tester = testerElement.toProto()
    }
    if (hasIssued()) {
        protoValue.issued = issuedElement.toProto()
    }
    if (hasParticipant()) {
      protoValue.addAllParticipant(participant.map { it.toProto() })
    }
    if (hasSetup()) {
        protoValue.setup = setup.toProto()
    }
    if (hasTest()) {
      protoValue.addAllTest(test.map { it.toProto() })
    }
    if (hasTeardown()) {
        protoValue.teardown = teardown.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestReport.TestReportParticipantComponent.toProto():
    TestReport.Participant {
    val protoValue = TestReport.Participant.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.type = TestReport.Participant.TypeCode.newBuilder()
          .setValue(
              TestReportParticipantTypeCode.Value.valueOf(
                  type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasUri()) {
        protoValue.uri = uriElement.toProto()
    }
    if (hasDisplay()) {
        protoValue.display = displayElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestReport.TestReportSetupComponent.toProto():
    TestReport.Setup {
    val protoValue = TestReport.Setup.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAction()) {
      protoValue.addAllAction(action.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestReport.SetupActionComponent.toProto():
    TestReport.Setup.SetupAction {
    val protoValue =
      TestReport.Setup.SetupAction.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasOperation()) {
        protoValue.operation = operation.toProto()
    }
    if (hasAssert()) {
        protoValue.assertValue = assert.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestReport.SetupActionOperationComponent.toProto():
    TestReport.Setup.SetupAction.Operation {
    val protoValue =
      TestReport.Setup.SetupAction.Operation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.result = TestReport.Setup.SetupAction.Operation.ResultCode.newBuilder()
          .setValue(
              TestReportActionResultCode.Value.valueOf(
                  result.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasMessage()) {
        protoValue.message = messageElement.toProto()
    }
    if (hasDetail()) {
        protoValue.detail = detailElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestReport.SetupActionAssertComponent.toProto():
    TestReport.Setup.SetupAction.Assert {
    val protoValue =
      TestReport.Setup.SetupAction.Assert.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.result = TestReport.Setup.SetupAction.Assert.ResultCode.newBuilder()
          .setValue(
              TestReportActionResultCode.Value.valueOf(
                  result.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasMessage()) {
        protoValue.message = messageElement.toProto()
    }
    if (hasDetail()) {
        protoValue.detail = detailElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestReport.TestReportTestComponent.toProto(): TestReport.Test {
    val protoValue = TestReport.Test.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
        protoValue.name = nameElement.toProto()
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasAction()) {
      protoValue.addAllAction(action.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestReport.TestActionComponent.toProto():
    TestReport.Test.TestAction {
    val protoValue = TestReport.Test.TestAction.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestReport.TestReportTeardownComponent.toProto():
    TestReport.Teardown {
    val protoValue = TestReport.Teardown.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAction()) {
      protoValue.addAllAction(action.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestReport.TeardownActionComponent.toProto():
    TestReport.Teardown.TeardownAction {
    val protoValue =
      TestReport.Teardown.TeardownAction.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun TestReport.Participant.toHapi():
    org.hl7.fhir.r4.model.TestReport.TestReportParticipantComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TestReportParticipantComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.type = org.hl7.fhir.r4.model.TestReport.TestReportParticipantType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasUri()) {
        hapiValue.uriElement = uri.toHapi()
    }
    if (hasDisplay()) {
        hapiValue.displayElement = display.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun TestReport.Setup.toHapi(): org.hl7.fhir.r4.model.TestReport.TestReportSetupComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TestReportSetupComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (actionCount > 0) {
        hapiValue.action = actionList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun TestReport.Setup.SetupAction.toHapi():
    org.hl7.fhir.r4.model.TestReport.SetupActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.SetupActionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasOperation()) {
        hapiValue.operation = operation.toHapi()
    }
    if (hasAssertValue()) {
        hapiValue.assert = assertValue.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun TestReport.Setup.SetupAction.Operation.toHapi():
    org.hl7.fhir.r4.model.TestReport.SetupActionOperationComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.SetupActionOperationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.result = org.hl7.fhir.r4.model.TestReport.TestReportActionResult.valueOf(
          result.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasMessage()) {
        hapiValue.messageElement = message.toHapi()
    }
    if (hasDetail()) {
        hapiValue.detailElement = detail.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun TestReport.Setup.SetupAction.Assert.toHapi():
    org.hl7.fhir.r4.model.TestReport.SetupActionAssertComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.SetupActionAssertComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.result = org.hl7.fhir.r4.model.TestReport.TestReportActionResult.valueOf(
          result.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasMessage()) {
        hapiValue.messageElement = message.toHapi()
    }
    if (hasDetail()) {
        hapiValue.detailElement = detail.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun TestReport.Test.toHapi(): org.hl7.fhir.r4.model.TestReport.TestReportTestComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TestReportTestComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
        hapiValue.nameElement = name.toHapi()
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (actionCount > 0) {
        hapiValue.action = actionList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun TestReport.Test.TestAction.toHapi():
    org.hl7.fhir.r4.model.TestReport.TestActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TestActionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun TestReport.Teardown.toHapi():
    org.hl7.fhir.r4.model.TestReport.TestReportTeardownComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TestReportTeardownComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (actionCount > 0) {
        hapiValue.action = actionList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun TestReport.Teardown.TeardownAction.toHapi():
    org.hl7.fhir.r4.model.TestReport.TeardownActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestReport.TeardownActionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    return hapiValue
  }
}
