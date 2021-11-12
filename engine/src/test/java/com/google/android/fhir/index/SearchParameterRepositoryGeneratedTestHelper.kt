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
import org.hl7.fhir.r4.model.Account
import org.hl7.fhir.r4.model.ActivityDefinition
import org.hl7.fhir.r4.model.AdverseEvent
import org.hl7.fhir.r4.model.AllergyIntolerance
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.AppointmentResponse
import org.hl7.fhir.r4.model.AuditEvent
import org.hl7.fhir.r4.model.Basic
import org.hl7.fhir.r4.model.BodyStructure
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CapabilityStatement
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.CareTeam
import org.hl7.fhir.r4.model.ChargeItem
import org.hl7.fhir.r4.model.ChargeItemDefinition
import org.hl7.fhir.r4.model.Claim
import org.hl7.fhir.r4.model.ClaimResponse
import org.hl7.fhir.r4.model.ClinicalImpression
import org.hl7.fhir.r4.model.CodeSystem
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.CompartmentDefinition
import org.hl7.fhir.r4.model.Composition
import org.hl7.fhir.r4.model.ConceptMap
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Consent
import org.hl7.fhir.r4.model.Contract
import org.hl7.fhir.r4.model.Coverage
import org.hl7.fhir.r4.model.CoverageEligibilityRequest
import org.hl7.fhir.r4.model.CoverageEligibilityResponse
import org.hl7.fhir.r4.model.DetectedIssue
import org.hl7.fhir.r4.model.Device
import org.hl7.fhir.r4.model.DeviceDefinition
import org.hl7.fhir.r4.model.DeviceMetric
import org.hl7.fhir.r4.model.DeviceRequest
import org.hl7.fhir.r4.model.DeviceUseStatement
import org.hl7.fhir.r4.model.DiagnosticReport
import org.hl7.fhir.r4.model.DocumentManifest
import org.hl7.fhir.r4.model.DocumentReference
import org.hl7.fhir.r4.model.EffectEvidenceSynthesis
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Endpoint
import org.hl7.fhir.r4.model.EnrollmentRequest
import org.hl7.fhir.r4.model.EnrollmentResponse
import org.hl7.fhir.r4.model.EpisodeOfCare
import org.hl7.fhir.r4.model.EventDefinition
import org.hl7.fhir.r4.model.Evidence
import org.hl7.fhir.r4.model.EvidenceVariable
import org.hl7.fhir.r4.model.ExampleScenario
import org.hl7.fhir.r4.model.ExplanationOfBenefit
import org.hl7.fhir.r4.model.FamilyMemberHistory
import org.hl7.fhir.r4.model.Flag
import org.hl7.fhir.r4.model.Goal
import org.hl7.fhir.r4.model.GraphDefinition
import org.hl7.fhir.r4.model.Group
import org.hl7.fhir.r4.model.GuidanceResponse
import org.hl7.fhir.r4.model.HealthcareService
import org.hl7.fhir.r4.model.ImagingStudy
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.ImmunizationEvaluation
import org.hl7.fhir.r4.model.ImmunizationRecommendation
import org.hl7.fhir.r4.model.ImplementationGuide
import org.hl7.fhir.r4.model.Invoice
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Linkage
import org.hl7.fhir.r4.model.ListResource
import org.hl7.fhir.r4.model.Location
import org.hl7.fhir.r4.model.Measure
import org.hl7.fhir.r4.model.MeasureReport
import org.hl7.fhir.r4.model.Media
import org.hl7.fhir.r4.model.Medication
import org.hl7.fhir.r4.model.MedicationAdministration
import org.hl7.fhir.r4.model.MedicationDispense
import org.hl7.fhir.r4.model.MedicationKnowledge
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.MedicationStatement
import org.hl7.fhir.r4.model.MedicinalProduct
import org.hl7.fhir.r4.model.MedicinalProductAuthorization
import org.hl7.fhir.r4.model.MedicinalProductContraindication
import org.hl7.fhir.r4.model.MedicinalProductIndication
import org.hl7.fhir.r4.model.MedicinalProductInteraction
import org.hl7.fhir.r4.model.MedicinalProductPackaged
import org.hl7.fhir.r4.model.MedicinalProductPharmaceutical
import org.hl7.fhir.r4.model.MedicinalProductUndesirableEffect
import org.hl7.fhir.r4.model.MessageDefinition
import org.hl7.fhir.r4.model.MessageHeader
import org.hl7.fhir.r4.model.MolecularSequence
import org.hl7.fhir.r4.model.NamingSystem
import org.hl7.fhir.r4.model.NutritionOrder
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.OperationDefinition
import org.hl7.fhir.r4.model.Organization
import org.hl7.fhir.r4.model.OrganizationAffiliation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.PaymentNotice
import org.hl7.fhir.r4.model.PaymentReconciliation
import org.hl7.fhir.r4.model.Person
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Practitioner
import org.hl7.fhir.r4.model.PractitionerRole
import org.hl7.fhir.r4.model.Procedure
import org.hl7.fhir.r4.model.Provenance
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.RelatedPerson
import org.hl7.fhir.r4.model.RequestGroup
import org.hl7.fhir.r4.model.ResearchDefinition
import org.hl7.fhir.r4.model.ResearchElementDefinition
import org.hl7.fhir.r4.model.ResearchStudy
import org.hl7.fhir.r4.model.ResearchSubject
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.RiskAssessment
import org.hl7.fhir.r4.model.RiskEvidenceSynthesis
import org.hl7.fhir.r4.model.Schedule
import org.hl7.fhir.r4.model.SearchParameter
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.Slot
import org.hl7.fhir.r4.model.Specimen
import org.hl7.fhir.r4.model.SpecimenDefinition
import org.hl7.fhir.r4.model.StructureDefinition
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.model.Subscription
import org.hl7.fhir.r4.model.Substance
import org.hl7.fhir.r4.model.SubstanceSpecification
import org.hl7.fhir.r4.model.SupplyDelivery
import org.hl7.fhir.r4.model.SupplyRequest
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.model.TerminologyCapabilities
import org.hl7.fhir.r4.model.TestReport
import org.hl7.fhir.r4.model.TestScript
import org.hl7.fhir.r4.model.ValueSet
import org.hl7.fhir.r4.model.VerificationResult
import org.hl7.fhir.r4.model.VisionPrescription

/**
 * This File is Generated from com.google.android.fhir.codegen.SearchParameterRepositoryGenerator
 * all changes to this file must be made through the aforementioned file only
 */
internal fun getAllResources(): List<Resource> {
  val resourceList =
    listOf<Resource>(
      Appointment(),
      Account(),
      Invoice(),
      EventDefinition(),
      DocumentManifest(),
      MessageDefinition(),
      Goal(),
      MedicinalProductPackaged(),
      Endpoint(),
      EnrollmentRequest(),
      Consent(),
      Medication(),
      CapabilityStatement(),
      Measure(),
      ResearchSubject(),
      Subscription(),
      DocumentReference(),
      GraphDefinition(),
      CoverageEligibilityResponse(),
      MeasureReport(),
      PractitionerRole(),
      ServiceRequest(),
      RelatedPerson(),
      SupplyRequest(),
      Practitioner(),
      VerificationResult(),
      BodyStructure(),
      Slot(),
      Contract(),
      Person(),
      RiskAssessment(),
      Group(),
      PaymentNotice(),
      ResearchDefinition(),
      Organization(),
      CareTeam(),
      ImplementationGuide(),
      ImagingStudy(),
      FamilyMemberHistory(),
      ChargeItem(),
      ResearchElementDefinition(),
      Encounter(),
      Substance(),
      SubstanceSpecification(),
      SearchParameter(),
      ActivityDefinition(),
      Communication(),
      Linkage(),
      ImmunizationEvaluation(),
      DeviceUseStatement(),
      RequestGroup(),
      DeviceRequest(),
      MessageHeader(),
      ImmunizationRecommendation(),
      Provenance(),
      Task(),
      Questionnaire(),
      ExplanationOfBenefit(),
      MedicinalProductPharmaceutical(),
      ResearchStudy(),
      Specimen(),
      AllergyIntolerance(),
      CarePlan(),
      StructureDefinition(),
      EpisodeOfCare(),
      ChargeItemDefinition(),
      Procedure(),
      ListResource(),
      ConceptMap(),
      OperationDefinition(),
      ValueSet(),
      MedicationRequest(),
      Immunization(),
      EffectEvidenceSynthesis(),
      Device(),
      VisionPrescription(),
      Media(),
      MedicinalProductContraindication(),
      EvidenceVariable(),
      MolecularSequence(),
      MedicinalProduct(),
      DeviceMetric(),
      Flag(),
      CodeSystem(),
      RiskEvidenceSynthesis(),
      AppointmentResponse(),
      StructureMap(),
      AdverseEvent(),
      GuidanceResponse(),
      Observation(),
      MedicationAdministration(),
      EnrollmentResponse(),
      Library(),
      MedicinalProductInteraction(),
      MedicationStatement(),
      CommunicationRequest(),
      TestScript(),
      Basic(),
      TestReport(),
      ClaimResponse(),
      MedicationDispense(),
      DiagnosticReport(),
      OrganizationAffiliation(),
      HealthcareService(),
      MedicinalProductIndication(),
      NutritionOrder(),
      TerminologyCapabilities(),
      Evidence(),
      AuditEvent(),
      PaymentReconciliation(),
      Condition(),
      SpecimenDefinition(),
      Composition(),
      DetectedIssue(),
      Bundle(),
      CompartmentDefinition(),
      MedicationKnowledge(),
      Patient(),
      Coverage(),
      QuestionnaireResponse(),
      CoverageEligibilityRequest(),
      NamingSystem(),
      MedicinalProductUndesirableEffect(),
      ExampleScenario(),
      SupplyDelivery(),
      Schedule(),
      ClinicalImpression(),
      DeviceDefinition(),
      PlanDefinition(),
      MedicinalProductAuthorization(),
      Claim(),
      Location(),
    )
  return resourceList
}
