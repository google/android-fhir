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
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

public object TestScriptConverter {
  @JvmStatic
  public fun TestScript.toHapi(): org.hl7.fhir.r4.model.TestScript {
    val hapiValue = org.hl7.fhir.r4.model.TestScript()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_", "")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map { it.toHapi() })
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setOrigin(originList.map { it.toHapi() })
    hapiValue.setDestination(destinationList.map { it.toHapi() })
    hapiValue.setMetadata(metadata.toHapi())
    hapiValue.setFixture(fixtureList.map { it.toHapi() })
    hapiValue.setProfile(profileList.map { it.toHapi() })
    hapiValue.setVariable(variableList.map { it.toHapi() })
    hapiValue.setSetup(setup.toHapi())
    hapiValue.setTest(testList.map { it.toHapi() })
    hapiValue.setTeardown(teardown.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.TestScript.toProto(): TestScript {
    val protoValue =
      TestScript.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .setIdentifier(identifier.toProto())
        .setVersion(versionElement.toProto())
        .setName(nameElement.toProto())
        .setTitle(titleElement.toProto())
        .setStatus(
          TestScript.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setExperimental(experimentalElement.toProto())
        .setDate(dateElement.toProto())
        .setPublisher(publisherElement.toProto())
        .addAllContact(contact.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllUseContext(useContext.map { it.toProto() })
        .addAllJurisdiction(jurisdiction.map { it.toProto() })
        .setPurpose(purposeElement.toProto())
        .setCopyright(copyrightElement.toProto())
        .addAllOrigin(origin.map { it.toProto() })
        .addAllDestination(destination.map { it.toProto() })
        .setMetadata(metadata.toProto())
        .addAllFixture(fixture.map { it.toProto() })
        .addAllProfile(profile.map { it.toProto() })
        .addAllVariable(variable.map { it.toProto() })
        .setSetup(setup.toProto())
        .addAllTest(test.map { it.toProto() })
        .setTeardown(teardown.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestScriptOriginComponent.toProto():
    TestScript.Origin {
    val protoValue =
      TestScript.Origin.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setIndex(indexElement.toProto())
        .setProfile(profile.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestScriptDestinationComponent.toProto():
    TestScript.Destination {
    val protoValue =
      TestScript.Destination.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setIndex(indexElement.toProto())
        .setProfile(profile.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestScriptMetadataComponent.toProto():
    TestScript.Metadata {
    val protoValue =
      TestScript.Metadata.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllLink(link.map { it.toProto() })
        .addAllCapability(capability.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestScriptMetadataLinkComponent.toProto():
    TestScript.Metadata.Link {
    val protoValue =
      TestScript.Metadata.Link.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .setDescription(descriptionElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestScriptMetadataCapabilityComponent.toProto():
    TestScript.Metadata.Capability {
    val protoValue =
      TestScript.Metadata.Capability.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setRequired(requiredElement.toProto())
        .setValidated(validatedElement.toProto())
        .setDescription(descriptionElement.toProto())
        .addAllOrigin(origin.map { it.toProto() })
        .setDestination(destinationElement.toProto())
        .addAllLink(link.map { it.toProto() })
        .setCapabilities(capabilitiesElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestScriptFixtureComponent.toProto():
    TestScript.Fixture {
    val protoValue =
      TestScript.Fixture.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setAutocreate(autocreateElement.toProto())
        .setAutodelete(autodeleteElement.toProto())
        .setResource(resource.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestScriptVariableComponent.toProto():
    TestScript.Variable {
    val protoValue =
      TestScript.Variable.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setDefaultValue(defaultValueElement.toProto())
        .setDescription(descriptionElement.toProto())
        .setExpression(expressionElement.toProto())
        .setHeaderField(headerFieldElement.toProto())
        .setHint(hintElement.toProto())
        .setPath(pathElement.toProto())
        .setSourceId(sourceIdElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestScriptSetupComponent.toProto():
    TestScript.Setup {
    val protoValue =
      TestScript.Setup.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllAction(action.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.SetupActionComponent.toProto():
    TestScript.Setup.SetupAction {
    val protoValue =
      TestScript.Setup.SetupAction.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setOperation(operation.toProto())
        .setAssertValue(assert.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.SetupActionOperationComponent.toProto():
    TestScript.Setup.SetupAction.Operation {
    val protoValue =
      TestScript.Setup.SetupAction.Operation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .setResource(
          TestScript.Setup.SetupAction.Operation.ResourceCode.newBuilder()
            .setValue(FHIRDefinedTypeValueSet.Value.valueOf(resource))
            .build()
        )
        .setLabel(labelElement.toProto())
        .setDescription(descriptionElement.toProto())
        .setAccept(
          TestScript.Setup.SetupAction.Operation.AcceptCode.newBuilder().setValue(accept).build()
        )
        .setContentType(
          TestScript.Setup.SetupAction.Operation.ContentTypeCode.newBuilder()
            .setValue(contentType)
            .build()
        )
        .setDestination(destinationElement.toProto())
        .setEncodeRequestUrl(encodeRequestUrlElement.toProto())
        .setMethod(
          TestScript.Setup.SetupAction.Operation.MethodCode.newBuilder()
            .setValue(
              TestScriptRequestMethodCode.Value.valueOf(
                method.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setOrigin(originElement.toProto())
        .setParams(paramsElement.toProto())
        .addAllRequestHeader(requestHeader.map { it.toProto() })
        .setRequestId(requestIdElement.toProto())
        .setResponseId(responseIdElement.toProto())
        .setSourceId(sourceIdElement.toProto())
        .setTargetId(targetIdElement.toProto())
        .setUrl(urlElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.SetupActionOperationRequestHeaderComponent.toProto():
    TestScript.Setup.SetupAction.Operation.RequestHeader {
    val protoValue =
      TestScript.Setup.SetupAction.Operation.RequestHeader.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setField(fieldElement.toProto())
        .setValue(valueElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.SetupActionAssertComponent.toProto():
    TestScript.Setup.SetupAction.Assert {
    val protoValue =
      TestScript.Setup.SetupAction.Assert.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setLabel(labelElement.toProto())
        .setDescription(descriptionElement.toProto())
        .setDirection(
          TestScript.Setup.SetupAction.Assert.DirectionCode.newBuilder()
            .setValue(
              AssertionDirectionTypeCode.Value.valueOf(
                direction.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setCompareToSourceId(compareToSourceIdElement.toProto())
        .setCompareToSourceExpression(compareToSourceExpressionElement.toProto())
        .setCompareToSourcePath(compareToSourcePathElement.toProto())
        .setContentType(
          TestScript.Setup.SetupAction.Assert.ContentTypeCode.newBuilder()
            .setValue(contentType)
            .build()
        )
        .setExpression(expressionElement.toProto())
        .setHeaderField(headerFieldElement.toProto())
        .setMinimumId(minimumIdElement.toProto())
        .setNavigationLinks(navigationLinksElement.toProto())
        .setOperator(
          TestScript.Setup.SetupAction.Assert.OperatorCode.newBuilder()
            .setValue(
              AssertionOperatorTypeCode.Value.valueOf(
                operator.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setPath(pathElement.toProto())
        .setRequestMethod(
          TestScript.Setup.SetupAction.Assert.RequestMethodCode.newBuilder()
            .setValue(
              TestScriptRequestMethodCode.Value.valueOf(
                requestMethod.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setRequestUrl(requestURLElement.toProto())
        .setResource(
          TestScript.Setup.SetupAction.Assert.ResourceCode.newBuilder()
            .setValue(FHIRDefinedTypeValueSet.Value.valueOf(resource))
            .build()
        )
        .setResponse(
          TestScript.Setup.SetupAction.Assert.ResponseCode.newBuilder()
            .setValue(
              AssertionResponseTypesCode.Value.valueOf(
                response.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setResponseCode(responseCodeElement.toProto())
        .setSourceId(sourceIdElement.toProto())
        .setValidateProfileId(validateProfileIdElement.toProto())
        .setValue(valueElement.toProto())
        .setWarningOnly(warningOnlyElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestScriptTestComponent.toProto(): TestScript.Test {
    val protoValue =
      TestScript.Test.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setDescription(descriptionElement.toProto())
        .addAllAction(action.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestActionComponent.toProto():
    TestScript.Test.TestAction {
    val protoValue =
      TestScript.Test.TestAction.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TestScriptTeardownComponent.toProto():
    TestScript.Teardown {
    val protoValue =
      TestScript.Teardown.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllAction(action.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TestScript.TeardownActionComponent.toProto():
    TestScript.Teardown.TeardownAction {
    val protoValue =
      TestScript.Teardown.TeardownAction.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun TestScript.Origin.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptOriginComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptOriginComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIndexElement(index.toHapi())
    hapiValue.setProfile(profile.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Destination.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptDestinationComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptDestinationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIndexElement(index.toHapi())
    hapiValue.setProfile(profile.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Metadata.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptMetadataComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptMetadataComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setLink(linkList.map { it.toHapi() })
    hapiValue.setCapability(capabilityList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Metadata.Link.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptMetadataLinkComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptMetadataLinkComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Metadata.Capability.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptMetadataCapabilityComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptMetadataCapabilityComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setRequiredElement(required.toHapi())
    hapiValue.setValidatedElement(validated.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setOrigin(originList.map { it.toHapi() })
    hapiValue.setDestinationElement(destination.toHapi())
    hapiValue.setLink(linkList.map { it.toHapi() })
    hapiValue.setCapabilitiesElement(capabilities.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Fixture.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptFixtureComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptFixtureComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setAutocreateElement(autocreate.toHapi())
    hapiValue.setAutodeleteElement(autodelete.toHapi())
    hapiValue.setResource(resource.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Variable.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptVariableComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptVariableComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDefaultValueElement(defaultValue.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setExpressionElement(expression.toHapi())
    hapiValue.setHeaderFieldElement(headerField.toHapi())
    hapiValue.setHintElement(hint.toHapi())
    hapiValue.setPathElement(path.toHapi())
    hapiValue.setSourceIdElement(sourceId.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Setup.toHapi(): org.hl7.fhir.r4.model.TestScript.TestScriptSetupComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptSetupComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setAction(actionList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Setup.SetupAction.toHapi():
    org.hl7.fhir.r4.model.TestScript.SetupActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.SetupActionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setOperation(operation.toHapi())
    hapiValue.setAssert(assertValue.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Setup.SetupAction.Operation.toHapi():
    org.hl7.fhir.r4.model.TestScript.SetupActionOperationComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.SetupActionOperationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setResource(resource.value.name)
    hapiValue.setLabelElement(label.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setAccept(accept.value)
    hapiValue.setContentType(contentType.value)
    hapiValue.setDestinationElement(destination.toHapi())
    hapiValue.setEncodeRequestUrlElement(encodeRequestUrl.toHapi())
    hapiValue.setMethod(
      org.hl7.fhir.r4.model.TestScript.TestScriptRequestMethodCode.valueOf(
        method.value.name.replace("_", "")
      )
    )
    hapiValue.setOriginElement(origin.toHapi())
    hapiValue.setParamsElement(params.toHapi())
    hapiValue.setRequestHeader(requestHeaderList.map { it.toHapi() })
    hapiValue.setRequestIdElement(requestId.toHapi())
    hapiValue.setResponseIdElement(responseId.toHapi())
    hapiValue.setSourceIdElement(sourceId.toHapi())
    hapiValue.setTargetIdElement(targetId.toHapi())
    hapiValue.setUrlElement(url.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Setup.SetupAction.Operation.RequestHeader.toHapi():
    org.hl7.fhir.r4.model.TestScript.SetupActionOperationRequestHeaderComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.SetupActionOperationRequestHeaderComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setFieldElement(field.toHapi())
    hapiValue.setValueElement(value.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Setup.SetupAction.Assert.toHapi():
    org.hl7.fhir.r4.model.TestScript.SetupActionAssertComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.SetupActionAssertComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setLabelElement(label.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setDirection(
      org.hl7.fhir.r4.model.TestScript.AssertionDirectionType.valueOf(
        direction.value.name.replace("_", "")
      )
    )
    hapiValue.setCompareToSourceIdElement(compareToSourceId.toHapi())
    hapiValue.setCompareToSourceExpressionElement(compareToSourceExpression.toHapi())
    hapiValue.setCompareToSourcePathElement(compareToSourcePath.toHapi())
    hapiValue.setContentType(contentType.value)
    hapiValue.setExpressionElement(expression.toHapi())
    hapiValue.setHeaderFieldElement(headerField.toHapi())
    hapiValue.setMinimumIdElement(minimumId.toHapi())
    hapiValue.setNavigationLinksElement(navigationLinks.toHapi())
    hapiValue.setOperator(
      org.hl7.fhir.r4.model.TestScript.AssertionOperatorType.valueOf(
        operator.value.name.replace("_", "")
      )
    )
    hapiValue.setPathElement(path.toHapi())
    hapiValue.setRequestMethod(
      org.hl7.fhir.r4.model.TestScript.TestScriptRequestMethodCode.valueOf(
        requestMethod.value.name.replace("_", "")
      )
    )
    hapiValue.setRequestURLElement(requestUrl.toHapi())
    hapiValue.setResource(resource.value.name)
    hapiValue.setResponse(
      org.hl7.fhir.r4.model.TestScript.AssertionResponseTypes.valueOf(
        response.value.name.replace("_", "")
      )
    )
    hapiValue.setResponseCodeElement(responseCode.toHapi())
    hapiValue.setSourceIdElement(sourceId.toHapi())
    hapiValue.setValidateProfileIdElement(validateProfileId.toHapi())
    hapiValue.setValueElement(value.toHapi())
    hapiValue.setWarningOnlyElement(warningOnly.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Test.toHapi(): org.hl7.fhir.r4.model.TestScript.TestScriptTestComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptTestComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setAction(actionList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Test.TestAction.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestActionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Teardown.toHapi():
    org.hl7.fhir.r4.model.TestScript.TestScriptTeardownComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TestScriptTeardownComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setAction(actionList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun TestScript.Teardown.TeardownAction.toHapi():
    org.hl7.fhir.r4.model.TestScript.TeardownActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.TestScript.TeardownActionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    return hapiValue
  }
}
