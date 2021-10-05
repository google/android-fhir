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

package com.google.android.fhir.index

import kotlin.collections.List
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Resource

internal fun getSearchParamList(resource: Resource): List<SearchParamDef> {
  // This File is Generated from com.google.android.fhir.codegen.IndexGenerator all changes to this
  // file must be made through the aforementioned file only
  return when (resource.fhirType()) {
    "Appointment" ->
      listOf(
        SearchParamDef(
          "actor",
          Enumerations.SearchParamType.REFERENCE,
          "Appointment.participant.actor"
        ),
        SearchParamDef(
          "appointment-type",
          Enumerations.SearchParamType.TOKEN,
          "Appointment.appointmentType"
        ),
        SearchParamDef("based-on", Enumerations.SearchParamType.REFERENCE, "Appointment.basedOn"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Appointment.start"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Appointment.identifier"),
        SearchParamDef(
          "location",
          Enumerations.SearchParamType.REFERENCE,
          "Appointment.participant.actor.where(resolve() is Location)"
        ),
        SearchParamDef(
          "part-status",
          Enumerations.SearchParamType.TOKEN,
          "Appointment.participant.status"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Appointment.participant.actor.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "practitioner",
          Enumerations.SearchParamType.REFERENCE,
          "Appointment.participant.actor.where(resolve() is Practitioner)"
        ),
        SearchParamDef("reason-code", Enumerations.SearchParamType.TOKEN, "Appointment.reasonCode"),
        SearchParamDef(
          "reason-reference",
          Enumerations.SearchParamType.REFERENCE,
          "Appointment.reasonReference"
        ),
        SearchParamDef(
          "service-category",
          Enumerations.SearchParamType.TOKEN,
          "Appointment.serviceCategory"
        ),
        SearchParamDef(
          "service-type",
          Enumerations.SearchParamType.TOKEN,
          "Appointment.serviceType"
        ),
        SearchParamDef("slot", Enumerations.SearchParamType.REFERENCE, "Appointment.slot"),
        SearchParamDef("specialty", Enumerations.SearchParamType.TOKEN, "Appointment.specialty"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Appointment.status"),
        SearchParamDef(
          "supporting-info",
          Enumerations.SearchParamType.REFERENCE,
          "Appointment.supportingInformation"
        ),
      )
    "Account" ->
      listOf(
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Account.identifier"),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "Account.name"),
        SearchParamDef("owner", Enumerations.SearchParamType.REFERENCE, "Account.owner"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Account.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("period", Enumerations.SearchParamType.DATE, "Account.servicePeriod"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Account.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Account.subject"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Account.type"),
      )
    "Invoice" ->
      listOf(
        SearchParamDef("account", Enumerations.SearchParamType.REFERENCE, "Invoice.account"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Invoice.date"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Invoice.identifier"),
        SearchParamDef("issuer", Enumerations.SearchParamType.REFERENCE, "Invoice.issuer"),
        SearchParamDef(
          "participant",
          Enumerations.SearchParamType.REFERENCE,
          "Invoice.participant.actor"
        ),
        SearchParamDef(
          "participant-role",
          Enumerations.SearchParamType.TOKEN,
          "Invoice.participant.role"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Invoice.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("recipient", Enumerations.SearchParamType.REFERENCE, "Invoice.recipient"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Invoice.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Invoice.subject"),
        SearchParamDef("totalgross", Enumerations.SearchParamType.QUANTITY, "Invoice.totalGross"),
        SearchParamDef("totalnet", Enumerations.SearchParamType.QUANTITY, "Invoice.totalNet"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Invoice.type"),
      )
    "EventDefinition" ->
      listOf(
        SearchParamDef(
          "composed-of",
          Enumerations.SearchParamType.REFERENCE,
          "EventDefinition.relatedArtifact.where(type='composed-of').resource"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(EventDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(EventDefinition.useContext.value as Quantity) | (EventDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "EventDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "EventDefinition.date"),
        SearchParamDef(
          "depends-on",
          Enumerations.SearchParamType.REFERENCE,
          "EventDefinition.relatedArtifact.where(type='depends-on').resource"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "EventDefinition.relatedArtifact.where(type='derived-from').resource"
        ),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "EventDefinition.description"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "EventDefinition.effectivePeriod"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "EventDefinition.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "EventDefinition.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "EventDefinition.name"),
        SearchParamDef(
          "predecessor",
          Enumerations.SearchParamType.REFERENCE,
          "EventDefinition.relatedArtifact.where(type='predecessor').resource"
        ),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "EventDefinition.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "EventDefinition.status"),
        SearchParamDef(
          "successor",
          Enumerations.SearchParamType.REFERENCE,
          "EventDefinition.relatedArtifact.where(type='successor').resource"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "EventDefinition.title"),
        SearchParamDef("topic", Enumerations.SearchParamType.TOKEN, "EventDefinition.topic"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "EventDefinition.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "EventDefinition.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "EventDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "EventDefinition.useContext"
        ),
      )
    "DocumentManifest" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "DocumentManifest.masterIdentifier | DocumentManifest.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentManifest.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "DocumentManifest.type"),
        SearchParamDef("author", Enumerations.SearchParamType.REFERENCE, "DocumentManifest.author"),
        SearchParamDef("created", Enumerations.SearchParamType.DATE, "DocumentManifest.created"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "DocumentManifest.description"
        ),
        SearchParamDef("item", Enumerations.SearchParamType.REFERENCE, "DocumentManifest.content"),
        SearchParamDef(
          "recipient",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentManifest.recipient"
        ),
        SearchParamDef(
          "related-id",
          Enumerations.SearchParamType.TOKEN,
          "DocumentManifest.related.identifier"
        ),
        SearchParamDef(
          "related-ref",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentManifest.related.ref"
        ),
        SearchParamDef("source", Enumerations.SearchParamType.URI, "DocumentManifest.source"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "DocumentManifest.status"),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentManifest.subject"
        ),
      )
    "MessageDefinition" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(MessageDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(MessageDefinition.useContext.value as Quantity) | (MessageDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "MessageDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "MessageDefinition.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "MessageDefinition.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "MessageDefinition.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "MessageDefinition.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "MessageDefinition.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "MessageDefinition.status"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "MessageDefinition.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "MessageDefinition.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "MessageDefinition.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "MessageDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "MessageDefinition.useContext"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MessageDefinition.identifier"
        ),
        SearchParamDef(
          "category",
          Enumerations.SearchParamType.TOKEN,
          "MessageDefinition.category"
        ),
        SearchParamDef("event", Enumerations.SearchParamType.TOKEN, "MessageDefinition.event"),
        SearchParamDef("focus", Enumerations.SearchParamType.TOKEN, "MessageDefinition.focus.code"),
        SearchParamDef(
          "parent",
          Enumerations.SearchParamType.REFERENCE,
          "MessageDefinition.parent"
        ),
      )
    "Goal" ->
      listOf(
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Goal.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Goal.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "achievement-status",
          Enumerations.SearchParamType.TOKEN,
          "Goal.achievementStatus"
        ),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "Goal.category"),
        SearchParamDef(
          "lifecycle-status",
          Enumerations.SearchParamType.TOKEN,
          "Goal.lifecycleStatus"
        ),
        SearchParamDef("start-date", Enumerations.SearchParamType.DATE, "(Goal.start as date)"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Goal.subject"),
        SearchParamDef(
          "target-date",
          Enumerations.SearchParamType.DATE,
          "(Goal.target.due as date)"
        ),
      )
    "MedicinalProductPackaged" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MedicinalProductPackaged.identifier"
        ),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "MedicinalProductPackaged.subject"
        ),
      )
    "Endpoint" ->
      listOf(
        SearchParamDef(
          "connection-type",
          Enumerations.SearchParamType.TOKEN,
          "Endpoint.connectionType"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Endpoint.identifier"),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "Endpoint.name"),
        SearchParamDef(
          "organization",
          Enumerations.SearchParamType.REFERENCE,
          "Endpoint.managingOrganization"
        ),
        SearchParamDef("payload-type", Enumerations.SearchParamType.TOKEN, "Endpoint.payloadType"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Endpoint.status"),
      )
    "EnrollmentRequest" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "EnrollmentRequest.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "EnrollmentRequest.candidate"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "EnrollmentRequest.status"),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "EnrollmentRequest.candidate"
        ),
      )
    "Consent" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Consent.dateTime"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Consent.identifier"),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "Consent.patient"),
        SearchParamDef("action", Enumerations.SearchParamType.TOKEN, "Consent.provision.action"),
        SearchParamDef(
          "actor",
          Enumerations.SearchParamType.REFERENCE,
          "Consent.provision.actor.reference"
        ),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "Consent.category"),
        SearchParamDef("consentor", Enumerations.SearchParamType.REFERENCE, "Consent.performer"),
        SearchParamDef(
          "data",
          Enumerations.SearchParamType.REFERENCE,
          "Consent.provision.data.reference"
        ),
        SearchParamDef(
          "organization",
          Enumerations.SearchParamType.REFERENCE,
          "Consent.organization"
        ),
        SearchParamDef("period", Enumerations.SearchParamType.DATE, "Consent.provision.period"),
        SearchParamDef("purpose", Enumerations.SearchParamType.TOKEN, "Consent.provision.purpose"),
        SearchParamDef("scope", Enumerations.SearchParamType.TOKEN, "Consent.scope"),
        SearchParamDef(
          "security-label",
          Enumerations.SearchParamType.TOKEN,
          "Consent.provision.securityLabel"
        ),
        SearchParamDef(
          "source-reference",
          Enumerations.SearchParamType.REFERENCE,
          "Consent.source"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Consent.status"),
      )
    "Medication" ->
      listOf(
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "Medication.code"),
        SearchParamDef(
          "expiration-date",
          Enumerations.SearchParamType.DATE,
          "Medication.batch.expirationDate"
        ),
        SearchParamDef("form", Enumerations.SearchParamType.TOKEN, "Medication.form"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Medication.identifier"),
        SearchParamDef(
          "ingredient",
          Enumerations.SearchParamType.REFERENCE,
          "(Medication.ingredient.item as Reference)"
        ),
        SearchParamDef(
          "ingredient-code",
          Enumerations.SearchParamType.TOKEN,
          "(Medication.ingredient.item as CodeableConcept)"
        ),
        SearchParamDef(
          "lot-number",
          Enumerations.SearchParamType.TOKEN,
          "Medication.batch.lotNumber"
        ),
        SearchParamDef(
          "manufacturer",
          Enumerations.SearchParamType.REFERENCE,
          "Medication.manufacturer"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Medication.status"),
      )
    "CapabilityStatement" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(CapabilityStatement.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(CapabilityStatement.useContext.value as Quantity) | (CapabilityStatement.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "CapabilityStatement.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "CapabilityStatement.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "CapabilityStatement.description"
        ),
        SearchParamDef(
          "fhirversion",
          Enumerations.SearchParamType.TOKEN,
          "CapabilityStatement.version"
        ),
        SearchParamDef("format", Enumerations.SearchParamType.TOKEN, "CapabilityStatement.format"),
        SearchParamDef(
          "guide",
          Enumerations.SearchParamType.REFERENCE,
          "CapabilityStatement.implementationGuide"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "CapabilityStatement.jurisdiction"
        ),
        SearchParamDef("mode", Enumerations.SearchParamType.TOKEN, "CapabilityStatement.rest.mode"),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "CapabilityStatement.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "CapabilityStatement.publisher"
        ),
        SearchParamDef(
          "resource",
          Enumerations.SearchParamType.TOKEN,
          "CapabilityStatement.rest.resource.type"
        ),
        SearchParamDef(
          "resource-profile",
          Enumerations.SearchParamType.REFERENCE,
          "CapabilityStatement.rest.resource.profile"
        ),
        SearchParamDef(
          "security-service",
          Enumerations.SearchParamType.TOKEN,
          "CapabilityStatement.rest.security.service"
        ),
        SearchParamDef(
          "software",
          Enumerations.SearchParamType.STRING,
          "CapabilityStatement.software.name"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "CapabilityStatement.status"),
        SearchParamDef(
          "supported-profile",
          Enumerations.SearchParamType.REFERENCE,
          "CapabilityStatement.rest.resource.supportedProfile"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "CapabilityStatement.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "CapabilityStatement.url"),
        SearchParamDef(
          "version",
          Enumerations.SearchParamType.TOKEN,
          "CapabilityStatement.version"
        ),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "CapabilityStatement.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "CapabilityStatement.useContext"
        ),
      )
    "Measure" ->
      listOf(
        SearchParamDef(
          "composed-of",
          Enumerations.SearchParamType.REFERENCE,
          "Measure.relatedArtifact.where(type='composed-of').resource"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(Measure.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(Measure.useContext.value as Quantity) | (Measure.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "Measure.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Measure.date"),
        SearchParamDef(
          "depends-on",
          Enumerations.SearchParamType.REFERENCE,
          "Measure.relatedArtifact.where(type='depends-on').resource | Measure.library"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "Measure.relatedArtifact.where(type='derived-from').resource"
        ),
        SearchParamDef("description", Enumerations.SearchParamType.STRING, "Measure.description"),
        SearchParamDef("effective", Enumerations.SearchParamType.DATE, "Measure.effectivePeriod"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Measure.identifier"),
        SearchParamDef("jurisdiction", Enumerations.SearchParamType.TOKEN, "Measure.jurisdiction"),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "Measure.name"),
        SearchParamDef(
          "predecessor",
          Enumerations.SearchParamType.REFERENCE,
          "Measure.relatedArtifact.where(type='predecessor').resource"
        ),
        SearchParamDef("publisher", Enumerations.SearchParamType.STRING, "Measure.publisher"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Measure.status"),
        SearchParamDef(
          "successor",
          Enumerations.SearchParamType.REFERENCE,
          "Measure.relatedArtifact.where(type='successor').resource"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "Measure.title"),
        SearchParamDef("topic", Enumerations.SearchParamType.TOKEN, "Measure.topic"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "Measure.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "Measure.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "Measure.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "Measure.useContext"
        ),
      )
    "ResearchSubject" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ResearchSubject.period"),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ResearchSubject.identifier"
        ),
        SearchParamDef(
          "individual",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchSubject.individual"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchSubject.individual"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ResearchSubject.status"),
        SearchParamDef("study", Enumerations.SearchParamType.REFERENCE, "ResearchSubject.study"),
      )
    "Subscription" ->
      listOf(
        SearchParamDef("contact", Enumerations.SearchParamType.TOKEN, "Subscription.contact"),
        SearchParamDef("criteria", Enumerations.SearchParamType.STRING, "Subscription.criteria"),
        SearchParamDef(
          "payload",
          Enumerations.SearchParamType.TOKEN,
          "Subscription.channel.payload"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Subscription.status"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Subscription.channel.type"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "Subscription.channel.endpoint"),
      )
    "DocumentReference" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "DocumentReference.masterIdentifier | DocumentReference.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentReference.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "DocumentReference.type"),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentReference.context.encounter"
        ),
        SearchParamDef(
          "authenticator",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentReference.authenticator"
        ),
        SearchParamDef(
          "author",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentReference.author"
        ),
        SearchParamDef(
          "category",
          Enumerations.SearchParamType.TOKEN,
          "DocumentReference.category"
        ),
        SearchParamDef(
          "contenttype",
          Enumerations.SearchParamType.TOKEN,
          "DocumentReference.content.attachment.contentType"
        ),
        SearchParamDef(
          "custodian",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentReference.custodian"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "DocumentReference.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "DocumentReference.description"
        ),
        SearchParamDef(
          "event",
          Enumerations.SearchParamType.TOKEN,
          "DocumentReference.context.event"
        ),
        SearchParamDef(
          "facility",
          Enumerations.SearchParamType.TOKEN,
          "DocumentReference.context.facilityType"
        ),
        SearchParamDef(
          "format",
          Enumerations.SearchParamType.TOKEN,
          "DocumentReference.content.format"
        ),
        SearchParamDef(
          "language",
          Enumerations.SearchParamType.TOKEN,
          "DocumentReference.content.attachment.language"
        ),
        SearchParamDef(
          "location",
          Enumerations.SearchParamType.URI,
          "DocumentReference.content.attachment.url"
        ),
        SearchParamDef(
          "period",
          Enumerations.SearchParamType.DATE,
          "DocumentReference.context.period"
        ),
        SearchParamDef(
          "related",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentReference.context.related"
        ),
        SearchParamDef(
          "relatesto",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentReference.relatesTo.target"
        ),
        SearchParamDef(
          "relation",
          Enumerations.SearchParamType.TOKEN,
          "DocumentReference.relatesTo.code"
        ),
        SearchParamDef(
          "security-label",
          Enumerations.SearchParamType.TOKEN,
          "DocumentReference.securityLabel"
        ),
        SearchParamDef(
          "setting",
          Enumerations.SearchParamType.TOKEN,
          "DocumentReference.context.practiceSetting"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "DocumentReference.status"),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "DocumentReference.subject"
        ),
        SearchParamDef(
          "relationship",
          Enumerations.SearchParamType.COMPOSITE,
          "DocumentReference.relatesTo"
        ),
      )
    "GraphDefinition" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(GraphDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(GraphDefinition.useContext.value as Quantity) | (GraphDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "GraphDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "GraphDefinition.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "GraphDefinition.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "GraphDefinition.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "GraphDefinition.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "GraphDefinition.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "GraphDefinition.status"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "GraphDefinition.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "GraphDefinition.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "GraphDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "GraphDefinition.useContext"
        ),
        SearchParamDef("start", Enumerations.SearchParamType.TOKEN, "GraphDefinition.start"),
      )
    "CoverageEligibilityResponse" ->
      listOf(
        SearchParamDef(
          "created",
          Enumerations.SearchParamType.DATE,
          "CoverageEligibilityResponse.created"
        ),
        SearchParamDef(
          "disposition",
          Enumerations.SearchParamType.STRING,
          "CoverageEligibilityResponse.disposition"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "CoverageEligibilityResponse.identifier"
        ),
        SearchParamDef(
          "insurer",
          Enumerations.SearchParamType.REFERENCE,
          "CoverageEligibilityResponse.insurer"
        ),
        SearchParamDef(
          "outcome",
          Enumerations.SearchParamType.TOKEN,
          "CoverageEligibilityResponse.outcome"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "CoverageEligibilityResponse.patient"
        ),
        SearchParamDef(
          "request",
          Enumerations.SearchParamType.REFERENCE,
          "CoverageEligibilityResponse.request"
        ),
        SearchParamDef(
          "requestor",
          Enumerations.SearchParamType.REFERENCE,
          "CoverageEligibilityResponse.requestor"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "CoverageEligibilityResponse.status"
        ),
      )
    "MeasureReport" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "MeasureReport.date"),
        SearchParamDef(
          "evaluated-resource",
          Enumerations.SearchParamType.REFERENCE,
          "MeasureReport.evaluatedResource"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MeasureReport.identifier"
        ),
        SearchParamDef("measure", Enumerations.SearchParamType.REFERENCE, "MeasureReport.measure"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "MeasureReport.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("period", Enumerations.SearchParamType.DATE, "MeasureReport.period"),
        SearchParamDef(
          "reporter",
          Enumerations.SearchParamType.REFERENCE,
          "MeasureReport.reporter"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "MeasureReport.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "MeasureReport.subject"),
      )
    "PractitionerRole" ->
      listOf(
        SearchParamDef(
          "email",
          Enumerations.SearchParamType.TOKEN,
          "PractitionerRole.telecom.where(system='email')"
        ),
        SearchParamDef(
          "phone",
          Enumerations.SearchParamType.TOKEN,
          "PractitionerRole.telecom.where(system='phone')"
        ),
        SearchParamDef("telecom", Enumerations.SearchParamType.TOKEN, "PractitionerRole.telecom"),
        SearchParamDef("active", Enumerations.SearchParamType.TOKEN, "PractitionerRole.active"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "PractitionerRole.period"),
        SearchParamDef(
          "endpoint",
          Enumerations.SearchParamType.REFERENCE,
          "PractitionerRole.endpoint"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "PractitionerRole.identifier"
        ),
        SearchParamDef(
          "location",
          Enumerations.SearchParamType.REFERENCE,
          "PractitionerRole.location"
        ),
        SearchParamDef(
          "organization",
          Enumerations.SearchParamType.REFERENCE,
          "PractitionerRole.organization"
        ),
        SearchParamDef(
          "practitioner",
          Enumerations.SearchParamType.REFERENCE,
          "PractitionerRole.practitioner"
        ),
        SearchParamDef("role", Enumerations.SearchParamType.TOKEN, "PractitionerRole.code"),
        SearchParamDef(
          "service",
          Enumerations.SearchParamType.REFERENCE,
          "PractitionerRole.healthcareService"
        ),
        SearchParamDef(
          "specialty",
          Enumerations.SearchParamType.TOKEN,
          "PractitionerRole.specialty"
        ),
      )
    "ServiceRequest" ->
      listOf(
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "ServiceRequest.code"),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ServiceRequest.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "ServiceRequest.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "ServiceRequest.encounter"
        ),
        SearchParamDef("authored", Enumerations.SearchParamType.DATE, "ServiceRequest.authoredOn"),
        SearchParamDef(
          "based-on",
          Enumerations.SearchParamType.REFERENCE,
          "ServiceRequest.basedOn"
        ),
        SearchParamDef("body-site", Enumerations.SearchParamType.TOKEN, "ServiceRequest.bodySite"),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "ServiceRequest.category"),
        SearchParamDef(
          "instantiates-canonical",
          Enumerations.SearchParamType.REFERENCE,
          "ServiceRequest.instantiatesCanonical"
        ),
        SearchParamDef(
          "instantiates-uri",
          Enumerations.SearchParamType.URI,
          "ServiceRequest.instantiatesUri"
        ),
        SearchParamDef("intent", Enumerations.SearchParamType.TOKEN, "ServiceRequest.intent"),
        SearchParamDef(
          "occurrence",
          Enumerations.SearchParamType.DATE,
          "ServiceRequest.occurrence"
        ),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "ServiceRequest.performer"
        ),
        SearchParamDef(
          "performer-type",
          Enumerations.SearchParamType.TOKEN,
          "ServiceRequest.performerType"
        ),
        SearchParamDef("priority", Enumerations.SearchParamType.TOKEN, "ServiceRequest.priority"),
        SearchParamDef(
          "replaces",
          Enumerations.SearchParamType.REFERENCE,
          "ServiceRequest.replaces"
        ),
        SearchParamDef(
          "requester",
          Enumerations.SearchParamType.REFERENCE,
          "ServiceRequest.requester"
        ),
        SearchParamDef(
          "requisition",
          Enumerations.SearchParamType.TOKEN,
          "ServiceRequest.requisition"
        ),
        SearchParamDef(
          "specimen",
          Enumerations.SearchParamType.REFERENCE,
          "ServiceRequest.specimen"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ServiceRequest.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "ServiceRequest.subject"),
      )
    "RelatedPerson" ->
      listOf(
        SearchParamDef("address", Enumerations.SearchParamType.STRING, "RelatedPerson.address"),
        SearchParamDef(
          "address-city",
          Enumerations.SearchParamType.STRING,
          "RelatedPerson.address.city"
        ),
        SearchParamDef(
          "address-country",
          Enumerations.SearchParamType.STRING,
          "RelatedPerson.address.country"
        ),
        SearchParamDef(
          "address-postalcode",
          Enumerations.SearchParamType.STRING,
          "RelatedPerson.address.postalCode"
        ),
        SearchParamDef(
          "address-state",
          Enumerations.SearchParamType.STRING,
          "RelatedPerson.address.state"
        ),
        SearchParamDef(
          "address-use",
          Enumerations.SearchParamType.TOKEN,
          "RelatedPerson.address.use"
        ),
        SearchParamDef("birthdate", Enumerations.SearchParamType.DATE, "RelatedPerson.birthDate"),
        SearchParamDef(
          "email",
          Enumerations.SearchParamType.TOKEN,
          "RelatedPerson.telecom.where(system='email')"
        ),
        SearchParamDef("gender", Enumerations.SearchParamType.TOKEN, "RelatedPerson.gender"),
        SearchParamDef(
          "phone",
          Enumerations.SearchParamType.TOKEN,
          "RelatedPerson.telecom.where(system='phone')"
        ),
        SearchParamDef("phonetic", Enumerations.SearchParamType.STRING, "RelatedPerson.name"),
        SearchParamDef("telecom", Enumerations.SearchParamType.TOKEN, "RelatedPerson.telecom"),
        SearchParamDef("active", Enumerations.SearchParamType.TOKEN, "RelatedPerson.active"),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "RelatedPerson.identifier"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "RelatedPerson.name"),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "RelatedPerson.patient"),
        SearchParamDef(
          "relationship",
          Enumerations.SearchParamType.TOKEN,
          "RelatedPerson.relationship"
        ),
      )
    "SupplyRequest" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "SupplyRequest.authoredOn"),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "SupplyRequest.identifier"
        ),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "SupplyRequest.category"),
        SearchParamDef(
          "requester",
          Enumerations.SearchParamType.REFERENCE,
          "SupplyRequest.requester"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "SupplyRequest.status"),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "SupplyRequest.deliverTo"
        ),
        SearchParamDef(
          "supplier",
          Enumerations.SearchParamType.REFERENCE,
          "SupplyRequest.supplier"
        ),
      )
    "Practitioner" ->
      listOf(
        SearchParamDef("address", Enumerations.SearchParamType.STRING, "Practitioner.address"),
        SearchParamDef(
          "address-city",
          Enumerations.SearchParamType.STRING,
          "Practitioner.address.city"
        ),
        SearchParamDef(
          "address-country",
          Enumerations.SearchParamType.STRING,
          "Practitioner.address.country"
        ),
        SearchParamDef(
          "address-postalcode",
          Enumerations.SearchParamType.STRING,
          "Practitioner.address.postalCode"
        ),
        SearchParamDef(
          "address-state",
          Enumerations.SearchParamType.STRING,
          "Practitioner.address.state"
        ),
        SearchParamDef(
          "address-use",
          Enumerations.SearchParamType.TOKEN,
          "Practitioner.address.use"
        ),
        SearchParamDef(
          "email",
          Enumerations.SearchParamType.TOKEN,
          "Practitioner.telecom.where(system='email')"
        ),
        SearchParamDef("family", Enumerations.SearchParamType.STRING, "Practitioner.name.family"),
        SearchParamDef("gender", Enumerations.SearchParamType.TOKEN, "Practitioner.gender"),
        SearchParamDef("given", Enumerations.SearchParamType.STRING, "Practitioner.name.given"),
        SearchParamDef(
          "phone",
          Enumerations.SearchParamType.TOKEN,
          "Practitioner.telecom.where(system='phone')"
        ),
        SearchParamDef("phonetic", Enumerations.SearchParamType.STRING, "Practitioner.name"),
        SearchParamDef("telecom", Enumerations.SearchParamType.TOKEN, "Practitioner.telecom"),
        SearchParamDef("active", Enumerations.SearchParamType.TOKEN, "Practitioner.active"),
        SearchParamDef(
          "communication",
          Enumerations.SearchParamType.TOKEN,
          "Practitioner.communication"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Practitioner.identifier"),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "Practitioner.name"),
      )
    "VerificationResult" ->
      listOf(
        SearchParamDef(
          "target",
          Enumerations.SearchParamType.REFERENCE,
          "VerificationResult.target"
        ),
      )
    "BodyStructure" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "BodyStructure.identifier"
        ),
        SearchParamDef("location", Enumerations.SearchParamType.TOKEN, "BodyStructure.location"),
        SearchParamDef(
          "morphology",
          Enumerations.SearchParamType.TOKEN,
          "BodyStructure.morphology"
        ),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "BodyStructure.patient"),
      )
    "Slot" ->
      listOf(
        SearchParamDef(
          "appointment-type",
          Enumerations.SearchParamType.TOKEN,
          "Slot.appointmentType"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Slot.identifier"),
        SearchParamDef("schedule", Enumerations.SearchParamType.REFERENCE, "Slot.schedule"),
        SearchParamDef(
          "service-category",
          Enumerations.SearchParamType.TOKEN,
          "Slot.serviceCategory"
        ),
        SearchParamDef("service-type", Enumerations.SearchParamType.TOKEN, "Slot.serviceType"),
        SearchParamDef("specialty", Enumerations.SearchParamType.TOKEN, "Slot.specialty"),
        SearchParamDef("start", Enumerations.SearchParamType.DATE, "Slot.start"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Slot.status"),
      )
    "Contract" ->
      listOf(
        SearchParamDef("authority", Enumerations.SearchParamType.REFERENCE, "Contract.authority"),
        SearchParamDef("domain", Enumerations.SearchParamType.REFERENCE, "Contract.domain"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Contract.identifier"),
        SearchParamDef(
          "instantiates",
          Enumerations.SearchParamType.URI,
          "Contract.instantiatesUri"
        ),
        SearchParamDef("issued", Enumerations.SearchParamType.DATE, "Contract.issued"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Contract.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("signer", Enumerations.SearchParamType.REFERENCE, "Contract.signer.party"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Contract.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Contract.subject"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "Contract.url"),
      )
    "Person" ->
      listOf(
        SearchParamDef("address", Enumerations.SearchParamType.STRING, "Person.address"),
        SearchParamDef("address-city", Enumerations.SearchParamType.STRING, "Person.address.city"),
        SearchParamDef(
          "address-country",
          Enumerations.SearchParamType.STRING,
          "Person.address.country"
        ),
        SearchParamDef(
          "address-postalcode",
          Enumerations.SearchParamType.STRING,
          "Person.address.postalCode"
        ),
        SearchParamDef(
          "address-state",
          Enumerations.SearchParamType.STRING,
          "Person.address.state"
        ),
        SearchParamDef("address-use", Enumerations.SearchParamType.TOKEN, "Person.address.use"),
        SearchParamDef("birthdate", Enumerations.SearchParamType.DATE, "Person.birthDate"),
        SearchParamDef(
          "email",
          Enumerations.SearchParamType.TOKEN,
          "Person.telecom.where(system='email')"
        ),
        SearchParamDef("gender", Enumerations.SearchParamType.TOKEN, "Person.gender"),
        SearchParamDef(
          "phone",
          Enumerations.SearchParamType.TOKEN,
          "Person.telecom.where(system='phone')"
        ),
        SearchParamDef("phonetic", Enumerations.SearchParamType.STRING, "Person.name"),
        SearchParamDef("telecom", Enumerations.SearchParamType.TOKEN, "Person.telecom"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Person.identifier"),
        SearchParamDef("link", Enumerations.SearchParamType.REFERENCE, "Person.link.target"),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "Person.name"),
        SearchParamDef(
          "organization",
          Enumerations.SearchParamType.REFERENCE,
          "Person.managingOrganization"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Person.link.target.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "practitioner",
          Enumerations.SearchParamType.REFERENCE,
          "Person.link.target.where(resolve() is Practitioner)"
        ),
        SearchParamDef(
          "relatedperson",
          Enumerations.SearchParamType.REFERENCE,
          "Person.link.target.where(resolve() is RelatedPerson)"
        ),
      )
    "RiskAssessment" ->
      listOf(
        SearchParamDef(
          "date",
          Enumerations.SearchParamType.DATE,
          "(RiskAssessment.occurrence as dateTime)"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "RiskAssessment.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "RiskAssessment.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "RiskAssessment.encounter"
        ),
        SearchParamDef(
          "condition",
          Enumerations.SearchParamType.REFERENCE,
          "RiskAssessment.condition"
        ),
        SearchParamDef("method", Enumerations.SearchParamType.TOKEN, "RiskAssessment.method"),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "RiskAssessment.performer"
        ),
        SearchParamDef(
          "probability",
          Enumerations.SearchParamType.NUMBER,
          "RiskAssessment.prediction.probability"
        ),
        SearchParamDef(
          "risk",
          Enumerations.SearchParamType.TOKEN,
          "RiskAssessment.prediction.qualitativeRisk"
        ),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "RiskAssessment.subject"),
      )
    "Group" ->
      listOf(
        SearchParamDef("actual", Enumerations.SearchParamType.TOKEN, "Group.actual"),
        SearchParamDef(
          "characteristic",
          Enumerations.SearchParamType.TOKEN,
          "Group.characteristic.code"
        ),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "Group.code"),
        SearchParamDef(
          "exclude",
          Enumerations.SearchParamType.TOKEN,
          "Group.characteristic.exclude"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Group.identifier"),
        SearchParamDef(
          "managing-entity",
          Enumerations.SearchParamType.REFERENCE,
          "Group.managingEntity"
        ),
        SearchParamDef("member", Enumerations.SearchParamType.REFERENCE, "Group.member.entity"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Group.type"),
        SearchParamDef(
          "value",
          Enumerations.SearchParamType.TOKEN,
          "(Group.characteristic.value as CodeableConcept) | (Group.characteristic.value as boolean)"
        ),
        SearchParamDef(
          "characteristic-value",
          Enumerations.SearchParamType.COMPOSITE,
          "Group.characteristic"
        ),
      )
    "PaymentNotice" ->
      listOf(
        SearchParamDef("created", Enumerations.SearchParamType.DATE, "PaymentNotice.created"),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "PaymentNotice.identifier"
        ),
        SearchParamDef(
          "payment-status",
          Enumerations.SearchParamType.TOKEN,
          "PaymentNotice.paymentStatus"
        ),
        SearchParamDef(
          "provider",
          Enumerations.SearchParamType.REFERENCE,
          "PaymentNotice.provider"
        ),
        SearchParamDef("request", Enumerations.SearchParamType.REFERENCE, "PaymentNotice.request"),
        SearchParamDef(
          "response",
          Enumerations.SearchParamType.REFERENCE,
          "PaymentNotice.response"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "PaymentNotice.status"),
      )
    "ResearchDefinition" ->
      listOf(
        SearchParamDef(
          "composed-of",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchDefinition.relatedArtifact.where(type='composed-of').resource"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(ResearchDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(ResearchDefinition.useContext.value as Quantity) | (ResearchDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "ResearchDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ResearchDefinition.date"),
        SearchParamDef(
          "depends-on",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchDefinition.relatedArtifact.where(type='depends-on').resource | ResearchDefinition.library"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchDefinition.relatedArtifact.where(type='derived-from').resource"
        ),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "ResearchDefinition.description"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "ResearchDefinition.effectivePeriod"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ResearchDefinition.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "ResearchDefinition.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "ResearchDefinition.name"),
        SearchParamDef(
          "predecessor",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchDefinition.relatedArtifact.where(type='predecessor').resource"
        ),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "ResearchDefinition.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ResearchDefinition.status"),
        SearchParamDef(
          "successor",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchDefinition.relatedArtifact.where(type='successor').resource"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "ResearchDefinition.title"),
        SearchParamDef("topic", Enumerations.SearchParamType.TOKEN, "ResearchDefinition.topic"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "ResearchDefinition.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "ResearchDefinition.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "ResearchDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "ResearchDefinition.useContext"
        ),
      )
    "Organization" ->
      listOf(
        SearchParamDef("active", Enumerations.SearchParamType.TOKEN, "Organization.active"),
        SearchParamDef("address", Enumerations.SearchParamType.STRING, "Organization.address"),
        SearchParamDef(
          "address-city",
          Enumerations.SearchParamType.STRING,
          "Organization.address.city"
        ),
        SearchParamDef(
          "address-country",
          Enumerations.SearchParamType.STRING,
          "Organization.address.country"
        ),
        SearchParamDef(
          "address-postalcode",
          Enumerations.SearchParamType.STRING,
          "Organization.address.postalCode"
        ),
        SearchParamDef(
          "address-state",
          Enumerations.SearchParamType.STRING,
          "Organization.address.state"
        ),
        SearchParamDef(
          "address-use",
          Enumerations.SearchParamType.TOKEN,
          "Organization.address.use"
        ),
        SearchParamDef("endpoint", Enumerations.SearchParamType.REFERENCE, "Organization.endpoint"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Organization.identifier"),
        SearchParamDef(
          "name",
          Enumerations.SearchParamType.STRING,
          "Organization.name | Organization.alias"
        ),
        SearchParamDef("partof", Enumerations.SearchParamType.REFERENCE, "Organization.partOf"),
        SearchParamDef("phonetic", Enumerations.SearchParamType.STRING, "Organization.name"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Organization.type"),
      )
    "CareTeam" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "CareTeam.period"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "CareTeam.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "CareTeam.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "CareTeam.category"),
        SearchParamDef("encounter", Enumerations.SearchParamType.REFERENCE, "CareTeam.encounter"),
        SearchParamDef(
          "participant",
          Enumerations.SearchParamType.REFERENCE,
          "CareTeam.participant.member"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "CareTeam.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "CareTeam.subject"),
      )
    "ImplementationGuide" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(ImplementationGuide.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(ImplementationGuide.useContext.value as Quantity) | (ImplementationGuide.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "ImplementationGuide.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ImplementationGuide.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "ImplementationGuide.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "ImplementationGuide.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "ImplementationGuide.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "ImplementationGuide.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ImplementationGuide.status"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "ImplementationGuide.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "ImplementationGuide.url"),
        SearchParamDef(
          "version",
          Enumerations.SearchParamType.TOKEN,
          "ImplementationGuide.version"
        ),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "ImplementationGuide.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "ImplementationGuide.useContext"
        ),
        SearchParamDef(
          "depends-on",
          Enumerations.SearchParamType.REFERENCE,
          "ImplementationGuide.dependsOn.uri"
        ),
        SearchParamDef(
          "experimental",
          Enumerations.SearchParamType.TOKEN,
          "ImplementationGuide.experimental"
        ),
        SearchParamDef(
          "global",
          Enumerations.SearchParamType.REFERENCE,
          "ImplementationGuide.global.profile"
        ),
        SearchParamDef(
          "resource",
          Enumerations.SearchParamType.REFERENCE,
          "ImplementationGuide.definition.resource.reference"
        ),
      )
    "ImagingStudy" ->
      listOf(
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "ImagingStudy.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "ImagingStudy.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("basedon", Enumerations.SearchParamType.REFERENCE, "ImagingStudy.basedOn"),
        SearchParamDef(
          "bodysite",
          Enumerations.SearchParamType.TOKEN,
          "ImagingStudy.series.bodySite"
        ),
        SearchParamDef(
          "dicom-class",
          Enumerations.SearchParamType.TOKEN,
          "ImagingStudy.series.instance.sopClass"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "ImagingStudy.encounter"
        ),
        SearchParamDef(
          "endpoint",
          Enumerations.SearchParamType.REFERENCE,
          "ImagingStudy.endpoint | ImagingStudy.series.endpoint"
        ),
        SearchParamDef(
          "instance",
          Enumerations.SearchParamType.TOKEN,
          "ImagingStudy.series.instance.uid"
        ),
        SearchParamDef(
          "interpreter",
          Enumerations.SearchParamType.REFERENCE,
          "ImagingStudy.interpreter"
        ),
        SearchParamDef(
          "modality",
          Enumerations.SearchParamType.TOKEN,
          "ImagingStudy.series.modality"
        ),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "ImagingStudy.series.performer.actor"
        ),
        SearchParamDef("reason", Enumerations.SearchParamType.TOKEN, "ImagingStudy.reasonCode"),
        SearchParamDef("referrer", Enumerations.SearchParamType.REFERENCE, "ImagingStudy.referrer"),
        SearchParamDef("series", Enumerations.SearchParamType.TOKEN, "ImagingStudy.series.uid"),
        SearchParamDef("started", Enumerations.SearchParamType.DATE, "ImagingStudy.started"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ImagingStudy.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "ImagingStudy.subject"),
      )
    "FamilyMemberHistory" ->
      listOf(
        SearchParamDef(
          "code",
          Enumerations.SearchParamType.TOKEN,
          "FamilyMemberHistory.condition.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "FamilyMemberHistory.date"),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "FamilyMemberHistory.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "FamilyMemberHistory.patient"
        ),
        SearchParamDef(
          "instantiates-canonical",
          Enumerations.SearchParamType.REFERENCE,
          "FamilyMemberHistory.instantiatesCanonical"
        ),
        SearchParamDef(
          "instantiates-uri",
          Enumerations.SearchParamType.URI,
          "FamilyMemberHistory.instantiatesUri"
        ),
        SearchParamDef(
          "relationship",
          Enumerations.SearchParamType.TOKEN,
          "FamilyMemberHistory.relationship"
        ),
        SearchParamDef("sex", Enumerations.SearchParamType.TOKEN, "FamilyMemberHistory.sex"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "FamilyMemberHistory.status"),
      )
    "ChargeItem" ->
      listOf(
        SearchParamDef("account", Enumerations.SearchParamType.REFERENCE, "ChargeItem.account"),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "ChargeItem.code"),
        SearchParamDef("context", Enumerations.SearchParamType.REFERENCE, "ChargeItem.context"),
        SearchParamDef("entered-date", Enumerations.SearchParamType.DATE, "ChargeItem.enteredDate"),
        SearchParamDef("enterer", Enumerations.SearchParamType.REFERENCE, "ChargeItem.enterer"),
        SearchParamDef(
          "factor-override",
          Enumerations.SearchParamType.NUMBER,
          "ChargeItem.factorOverride"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "ChargeItem.identifier"),
        SearchParamDef("occurrence", Enumerations.SearchParamType.DATE, "ChargeItem.occurrence"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "ChargeItem.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "performer-actor",
          Enumerations.SearchParamType.REFERENCE,
          "ChargeItem.performer.actor"
        ),
        SearchParamDef(
          "performer-function",
          Enumerations.SearchParamType.TOKEN,
          "ChargeItem.performer.function"
        ),
        SearchParamDef(
          "performing-organization",
          Enumerations.SearchParamType.REFERENCE,
          "ChargeItem.performingOrganization"
        ),
        SearchParamDef(
          "price-override",
          Enumerations.SearchParamType.QUANTITY,
          "ChargeItem.priceOverride"
        ),
        SearchParamDef("quantity", Enumerations.SearchParamType.QUANTITY, "ChargeItem.quantity"),
        SearchParamDef(
          "requesting-organization",
          Enumerations.SearchParamType.REFERENCE,
          "ChargeItem.requestingOrganization"
        ),
        SearchParamDef("service", Enumerations.SearchParamType.REFERENCE, "ChargeItem.service"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "ChargeItem.subject"),
      )
    "ResearchElementDefinition" ->
      listOf(
        SearchParamDef(
          "composed-of",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchElementDefinition.relatedArtifact.where(type='composed-of').resource"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(ResearchElementDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(ResearchElementDefinition.useContext.value as Quantity) | (ResearchElementDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "ResearchElementDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ResearchElementDefinition.date"),
        SearchParamDef(
          "depends-on",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchElementDefinition.relatedArtifact.where(type='depends-on').resource | ResearchElementDefinition.library"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchElementDefinition.relatedArtifact.where(type='derived-from').resource"
        ),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "ResearchElementDefinition.description"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "ResearchElementDefinition.effectivePeriod"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ResearchElementDefinition.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "ResearchElementDefinition.jurisdiction"
        ),
        SearchParamDef(
          "name",
          Enumerations.SearchParamType.STRING,
          "ResearchElementDefinition.name"
        ),
        SearchParamDef(
          "predecessor",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchElementDefinition.relatedArtifact.where(type='predecessor').resource"
        ),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "ResearchElementDefinition.publisher"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "ResearchElementDefinition.status"
        ),
        SearchParamDef(
          "successor",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchElementDefinition.relatedArtifact.where(type='successor').resource"
        ),
        SearchParamDef(
          "title",
          Enumerations.SearchParamType.STRING,
          "ResearchElementDefinition.title"
        ),
        SearchParamDef(
          "topic",
          Enumerations.SearchParamType.TOKEN,
          "ResearchElementDefinition.topic"
        ),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "ResearchElementDefinition.url"),
        SearchParamDef(
          "version",
          Enumerations.SearchParamType.TOKEN,
          "ResearchElementDefinition.version"
        ),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "ResearchElementDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "ResearchElementDefinition.useContext"
        ),
      )
    "Encounter" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Encounter.period"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Encounter.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Encounter.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Encounter.type"),
        SearchParamDef("account", Enumerations.SearchParamType.REFERENCE, "Encounter.account"),
        SearchParamDef(
          "appointment",
          Enumerations.SearchParamType.REFERENCE,
          "Encounter.appointment"
        ),
        SearchParamDef("based-on", Enumerations.SearchParamType.REFERENCE, "Encounter.basedOn"),
        SearchParamDef("class", Enumerations.SearchParamType.TOKEN, "Encounter.class"),
        SearchParamDef(
          "diagnosis",
          Enumerations.SearchParamType.REFERENCE,
          "Encounter.diagnosis.condition"
        ),
        SearchParamDef(
          "episode-of-care",
          Enumerations.SearchParamType.REFERENCE,
          "Encounter.episodeOfCare"
        ),
        SearchParamDef("length", Enumerations.SearchParamType.QUANTITY, "Encounter.length"),
        SearchParamDef(
          "location",
          Enumerations.SearchParamType.REFERENCE,
          "Encounter.location.location"
        ),
        SearchParamDef(
          "location-period",
          Enumerations.SearchParamType.DATE,
          "Encounter.location.period"
        ),
        SearchParamDef("part-of", Enumerations.SearchParamType.REFERENCE, "Encounter.partOf"),
        SearchParamDef(
          "participant",
          Enumerations.SearchParamType.REFERENCE,
          "Encounter.participant.individual"
        ),
        SearchParamDef(
          "participant-type",
          Enumerations.SearchParamType.TOKEN,
          "Encounter.participant.type"
        ),
        SearchParamDef(
          "practitioner",
          Enumerations.SearchParamType.REFERENCE,
          "Encounter.participant.individual.where(resolve() is Practitioner)"
        ),
        SearchParamDef("reason-code", Enumerations.SearchParamType.TOKEN, "Encounter.reasonCode"),
        SearchParamDef(
          "reason-reference",
          Enumerations.SearchParamType.REFERENCE,
          "Encounter.reasonReference"
        ),
        SearchParamDef(
          "service-provider",
          Enumerations.SearchParamType.REFERENCE,
          "Encounter.serviceProvider"
        ),
        SearchParamDef(
          "special-arrangement",
          Enumerations.SearchParamType.TOKEN,
          "Encounter.hospitalization.specialArrangement"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Encounter.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Encounter.subject"),
      )
    "Substance" ->
      listOf(
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "Substance.category"),
        SearchParamDef(
          "code",
          Enumerations.SearchParamType.TOKEN,
          "Substance.code | (Substance.ingredient.substance as CodeableConcept)"
        ),
        SearchParamDef(
          "container-identifier",
          Enumerations.SearchParamType.TOKEN,
          "Substance.instance.identifier"
        ),
        SearchParamDef("expiry", Enumerations.SearchParamType.DATE, "Substance.instance.expiry"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Substance.identifier"),
        SearchParamDef(
          "quantity",
          Enumerations.SearchParamType.QUANTITY,
          "Substance.instance.quantity"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Substance.status"),
        SearchParamDef(
          "substance-reference",
          Enumerations.SearchParamType.REFERENCE,
          "(Substance.ingredient.substance as Reference)"
        ),
      )
    "SubstanceSpecification" ->
      listOf(
        SearchParamDef(
          "code",
          Enumerations.SearchParamType.TOKEN,
          "SubstanceSpecification.code.code"
        ),
      )
    "SearchParameter" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(SearchParameter.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(SearchParameter.useContext.value as Quantity) | (SearchParameter.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "SearchParameter.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "SearchParameter.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "SearchParameter.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "SearchParameter.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "SearchParameter.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "SearchParameter.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "SearchParameter.status"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "SearchParameter.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "SearchParameter.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "SearchParameter.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "SearchParameter.useContext"
        ),
        SearchParamDef("base", Enumerations.SearchParamType.TOKEN, "SearchParameter.base"),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "SearchParameter.code"),
        SearchParamDef(
          "component",
          Enumerations.SearchParamType.REFERENCE,
          "SearchParameter.component.definition"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "SearchParameter.derivedFrom"
        ),
        SearchParamDef("target", Enumerations.SearchParamType.TOKEN, "SearchParameter.target"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "SearchParameter.type"),
      )
    "ActivityDefinition" ->
      listOf(
        SearchParamDef(
          "composed-of",
          Enumerations.SearchParamType.REFERENCE,
          "ActivityDefinition.relatedArtifact.where(type='composed-of').resource"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(ActivityDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(ActivityDefinition.useContext.value as Quantity) | (ActivityDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "ActivityDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ActivityDefinition.date"),
        SearchParamDef(
          "depends-on",
          Enumerations.SearchParamType.REFERENCE,
          "ActivityDefinition.relatedArtifact.where(type='depends-on').resource | ActivityDefinition.library"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "ActivityDefinition.relatedArtifact.where(type='derived-from').resource"
        ),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "ActivityDefinition.description"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "ActivityDefinition.effectivePeriod"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ActivityDefinition.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "ActivityDefinition.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "ActivityDefinition.name"),
        SearchParamDef(
          "predecessor",
          Enumerations.SearchParamType.REFERENCE,
          "ActivityDefinition.relatedArtifact.where(type='predecessor').resource"
        ),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "ActivityDefinition.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ActivityDefinition.status"),
        SearchParamDef(
          "successor",
          Enumerations.SearchParamType.REFERENCE,
          "ActivityDefinition.relatedArtifact.where(type='successor').resource"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "ActivityDefinition.title"),
        SearchParamDef("topic", Enumerations.SearchParamType.TOKEN, "ActivityDefinition.topic"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "ActivityDefinition.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "ActivityDefinition.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "ActivityDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "ActivityDefinition.useContext"
        ),
      )
    "Communication" ->
      listOf(
        SearchParamDef("based-on", Enumerations.SearchParamType.REFERENCE, "Communication.basedOn"),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "Communication.category"),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "Communication.encounter"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "Communication.identifier"
        ),
        SearchParamDef(
          "instantiates-canonical",
          Enumerations.SearchParamType.REFERENCE,
          "Communication.instantiatesCanonical"
        ),
        SearchParamDef(
          "instantiates-uri",
          Enumerations.SearchParamType.URI,
          "Communication.instantiatesUri"
        ),
        SearchParamDef("medium", Enumerations.SearchParamType.TOKEN, "Communication.medium"),
        SearchParamDef("part-of", Enumerations.SearchParamType.REFERENCE, "Communication.partOf"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Communication.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("received", Enumerations.SearchParamType.DATE, "Communication.received"),
        SearchParamDef(
          "recipient",
          Enumerations.SearchParamType.REFERENCE,
          "Communication.recipient"
        ),
        SearchParamDef("sender", Enumerations.SearchParamType.REFERENCE, "Communication.sender"),
        SearchParamDef("sent", Enumerations.SearchParamType.DATE, "Communication.sent"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Communication.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Communication.subject"),
      )
    "InsurancePlan" ->
      listOf(
        SearchParamDef(
          "address",
          Enumerations.SearchParamType.STRING,
          "InsurancePlan.contact.address"
        ),
        SearchParamDef(
          "address-city",
          Enumerations.SearchParamType.STRING,
          "InsurancePlan.contact.address.city"
        ),
        SearchParamDef(
          "address-country",
          Enumerations.SearchParamType.STRING,
          "InsurancePlan.contact.address.country"
        ),
        SearchParamDef(
          "address-postalcode",
          Enumerations.SearchParamType.STRING,
          "InsurancePlan.contact.address.postalCode"
        ),
        SearchParamDef(
          "address-state",
          Enumerations.SearchParamType.STRING,
          "InsurancePlan.contact.address.state"
        ),
        SearchParamDef(
          "address-use",
          Enumerations.SearchParamType.TOKEN,
          "InsurancePlan.contact.address.use"
        ),
        SearchParamDef(
          "administered-by",
          Enumerations.SearchParamType.REFERENCE,
          "InsurancePlan.administeredBy"
        ),
        SearchParamDef(
          "endpoint",
          Enumerations.SearchParamType.REFERENCE,
          "InsurancePlan.endpoint"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "InsurancePlan.identifier"
        ),
        SearchParamDef("owned-by", Enumerations.SearchParamType.REFERENCE, "InsurancePlan.ownedBy"),
        SearchParamDef("phonetic", Enumerations.SearchParamType.STRING, "InsurancePlan.name"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "InsurancePlan.status"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "InsurancePlan.type"),
      )
    "Linkage" ->
      listOf(
        SearchParamDef("author", Enumerations.SearchParamType.REFERENCE, "Linkage.author"),
        SearchParamDef("item", Enumerations.SearchParamType.REFERENCE, "Linkage.item.resource"),
        SearchParamDef("source", Enumerations.SearchParamType.REFERENCE, "Linkage.item.resource"),
      )
    "ImmunizationEvaluation" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ImmunizationEvaluation.date"),
        SearchParamDef(
          "dose-status",
          Enumerations.SearchParamType.TOKEN,
          "ImmunizationEvaluation.doseStatus"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ImmunizationEvaluation.identifier"
        ),
        SearchParamDef(
          "immunization-event",
          Enumerations.SearchParamType.REFERENCE,
          "ImmunizationEvaluation.immunizationEvent"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "ImmunizationEvaluation.patient"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "ImmunizationEvaluation.status"
        ),
        SearchParamDef(
          "target-disease",
          Enumerations.SearchParamType.TOKEN,
          "ImmunizationEvaluation.targetDisease"
        ),
      )
    "DeviceUseStatement" ->
      listOf(
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceUseStatement.subject"
        ),
        SearchParamDef(
          "device",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceUseStatement.device"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "DeviceUseStatement.identifier"
        ),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceUseStatement.subject"
        ),
      )
    "RequestGroup" ->
      listOf(
        SearchParamDef("author", Enumerations.SearchParamType.REFERENCE, "RequestGroup.author"),
        SearchParamDef("authored", Enumerations.SearchParamType.DATE, "RequestGroup.authoredOn"),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "RequestGroup.code"),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "RequestGroup.encounter"
        ),
        SearchParamDef(
          "group-identifier",
          Enumerations.SearchParamType.TOKEN,
          "RequestGroup.groupIdentifier"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "RequestGroup.identifier"),
        SearchParamDef(
          "instantiates-canonical",
          Enumerations.SearchParamType.REFERENCE,
          "RequestGroup.instantiatesCanonical"
        ),
        SearchParamDef(
          "instantiates-uri",
          Enumerations.SearchParamType.URI,
          "RequestGroup.instantiatesUri"
        ),
        SearchParamDef("intent", Enumerations.SearchParamType.TOKEN, "RequestGroup.intent"),
        SearchParamDef(
          "participant",
          Enumerations.SearchParamType.REFERENCE,
          "RequestGroup.action.participant"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "RequestGroup.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("priority", Enumerations.SearchParamType.TOKEN, "RequestGroup.priority"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "RequestGroup.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "RequestGroup.subject"),
      )
    "DeviceRequest" ->
      listOf(
        SearchParamDef(
          "code",
          Enumerations.SearchParamType.TOKEN,
          "(DeviceRequest.code as CodeableConcept)"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "DeviceRequest.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceRequest.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceRequest.encounter"
        ),
        SearchParamDef(
          "authored-on",
          Enumerations.SearchParamType.DATE,
          "DeviceRequest.authoredOn"
        ),
        SearchParamDef("based-on", Enumerations.SearchParamType.REFERENCE, "DeviceRequest.basedOn"),
        SearchParamDef(
          "device",
          Enumerations.SearchParamType.REFERENCE,
          "(DeviceRequest.code as Reference)"
        ),
        SearchParamDef(
          "event-date",
          Enumerations.SearchParamType.DATE,
          "(DeviceRequest.occurrence as dateTime) | (DeviceRequest.occurrence as Period)"
        ),
        SearchParamDef(
          "group-identifier",
          Enumerations.SearchParamType.TOKEN,
          "DeviceRequest.groupIdentifier"
        ),
        SearchParamDef(
          "instantiates-canonical",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceRequest.instantiatesCanonical"
        ),
        SearchParamDef(
          "instantiates-uri",
          Enumerations.SearchParamType.URI,
          "DeviceRequest.instantiatesUri"
        ),
        SearchParamDef(
          "insurance",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceRequest.insurance"
        ),
        SearchParamDef("intent", Enumerations.SearchParamType.TOKEN, "DeviceRequest.intent"),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceRequest.performer"
        ),
        SearchParamDef(
          "prior-request",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceRequest.priorRequest"
        ),
        SearchParamDef(
          "requester",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceRequest.requester"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "DeviceRequest.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "DeviceRequest.subject"),
      )
    "MessageHeader" ->
      listOf(
        SearchParamDef("author", Enumerations.SearchParamType.REFERENCE, "MessageHeader.author"),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "MessageHeader.response.code"),
        SearchParamDef(
          "destination",
          Enumerations.SearchParamType.STRING,
          "MessageHeader.destination.name"
        ),
        SearchParamDef(
          "destination-uri",
          Enumerations.SearchParamType.URI,
          "MessageHeader.destination.endpoint"
        ),
        SearchParamDef("enterer", Enumerations.SearchParamType.REFERENCE, "MessageHeader.enterer"),
        SearchParamDef("event", Enumerations.SearchParamType.TOKEN, "MessageHeader.event"),
        SearchParamDef("focus", Enumerations.SearchParamType.REFERENCE, "MessageHeader.focus"),
        SearchParamDef(
          "receiver",
          Enumerations.SearchParamType.REFERENCE,
          "MessageHeader.destination.receiver"
        ),
        SearchParamDef(
          "response-id",
          Enumerations.SearchParamType.TOKEN,
          "MessageHeader.response.identifier"
        ),
        SearchParamDef(
          "responsible",
          Enumerations.SearchParamType.REFERENCE,
          "MessageHeader.responsible"
        ),
        SearchParamDef("sender", Enumerations.SearchParamType.REFERENCE, "MessageHeader.sender"),
        SearchParamDef("source", Enumerations.SearchParamType.STRING, "MessageHeader.source.name"),
        SearchParamDef(
          "source-uri",
          Enumerations.SearchParamType.URI,
          "MessageHeader.source.endpoint"
        ),
        SearchParamDef(
          "target",
          Enumerations.SearchParamType.REFERENCE,
          "MessageHeader.destination.target"
        ),
      )
    "ImmunizationRecommendation" ->
      listOf(
        SearchParamDef(
          "date",
          Enumerations.SearchParamType.DATE,
          "ImmunizationRecommendation.date"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ImmunizationRecommendation.identifier"
        ),
        SearchParamDef(
          "information",
          Enumerations.SearchParamType.REFERENCE,
          "ImmunizationRecommendation.recommendation.supportingPatientInformation"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "ImmunizationRecommendation.patient"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "ImmunizationRecommendation.recommendation.forecastStatus"
        ),
        SearchParamDef(
          "support",
          Enumerations.SearchParamType.REFERENCE,
          "ImmunizationRecommendation.recommendation.supportingImmunization"
        ),
        SearchParamDef(
          "target-disease",
          Enumerations.SearchParamType.TOKEN,
          "ImmunizationRecommendation.recommendation.targetDisease"
        ),
        SearchParamDef(
          "vaccine-type",
          Enumerations.SearchParamType.TOKEN,
          "ImmunizationRecommendation.recommendation.vaccineCode"
        ),
      )
    "Provenance" ->
      listOf(
        SearchParamDef("agent", Enumerations.SearchParamType.REFERENCE, "Provenance.agent.who"),
        SearchParamDef("agent-role", Enumerations.SearchParamType.TOKEN, "Provenance.agent.role"),
        SearchParamDef("agent-type", Enumerations.SearchParamType.TOKEN, "Provenance.agent.type"),
        SearchParamDef("entity", Enumerations.SearchParamType.REFERENCE, "Provenance.entity.what"),
        SearchParamDef("location", Enumerations.SearchParamType.REFERENCE, "Provenance.location"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Provenance.target.where(resolve() is Patient)"
        ),
        SearchParamDef("recorded", Enumerations.SearchParamType.DATE, "Provenance.recorded"),
        SearchParamDef(
          "signature-type",
          Enumerations.SearchParamType.TOKEN,
          "Provenance.signature.type"
        ),
        SearchParamDef("target", Enumerations.SearchParamType.REFERENCE, "Provenance.target"),
        SearchParamDef(
          "when",
          Enumerations.SearchParamType.DATE,
          "(Provenance.occurred as dateTime)"
        ),
      )
    "Task" ->
      listOf(
        SearchParamDef("authored-on", Enumerations.SearchParamType.DATE, "Task.authoredOn"),
        SearchParamDef("based-on", Enumerations.SearchParamType.REFERENCE, "Task.basedOn"),
        SearchParamDef(
          "business-status",
          Enumerations.SearchParamType.TOKEN,
          "Task.businessStatus"
        ),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "Task.code"),
        SearchParamDef("encounter", Enumerations.SearchParamType.REFERENCE, "Task.encounter"),
        SearchParamDef("focus", Enumerations.SearchParamType.REFERENCE, "Task.focus"),
        SearchParamDef(
          "group-identifier",
          Enumerations.SearchParamType.TOKEN,
          "Task.groupIdentifier"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Task.identifier"),
        SearchParamDef("intent", Enumerations.SearchParamType.TOKEN, "Task.intent"),
        SearchParamDef("modified", Enumerations.SearchParamType.DATE, "Task.lastModified"),
        SearchParamDef("owner", Enumerations.SearchParamType.REFERENCE, "Task.owner"),
        SearchParamDef("part-of", Enumerations.SearchParamType.REFERENCE, "Task.partOf"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Task.for.where(resolve() is Patient)"
        ),
        SearchParamDef("performer", Enumerations.SearchParamType.TOKEN, "Task.performerType"),
        SearchParamDef("period", Enumerations.SearchParamType.DATE, "Task.executionPeriod"),
        SearchParamDef("priority", Enumerations.SearchParamType.TOKEN, "Task.priority"),
        SearchParamDef("requester", Enumerations.SearchParamType.REFERENCE, "Task.requester"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Task.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Task.for"),
      )
    "Questionnaire" ->
      listOf(
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "Questionnaire.item.code"),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(Questionnaire.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(Questionnaire.useContext.value as Quantity) | (Questionnaire.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "Questionnaire.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Questionnaire.date"),
        SearchParamDef(
          "definition",
          Enumerations.SearchParamType.URI,
          "Questionnaire.item.definition"
        ),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "Questionnaire.description"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "Questionnaire.effectivePeriod"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "Questionnaire.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "Questionnaire.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "Questionnaire.name"),
        SearchParamDef("publisher", Enumerations.SearchParamType.STRING, "Questionnaire.publisher"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Questionnaire.status"),
        SearchParamDef(
          "subject-type",
          Enumerations.SearchParamType.TOKEN,
          "Questionnaire.subjectType"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "Questionnaire.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "Questionnaire.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "Questionnaire.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "Questionnaire.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "Questionnaire.useContext"
        ),
      )
    "ExplanationOfBenefit" ->
      listOf(
        SearchParamDef(
          "care-team",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.careTeam.provider"
        ),
        SearchParamDef(
          "claim",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.claim"
        ),
        SearchParamDef(
          "coverage",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.insurance.coverage"
        ),
        SearchParamDef(
          "created",
          Enumerations.SearchParamType.DATE,
          "ExplanationOfBenefit.created"
        ),
        SearchParamDef(
          "detail-udi",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.item.detail.udi"
        ),
        SearchParamDef(
          "disposition",
          Enumerations.SearchParamType.STRING,
          "ExplanationOfBenefit.disposition"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.item.encounter"
        ),
        SearchParamDef(
          "enterer",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.enterer"
        ),
        SearchParamDef(
          "facility",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.facility"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ExplanationOfBenefit.identifier"
        ),
        SearchParamDef(
          "item-udi",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.item.udi"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.patient"
        ),
        SearchParamDef(
          "payee",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.payee.party"
        ),
        SearchParamDef(
          "procedure-udi",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.procedure.udi"
        ),
        SearchParamDef(
          "provider",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.provider"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ExplanationOfBenefit.status"),
        SearchParamDef(
          "subdetail-udi",
          Enumerations.SearchParamType.REFERENCE,
          "ExplanationOfBenefit.item.detail.subDetail.udi"
        ),
      )
    "MedicinalProductPharmaceutical" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MedicinalProductPharmaceutical.identifier"
        ),
        SearchParamDef(
          "route",
          Enumerations.SearchParamType.TOKEN,
          "MedicinalProductPharmaceutical.routeOfAdministration.code"
        ),
        SearchParamDef(
          "target-species",
          Enumerations.SearchParamType.TOKEN,
          "MedicinalProductPharmaceutical.routeOfAdministration.targetSpecies.code"
        ),
      )
    "ResearchStudy" ->
      listOf(
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "ResearchStudy.category"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ResearchStudy.period"),
        SearchParamDef("focus", Enumerations.SearchParamType.TOKEN, "ResearchStudy.focus"),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ResearchStudy.identifier"
        ),
        SearchParamDef("keyword", Enumerations.SearchParamType.TOKEN, "ResearchStudy.keyword"),
        SearchParamDef("location", Enumerations.SearchParamType.TOKEN, "ResearchStudy.location"),
        SearchParamDef("partof", Enumerations.SearchParamType.REFERENCE, "ResearchStudy.partOf"),
        SearchParamDef(
          "principalinvestigator",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchStudy.principalInvestigator"
        ),
        SearchParamDef(
          "protocol",
          Enumerations.SearchParamType.REFERENCE,
          "ResearchStudy.protocol"
        ),
        SearchParamDef("site", Enumerations.SearchParamType.REFERENCE, "ResearchStudy.site"),
        SearchParamDef("sponsor", Enumerations.SearchParamType.REFERENCE, "ResearchStudy.sponsor"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ResearchStudy.status"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "ResearchStudy.title"),
      )
    "Specimen" ->
      listOf(
        SearchParamDef(
          "accession",
          Enumerations.SearchParamType.TOKEN,
          "Specimen.accessionIdentifier"
        ),
        SearchParamDef(
          "bodysite",
          Enumerations.SearchParamType.TOKEN,
          "Specimen.collection.bodySite"
        ),
        SearchParamDef(
          "collected",
          Enumerations.SearchParamType.DATE,
          "Specimen.collection.collected"
        ),
        SearchParamDef(
          "collector",
          Enumerations.SearchParamType.REFERENCE,
          "Specimen.collection.collector"
        ),
        SearchParamDef("container", Enumerations.SearchParamType.TOKEN, "Specimen.container.type"),
        SearchParamDef(
          "container-id",
          Enumerations.SearchParamType.TOKEN,
          "Specimen.container.identifier"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Specimen.identifier"),
        SearchParamDef("parent", Enumerations.SearchParamType.REFERENCE, "Specimen.parent"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Specimen.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Specimen.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Specimen.subject"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Specimen.type"),
      )
    "AllergyIntolerance" ->
      listOf(
        SearchParamDef(
          "asserter",
          Enumerations.SearchParamType.REFERENCE,
          "AllergyIntolerance.asserter"
        ),
        SearchParamDef(
          "category",
          Enumerations.SearchParamType.TOKEN,
          "AllergyIntolerance.category"
        ),
        SearchParamDef(
          "clinical-status",
          Enumerations.SearchParamType.TOKEN,
          "AllergyIntolerance.clinicalStatus"
        ),
        SearchParamDef(
          "code",
          Enumerations.SearchParamType.TOKEN,
          "AllergyIntolerance.code | AllergyIntolerance.reaction.substance"
        ),
        SearchParamDef(
          "criticality",
          Enumerations.SearchParamType.TOKEN,
          "AllergyIntolerance.criticality"
        ),
        SearchParamDef(
          "date",
          Enumerations.SearchParamType.DATE,
          "AllergyIntolerance.recordedDate"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "AllergyIntolerance.identifier"
        ),
        SearchParamDef(
          "last-date",
          Enumerations.SearchParamType.DATE,
          "AllergyIntolerance.lastOccurrence"
        ),
        SearchParamDef(
          "manifestation",
          Enumerations.SearchParamType.TOKEN,
          "AllergyIntolerance.reaction.manifestation"
        ),
        SearchParamDef(
          "onset",
          Enumerations.SearchParamType.DATE,
          "AllergyIntolerance.reaction.onset"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "AllergyIntolerance.patient"
        ),
        SearchParamDef(
          "recorder",
          Enumerations.SearchParamType.REFERENCE,
          "AllergyIntolerance.recorder"
        ),
        SearchParamDef(
          "route",
          Enumerations.SearchParamType.TOKEN,
          "AllergyIntolerance.reaction.exposureRoute"
        ),
        SearchParamDef(
          "severity",
          Enumerations.SearchParamType.TOKEN,
          "AllergyIntolerance.reaction.severity"
        ),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "AllergyIntolerance.type"),
        SearchParamDef(
          "verification-status",
          Enumerations.SearchParamType.TOKEN,
          "AllergyIntolerance.verificationStatus"
        ),
      )
    "CarePlan" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "CarePlan.period"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "CarePlan.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "CarePlan.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "activity-code",
          Enumerations.SearchParamType.TOKEN,
          "CarePlan.activity.detail.code"
        ),
        SearchParamDef(
          "activity-date",
          Enumerations.SearchParamType.DATE,
          "CarePlan.activity.detail.scheduled"
        ),
        SearchParamDef(
          "activity-reference",
          Enumerations.SearchParamType.REFERENCE,
          "CarePlan.activity.reference"
        ),
        SearchParamDef("based-on", Enumerations.SearchParamType.REFERENCE, "CarePlan.basedOn"),
        SearchParamDef("care-team", Enumerations.SearchParamType.REFERENCE, "CarePlan.careTeam"),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "CarePlan.category"),
        SearchParamDef("condition", Enumerations.SearchParamType.REFERENCE, "CarePlan.addresses"),
        SearchParamDef("encounter", Enumerations.SearchParamType.REFERENCE, "CarePlan.encounter"),
        SearchParamDef("goal", Enumerations.SearchParamType.REFERENCE, "CarePlan.goal"),
        SearchParamDef(
          "instantiates-canonical",
          Enumerations.SearchParamType.REFERENCE,
          "CarePlan.instantiatesCanonical"
        ),
        SearchParamDef(
          "instantiates-uri",
          Enumerations.SearchParamType.URI,
          "CarePlan.instantiatesUri"
        ),
        SearchParamDef("intent", Enumerations.SearchParamType.TOKEN, "CarePlan.intent"),
        SearchParamDef("part-of", Enumerations.SearchParamType.REFERENCE, "CarePlan.partOf"),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "CarePlan.activity.detail.performer"
        ),
        SearchParamDef("replaces", Enumerations.SearchParamType.REFERENCE, "CarePlan.replaces"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "CarePlan.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "CarePlan.subject"),
      )
    "StructureDefinition" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(StructureDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(StructureDefinition.useContext.value as Quantity) | (StructureDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "StructureDefinition.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "StructureDefinition.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "StructureDefinition.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "StructureDefinition.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "StructureDefinition.status"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "StructureDefinition.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "StructureDefinition.url"),
        SearchParamDef(
          "version",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.version"
        ),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "StructureDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "StructureDefinition.useContext"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.identifier"
        ),
        SearchParamDef(
          "abstract",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.abstract"
        ),
        SearchParamDef(
          "base",
          Enumerations.SearchParamType.REFERENCE,
          "StructureDefinition.baseDefinition"
        ),
        SearchParamDef(
          "base-path",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.snapshot.element.base.path | StructureDefinition.differential.element.base.path"
        ),
        SearchParamDef(
          "derivation",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.derivation"
        ),
        SearchParamDef(
          "experimental",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.experimental"
        ),
        SearchParamDef(
          "ext-context",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.context.type"
        ),
        SearchParamDef(
          "keyword",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.keyword"
        ),
        SearchParamDef("kind", Enumerations.SearchParamType.TOKEN, "StructureDefinition.kind"),
        SearchParamDef(
          "path",
          Enumerations.SearchParamType.TOKEN,
          "StructureDefinition.snapshot.element.path | StructureDefinition.differential.element.path"
        ),
        SearchParamDef("type", Enumerations.SearchParamType.URI, "StructureDefinition.type"),
        SearchParamDef(
          "valueset",
          Enumerations.SearchParamType.REFERENCE,
          "StructureDefinition.snapshot.element.binding.valueSet"
        ),
      )
    "EpisodeOfCare" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "EpisodeOfCare.period"),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "EpisodeOfCare.identifier"
        ),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "EpisodeOfCare.patient"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "EpisodeOfCare.type"),
        SearchParamDef(
          "care-manager",
          Enumerations.SearchParamType.REFERENCE,
          "EpisodeOfCare.careManager.where(resolve() is Practitioner)"
        ),
        SearchParamDef(
          "condition",
          Enumerations.SearchParamType.REFERENCE,
          "EpisodeOfCare.diagnosis.condition"
        ),
        SearchParamDef(
          "incoming-referral",
          Enumerations.SearchParamType.REFERENCE,
          "EpisodeOfCare.referralRequest"
        ),
        SearchParamDef(
          "organization",
          Enumerations.SearchParamType.REFERENCE,
          "EpisodeOfCare.managingOrganization"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "EpisodeOfCare.status"),
      )
    "ChargeItemDefinition" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(ChargeItemDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(ChargeItemDefinition.useContext.value as Quantity) | (ChargeItemDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "ChargeItemDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ChargeItemDefinition.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "ChargeItemDefinition.description"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "ChargeItemDefinition.effectivePeriod"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ChargeItemDefinition.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "ChargeItemDefinition.jurisdiction"
        ),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "ChargeItemDefinition.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ChargeItemDefinition.status"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "ChargeItemDefinition.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "ChargeItemDefinition.url"),
        SearchParamDef(
          "version",
          Enumerations.SearchParamType.TOKEN,
          "ChargeItemDefinition.version"
        ),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "ChargeItemDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "ChargeItemDefinition.useContext"
        ),
      )
    "Procedure" ->
      listOf(
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "Procedure.code"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Procedure.performed"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Procedure.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Procedure.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("encounter", Enumerations.SearchParamType.REFERENCE, "Procedure.encounter"),
        SearchParamDef("based-on", Enumerations.SearchParamType.REFERENCE, "Procedure.basedOn"),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "Procedure.category"),
        SearchParamDef(
          "instantiates-canonical",
          Enumerations.SearchParamType.REFERENCE,
          "Procedure.instantiatesCanonical"
        ),
        SearchParamDef(
          "instantiates-uri",
          Enumerations.SearchParamType.URI,
          "Procedure.instantiatesUri"
        ),
        SearchParamDef("location", Enumerations.SearchParamType.REFERENCE, "Procedure.location"),
        SearchParamDef("part-of", Enumerations.SearchParamType.REFERENCE, "Procedure.partOf"),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "Procedure.performer.actor"
        ),
        SearchParamDef("reason-code", Enumerations.SearchParamType.TOKEN, "Procedure.reasonCode"),
        SearchParamDef(
          "reason-reference",
          Enumerations.SearchParamType.REFERENCE,
          "Procedure.reasonReference"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Procedure.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Procedure.subject"),
      )
    "List" ->
      listOf(
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "List.code"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "List.date"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "List.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "List.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("encounter", Enumerations.SearchParamType.REFERENCE, "List.encounter"),
        SearchParamDef("empty-reason", Enumerations.SearchParamType.TOKEN, "List.emptyReason"),
        SearchParamDef("item", Enumerations.SearchParamType.REFERENCE, "List.entry.item"),
        SearchParamDef("notes", Enumerations.SearchParamType.STRING, "List.note.text"),
        SearchParamDef("source", Enumerations.SearchParamType.REFERENCE, "List.source"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "List.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "List.subject"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "List.title"),
      )
    "ConceptMap" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(ConceptMap.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(ConceptMap.useContext.value as Quantity) | (ConceptMap.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "ConceptMap.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ConceptMap.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "ConceptMap.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "ConceptMap.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "ConceptMap.name"),
        SearchParamDef("publisher", Enumerations.SearchParamType.STRING, "ConceptMap.publisher"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ConceptMap.status"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "ConceptMap.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "ConceptMap.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "ConceptMap.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "ConceptMap.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "ConceptMap.useContext"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "ConceptMap.identifier"),
        SearchParamDef(
          "dependson",
          Enumerations.SearchParamType.URI,
          "ConceptMap.group.element.target.dependsOn.property"
        ),
        SearchParamDef(
          "other",
          Enumerations.SearchParamType.REFERENCE,
          "ConceptMap.group.unmapped.url"
        ),
        SearchParamDef(
          "product",
          Enumerations.SearchParamType.URI,
          "ConceptMap.group.element.target.product.property"
        ),
        SearchParamDef(
          "source",
          Enumerations.SearchParamType.REFERENCE,
          "(ConceptMap.source as canonical)"
        ),
        SearchParamDef(
          "source-code",
          Enumerations.SearchParamType.TOKEN,
          "ConceptMap.group.element.code"
        ),
        SearchParamDef(
          "source-system",
          Enumerations.SearchParamType.URI,
          "ConceptMap.group.source"
        ),
        SearchParamDef(
          "source-uri",
          Enumerations.SearchParamType.REFERENCE,
          "(ConceptMap.source as uri)"
        ),
        SearchParamDef(
          "target",
          Enumerations.SearchParamType.REFERENCE,
          "(ConceptMap.target as canonical)"
        ),
        SearchParamDef(
          "target-code",
          Enumerations.SearchParamType.TOKEN,
          "ConceptMap.group.element.target.code"
        ),
        SearchParamDef(
          "target-system",
          Enumerations.SearchParamType.URI,
          "ConceptMap.group.target"
        ),
        SearchParamDef(
          "target-uri",
          Enumerations.SearchParamType.REFERENCE,
          "(ConceptMap.target as uri)"
        ),
      )
    "OperationDefinition" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(OperationDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(OperationDefinition.useContext.value as Quantity) | (OperationDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "OperationDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "OperationDefinition.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "OperationDefinition.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "OperationDefinition.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "OperationDefinition.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "OperationDefinition.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "OperationDefinition.status"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "OperationDefinition.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "OperationDefinition.url"),
        SearchParamDef(
          "version",
          Enumerations.SearchParamType.TOKEN,
          "OperationDefinition.version"
        ),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "OperationDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "OperationDefinition.useContext"
        ),
        SearchParamDef("base", Enumerations.SearchParamType.REFERENCE, "OperationDefinition.base"),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "OperationDefinition.code"),
        SearchParamDef(
          "input-profile",
          Enumerations.SearchParamType.REFERENCE,
          "OperationDefinition.inputProfile"
        ),
        SearchParamDef(
          "instance",
          Enumerations.SearchParamType.TOKEN,
          "OperationDefinition.instance"
        ),
        SearchParamDef("kind", Enumerations.SearchParamType.TOKEN, "OperationDefinition.kind"),
        SearchParamDef(
          "output-profile",
          Enumerations.SearchParamType.REFERENCE,
          "OperationDefinition.outputProfile"
        ),
        SearchParamDef("system", Enumerations.SearchParamType.TOKEN, "OperationDefinition.system"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "OperationDefinition.type"),
      )
    "ValueSet" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(ValueSet.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(ValueSet.useContext.value as Quantity) | (ValueSet.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "ValueSet.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ValueSet.date"),
        SearchParamDef("description", Enumerations.SearchParamType.STRING, "ValueSet.description"),
        SearchParamDef("jurisdiction", Enumerations.SearchParamType.TOKEN, "ValueSet.jurisdiction"),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "ValueSet.name"),
        SearchParamDef("publisher", Enumerations.SearchParamType.STRING, "ValueSet.publisher"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ValueSet.status"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "ValueSet.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "ValueSet.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "ValueSet.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "ValueSet.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "ValueSet.useContext"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "ValueSet.identifier"),
        SearchParamDef(
          "code",
          Enumerations.SearchParamType.TOKEN,
          "ValueSet.expansion.contains.code | ValueSet.compose.include.concept.code"
        ),
        SearchParamDef(
          "expansion",
          Enumerations.SearchParamType.URI,
          "ValueSet.expansion.identifier"
        ),
        SearchParamDef(
          "reference",
          Enumerations.SearchParamType.URI,
          "ValueSet.compose.include.system"
        ),
      )
    "MedicationRequest" ->
      listOf(
        SearchParamDef(
          "code",
          Enumerations.SearchParamType.TOKEN,
          "(MedicationRequest.medication as CodeableConcept)"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MedicationRequest.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationRequest.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "medication",
          Enumerations.SearchParamType.REFERENCE,
          "(MedicationRequest.medication as Reference)"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "MedicationRequest.status"),
        SearchParamDef(
          "authoredon",
          Enumerations.SearchParamType.DATE,
          "MedicationRequest.authoredOn"
        ),
        SearchParamDef(
          "category",
          Enumerations.SearchParamType.TOKEN,
          "MedicationRequest.category"
        ),
        SearchParamDef(
          "date",
          Enumerations.SearchParamType.DATE,
          "MedicationRequest.dosageInstruction.timing.event"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationRequest.encounter"
        ),
        SearchParamDef(
          "intended-dispenser",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationRequest.dispenseRequest.performer"
        ),
        SearchParamDef(
          "intended-performer",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationRequest.performer"
        ),
        SearchParamDef(
          "intended-performertype",
          Enumerations.SearchParamType.TOKEN,
          "MedicationRequest.performerType"
        ),
        SearchParamDef("intent", Enumerations.SearchParamType.TOKEN, "MedicationRequest.intent"),
        SearchParamDef(
          "priority",
          Enumerations.SearchParamType.TOKEN,
          "MedicationRequest.priority"
        ),
        SearchParamDef(
          "requester",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationRequest.requester"
        ),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationRequest.subject"
        ),
      )
    "Immunization" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Immunization.occurrence"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Immunization.identifier"),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "Immunization.patient"),
        SearchParamDef("location", Enumerations.SearchParamType.REFERENCE, "Immunization.location"),
        SearchParamDef("lot-number", Enumerations.SearchParamType.STRING, "Immunization.lotNumber"),
        SearchParamDef(
          "manufacturer",
          Enumerations.SearchParamType.REFERENCE,
          "Immunization.manufacturer"
        ),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "Immunization.performer.actor"
        ),
        SearchParamDef(
          "reaction",
          Enumerations.SearchParamType.REFERENCE,
          "Immunization.reaction.detail"
        ),
        SearchParamDef(
          "reaction-date",
          Enumerations.SearchParamType.DATE,
          "Immunization.reaction.date"
        ),
        SearchParamDef(
          "reason-code",
          Enumerations.SearchParamType.TOKEN,
          "Immunization.reasonCode"
        ),
        SearchParamDef(
          "reason-reference",
          Enumerations.SearchParamType.REFERENCE,
          "Immunization.reasonReference"
        ),
        SearchParamDef(
          "series",
          Enumerations.SearchParamType.STRING,
          "Immunization.protocolApplied.series"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Immunization.status"),
        SearchParamDef(
          "status-reason",
          Enumerations.SearchParamType.TOKEN,
          "Immunization.statusReason"
        ),
        SearchParamDef(
          "target-disease",
          Enumerations.SearchParamType.TOKEN,
          "Immunization.protocolApplied.targetDisease"
        ),
        SearchParamDef(
          "vaccine-code",
          Enumerations.SearchParamType.TOKEN,
          "Immunization.vaccineCode"
        ),
      )
    "EffectEvidenceSynthesis" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(EffectEvidenceSynthesis.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(EffectEvidenceSynthesis.useContext.value as Quantity) | (EffectEvidenceSynthesis.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "EffectEvidenceSynthesis.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "EffectEvidenceSynthesis.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "EffectEvidenceSynthesis.description"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "EffectEvidenceSynthesis.effectivePeriod"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "EffectEvidenceSynthesis.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "EffectEvidenceSynthesis.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "EffectEvidenceSynthesis.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "EffectEvidenceSynthesis.publisher"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "EffectEvidenceSynthesis.status"
        ),
        SearchParamDef(
          "title",
          Enumerations.SearchParamType.STRING,
          "EffectEvidenceSynthesis.title"
        ),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "EffectEvidenceSynthesis.url"),
        SearchParamDef(
          "version",
          Enumerations.SearchParamType.TOKEN,
          "EffectEvidenceSynthesis.version"
        ),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "EffectEvidenceSynthesis.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "EffectEvidenceSynthesis.useContext"
        ),
      )
    "Device" ->
      listOf(
        SearchParamDef(
          "device-name",
          Enumerations.SearchParamType.STRING,
          "Device.deviceName.name | Device.type.coding.display | Device.type.text"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Device.identifier"),
        SearchParamDef("location", Enumerations.SearchParamType.REFERENCE, "Device.location"),
        SearchParamDef("manufacturer", Enumerations.SearchParamType.STRING, "Device.manufacturer"),
        SearchParamDef("model", Enumerations.SearchParamType.STRING, "Device.modelNumber"),
        SearchParamDef("organization", Enumerations.SearchParamType.REFERENCE, "Device.owner"),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "Device.patient"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Device.status"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Device.type"),
        SearchParamDef(
          "udi-carrier",
          Enumerations.SearchParamType.STRING,
          "Device.udiCarrier.carrierHRF"
        ),
        SearchParamDef(
          "udi-di",
          Enumerations.SearchParamType.STRING,
          "Device.udiCarrier.deviceIdentifier"
        ),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "Device.url"),
      )
    "VisionPrescription" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "VisionPrescription.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "VisionPrescription.patient"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "VisionPrescription.encounter"
        ),
        SearchParamDef(
          "datewritten",
          Enumerations.SearchParamType.DATE,
          "VisionPrescription.dateWritten"
        ),
        SearchParamDef(
          "prescriber",
          Enumerations.SearchParamType.REFERENCE,
          "VisionPrescription.prescriber"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "VisionPrescription.status"),
      )
    "Resource" ->
      listOf(
        SearchParamDef("_id", Enumerations.SearchParamType.TOKEN, "Resource.id"),
        SearchParamDef(
          "_lastUpdated",
          Enumerations.SearchParamType.DATE,
          "Resource.meta.lastUpdated"
        ),
        SearchParamDef("_profile", Enumerations.SearchParamType.URI, "Resource.meta.profile"),
        SearchParamDef("_security", Enumerations.SearchParamType.TOKEN, "Resource.meta.security"),
        SearchParamDef("_source", Enumerations.SearchParamType.URI, "Resource.meta.source"),
        SearchParamDef("_tag", Enumerations.SearchParamType.TOKEN, "Resource.meta.tag"),
      )
    "Media" ->
      listOf(
        SearchParamDef("based-on", Enumerations.SearchParamType.REFERENCE, "Media.basedOn"),
        SearchParamDef("created", Enumerations.SearchParamType.DATE, "Media.created"),
        SearchParamDef("device", Enumerations.SearchParamType.REFERENCE, "Media.device"),
        SearchParamDef("encounter", Enumerations.SearchParamType.REFERENCE, "Media.encounter"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Media.identifier"),
        SearchParamDef("modality", Enumerations.SearchParamType.TOKEN, "Media.modality"),
        SearchParamDef("operator", Enumerations.SearchParamType.REFERENCE, "Media.operator"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Media.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("site", Enumerations.SearchParamType.TOKEN, "Media.bodySite"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Media.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Media.subject"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Media.type"),
        SearchParamDef("view", Enumerations.SearchParamType.TOKEN, "Media.view"),
      )
    "MedicinalProductContraindication" ->
      listOf(
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "MedicinalProductContraindication.subject"
        ),
      )
    "EvidenceVariable" ->
      listOf(
        SearchParamDef(
          "composed-of",
          Enumerations.SearchParamType.REFERENCE,
          "EvidenceVariable.relatedArtifact.where(type='composed-of').resource"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(EvidenceVariable.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(EvidenceVariable.useContext.value as Quantity) | (EvidenceVariable.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "EvidenceVariable.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "EvidenceVariable.date"),
        SearchParamDef(
          "depends-on",
          Enumerations.SearchParamType.REFERENCE,
          "EvidenceVariable.relatedArtifact.where(type='depends-on').resource"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "EvidenceVariable.relatedArtifact.where(type='derived-from').resource"
        ),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "EvidenceVariable.description"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "EvidenceVariable.effectivePeriod"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "EvidenceVariable.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "EvidenceVariable.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "EvidenceVariable.name"),
        SearchParamDef(
          "predecessor",
          Enumerations.SearchParamType.REFERENCE,
          "EvidenceVariable.relatedArtifact.where(type='predecessor').resource"
        ),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "EvidenceVariable.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "EvidenceVariable.status"),
        SearchParamDef(
          "successor",
          Enumerations.SearchParamType.REFERENCE,
          "EvidenceVariable.relatedArtifact.where(type='successor').resource"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "EvidenceVariable.title"),
        SearchParamDef("topic", Enumerations.SearchParamType.TOKEN, "EvidenceVariable.topic"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "EvidenceVariable.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "EvidenceVariable.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "EvidenceVariable.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "EvidenceVariable.useContext"
        ),
      )
    "MolecularSequence" ->
      listOf(
        SearchParamDef(
          "chromosome",
          Enumerations.SearchParamType.TOKEN,
          "MolecularSequence.referenceSeq.chromosome"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MolecularSequence.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "MolecularSequence.patient"
        ),
        SearchParamDef(
          "referenceseqid",
          Enumerations.SearchParamType.TOKEN,
          "MolecularSequence.referenceSeq.referenceSeqId"
        ),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "MolecularSequence.type"),
        SearchParamDef(
          "variant-end",
          Enumerations.SearchParamType.NUMBER,
          "MolecularSequence.variant.end"
        ),
        SearchParamDef(
          "variant-start",
          Enumerations.SearchParamType.NUMBER,
          "MolecularSequence.variant.start"
        ),
        SearchParamDef(
          "window-end",
          Enumerations.SearchParamType.NUMBER,
          "MolecularSequence.referenceSeq.windowEnd"
        ),
        SearchParamDef(
          "window-start",
          Enumerations.SearchParamType.NUMBER,
          "MolecularSequence.referenceSeq.windowStart"
        ),
        SearchParamDef(
          "chromosome-variant-coordinate",
          Enumerations.SearchParamType.COMPOSITE,
          "MolecularSequence.variant"
        ),
        SearchParamDef(
          "chromosome-window-coordinate",
          Enumerations.SearchParamType.COMPOSITE,
          "MolecularSequence.referenceSeq"
        ),
        SearchParamDef(
          "referenceseqid-variant-coordinate",
          Enumerations.SearchParamType.COMPOSITE,
          "MolecularSequence.variant"
        ),
        SearchParamDef(
          "referenceseqid-window-coordinate",
          Enumerations.SearchParamType.COMPOSITE,
          "MolecularSequence.referenceSeq"
        ),
      )
    "MedicinalProduct" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MedicinalProduct.identifier"
        ),
        SearchParamDef(
          "name",
          Enumerations.SearchParamType.STRING,
          "MedicinalProduct.name.productName"
        ),
        SearchParamDef(
          "name-language",
          Enumerations.SearchParamType.TOKEN,
          "MedicinalProduct.name.countryLanguage.language"
        ),
      )
    "DeviceMetric" ->
      listOf(
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "DeviceMetric.category"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "DeviceMetric.identifier"),
        SearchParamDef("parent", Enumerations.SearchParamType.REFERENCE, "DeviceMetric.parent"),
        SearchParamDef("source", Enumerations.SearchParamType.REFERENCE, "DeviceMetric.source"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "DeviceMetric.type"),
      )
    "Flag" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Flag.period"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Flag.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("encounter", Enumerations.SearchParamType.REFERENCE, "Flag.encounter"),
        SearchParamDef("author", Enumerations.SearchParamType.REFERENCE, "Flag.author"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Flag.identifier"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Flag.subject"),
      )
    "CodeSystem" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(CodeSystem.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(CodeSystem.useContext.value as Quantity) | (CodeSystem.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "CodeSystem.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "CodeSystem.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "CodeSystem.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "CodeSystem.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "CodeSystem.name"),
        SearchParamDef("publisher", Enumerations.SearchParamType.STRING, "CodeSystem.publisher"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "CodeSystem.status"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "CodeSystem.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "CodeSystem.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "CodeSystem.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "CodeSystem.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "CodeSystem.useContext"
        ),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "CodeSystem.concept.code"),
        SearchParamDef("content-mode", Enumerations.SearchParamType.TOKEN, "CodeSystem.content"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "CodeSystem.identifier"),
        SearchParamDef(
          "language",
          Enumerations.SearchParamType.TOKEN,
          "CodeSystem.concept.designation.language"
        ),
        SearchParamDef(
          "supplements",
          Enumerations.SearchParamType.REFERENCE,
          "CodeSystem.supplements"
        ),
        SearchParamDef("system", Enumerations.SearchParamType.URI, "CodeSystem.url"),
      )
    "RiskEvidenceSynthesis" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(RiskEvidenceSynthesis.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(RiskEvidenceSynthesis.useContext.value as Quantity) | (RiskEvidenceSynthesis.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "RiskEvidenceSynthesis.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "RiskEvidenceSynthesis.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "RiskEvidenceSynthesis.description"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "RiskEvidenceSynthesis.effectivePeriod"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "RiskEvidenceSynthesis.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "RiskEvidenceSynthesis.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "RiskEvidenceSynthesis.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "RiskEvidenceSynthesis.publisher"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "RiskEvidenceSynthesis.status"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "RiskEvidenceSynthesis.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "RiskEvidenceSynthesis.url"),
        SearchParamDef(
          "version",
          Enumerations.SearchParamType.TOKEN,
          "RiskEvidenceSynthesis.version"
        ),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "RiskEvidenceSynthesis.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "RiskEvidenceSynthesis.useContext"
        ),
      )
    "AppointmentResponse" ->
      listOf(
        SearchParamDef(
          "actor",
          Enumerations.SearchParamType.REFERENCE,
          "AppointmentResponse.actor"
        ),
        SearchParamDef(
          "appointment",
          Enumerations.SearchParamType.REFERENCE,
          "AppointmentResponse.appointment"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "AppointmentResponse.identifier"
        ),
        SearchParamDef(
          "location",
          Enumerations.SearchParamType.REFERENCE,
          "AppointmentResponse.actor.where(resolve() is Location)"
        ),
        SearchParamDef(
          "part-status",
          Enumerations.SearchParamType.TOKEN,
          "AppointmentResponse.participantStatus"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "AppointmentResponse.actor.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "practitioner",
          Enumerations.SearchParamType.REFERENCE,
          "AppointmentResponse.actor.where(resolve() is Practitioner)"
        ),
      )
    "StructureMap" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(StructureMap.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(StructureMap.useContext.value as Quantity) | (StructureMap.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "StructureMap.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "StructureMap.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "StructureMap.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "StructureMap.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "StructureMap.name"),
        SearchParamDef("publisher", Enumerations.SearchParamType.STRING, "StructureMap.publisher"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "StructureMap.status"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "StructureMap.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "StructureMap.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "StructureMap.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "StructureMap.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "StructureMap.useContext"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "StructureMap.identifier"),
      )
    "AdverseEvent" ->
      listOf(
        SearchParamDef("actuality", Enumerations.SearchParamType.TOKEN, "AdverseEvent.actuality"),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "AdverseEvent.category"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "AdverseEvent.date"),
        SearchParamDef("event", Enumerations.SearchParamType.TOKEN, "AdverseEvent.event"),
        SearchParamDef("location", Enumerations.SearchParamType.REFERENCE, "AdverseEvent.location"),
        SearchParamDef("recorder", Enumerations.SearchParamType.REFERENCE, "AdverseEvent.recorder"),
        SearchParamDef(
          "resultingcondition",
          Enumerations.SearchParamType.REFERENCE,
          "AdverseEvent.resultingCondition"
        ),
        SearchParamDef(
          "seriousness",
          Enumerations.SearchParamType.TOKEN,
          "AdverseEvent.seriousness"
        ),
        SearchParamDef("severity", Enumerations.SearchParamType.TOKEN, "AdverseEvent.severity"),
        SearchParamDef("study", Enumerations.SearchParamType.REFERENCE, "AdverseEvent.study"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "AdverseEvent.subject"),
        SearchParamDef(
          "substance",
          Enumerations.SearchParamType.REFERENCE,
          "AdverseEvent.suspectEntity.instance"
        ),
      )
    "GuidanceResponse" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "GuidanceResponse.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "GuidanceResponse.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "request",
          Enumerations.SearchParamType.TOKEN,
          "GuidanceResponse.requestIdentifier"
        ),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "GuidanceResponse.subject"
        ),
      )
    "Observation" ->
      listOf(
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "Observation.code"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Observation.effective"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Observation.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Observation.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "Observation.encounter"
        ),
        SearchParamDef("based-on", Enumerations.SearchParamType.REFERENCE, "Observation.basedOn"),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "Observation.category"),
        SearchParamDef(
          "combo-code",
          Enumerations.SearchParamType.TOKEN,
          "Observation.code | Observation.component.code"
        ),
        SearchParamDef(
          "combo-data-absent-reason",
          Enumerations.SearchParamType.TOKEN,
          "Observation.dataAbsentReason | Observation.component.dataAbsentReason"
        ),
        SearchParamDef(
          "combo-value-concept",
          Enumerations.SearchParamType.TOKEN,
          "(Observation.value as CodeableConcept) | (Observation.component.value as CodeableConcept)"
        ),
        SearchParamDef(
          "combo-value-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(Observation.value as Quantity) | (Observation.value as SampledData) | (Observation.component.value as Quantity) | (Observation.component.value as SampledData)"
        ),
        SearchParamDef(
          "component-code",
          Enumerations.SearchParamType.TOKEN,
          "Observation.component.code"
        ),
        SearchParamDef(
          "component-data-absent-reason",
          Enumerations.SearchParamType.TOKEN,
          "Observation.component.dataAbsentReason"
        ),
        SearchParamDef(
          "component-value-concept",
          Enumerations.SearchParamType.TOKEN,
          "(Observation.component.value as CodeableConcept)"
        ),
        SearchParamDef(
          "component-value-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(Observation.component.value as Quantity) | (Observation.component.value as SampledData)"
        ),
        SearchParamDef(
          "data-absent-reason",
          Enumerations.SearchParamType.TOKEN,
          "Observation.dataAbsentReason"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "Observation.derivedFrom"
        ),
        SearchParamDef("device", Enumerations.SearchParamType.REFERENCE, "Observation.device"),
        SearchParamDef("focus", Enumerations.SearchParamType.REFERENCE, "Observation.focus"),
        SearchParamDef(
          "has-member",
          Enumerations.SearchParamType.REFERENCE,
          "Observation.hasMember"
        ),
        SearchParamDef("method", Enumerations.SearchParamType.TOKEN, "Observation.method"),
        SearchParamDef("part-of", Enumerations.SearchParamType.REFERENCE, "Observation.partOf"),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "Observation.performer"
        ),
        SearchParamDef("specimen", Enumerations.SearchParamType.REFERENCE, "Observation.specimen"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Observation.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Observation.subject"),
        SearchParamDef(
          "value-concept",
          Enumerations.SearchParamType.TOKEN,
          "(Observation.value as CodeableConcept)"
        ),
        SearchParamDef(
          "value-date",
          Enumerations.SearchParamType.DATE,
          "(Observation.value as dateTime) | (Observation.value as Period)"
        ),
        SearchParamDef(
          "value-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(Observation.value as Quantity) | (Observation.value as SampledData)"
        ),
        SearchParamDef(
          "value-string",
          Enumerations.SearchParamType.STRING,
          "(Observation.value as string) | (Observation.value as CodeableConcept).text"
        ),
        SearchParamDef("code-value-concept", Enumerations.SearchParamType.COMPOSITE, "Observation"),
        SearchParamDef("code-value-date", Enumerations.SearchParamType.COMPOSITE, "Observation"),
        SearchParamDef(
          "code-value-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "Observation"
        ),
        SearchParamDef("code-value-string", Enumerations.SearchParamType.COMPOSITE, "Observation"),
        SearchParamDef(
          "combo-code-value-concept",
          Enumerations.SearchParamType.COMPOSITE,
          "Observation | Observation.component"
        ),
        SearchParamDef(
          "combo-code-value-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "Observation | Observation.component"
        ),
        SearchParamDef(
          "component-code-value-concept",
          Enumerations.SearchParamType.COMPOSITE,
          "Observation.component"
        ),
        SearchParamDef(
          "component-code-value-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "Observation.component"
        ),
      )
    "MedicationAdministration" ->
      listOf(
        SearchParamDef(
          "code",
          Enumerations.SearchParamType.TOKEN,
          "(MedicationAdministration.medication as CodeableConcept)"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MedicationAdministration.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationAdministration.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationAdministration.context"
        ),
        SearchParamDef(
          "device",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationAdministration.device"
        ),
        SearchParamDef(
          "effective-time",
          Enumerations.SearchParamType.DATE,
          "MedicationAdministration.effective"
        ),
        SearchParamDef(
          "medication",
          Enumerations.SearchParamType.REFERENCE,
          "(MedicationAdministration.medication as Reference)"
        ),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationAdministration.performer.actor"
        ),
        SearchParamDef(
          "reason-given",
          Enumerations.SearchParamType.TOKEN,
          "MedicationAdministration.reasonCode"
        ),
        SearchParamDef(
          "reason-not-given",
          Enumerations.SearchParamType.TOKEN,
          "MedicationAdministration.statusReason"
        ),
        SearchParamDef(
          "request",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationAdministration.request"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "MedicationAdministration.status"
        ),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationAdministration.subject"
        ),
      )
    "EnrollmentResponse" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "EnrollmentResponse.identifier"
        ),
        SearchParamDef(
          "request",
          Enumerations.SearchParamType.REFERENCE,
          "EnrollmentResponse.request"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "EnrollmentResponse.status"),
      )
    "Library" ->
      listOf(
        SearchParamDef(
          "composed-of",
          Enumerations.SearchParamType.REFERENCE,
          "Library.relatedArtifact.where(type='composed-of').resource"
        ),
        SearchParamDef(
          "content-type",
          Enumerations.SearchParamType.TOKEN,
          "Library.content.contentType"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(Library.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(Library.useContext.value as Quantity) | (Library.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "Library.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Library.date"),
        SearchParamDef(
          "depends-on",
          Enumerations.SearchParamType.REFERENCE,
          "Library.relatedArtifact.where(type='depends-on').resource"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "Library.relatedArtifact.where(type='derived-from').resource"
        ),
        SearchParamDef("description", Enumerations.SearchParamType.STRING, "Library.description"),
        SearchParamDef("effective", Enumerations.SearchParamType.DATE, "Library.effectivePeriod"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Library.identifier"),
        SearchParamDef("jurisdiction", Enumerations.SearchParamType.TOKEN, "Library.jurisdiction"),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "Library.name"),
        SearchParamDef(
          "predecessor",
          Enumerations.SearchParamType.REFERENCE,
          "Library.relatedArtifact.where(type='predecessor').resource"
        ),
        SearchParamDef("publisher", Enumerations.SearchParamType.STRING, "Library.publisher"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Library.status"),
        SearchParamDef(
          "successor",
          Enumerations.SearchParamType.REFERENCE,
          "Library.relatedArtifact.where(type='successor').resource"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "Library.title"),
        SearchParamDef("topic", Enumerations.SearchParamType.TOKEN, "Library.topic"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Library.type"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "Library.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "Library.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "Library.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "Library.useContext"
        ),
      )
    "MedicinalProductInteraction" ->
      listOf(
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "MedicinalProductInteraction.subject"
        ),
      )
    "MedicationStatement" ->
      listOf(
        SearchParamDef(
          "code",
          Enumerations.SearchParamType.TOKEN,
          "(MedicationStatement.medication as CodeableConcept)"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MedicationStatement.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationStatement.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "medication",
          Enumerations.SearchParamType.REFERENCE,
          "(MedicationStatement.medication as Reference)"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "MedicationStatement.status"),
        SearchParamDef(
          "category",
          Enumerations.SearchParamType.TOKEN,
          "MedicationStatement.category"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationStatement.context"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "MedicationStatement.effective"
        ),
        SearchParamDef(
          "part-of",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationStatement.partOf"
        ),
        SearchParamDef(
          "source",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationStatement.informationSource"
        ),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationStatement.subject"
        ),
      )
    "CommunicationRequest" ->
      listOf(
        SearchParamDef(
          "authored",
          Enumerations.SearchParamType.DATE,
          "CommunicationRequest.authoredOn"
        ),
        SearchParamDef(
          "based-on",
          Enumerations.SearchParamType.REFERENCE,
          "CommunicationRequest.basedOn"
        ),
        SearchParamDef(
          "category",
          Enumerations.SearchParamType.TOKEN,
          "CommunicationRequest.category"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "CommunicationRequest.encounter"
        ),
        SearchParamDef(
          "group-identifier",
          Enumerations.SearchParamType.TOKEN,
          "CommunicationRequest.groupIdentifier"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "CommunicationRequest.identifier"
        ),
        SearchParamDef("medium", Enumerations.SearchParamType.TOKEN, "CommunicationRequest.medium"),
        SearchParamDef(
          "occurrence",
          Enumerations.SearchParamType.DATE,
          "(CommunicationRequest.occurrence as dateTime)"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "CommunicationRequest.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "priority",
          Enumerations.SearchParamType.TOKEN,
          "CommunicationRequest.priority"
        ),
        SearchParamDef(
          "recipient",
          Enumerations.SearchParamType.REFERENCE,
          "CommunicationRequest.recipient"
        ),
        SearchParamDef(
          "replaces",
          Enumerations.SearchParamType.REFERENCE,
          "CommunicationRequest.replaces"
        ),
        SearchParamDef(
          "requester",
          Enumerations.SearchParamType.REFERENCE,
          "CommunicationRequest.requester"
        ),
        SearchParamDef(
          "sender",
          Enumerations.SearchParamType.REFERENCE,
          "CommunicationRequest.sender"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "CommunicationRequest.status"),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "CommunicationRequest.subject"
        ),
      )
    "TestScript" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(TestScript.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(TestScript.useContext.value as Quantity) | (TestScript.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "TestScript.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "TestScript.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "TestScript.description"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "TestScript.identifier"),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "TestScript.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "TestScript.name"),
        SearchParamDef("publisher", Enumerations.SearchParamType.STRING, "TestScript.publisher"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "TestScript.status"),
        SearchParamDef(
          "testscript-capability",
          Enumerations.SearchParamType.STRING,
          "TestScript.metadata.capability.description"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "TestScript.title"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "TestScript.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "TestScript.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "TestScript.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "TestScript.useContext"
        ),
      )
    "Basic" ->
      listOf(
        SearchParamDef("author", Enumerations.SearchParamType.REFERENCE, "Basic.author"),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "Basic.code"),
        SearchParamDef("created", Enumerations.SearchParamType.DATE, "Basic.created"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Basic.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Basic.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Basic.subject"),
      )
    "TestReport" ->
      listOf(
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "TestReport.identifier"),
        SearchParamDef("issued", Enumerations.SearchParamType.DATE, "TestReport.issued"),
        SearchParamDef(
          "participant",
          Enumerations.SearchParamType.URI,
          "TestReport.participant.uri"
        ),
        SearchParamDef("result", Enumerations.SearchParamType.TOKEN, "TestReport.result"),
        SearchParamDef("tester", Enumerations.SearchParamType.STRING, "TestReport.tester"),
        SearchParamDef(
          "testscript",
          Enumerations.SearchParamType.REFERENCE,
          "TestReport.testScript"
        ),
      )
    "ClaimResponse" ->
      listOf(
        SearchParamDef("created", Enumerations.SearchParamType.DATE, "ClaimResponse.created"),
        SearchParamDef(
          "disposition",
          Enumerations.SearchParamType.STRING,
          "ClaimResponse.disposition"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ClaimResponse.identifier"
        ),
        SearchParamDef("insurer", Enumerations.SearchParamType.REFERENCE, "ClaimResponse.insurer"),
        SearchParamDef("outcome", Enumerations.SearchParamType.TOKEN, "ClaimResponse.outcome"),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "ClaimResponse.patient"),
        SearchParamDef(
          "payment-date",
          Enumerations.SearchParamType.DATE,
          "ClaimResponse.payment.date"
        ),
        SearchParamDef("request", Enumerations.SearchParamType.REFERENCE, "ClaimResponse.request"),
        SearchParamDef(
          "requestor",
          Enumerations.SearchParamType.REFERENCE,
          "ClaimResponse.requestor"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ClaimResponse.status"),
        SearchParamDef("use", Enumerations.SearchParamType.TOKEN, "ClaimResponse.use"),
      )
    "MedicationDispense" ->
      listOf(
        SearchParamDef(
          "code",
          Enumerations.SearchParamType.TOKEN,
          "(MedicationDispense.medication as CodeableConcept)"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MedicationDispense.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationDispense.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "medication",
          Enumerations.SearchParamType.REFERENCE,
          "(MedicationDispense.medication as Reference)"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "MedicationDispense.status"),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationDispense.context"
        ),
        SearchParamDef(
          "destination",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationDispense.destination"
        ),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationDispense.performer.actor"
        ),
        SearchParamDef(
          "prescription",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationDispense.authorizingPrescription"
        ),
        SearchParamDef(
          "receiver",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationDispense.receiver"
        ),
        SearchParamDef(
          "responsibleparty",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationDispense.substitution.responsibleParty"
        ),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationDispense.subject"
        ),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "MedicationDispense.type"),
        SearchParamDef(
          "whenhandedover",
          Enumerations.SearchParamType.DATE,
          "MedicationDispense.whenHandedOver"
        ),
        SearchParamDef(
          "whenprepared",
          Enumerations.SearchParamType.DATE,
          "MedicationDispense.whenPrepared"
        ),
      )
    "DiagnosticReport" ->
      listOf(
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "DiagnosticReport.code"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "DiagnosticReport.effective"),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "DiagnosticReport.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "DiagnosticReport.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "DiagnosticReport.encounter"
        ),
        SearchParamDef(
          "based-on",
          Enumerations.SearchParamType.REFERENCE,
          "DiagnosticReport.basedOn"
        ),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "DiagnosticReport.category"),
        SearchParamDef(
          "conclusion",
          Enumerations.SearchParamType.TOKEN,
          "DiagnosticReport.conclusionCode"
        ),
        SearchParamDef("issued", Enumerations.SearchParamType.DATE, "DiagnosticReport.issued"),
        SearchParamDef(
          "media",
          Enumerations.SearchParamType.REFERENCE,
          "DiagnosticReport.media.link"
        ),
        SearchParamDef(
          "performer",
          Enumerations.SearchParamType.REFERENCE,
          "DiagnosticReport.performer"
        ),
        SearchParamDef("result", Enumerations.SearchParamType.REFERENCE, "DiagnosticReport.result"),
        SearchParamDef(
          "results-interpreter",
          Enumerations.SearchParamType.REFERENCE,
          "DiagnosticReport.resultsInterpreter"
        ),
        SearchParamDef(
          "specimen",
          Enumerations.SearchParamType.REFERENCE,
          "DiagnosticReport.specimen"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "DiagnosticReport.status"),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "DiagnosticReport.subject"
        ),
      )
    "OrganizationAffiliation" ->
      listOf(
        SearchParamDef(
          "active",
          Enumerations.SearchParamType.TOKEN,
          "OrganizationAffiliation.active"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "OrganizationAffiliation.period"),
        SearchParamDef(
          "email",
          Enumerations.SearchParamType.TOKEN,
          "OrganizationAffiliation.telecom.where(system='email')"
        ),
        SearchParamDef(
          "endpoint",
          Enumerations.SearchParamType.REFERENCE,
          "OrganizationAffiliation.endpoint"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "OrganizationAffiliation.identifier"
        ),
        SearchParamDef(
          "location",
          Enumerations.SearchParamType.REFERENCE,
          "OrganizationAffiliation.location"
        ),
        SearchParamDef(
          "network",
          Enumerations.SearchParamType.REFERENCE,
          "OrganizationAffiliation.network"
        ),
        SearchParamDef(
          "participating-organization",
          Enumerations.SearchParamType.REFERENCE,
          "OrganizationAffiliation.participatingOrganization"
        ),
        SearchParamDef(
          "phone",
          Enumerations.SearchParamType.TOKEN,
          "OrganizationAffiliation.telecom.where(system='phone')"
        ),
        SearchParamDef(
          "primary-organization",
          Enumerations.SearchParamType.REFERENCE,
          "OrganizationAffiliation.organization"
        ),
        SearchParamDef("role", Enumerations.SearchParamType.TOKEN, "OrganizationAffiliation.code"),
        SearchParamDef(
          "service",
          Enumerations.SearchParamType.REFERENCE,
          "OrganizationAffiliation.healthcareService"
        ),
        SearchParamDef(
          "specialty",
          Enumerations.SearchParamType.TOKEN,
          "OrganizationAffiliation.specialty"
        ),
        SearchParamDef(
          "telecom",
          Enumerations.SearchParamType.TOKEN,
          "OrganizationAffiliation.telecom"
        ),
      )
    "HealthcareService" ->
      listOf(
        SearchParamDef("active", Enumerations.SearchParamType.TOKEN, "HealthcareService.active"),
        SearchParamDef(
          "characteristic",
          Enumerations.SearchParamType.TOKEN,
          "HealthcareService.characteristic"
        ),
        SearchParamDef(
          "coverage-area",
          Enumerations.SearchParamType.REFERENCE,
          "HealthcareService.coverageArea"
        ),
        SearchParamDef(
          "endpoint",
          Enumerations.SearchParamType.REFERENCE,
          "HealthcareService.endpoint"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "HealthcareService.identifier"
        ),
        SearchParamDef(
          "location",
          Enumerations.SearchParamType.REFERENCE,
          "HealthcareService.location"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "HealthcareService.name"),
        SearchParamDef(
          "organization",
          Enumerations.SearchParamType.REFERENCE,
          "HealthcareService.providedBy"
        ),
        SearchParamDef("program", Enumerations.SearchParamType.TOKEN, "HealthcareService.program"),
        SearchParamDef(
          "service-category",
          Enumerations.SearchParamType.TOKEN,
          "HealthcareService.category"
        ),
        SearchParamDef(
          "service-type",
          Enumerations.SearchParamType.TOKEN,
          "HealthcareService.type"
        ),
        SearchParamDef(
          "specialty",
          Enumerations.SearchParamType.TOKEN,
          "HealthcareService.specialty"
        ),
      )
    "MedicinalProductIndication" ->
      listOf(
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "MedicinalProductIndication.subject"
        ),
      )
    "NutritionOrder" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "NutritionOrder.identifier"
        ),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "NutritionOrder.patient"),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "NutritionOrder.encounter"
        ),
        SearchParamDef(
          "additive",
          Enumerations.SearchParamType.TOKEN,
          "NutritionOrder.enteralFormula.additiveType"
        ),
        SearchParamDef("datetime", Enumerations.SearchParamType.DATE, "NutritionOrder.dateTime"),
        SearchParamDef(
          "formula",
          Enumerations.SearchParamType.TOKEN,
          "NutritionOrder.enteralFormula.baseFormulaType"
        ),
        SearchParamDef(
          "instantiates-canonical",
          Enumerations.SearchParamType.REFERENCE,
          "NutritionOrder.instantiatesCanonical"
        ),
        SearchParamDef(
          "instantiates-uri",
          Enumerations.SearchParamType.URI,
          "NutritionOrder.instantiatesUri"
        ),
        SearchParamDef(
          "oraldiet",
          Enumerations.SearchParamType.TOKEN,
          "NutritionOrder.oralDiet.type"
        ),
        SearchParamDef(
          "provider",
          Enumerations.SearchParamType.REFERENCE,
          "NutritionOrder.orderer"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "NutritionOrder.status"),
        SearchParamDef(
          "supplement",
          Enumerations.SearchParamType.TOKEN,
          "NutritionOrder.supplement.type"
        ),
      )
    "TerminologyCapabilities" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(TerminologyCapabilities.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(TerminologyCapabilities.useContext.value as Quantity) | (TerminologyCapabilities.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "TerminologyCapabilities.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "TerminologyCapabilities.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "TerminologyCapabilities.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "TerminologyCapabilities.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "TerminologyCapabilities.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "TerminologyCapabilities.publisher"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "TerminologyCapabilities.status"
        ),
        SearchParamDef(
          "title",
          Enumerations.SearchParamType.STRING,
          "TerminologyCapabilities.title"
        ),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "TerminologyCapabilities.url"),
        SearchParamDef(
          "version",
          Enumerations.SearchParamType.TOKEN,
          "TerminologyCapabilities.version"
        ),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "TerminologyCapabilities.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "TerminologyCapabilities.useContext"
        ),
      )
    "Evidence" ->
      listOf(
        SearchParamDef(
          "composed-of",
          Enumerations.SearchParamType.REFERENCE,
          "Evidence.relatedArtifact.where(type='composed-of').resource"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(Evidence.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(Evidence.useContext.value as Quantity) | (Evidence.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "Evidence.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Evidence.date"),
        SearchParamDef(
          "depends-on",
          Enumerations.SearchParamType.REFERENCE,
          "Evidence.relatedArtifact.where(type='depends-on').resource"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "Evidence.relatedArtifact.where(type='derived-from').resource"
        ),
        SearchParamDef("description", Enumerations.SearchParamType.STRING, "Evidence.description"),
        SearchParamDef("effective", Enumerations.SearchParamType.DATE, "Evidence.effectivePeriod"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Evidence.identifier"),
        SearchParamDef("jurisdiction", Enumerations.SearchParamType.TOKEN, "Evidence.jurisdiction"),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "Evidence.name"),
        SearchParamDef(
          "predecessor",
          Enumerations.SearchParamType.REFERENCE,
          "Evidence.relatedArtifact.where(type='predecessor').resource"
        ),
        SearchParamDef("publisher", Enumerations.SearchParamType.STRING, "Evidence.publisher"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Evidence.status"),
        SearchParamDef(
          "successor",
          Enumerations.SearchParamType.REFERENCE,
          "Evidence.relatedArtifact.where(type='successor').resource"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "Evidence.title"),
        SearchParamDef("topic", Enumerations.SearchParamType.TOKEN, "Evidence.topic"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "Evidence.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "Evidence.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "Evidence.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "Evidence.useContext"
        ),
      )
    "AuditEvent" ->
      listOf(
        SearchParamDef("action", Enumerations.SearchParamType.TOKEN, "AuditEvent.action"),
        SearchParamDef(
          "address",
          Enumerations.SearchParamType.STRING,
          "AuditEvent.agent.network.address"
        ),
        SearchParamDef("agent", Enumerations.SearchParamType.REFERENCE, "AuditEvent.agent.who"),
        SearchParamDef("agent-name", Enumerations.SearchParamType.STRING, "AuditEvent.agent.name"),
        SearchParamDef("agent-role", Enumerations.SearchParamType.TOKEN, "AuditEvent.agent.role"),
        SearchParamDef("altid", Enumerations.SearchParamType.TOKEN, "AuditEvent.agent.altId"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "AuditEvent.recorded"),
        SearchParamDef("entity", Enumerations.SearchParamType.REFERENCE, "AuditEvent.entity.what"),
        SearchParamDef(
          "entity-name",
          Enumerations.SearchParamType.STRING,
          "AuditEvent.entity.name"
        ),
        SearchParamDef("entity-role", Enumerations.SearchParamType.TOKEN, "AuditEvent.entity.role"),
        SearchParamDef("entity-type", Enumerations.SearchParamType.TOKEN, "AuditEvent.entity.type"),
        SearchParamDef("outcome", Enumerations.SearchParamType.TOKEN, "AuditEvent.outcome"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "AuditEvent.agent.who.where(resolve() is Patient) | AuditEvent.entity.what.where(resolve() is Patient)"
        ),
        SearchParamDef("policy", Enumerations.SearchParamType.URI, "AuditEvent.agent.policy"),
        SearchParamDef("site", Enumerations.SearchParamType.TOKEN, "AuditEvent.source.site"),
        SearchParamDef(
          "source",
          Enumerations.SearchParamType.REFERENCE,
          "AuditEvent.source.observer"
        ),
        SearchParamDef("subtype", Enumerations.SearchParamType.TOKEN, "AuditEvent.subtype"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "AuditEvent.type"),
      )
    "PaymentReconciliation" ->
      listOf(
        SearchParamDef(
          "created",
          Enumerations.SearchParamType.DATE,
          "PaymentReconciliation.created"
        ),
        SearchParamDef(
          "disposition",
          Enumerations.SearchParamType.STRING,
          "PaymentReconciliation.disposition"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "PaymentReconciliation.identifier"
        ),
        SearchParamDef(
          "outcome",
          Enumerations.SearchParamType.TOKEN,
          "PaymentReconciliation.outcome"
        ),
        SearchParamDef(
          "payment-issuer",
          Enumerations.SearchParamType.REFERENCE,
          "PaymentReconciliation.paymentIssuer"
        ),
        SearchParamDef(
          "request",
          Enumerations.SearchParamType.REFERENCE,
          "PaymentReconciliation.request"
        ),
        SearchParamDef(
          "requestor",
          Enumerations.SearchParamType.REFERENCE,
          "PaymentReconciliation.requestor"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "PaymentReconciliation.status"
        ),
      )
    "Condition" ->
      listOf(
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "Condition.code"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Condition.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Condition.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "abatement-age",
          Enumerations.SearchParamType.QUANTITY,
          "Condition.abatement.as(Age) | Condition.abatement.as(Range)"
        ),
        SearchParamDef(
          "abatement-date",
          Enumerations.SearchParamType.DATE,
          "Condition.abatement.as(dateTime) | Condition.abatement.as(Period)"
        ),
        SearchParamDef(
          "abatement-string",
          Enumerations.SearchParamType.STRING,
          "Condition.abatement.as(string)"
        ),
        SearchParamDef("asserter", Enumerations.SearchParamType.REFERENCE, "Condition.asserter"),
        SearchParamDef("body-site", Enumerations.SearchParamType.TOKEN, "Condition.bodySite"),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "Condition.category"),
        SearchParamDef(
          "clinical-status",
          Enumerations.SearchParamType.TOKEN,
          "Condition.clinicalStatus"
        ),
        SearchParamDef("encounter", Enumerations.SearchParamType.REFERENCE, "Condition.encounter"),
        SearchParamDef("evidence", Enumerations.SearchParamType.TOKEN, "Condition.evidence.code"),
        SearchParamDef(
          "evidence-detail",
          Enumerations.SearchParamType.REFERENCE,
          "Condition.evidence.detail"
        ),
        SearchParamDef(
          "onset-age",
          Enumerations.SearchParamType.QUANTITY,
          "Condition.onset.as(Age) | Condition.onset.as(Range)"
        ),
        SearchParamDef(
          "onset-date",
          Enumerations.SearchParamType.DATE,
          "Condition.onset.as(dateTime) | Condition.onset.as(Period)"
        ),
        SearchParamDef(
          "onset-info",
          Enumerations.SearchParamType.STRING,
          "Condition.onset.as(string)"
        ),
        SearchParamDef(
          "recorded-date",
          Enumerations.SearchParamType.DATE,
          "Condition.recordedDate"
        ),
        SearchParamDef("severity", Enumerations.SearchParamType.TOKEN, "Condition.severity"),
        SearchParamDef("stage", Enumerations.SearchParamType.TOKEN, "Condition.stage.summary"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Condition.subject"),
        SearchParamDef(
          "verification-status",
          Enumerations.SearchParamType.TOKEN,
          "Condition.verificationStatus"
        ),
      )
    "SpecimenDefinition" ->
      listOf(
        SearchParamDef(
          "container",
          Enumerations.SearchParamType.TOKEN,
          "SpecimenDefinition.typeTested.container.type"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "SpecimenDefinition.identifier"
        ),
        SearchParamDef(
          "type",
          Enumerations.SearchParamType.TOKEN,
          "SpecimenDefinition.typeCollected"
        ),
      )
    "Composition" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Composition.date"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Composition.identifier"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "Composition.subject.where(resolve() is Patient)"
        ),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Composition.type"),
        SearchParamDef(
          "attester",
          Enumerations.SearchParamType.REFERENCE,
          "Composition.attester.party"
        ),
        SearchParamDef("author", Enumerations.SearchParamType.REFERENCE, "Composition.author"),
        SearchParamDef("category", Enumerations.SearchParamType.TOKEN, "Composition.category"),
        SearchParamDef(
          "confidentiality",
          Enumerations.SearchParamType.TOKEN,
          "Composition.confidentiality"
        ),
        SearchParamDef("context", Enumerations.SearchParamType.TOKEN, "Composition.event.code"),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "Composition.encounter"
        ),
        SearchParamDef(
          "entry",
          Enumerations.SearchParamType.REFERENCE,
          "Composition.section.entry"
        ),
        SearchParamDef("period", Enumerations.SearchParamType.DATE, "Composition.event.period"),
        SearchParamDef(
          "related-id",
          Enumerations.SearchParamType.TOKEN,
          "(Composition.relatesTo.target as Identifier)"
        ),
        SearchParamDef(
          "related-ref",
          Enumerations.SearchParamType.REFERENCE,
          "(Composition.relatesTo.target as Reference)"
        ),
        SearchParamDef("section", Enumerations.SearchParamType.TOKEN, "Composition.section.code"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Composition.status"),
        SearchParamDef("subject", Enumerations.SearchParamType.REFERENCE, "Composition.subject"),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "Composition.title"),
      )
    "DetectedIssue" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "DetectedIssue.identifier"
        ),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "DetectedIssue.patient"),
        SearchParamDef("author", Enumerations.SearchParamType.REFERENCE, "DetectedIssue.author"),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "DetectedIssue.code"),
        SearchParamDef("identified", Enumerations.SearchParamType.DATE, "DetectedIssue.identified"),
        SearchParamDef(
          "implicated",
          Enumerations.SearchParamType.REFERENCE,
          "DetectedIssue.implicated"
        ),
      )
    "Bundle" ->
      listOf(
        SearchParamDef(
          "composition",
          Enumerations.SearchParamType.REFERENCE,
          "Bundle.entry[0].resource"
        ),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Bundle.identifier"),
        SearchParamDef(
          "message",
          Enumerations.SearchParamType.REFERENCE,
          "Bundle.entry[0].resource"
        ),
        SearchParamDef("timestamp", Enumerations.SearchParamType.DATE, "Bundle.timestamp"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Bundle.type"),
      )
    "CompartmentDefinition" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(CompartmentDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(CompartmentDefinition.useContext.value as Quantity) | (CompartmentDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "CompartmentDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "CompartmentDefinition.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "CompartmentDefinition.description"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "CompartmentDefinition.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "CompartmentDefinition.publisher"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "CompartmentDefinition.status"
        ),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "CompartmentDefinition.url"),
        SearchParamDef(
          "version",
          Enumerations.SearchParamType.TOKEN,
          "CompartmentDefinition.version"
        ),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "CompartmentDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "CompartmentDefinition.useContext"
        ),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "CompartmentDefinition.code"),
        SearchParamDef(
          "resource",
          Enumerations.SearchParamType.TOKEN,
          "CompartmentDefinition.resource.code"
        ),
      )
    "MedicationKnowledge" ->
      listOf(
        SearchParamDef(
          "classification",
          Enumerations.SearchParamType.TOKEN,
          "MedicationKnowledge.medicineClassification.classification"
        ),
        SearchParamDef(
          "classification-type",
          Enumerations.SearchParamType.TOKEN,
          "MedicationKnowledge.medicineClassification.type"
        ),
        SearchParamDef("code", Enumerations.SearchParamType.TOKEN, "MedicationKnowledge.code"),
        SearchParamDef(
          "doseform",
          Enumerations.SearchParamType.TOKEN,
          "MedicationKnowledge.doseForm"
        ),
        SearchParamDef(
          "ingredient",
          Enumerations.SearchParamType.REFERENCE,
          "(MedicationKnowledge.ingredient.item as Reference)"
        ),
        SearchParamDef(
          "ingredient-code",
          Enumerations.SearchParamType.TOKEN,
          "(MedicationKnowledge.ingredient.item as CodeableConcept)"
        ),
        SearchParamDef(
          "manufacturer",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationKnowledge.manufacturer"
        ),
        SearchParamDef(
          "monitoring-program-name",
          Enumerations.SearchParamType.TOKEN,
          "MedicationKnowledge.monitoringProgram.name"
        ),
        SearchParamDef(
          "monitoring-program-type",
          Enumerations.SearchParamType.TOKEN,
          "MedicationKnowledge.monitoringProgram.type"
        ),
        SearchParamDef(
          "monograph",
          Enumerations.SearchParamType.REFERENCE,
          "MedicationKnowledge.monograph.source"
        ),
        SearchParamDef(
          "monograph-type",
          Enumerations.SearchParamType.TOKEN,
          "MedicationKnowledge.monograph.type"
        ),
        SearchParamDef(
          "source-cost",
          Enumerations.SearchParamType.TOKEN,
          "MedicationKnowledge.cost.source"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "MedicationKnowledge.status"),
      )
    "Patient" ->
      listOf(
        SearchParamDef("active", Enumerations.SearchParamType.TOKEN, "Patient.active"),
        SearchParamDef("address", Enumerations.SearchParamType.STRING, "Patient.address"),
        SearchParamDef("address-city", Enumerations.SearchParamType.STRING, "Patient.address.city"),
        SearchParamDef(
          "address-country",
          Enumerations.SearchParamType.STRING,
          "Patient.address.country"
        ),
        SearchParamDef(
          "address-postalcode",
          Enumerations.SearchParamType.STRING,
          "Patient.address.postalCode"
        ),
        SearchParamDef(
          "address-state",
          Enumerations.SearchParamType.STRING,
          "Patient.address.state"
        ),
        SearchParamDef("address-use", Enumerations.SearchParamType.TOKEN, "Patient.address.use"),
        SearchParamDef("birthdate", Enumerations.SearchParamType.DATE, "Patient.birthDate"),
        SearchParamDef(
          "death-date",
          Enumerations.SearchParamType.DATE,
          "(Patient.deceased as dateTime)"
        ),
        SearchParamDef(
          "deceased",
          Enumerations.SearchParamType.TOKEN,
          "Patient.deceased.exists() and Patient.deceased != false"
        ),
        SearchParamDef(
          "email",
          Enumerations.SearchParamType.TOKEN,
          "Patient.telecom.where(system='email')"
        ),
        SearchParamDef("family", Enumerations.SearchParamType.STRING, "Patient.name.family"),
        SearchParamDef("gender", Enumerations.SearchParamType.TOKEN, "Patient.gender"),
        SearchParamDef(
          "general-practitioner",
          Enumerations.SearchParamType.REFERENCE,
          "Patient.generalPractitioner"
        ),
        SearchParamDef("given", Enumerations.SearchParamType.STRING, "Patient.name.given"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Patient.identifier"),
        SearchParamDef(
          "language",
          Enumerations.SearchParamType.TOKEN,
          "Patient.communication.language"
        ),
        SearchParamDef("link", Enumerations.SearchParamType.REFERENCE, "Patient.link.other"),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "Patient.name"),
        SearchParamDef(
          "organization",
          Enumerations.SearchParamType.REFERENCE,
          "Patient.managingOrganization"
        ),
        SearchParamDef(
          "phone",
          Enumerations.SearchParamType.TOKEN,
          "Patient.telecom.where(system='phone')"
        ),
        SearchParamDef("phonetic", Enumerations.SearchParamType.STRING, "Patient.name"),
        SearchParamDef("telecom", Enumerations.SearchParamType.TOKEN, "Patient.telecom"),
      )
    "Coverage" ->
      listOf(
        SearchParamDef(
          "beneficiary",
          Enumerations.SearchParamType.REFERENCE,
          "Coverage.beneficiary"
        ),
        SearchParamDef("class-type", Enumerations.SearchParamType.TOKEN, "Coverage.class.type"),
        SearchParamDef("class-value", Enumerations.SearchParamType.STRING, "Coverage.class.value"),
        SearchParamDef("dependent", Enumerations.SearchParamType.STRING, "Coverage.dependent"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Coverage.identifier"),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "Coverage.beneficiary"),
        SearchParamDef("payor", Enumerations.SearchParamType.REFERENCE, "Coverage.payor"),
        SearchParamDef(
          "policy-holder",
          Enumerations.SearchParamType.REFERENCE,
          "Coverage.policyHolder"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Coverage.status"),
        SearchParamDef("subscriber", Enumerations.SearchParamType.REFERENCE, "Coverage.subscriber"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Coverage.type"),
      )
    "QuestionnaireResponse" ->
      listOf(
        SearchParamDef(
          "author",
          Enumerations.SearchParamType.REFERENCE,
          "QuestionnaireResponse.author"
        ),
        SearchParamDef(
          "authored",
          Enumerations.SearchParamType.DATE,
          "QuestionnaireResponse.authored"
        ),
        SearchParamDef(
          "based-on",
          Enumerations.SearchParamType.REFERENCE,
          "QuestionnaireResponse.basedOn"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "QuestionnaireResponse.encounter"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "QuestionnaireResponse.identifier"
        ),
        SearchParamDef(
          "part-of",
          Enumerations.SearchParamType.REFERENCE,
          "QuestionnaireResponse.partOf"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "QuestionnaireResponse.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "questionnaire",
          Enumerations.SearchParamType.REFERENCE,
          "QuestionnaireResponse.questionnaire"
        ),
        SearchParamDef(
          "source",
          Enumerations.SearchParamType.REFERENCE,
          "QuestionnaireResponse.source"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "QuestionnaireResponse.status"
        ),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "QuestionnaireResponse.subject"
        ),
      )
    "CoverageEligibilityRequest" ->
      listOf(
        SearchParamDef(
          "created",
          Enumerations.SearchParamType.DATE,
          "CoverageEligibilityRequest.created"
        ),
        SearchParamDef(
          "enterer",
          Enumerations.SearchParamType.REFERENCE,
          "CoverageEligibilityRequest.enterer"
        ),
        SearchParamDef(
          "facility",
          Enumerations.SearchParamType.REFERENCE,
          "CoverageEligibilityRequest.facility"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "CoverageEligibilityRequest.identifier"
        ),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "CoverageEligibilityRequest.patient"
        ),
        SearchParamDef(
          "provider",
          Enumerations.SearchParamType.REFERENCE,
          "CoverageEligibilityRequest.provider"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "CoverageEligibilityRequest.status"
        ),
      )
    "NamingSystem" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(NamingSystem.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(NamingSystem.useContext.value as Quantity) | (NamingSystem.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "NamingSystem.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "NamingSystem.date"),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "NamingSystem.description"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "NamingSystem.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "NamingSystem.name"),
        SearchParamDef("publisher", Enumerations.SearchParamType.STRING, "NamingSystem.publisher"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "NamingSystem.status"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "NamingSystem.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "NamingSystem.useContext"
        ),
        SearchParamDef("contact", Enumerations.SearchParamType.STRING, "NamingSystem.contact.name"),
        SearchParamDef("id-type", Enumerations.SearchParamType.TOKEN, "NamingSystem.uniqueId.type"),
        SearchParamDef("kind", Enumerations.SearchParamType.TOKEN, "NamingSystem.kind"),
        SearchParamDef("period", Enumerations.SearchParamType.DATE, "NamingSystem.uniqueId.period"),
        SearchParamDef(
          "responsible",
          Enumerations.SearchParamType.STRING,
          "NamingSystem.responsible"
        ),
        SearchParamDef(
          "telecom",
          Enumerations.SearchParamType.TOKEN,
          "NamingSystem.contact.telecom"
        ),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "NamingSystem.type"),
        SearchParamDef("value", Enumerations.SearchParamType.STRING, "NamingSystem.uniqueId.value"),
      )
    "MedicinalProductUndesirableEffect" ->
      listOf(
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "MedicinalProductUndesirableEffect.subject"
        ),
      )
    "ExampleScenario" ->
      listOf(
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(ExampleScenario.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(ExampleScenario.useContext.value as Quantity) | (ExampleScenario.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "ExampleScenario.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ExampleScenario.date"),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ExampleScenario.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "ExampleScenario.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "ExampleScenario.name"),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "ExampleScenario.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ExampleScenario.status"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "ExampleScenario.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "ExampleScenario.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "ExampleScenario.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "ExampleScenario.useContext"
        ),
      )
    "SupplyDelivery" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "SupplyDelivery.identifier"
        ),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "SupplyDelivery.patient"),
        SearchParamDef(
          "receiver",
          Enumerations.SearchParamType.REFERENCE,
          "SupplyDelivery.receiver"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "SupplyDelivery.status"),
        SearchParamDef(
          "supplier",
          Enumerations.SearchParamType.REFERENCE,
          "SupplyDelivery.supplier"
        ),
      )
    "Schedule" ->
      listOf(
        SearchParamDef("active", Enumerations.SearchParamType.TOKEN, "Schedule.active"),
        SearchParamDef("actor", Enumerations.SearchParamType.REFERENCE, "Schedule.actor"),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "Schedule.planningHorizon"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Schedule.identifier"),
        SearchParamDef(
          "service-category",
          Enumerations.SearchParamType.TOKEN,
          "Schedule.serviceCategory"
        ),
        SearchParamDef("service-type", Enumerations.SearchParamType.TOKEN, "Schedule.serviceType"),
        SearchParamDef("specialty", Enumerations.SearchParamType.TOKEN, "Schedule.specialty"),
      )
    "ClinicalImpression" ->
      listOf(
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "ClinicalImpression.date"),
        SearchParamDef(
          "patient",
          Enumerations.SearchParamType.REFERENCE,
          "ClinicalImpression.subject.where(resolve() is Patient)"
        ),
        SearchParamDef(
          "assessor",
          Enumerations.SearchParamType.REFERENCE,
          "ClinicalImpression.assessor"
        ),
        SearchParamDef(
          "encounter",
          Enumerations.SearchParamType.REFERENCE,
          "ClinicalImpression.encounter"
        ),
        SearchParamDef(
          "finding-code",
          Enumerations.SearchParamType.TOKEN,
          "ClinicalImpression.finding.itemCodeableConcept"
        ),
        SearchParamDef(
          "finding-ref",
          Enumerations.SearchParamType.REFERENCE,
          "ClinicalImpression.finding.itemReference"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "ClinicalImpression.identifier"
        ),
        SearchParamDef(
          "investigation",
          Enumerations.SearchParamType.REFERENCE,
          "ClinicalImpression.investigation.item"
        ),
        SearchParamDef(
          "previous",
          Enumerations.SearchParamType.REFERENCE,
          "ClinicalImpression.previous"
        ),
        SearchParamDef(
          "problem",
          Enumerations.SearchParamType.REFERENCE,
          "ClinicalImpression.problem"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "ClinicalImpression.status"),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "ClinicalImpression.subject"
        ),
        SearchParamDef(
          "supporting-info",
          Enumerations.SearchParamType.REFERENCE,
          "ClinicalImpression.supportingInfo"
        ),
      )
    "DeviceDefinition" ->
      listOf(
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "DeviceDefinition.identifier"
        ),
        SearchParamDef(
          "parent",
          Enumerations.SearchParamType.REFERENCE,
          "DeviceDefinition.parentDevice"
        ),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "DeviceDefinition.type"),
      )
    "PlanDefinition" ->
      listOf(
        SearchParamDef(
          "composed-of",
          Enumerations.SearchParamType.REFERENCE,
          "PlanDefinition.relatedArtifact.where(type='composed-of').resource"
        ),
        SearchParamDef(
          "context",
          Enumerations.SearchParamType.TOKEN,
          "(PlanDefinition.useContext.value as CodeableConcept)"
        ),
        SearchParamDef(
          "context-quantity",
          Enumerations.SearchParamType.QUANTITY,
          "(PlanDefinition.useContext.value as Quantity) | (PlanDefinition.useContext.value as Range)"
        ),
        SearchParamDef(
          "context-type",
          Enumerations.SearchParamType.TOKEN,
          "PlanDefinition.useContext.code"
        ),
        SearchParamDef("date", Enumerations.SearchParamType.DATE, "PlanDefinition.date"),
        SearchParamDef(
          "definition",
          Enumerations.SearchParamType.REFERENCE,
          "PlanDefinition.action.definition"
        ),
        SearchParamDef(
          "depends-on",
          Enumerations.SearchParamType.REFERENCE,
          "PlanDefinition.relatedArtifact.where(type='depends-on').resource | PlanDefinition.library"
        ),
        SearchParamDef(
          "derived-from",
          Enumerations.SearchParamType.REFERENCE,
          "PlanDefinition.relatedArtifact.where(type='derived-from').resource"
        ),
        SearchParamDef(
          "description",
          Enumerations.SearchParamType.STRING,
          "PlanDefinition.description"
        ),
        SearchParamDef(
          "effective",
          Enumerations.SearchParamType.DATE,
          "PlanDefinition.effectivePeriod"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "PlanDefinition.identifier"
        ),
        SearchParamDef(
          "jurisdiction",
          Enumerations.SearchParamType.TOKEN,
          "PlanDefinition.jurisdiction"
        ),
        SearchParamDef("name", Enumerations.SearchParamType.STRING, "PlanDefinition.name"),
        SearchParamDef(
          "predecessor",
          Enumerations.SearchParamType.REFERENCE,
          "PlanDefinition.relatedArtifact.where(type='predecessor').resource"
        ),
        SearchParamDef(
          "publisher",
          Enumerations.SearchParamType.STRING,
          "PlanDefinition.publisher"
        ),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "PlanDefinition.status"),
        SearchParamDef(
          "successor",
          Enumerations.SearchParamType.REFERENCE,
          "PlanDefinition.relatedArtifact.where(type='successor').resource"
        ),
        SearchParamDef("title", Enumerations.SearchParamType.STRING, "PlanDefinition.title"),
        SearchParamDef("topic", Enumerations.SearchParamType.TOKEN, "PlanDefinition.topic"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "PlanDefinition.type"),
        SearchParamDef("url", Enumerations.SearchParamType.URI, "PlanDefinition.url"),
        SearchParamDef("version", Enumerations.SearchParamType.TOKEN, "PlanDefinition.version"),
        SearchParamDef(
          "context-type-quantity",
          Enumerations.SearchParamType.COMPOSITE,
          "PlanDefinition.useContext"
        ),
        SearchParamDef(
          "context-type-value",
          Enumerations.SearchParamType.COMPOSITE,
          "PlanDefinition.useContext"
        ),
      )
    "MedicinalProductAuthorization" ->
      listOf(
        SearchParamDef(
          "country",
          Enumerations.SearchParamType.TOKEN,
          "MedicinalProductAuthorization.country"
        ),
        SearchParamDef(
          "holder",
          Enumerations.SearchParamType.REFERENCE,
          "MedicinalProductAuthorization.holder"
        ),
        SearchParamDef(
          "identifier",
          Enumerations.SearchParamType.TOKEN,
          "MedicinalProductAuthorization.identifier"
        ),
        SearchParamDef(
          "status",
          Enumerations.SearchParamType.TOKEN,
          "MedicinalProductAuthorization.status"
        ),
        SearchParamDef(
          "subject",
          Enumerations.SearchParamType.REFERENCE,
          "MedicinalProductAuthorization.subject"
        ),
      )
    "Claim" ->
      listOf(
        SearchParamDef(
          "care-team",
          Enumerations.SearchParamType.REFERENCE,
          "Claim.careTeam.provider"
        ),
        SearchParamDef("created", Enumerations.SearchParamType.DATE, "Claim.created"),
        SearchParamDef(
          "detail-udi",
          Enumerations.SearchParamType.REFERENCE,
          "Claim.item.detail.udi"
        ),
        SearchParamDef("encounter", Enumerations.SearchParamType.REFERENCE, "Claim.item.encounter"),
        SearchParamDef("enterer", Enumerations.SearchParamType.REFERENCE, "Claim.enterer"),
        SearchParamDef("facility", Enumerations.SearchParamType.REFERENCE, "Claim.facility"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Claim.identifier"),
        SearchParamDef("insurer", Enumerations.SearchParamType.REFERENCE, "Claim.insurer"),
        SearchParamDef("item-udi", Enumerations.SearchParamType.REFERENCE, "Claim.item.udi"),
        SearchParamDef("patient", Enumerations.SearchParamType.REFERENCE, "Claim.patient"),
        SearchParamDef("payee", Enumerations.SearchParamType.REFERENCE, "Claim.payee.party"),
        SearchParamDef("priority", Enumerations.SearchParamType.TOKEN, "Claim.priority"),
        SearchParamDef(
          "procedure-udi",
          Enumerations.SearchParamType.REFERENCE,
          "Claim.procedure.udi"
        ),
        SearchParamDef("provider", Enumerations.SearchParamType.REFERENCE, "Claim.provider"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Claim.status"),
        SearchParamDef(
          "subdetail-udi",
          Enumerations.SearchParamType.REFERENCE,
          "Claim.item.detail.subDetail.udi"
        ),
        SearchParamDef("use", Enumerations.SearchParamType.TOKEN, "Claim.use"),
      )
    "Location" ->
      listOf(
        SearchParamDef("address", Enumerations.SearchParamType.STRING, "Location.address"),
        SearchParamDef(
          "address-city",
          Enumerations.SearchParamType.STRING,
          "Location.address.city"
        ),
        SearchParamDef(
          "address-country",
          Enumerations.SearchParamType.STRING,
          "Location.address.country"
        ),
        SearchParamDef(
          "address-postalcode",
          Enumerations.SearchParamType.STRING,
          "Location.address.postalCode"
        ),
        SearchParamDef(
          "address-state",
          Enumerations.SearchParamType.STRING,
          "Location.address.state"
        ),
        SearchParamDef("address-use", Enumerations.SearchParamType.TOKEN, "Location.address.use"),
        SearchParamDef("endpoint", Enumerations.SearchParamType.REFERENCE, "Location.endpoint"),
        SearchParamDef("identifier", Enumerations.SearchParamType.TOKEN, "Location.identifier"),
        SearchParamDef(
          "name",
          Enumerations.SearchParamType.STRING,
          "Location.name | Location.alias"
        ),
        SearchParamDef("near", Enumerations.SearchParamType.SPECIAL, "Location.position"),
        SearchParamDef(
          "operational-status",
          Enumerations.SearchParamType.TOKEN,
          "Location.operationalStatus"
        ),
        SearchParamDef(
          "organization",
          Enumerations.SearchParamType.REFERENCE,
          "Location.managingOrganization"
        ),
        SearchParamDef("partof", Enumerations.SearchParamType.REFERENCE, "Location.partOf"),
        SearchParamDef("status", Enumerations.SearchParamType.TOKEN, "Location.status"),
        SearchParamDef("type", Enumerations.SearchParamType.TOKEN, "Location.type"),
      )
    else -> emptyList()
  }
}
