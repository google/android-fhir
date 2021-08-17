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
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.AssertionDirectionTypeCode
import com.google.fhir.r4.core.AssertionOperatorTypeCode
import com.google.fhir.r4.core.AssertionResponseTypesCode
import com.google.fhir.r4.core.FHIRDefinedTypeValueSet
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.TestScript
import com.google.fhir.r4.core.TestScript.Metadata
import com.google.fhir.r4.core.TestScript.Setup
import com.google.fhir.r4.core.TestScript.Setup.SetupAction
import com.google.fhir.r4.core.TestScript.Setup.SetupAction.Assert
import com.google.fhir.r4.core.TestScript.Setup.SetupAction.Operation
import com.google.fhir.r4.core.TestScript.Teardown
import com.google.fhir.r4.core.TestScript.Test
import com.google.fhir.r4.core.TestScriptRequestMethodCode
import org.hl7.fhir.r4.model.Enumerations

object TestScriptConverter {
  fun TestScript.toHapi(): org.hl7.fhir.r4.model.TestScript {
    val hapiValue = org.hl7.fhir.r4.model.TestScript()
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
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    hapiValue.status =
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    if (hasExperimental()) {
      hapiValue.experimentalElement = experimental.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasPublisher()) {
      hapiValue.publisherElement = publisher.toHapi()
    }
    if (contactCount > 0) {
      hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (useContextCount > 0) {
      hapiValue.useContext = useContextList.map { it.toHapi() }
    }
    if (jurisdictionCount > 0) {
      hapiValue.jurisdiction = jurisdictionList.map { it.toHapi() }
    }
    if (hasPurpose()) {
      hapiValue.purposeElement = purpose.toHapi()
    }
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    if (originCount > 0) {
      hapiValue.origin = originList.map { it.toHapi() }
    }
    if (destinationCount > 0) {
      hapiValue.destination = destinationList.map { it.toHapi() }
    }
    if (hasMetadata()) {
      hapiValue.metadata = metadata.toHapi()
    }
    if (fixtureCount > 0) {
      hapiValue.fixture = fixtureList.map { it.toHapi() }
    }
    if (profileCount > 0) {
      hapiValue.profile = profileList.map { it.toHapi() }
    }
    if (variableCount > 0) {
      hapiValue.variable = variableList.map { it.toHapi() }
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

  fun org.hl7.fhir.r4.model.TestScript.toProto(): TestScript {
    val protoValue = TestScript.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    protoValue.status =
      TestScript.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasExperimental()) {
      protoValue.experimental = experimentalElement.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasPublisher()) {
      protoValue.publisher = publisherElement.toProto()
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasUseContext()) {
      protoValue.addAllUseContext(useContext.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasPurpose()) {
      protoValue.purpose = purposeElement.toProto()
    }
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    if (hasOrigin()) {
      protoValue.addAllOrigin(origin.map { it.toProto() })
    }
    if (hasDestination()) {
      protoValue.addAllDestination(destination.map { it.toProto() })
    }
    if (hasMetadata()) {
      protoValue.metadata = metadata.toProto()
    }
    if (hasFixture()) {
      protoValue.addAllFixture(fixture.map { it.toProto() })
    }
    if (hasProfile()) {
      protoValue.addAllProfile(profile.map { it.toProto() })
    }
    if (hasVariable()) {
      protoValue.addAllVariable(variable.map { it.toProto() })
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

  private fun org.hl7.fhir.r4.model.TestScript.TestScriptOriginComponent.toProto():
    TestScript.Origin {
    val protoValue = TestScript.Origin.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIndex()) {
      protoValue.index = indexElement.toProto()
    }
    if (hasProfile()) {
      protoValue.profile = profile.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.TestScriptDestinationComponent.toProto():
    TestScript.Destination {
    val protoValue = TestScript.Destination.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIndex()) {
      protoValue.index = indexElement.toProto()
    }
    if (hasProfile()) {
      protoValue.profile = profile.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.TestScriptMetadataComponent.toProto():
    TestScript.Metadata {
    val protoValue = TestScript.Metadata.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasLink()) {
      protoValue.addAllLink(link.map { it.toProto() })
    }
    if (hasCapability()) {
      protoValue.addAllCapability(capability.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.TestScriptMetadataLinkComponent.toProto():
    TestScript.Metadata.Link {
    val protoValue = TestScript.Metadata.Link.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.TestScriptMetadataCapabilityComponent.toProto():
    TestScript.Metadata.Capability {
    val protoValue =
      TestScript.Metadata.Capability.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRequired()) {
      protoValue.required = requiredElement.toProto()
    }
    if (hasValidated()) {
      protoValue.validated = validatedElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasOrigin()) {
      protoValue.addAllOrigin(origin.map { it.toProto() })
    }
    if (hasDestination()) {
      protoValue.destination = destinationElement.toProto()
    }
    if (hasLink()) {
      protoValue.addAllLink(link.map { it.toProto() })
    }
    if (hasCapabilities()) {
      protoValue.capabilities = capabilitiesElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.TestScriptFixtureComponent.toProto():
    TestScript.Fixture {
    val protoValue = TestScript.Fixture.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAutocreate()) {
      protoValue.autocreate = autocreateElement.toProto()
    }
    if (hasAutodelete()) {
      protoValue.autodelete = autodeleteElement.toProto()
    }
    if (hasResource()) {
      protoValue.resource = resource.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.TestScriptVariableComponent.toProto():
    TestScript.Variable {
    val protoValue = TestScript.Variable.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasDefaultValue()) {
      protoValue.defaultValue = defaultValueElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasExpression()) {
      protoValue.expression = expressionElement.toProto()
    }
    if (hasHeaderField()) {
      protoValue.headerField = headerFieldElement.toProto()
    }
    if (hasHint()) {
      protoValue.hint = hintElement.toProto()
    }
    if (hasPath()) {
      protoValue.path = pathElement.toProto()
    }
    if (hasSourceId()) {
      protoValue.sourceId = sourceIdElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.TestScriptSetupComponent.toProto():
    TestScript.Setup {
    val protoValue = TestScript.Setup.newBuilder().setId(String.newBuilder().setValue(id))
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

  private fun org.hl7.fhir.r4.model.TestScript.SetupActionComponent.toProto():
    TestScript.Setup.SetupAction {
    val protoValue =
      TestScript.Setup.SetupAction.newBuilder().setId(String.newBuilder().setValue(id))
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

  private fun org.hl7.fhir.r4.model.TestScript.SetupActionOperationComponent.toProto():
    TestScript.Setup.SetupAction.Operation {
    val protoValue =
      TestScript.Setup.SetupAction.Operation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    protoValue.resource =
      TestScript.Setup.SetupAction.Operation.ResourceCode.newBuilder()
        .setValue(FHIRDefinedTypeValueSet.Value.valueOf(resource))
        .build()
    if (hasLabel()) {
      protoValue.label = labelElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    protoValue.accept =
      TestScript.Setup.SetupAction.Operation.AcceptCode.newBuilder()
        .setValue(accept.protoCodeCheck())
        .build()
    protoValue.contentType =
      TestScript.Setup.SetupAction.Operation.ContentTypeCode.newBuilder()
        .setValue(contentType.protoCodeCheck())
        .build()
    if (hasDestination()) {
      protoValue.destination = destinationElement.toProto()
    }
    if (hasEncodeRequestUrl()) {
      protoValue.encodeRequestUrl = encodeRequestUrlElement.toProto()
    }
    protoValue.method =
      TestScript.Setup.SetupAction.Operation.MethodCode.newBuilder()
        .setValue(
          TestScriptRequestMethodCode.Value.valueOf(
            method.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasOrigin()) {
      protoValue.origin = originElement.toProto()
    }
    if (hasParams()) {
      protoValue.params = paramsElement.toProto()
    }
    if (hasRequestHeader()) {
      protoValue.addAllRequestHeader(requestHeader.map { it.toProto() })
    }
    if (hasRequestId()) {
      protoValue.requestId = requestIdElement.toProto()
    }
    if (hasResponseId()) {
      protoValue.responseId = responseIdElement.toProto()
    }
    if (hasSourceId()) {
      protoValue.sourceId = sourceIdElement.toProto()
    }
    if (hasTargetId()) {
      protoValue.targetId = targetIdElement.toProto()
    }
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.SetupActionOperationRequestHeaderComponent.toProto():
    TestScript.Setup.SetupAction.Operation.RequestHeader {
    val protoValue =
      TestScript.Setup.SetupAction.Operation.RequestHeader.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasField()) {
      protoValue.field = fieldElement.toProto()
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.SetupActionAssertComponent.toProto():
    TestScript.Setup.SetupAction.Assert {
    val protoValue =
      TestScript.Setup.SetupAction.Assert.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasLabel()) {
      protoValue.label = labelElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    protoValue.direction =
      TestScript.Setup.SetupAction.Assert.DirectionCode.newBuilder()
        .setValue(
          AssertionDirectionTypeCode.Value.valueOf(
            direction.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCompareToSourceId()) {
      protoValue.compareToSourceId = compareToSourceIdElement.toProto()
    }
    if (hasCompareToSourceExpression()) {
      protoValue.compareToSourceExpression = compareToSourceExpressionElement.toProto()
    }
    if (hasCompareToSourcePath()) {
      protoValue.compareToSourcePath = compareToSourcePathElement.toProto()
    }
    protoValue.contentType =
      TestScript.Setup.SetupAction.Assert.ContentTypeCode.newBuilder()
        .setValue(contentType.protoCodeCheck())
        .build()
    if (hasExpression()) {
      protoValue.expression = expressionElement.toProto()
    }
    if (hasHeaderField()) {
      protoValue.headerField = headerFieldElement.toProto()
    }
    if (hasMinimumId()) {
      protoValue.minimumId = minimumIdElement.toProto()
    }
    if (hasNavigationLinks()) {
      protoValue.navigationLinks = navigationLinksElement.toProto()
    }
    protoValue.operator =
      TestScript.Setup.SetupAction.Assert.OperatorCode.newBuilder()
        .setValue(
          AssertionOperatorTypeCode.Value.valueOf(
            operator.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasPath()) {
      protoValue.path = pathElement.toProto()
    }
    protoValue.requestMethod =
      TestScript.Setup.SetupAction.Assert.RequestMethodCode.newBuilder()
        .setValue(
          TestScriptRequestMethodCode.Value.valueOf(
            requestMethod.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasRequestURL()) {
      protoValue.requestUrl = requestURLElement.toProto()
    }
    protoValue.resource =
      TestScript.Setup.SetupAction.Assert.ResourceCode.newBuilder()
        .setValue(FHIRDefinedTypeValueSet.Value.valueOf(resource))
        .build()
    protoValue.response =
      TestScript.Setup.SetupAction.Assert.ResponseCode.newBuilder()
        .setValue(
          AssertionResponseTypesCode.Value.valueOf(
            response.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasResponseCode()) {
      protoValue.responseCode = responseCodeElement.toProto()
    }
    if (hasSourceId()) {
      protoValue.sourceId = sourceIdElement.toProto()
    }
    if (hasValidateProfileId()) {
      protoValue.validateProfileId = validateProfileIdElement.toProto()
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    if (hasWarningOnly()) {
      protoValue.warningOnly = warningOnlyElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.TestScriptTestComponent.toProto(): TestScript.Test {
    val protoValue = TestScript.Test.newBuilder().setId(String.newBuilder().setValue(id))
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

  private fun org.hl7.fhir.r4.model.TestScript.TestActionComponent.toProto():
    TestScript.Test.TestAction {
    val protoValue = TestScript.Test.TestAction.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.TestScript.TestScriptTeardownComponent.toProto():
    TestScript.Teardown {
    val protoValue = TestScript.Teardown.newBuilder().setId(String.newBuilder().setValue(id))
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

  private fun org.hl7.fhir.r4.model.TestScript.TeardownActionComponent.toProto():
    TestScript.Teardown.TeardownAction {
    val protoValue =
      TestScript.Teardown.TeardownAction.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun TestScript.Origin.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptOriginComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptOriginComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasIndex()) {
      hapiValue.indexElement = index.toHapi()
    }
    if (hasProfile()) {
      hapiValue.profile = profile.toHapi()
    }
    return hapiValue
  }

  private fun TestScript.Destination.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptDestinationComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptDestinationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasIndex()) {
      hapiValue.indexElement = index.toHapi()
    }
    if (hasProfile()) {
      hapiValue.profile = profile.toHapi()
    }
    return hapiValue
  }

  private fun TestScript.Metadata.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptMetadataComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptMetadataComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (linkCount > 0) {
      hapiValue.link = linkList.map { it.toHapi() }
    }
    if (capabilityCount > 0) {
      hapiValue.capability = capabilityList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun TestScript.Metadata.Link.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptMetadataLinkComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptMetadataLinkComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    return hapiValue
  }

  private fun TestScript.Metadata.Capability.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptMetadataCapabilityComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptMetadataCapabilityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasRequired()) {
      hapiValue.requiredElement = required.toHapi()
    }
    if (hasValidated()) {
      hapiValue.validatedElement = validated.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (originCount > 0) {
      hapiValue.origin = originList.map { it.toHapi() }
    }
    if (hasDestination()) {
      hapiValue.destinationElement = destination.toHapi()
    }
    if (linkCount > 0) {
      hapiValue.link = linkList.map { it.toHapi() }
    }
    if (hasCapabilities()) {
      hapiValue.capabilitiesElement = capabilities.toHapi()
    }
    return hapiValue
  }

  private fun TestScript.Fixture.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptFixtureComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptFixtureComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasAutocreate()) {
      hapiValue.autocreateElement = autocreate.toHapi()
    }
    if (hasAutodelete()) {
      hapiValue.autodeleteElement = autodelete.toHapi()
    }
    if (hasResource()) {
      hapiValue.resource = resource.toHapi()
    }
    return hapiValue
  }

  private fun TestScript.Variable.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptVariableComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptVariableComponent()
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
    if (hasDefaultValue()) {
      hapiValue.defaultValueElement = defaultValue.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasExpression()) {
      hapiValue.expressionElement = expression.toHapi()
    }
    if (hasHeaderField()) {
      hapiValue.headerFieldElement = headerField.toHapi()
    }
    if (hasHint()) {
      hapiValue.hintElement = hint.toHapi()
    }
    if (hasPath()) {
      hapiValue.pathElement = path.toHapi()
    }
    if (hasSourceId()) {
      hapiValue.sourceIdElement = sourceId.toHapi()
    }
    return hapiValue
  }

  private fun TestScript.Setup.toHapi(): org.hl7.fhir.r4.model.TestScript.TestScriptSetupComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptSetupComponent()
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

  private fun TestScript.Setup.SetupAction.toHapi():
    org.hl7.fhir.r4.model.TestScript.SetupActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.SetupActionComponent()
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

  private fun TestScript.Setup.SetupAction.Operation.toHapi():
    org.hl7.fhir.r4.model.TestScript.SetupActionOperationComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.SetupActionOperationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    hapiValue.resource = resource.value.name
    if (hasLabel()) {
      hapiValue.labelElement = label.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    hapiValue.accept = accept.value.hapiCodeCheck()
    hapiValue.contentType = contentType.value.hapiCodeCheck()
    if (hasDestination()) {
      hapiValue.destinationElement = destination.toHapi()
    }
    if (hasEncodeRequestUrl()) {
      hapiValue.encodeRequestUrlElement = encodeRequestUrl.toHapi()
    }
    hapiValue.method =
      org.hl7.fhir.r4.model.TestScript.TestScriptRequestMethodCode.valueOf(
        method.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasOrigin()) {
      hapiValue.originElement = origin.toHapi()
    }
    if (hasParams()) {
      hapiValue.paramsElement = params.toHapi()
    }
    if (requestHeaderCount > 0) {
      hapiValue.requestHeader = requestHeaderList.map { it.toHapi() }
    }
    if (hasRequestId()) {
      hapiValue.requestIdElement = requestId.toHapi()
    }
    if (hasResponseId()) {
      hapiValue.responseIdElement = responseId.toHapi()
    }
    if (hasSourceId()) {
      hapiValue.sourceIdElement = sourceId.toHapi()
    }
    if (hasTargetId()) {
      hapiValue.targetIdElement = targetId.toHapi()
    }
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    return hapiValue
  }

  private fun TestScript.Setup.SetupAction.Operation.RequestHeader.toHapi():
    org.hl7.fhir.r4.model.TestScript.SetupActionOperationRequestHeaderComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.SetupActionOperationRequestHeaderComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasField()) {
      hapiValue.fieldElement = field.toHapi()
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    return hapiValue
  }

  private fun TestScript.Setup.SetupAction.Assert.toHapi():
    org.hl7.fhir.r4.model.TestScript.SetupActionAssertComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.SetupActionAssertComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasLabel()) {
      hapiValue.labelElement = label.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    hapiValue.direction =
      org.hl7.fhir.r4.model.TestScript.AssertionDirectionType.valueOf(
        direction.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasCompareToSourceId()) {
      hapiValue.compareToSourceIdElement = compareToSourceId.toHapi()
    }
    if (hasCompareToSourceExpression()) {
      hapiValue.compareToSourceExpressionElement = compareToSourceExpression.toHapi()
    }
    if (hasCompareToSourcePath()) {
      hapiValue.compareToSourcePathElement = compareToSourcePath.toHapi()
    }
    hapiValue.contentType = contentType.value.hapiCodeCheck()
    if (hasExpression()) {
      hapiValue.expressionElement = expression.toHapi()
    }
    if (hasHeaderField()) {
      hapiValue.headerFieldElement = headerField.toHapi()
    }
    if (hasMinimumId()) {
      hapiValue.minimumIdElement = minimumId.toHapi()
    }
    if (hasNavigationLinks()) {
      hapiValue.navigationLinksElement = navigationLinks.toHapi()
    }
    hapiValue.operator =
      org.hl7.fhir.r4.model.TestScript.AssertionOperatorType.valueOf(
        operator.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPath()) {
      hapiValue.pathElement = path.toHapi()
    }
    hapiValue.requestMethod =
      org.hl7.fhir.r4.model.TestScript.TestScriptRequestMethodCode.valueOf(
        requestMethod.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasRequestUrl()) {
      hapiValue.requestURLElement = requestUrl.toHapi()
    }
    hapiValue.resource = resource.value.name
    hapiValue.response =
      org.hl7.fhir.r4.model.TestScript.AssertionResponseTypes.valueOf(
        response.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasResponseCode()) {
      hapiValue.responseCodeElement = responseCode.toHapi()
    }
    if (hasSourceId()) {
      hapiValue.sourceIdElement = sourceId.toHapi()
    }
    if (hasValidateProfileId()) {
      hapiValue.validateProfileIdElement = validateProfileId.toHapi()
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    if (hasWarningOnly()) {
      hapiValue.warningOnlyElement = warningOnly.toHapi()
    }
    return hapiValue
  }

  private fun TestScript.Test.toHapi(): org.hl7.fhir.r4.model.TestScript.TestScriptTestComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptTestComponent()
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

  private fun TestScript.Test.TestAction.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestActionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun TestScript.Teardown.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptTeardownComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptTeardownComponent()
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

  private fun TestScript.Teardown.TeardownAction.toHapi():
    org.hl7.fhir.r4.model.TestScript.TeardownActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TeardownActionComponent()
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
