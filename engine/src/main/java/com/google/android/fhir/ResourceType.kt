/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir

import org.hl7.fhir.exceptions.FHIRException

enum class ResourceType {
  Account,
  ActivityDefinition,
  AdverseEvent,
  AllergyIntolerance,
  Appointment,
  AppointmentResponse,
  AuditEvent,
  Basic,
  Binary,
  BiologicallyDerivedProduct,
  BodyStructure,
  Bundle,
  CapabilityStatement,
  CarePlan,
  CareTeam,
  CatalogEntry,
  ChargeItem,
  ChargeItemDefinition,
  Claim,
  ClaimResponse,
  ClinicalImpression,
  CodeSystem,
  Communication,
  CommunicationRequest,
  CompartmentDefinition,
  Composition,
  ConceptMap,
  Condition,
  Consent,
  Contract,
  Coverage,
  CoverageEligibilityRequest,
  CoverageEligibilityResponse,
  DetectedIssue,
  Device,
  DeviceDefinition,
  DeviceMetric,
  DeviceRequest,
  DeviceUseStatement,
  DiagnosticReport,
  DocumentManifest,
  DocumentReference,
  EffectEvidenceSynthesis,
  Encounter,
  Endpoint,
  EnrollmentRequest,
  EnrollmentResponse,
  EpisodeOfCare,
  EventDefinition,
  Evidence,
  EvidenceVariable,
  ExampleScenario,
  ExplanationOfBenefit,
  FamilyMemberHistory,
  Flag,
  Goal,
  GraphDefinition,
  Group,
  GuidanceResponse,
  HealthcareService,
  ImagingStudy,
  Immunization,
  ImmunizationEvaluation,
  ImmunizationRecommendation,
  ImplementationGuide,
  InsurancePlan,
  Invoice,
  Library,
  Linkage,
  List,
  Location,
  Measure,
  MeasureReport,
  Media,
  Medication,
  MedicationAdministration,
  MedicationDispense,
  MedicationKnowledge,
  MedicationRequest,
  MedicationStatement,
  MedicinalProduct,
  MedicinalProductAuthorization,
  MedicinalProductContraindication,
  MedicinalProductIndication,
  MedicinalProductIngredient,
  MedicinalProductInteraction,
  MedicinalProductManufactured,
  MedicinalProductPackaged,
  MedicinalProductPharmaceutical,
  MedicinalProductUndesirableEffect,
  MessageDefinition,
  MessageHeader,
  MolecularSequence,
  NamingSystem,
  NutritionOrder,
  Observation,
  ObservationDefinition,
  OperationDefinition,
  OperationOutcome,
  Organization,
  OrganizationAffiliation,
  Parameters,
  Patient,
  PaymentNotice,
  PaymentReconciliation,
  Person,
  PlanDefinition,
  Practitioner,
  PractitionerRole,
  Procedure,
  Provenance,
  Questionnaire,
  QuestionnaireResponse,
  RelatedPerson,
  RequestGroup,
  ResearchDefinition,
  ResearchElementDefinition,
  ResearchStudy,
  ResearchSubject,
  RiskAssessment,
  RiskEvidenceSynthesis,
  Schedule,
  SearchParameter,
  ServiceRequest,
  Slot,
  Specimen,
  SpecimenDefinition,
  StructureDefinition,
  StructureMap,
  Subscription,
  Substance,
  SubstanceNucleicAcid,
  SubstancePolymer,
  SubstanceProtein,
  SubstanceReferenceInformation,
  SubstanceSourceMaterial,
  SubstanceSpecification,
  SupplyDelivery,
  SupplyRequest,
  Task,
  TerminologyCapabilities,
  TestReport,
  TestScript,
  ValueSet,
  VerificationResult,
  VisionPrescription;

  val path: String
    get() {
      return when (this) {
        Account -> "account"
        ActivityDefinition -> "activitydefinition"
        AdverseEvent -> "adverseevent"
        AllergyIntolerance -> "allergyintolerance"
        Appointment -> "appointment"
        AppointmentResponse -> "appointmentresponse"
        AuditEvent -> "auditevent"
        Basic -> "basic"
        Binary -> "binary"
        BiologicallyDerivedProduct -> "biologicallyderivedproduct"
        BodyStructure -> "bodystructure"
        Bundle -> "bundle"
        CapabilityStatement -> "capabilitystatement"
        CarePlan -> "careplan"
        CareTeam -> "careteam"
        CatalogEntry -> "catalogentry"
        ChargeItem -> "chargeitem"
        ChargeItemDefinition -> "chargeitemdefinition"
        Claim -> "claim"
        ClaimResponse -> "claimresponse"
        ClinicalImpression -> "clinicalimpression"
        CodeSystem -> "codesystem"
        Communication -> "communication"
        CommunicationRequest -> "communicationrequest"
        CompartmentDefinition -> "compartmentdefinition"
        Composition -> "composition"
        ConceptMap -> "conceptmap"
        Condition -> "condition"
        Consent -> "consent"
        Contract -> "contract"
        Coverage -> "coverage"
        CoverageEligibilityRequest -> "coverageeligibilityrequest"
        CoverageEligibilityResponse -> "coverageeligibilityresponse"
        DetectedIssue -> "detectedissue"
        Device -> "device"
        DeviceDefinition -> "devicedefinition"
        DeviceMetric -> "devicemetric"
        DeviceRequest -> "devicerequest"
        DeviceUseStatement -> "deviceusestatement"
        DiagnosticReport -> "diagnosticreport"
        DocumentManifest -> "documentmanifest"
        DocumentReference -> "documentreference"
        EffectEvidenceSynthesis -> "effectevidencesynthesis"
        Encounter -> "encounter"
        Endpoint -> "endpoint"
        EnrollmentRequest -> "enrollmentrequest"
        EnrollmentResponse -> "enrollmentresponse"
        EpisodeOfCare -> "episodeofcare"
        EventDefinition -> "eventdefinition"
        Evidence -> "evidence"
        EvidenceVariable -> "evidencevariable"
        ExampleScenario -> "examplescenario"
        ExplanationOfBenefit -> "explanationofbenefit"
        FamilyMemberHistory -> "familymemberhistory"
        Flag -> "flag"
        Goal -> "goal"
        GraphDefinition -> "graphdefinition"
        Group -> "group"
        GuidanceResponse -> "guidanceresponse"
        HealthcareService -> "healthcareservice"
        ImagingStudy -> "imagingstudy"
        Immunization -> "immunization"
        ImmunizationEvaluation -> "immunizationevaluation"
        ImmunizationRecommendation -> "immunizationrecommendation"
        ImplementationGuide -> "implementationguide"
        InsurancePlan -> "insuranceplan"
        Invoice -> "invoice"
        Library -> "library"
        Linkage -> "linkage"
        List -> "list"
        Location -> "location"
        Measure -> "measure"
        MeasureReport -> "measurereport"
        Media -> "media"
        Medication -> "medication"
        MedicationAdministration -> "medicationadministration"
        MedicationDispense -> "medicationdispense"
        MedicationKnowledge -> "medicationknowledge"
        MedicationRequest -> "medicationrequest"
        MedicationStatement -> "medicationstatement"
        MedicinalProduct -> "medicinalproduct"
        MedicinalProductAuthorization -> "medicinalproductauthorization"
        MedicinalProductContraindication -> "medicinalproductcontraindication"
        MedicinalProductIndication -> "medicinalproductindication"
        MedicinalProductIngredient -> "medicinalproductingredient"
        MedicinalProductInteraction -> "medicinalproductinteraction"
        MedicinalProductManufactured -> "medicinalproductmanufactured"
        MedicinalProductPackaged -> "medicinalproductpackaged"
        MedicinalProductPharmaceutical -> "medicinalproductpharmaceutical"
        MedicinalProductUndesirableEffect -> "medicinalproductundesirableeffect"
        MessageDefinition -> "messagedefinition"
        MessageHeader -> "messageheader"
        MolecularSequence -> "molecularsequence"
        NamingSystem -> "namingsystem"
        NutritionOrder -> "nutritionorder"
        Observation -> "observation"
        ObservationDefinition -> "observationdefinition"
        OperationDefinition -> "operationdefinition"
        OperationOutcome -> "operationoutcome"
        Organization -> "organization"
        OrganizationAffiliation -> "organizationaffiliation"
        Parameters -> "parameters"
        Patient -> "patient"
        PaymentNotice -> "paymentnotice"
        PaymentReconciliation -> "paymentreconciliation"
        Person -> "person"
        PlanDefinition -> "plandefinition"
        Practitioner -> "practitioner"
        PractitionerRole -> "practitionerrole"
        Procedure -> "procedure"
        Provenance -> "provenance"
        Questionnaire -> "questionnaire"
        QuestionnaireResponse -> "questionnaireresponse"
        RelatedPerson -> "relatedperson"
        RequestGroup -> "requestgroup"
        ResearchDefinition -> "researchdefinition"
        ResearchElementDefinition -> "researchelementdefinition"
        ResearchStudy -> "researchstudy"
        ResearchSubject -> "researchsubject"
        RiskAssessment -> "riskassessment"
        RiskEvidenceSynthesis -> "riskevidencesynthesis"
        Schedule -> "schedule"
        SearchParameter -> "searchparameter"
        ServiceRequest -> "servicerequest"
        Slot -> "slot"
        Specimen -> "specimen"
        SpecimenDefinition -> "specimendefinition"
        StructureDefinition -> "structuredefinition"
        StructureMap -> "structuremap"
        Subscription -> "subscription"
        Substance -> "substance"
        SubstanceNucleicAcid -> "substancenucleicacid"
        SubstancePolymer -> "substancepolymer"
        SubstanceProtein -> "substanceprotein"
        SubstanceReferenceInformation -> "substancereferenceinformation"
        SubstanceSourceMaterial -> "substancesourcematerial"
        SubstanceSpecification -> "substancespecification"
        SupplyDelivery -> "supplydelivery"
        SupplyRequest -> "supplyrequest"
        Task -> "task"
        TerminologyCapabilities -> "terminologycapabilities"
        TestReport -> "testreport"
        TestScript -> "testscript"
        ValueSet -> "valueset"
        VerificationResult -> "verificationresult"
        VisionPrescription -> "visionprescription"
      }
    }

  companion object {
    @Throws(FHIRException::class)
    fun fromCode(code: String): ResourceType {
      if ("Account" == code) return Account
      if ("ActivityDefinition" == code) return ActivityDefinition
      if ("AdverseEvent" == code) return AdverseEvent
      if ("AllergyIntolerance" == code) return AllergyIntolerance
      if ("Appointment" == code) return Appointment
      if ("AppointmentResponse" == code) return AppointmentResponse
      if ("AuditEvent" == code) return AuditEvent
      if ("Basic" == code) return Basic
      if ("Binary" == code) return Binary
      if ("BiologicallyDerivedProduct" == code) return BiologicallyDerivedProduct
      if ("BodyStructure" == code) return BodyStructure
      if ("Bundle" == code) return Bundle
      if ("CapabilityStatement" == code) return CapabilityStatement
      if ("CarePlan" == code) return CarePlan
      if ("CareTeam" == code) return CareTeam
      if ("CatalogEntry" == code) return CatalogEntry
      if ("ChargeItem" == code) return ChargeItem
      if ("ChargeItemDefinition" == code) return ChargeItemDefinition
      if ("Claim" == code) return Claim
      if ("ClaimResponse" == code) return ClaimResponse
      if ("ClinicalImpression" == code) return ClinicalImpression
      if ("CodeSystem" == code) return CodeSystem
      if ("Communication" == code) return Communication
      if ("CommunicationRequest" == code) return CommunicationRequest
      if ("CompartmentDefinition" == code) return CompartmentDefinition
      if ("Composition" == code) return Composition
      if ("ConceptMap" == code) return ConceptMap
      if ("Condition" == code) return Condition
      if ("Consent" == code) return Consent
      if ("Contract" == code) return Contract
      if ("Coverage" == code) return Coverage
      if ("CoverageEligibilityRequest" == code) return CoverageEligibilityRequest
      if ("CoverageEligibilityResponse" == code) return CoverageEligibilityResponse
      if ("DetectedIssue" == code) return DetectedIssue
      if ("Device" == code) return Device
      if ("DeviceDefinition" == code) return DeviceDefinition
      if ("DeviceMetric" == code) return DeviceMetric
      if ("DeviceRequest" == code) return DeviceRequest
      if ("DeviceUseStatement" == code) return DeviceUseStatement
      if ("DiagnosticReport" == code) return DiagnosticReport
      if ("DocumentManifest" == code) return DocumentManifest
      if ("DocumentReference" == code) return DocumentReference
      if ("EffectEvidenceSynthesis" == code) return EffectEvidenceSynthesis
      if ("Encounter" == code) return Encounter
      if ("Endpoint" == code) return Endpoint
      if ("EnrollmentRequest" == code) return EnrollmentRequest
      if ("EnrollmentResponse" == code) return EnrollmentResponse
      if ("EpisodeOfCare" == code) return EpisodeOfCare
      if ("EventDefinition" == code) return EventDefinition
      if ("Evidence" == code) return Evidence
      if ("EvidenceVariable" == code) return EvidenceVariable
      if ("ExampleScenario" == code) return ExampleScenario
      if ("ExplanationOfBenefit" == code) return ExplanationOfBenefit
      if ("FamilyMemberHistory" == code) return FamilyMemberHistory
      if ("Flag" == code) return Flag
      if ("Goal" == code) return Goal
      if ("GraphDefinition" == code) return GraphDefinition
      if ("Group" == code) return Group
      if ("GuidanceResponse" == code) return GuidanceResponse
      if ("HealthcareService" == code) return HealthcareService
      if ("ImagingStudy" == code) return ImagingStudy
      if ("Immunization" == code) return Immunization
      if ("ImmunizationEvaluation" == code) return ImmunizationEvaluation
      if ("ImmunizationRecommendation" == code) return ImmunizationRecommendation
      if ("ImplementationGuide" == code) return ImplementationGuide
      if ("InsurancePlan" == code) return InsurancePlan
      if ("Invoice" == code) return Invoice
      if ("Library" == code) return Library
      if ("Linkage" == code) return Linkage
      if ("List" == code) return List
      if ("Location" == code) return Location
      if ("Measure" == code) return Measure
      if ("MeasureReport" == code) return MeasureReport
      if ("Media" == code) return Media
      if ("Medication" == code) return Medication
      if ("MedicationAdministration" == code) return MedicationAdministration
      if ("MedicationDispense" == code) return MedicationDispense
      if ("MedicationKnowledge" == code) return MedicationKnowledge
      if ("MedicationRequest" == code) return MedicationRequest
      if ("MedicationStatement" == code) return MedicationStatement
      if ("MedicinalProduct" == code) return MedicinalProduct
      if ("MedicinalProductAuthorization" == code) return MedicinalProductAuthorization
      if ("MedicinalProductContraindication" == code) return MedicinalProductContraindication
      if ("MedicinalProductIndication" == code) return MedicinalProductIndication
      if ("MedicinalProductIngredient" == code) return MedicinalProductIngredient
      if ("MedicinalProductInteraction" == code) return MedicinalProductInteraction
      if ("MedicinalProductManufactured" == code) return MedicinalProductManufactured
      if ("MedicinalProductPackaged" == code) return MedicinalProductPackaged
      if ("MedicinalProductPharmaceutical" == code) return MedicinalProductPharmaceutical
      if ("MedicinalProductUndesirableEffect" == code) return MedicinalProductUndesirableEffect
      if ("MessageDefinition" == code) return MessageDefinition
      if ("MessageHeader" == code) return MessageHeader
      if ("MolecularSequence" == code) return MolecularSequence
      if ("NamingSystem" == code) return NamingSystem
      if ("NutritionOrder" == code) return NutritionOrder
      if ("Observation" == code) return Observation
      if ("ObservationDefinition" == code) return ObservationDefinition
      if ("OperationDefinition" == code) return OperationDefinition
      if ("OperationOutcome" == code) return OperationOutcome
      if ("Organization" == code) return Organization
      if ("OrganizationAffiliation" == code) return OrganizationAffiliation
      if ("Parameters" == code) return Parameters
      if ("Patient" == code) return Patient
      if ("PaymentNotice" == code) return PaymentNotice
      if ("PaymentReconciliation" == code) return PaymentReconciliation
      if ("Person" == code) return Person
      if ("PlanDefinition" == code) return PlanDefinition
      if ("Practitioner" == code) return Practitioner
      if ("PractitionerRole" == code) return PractitionerRole
      if ("Procedure" == code) return Procedure
      if ("Provenance" == code) return Provenance
      if ("Questionnaire" == code) return Questionnaire
      if ("QuestionnaireResponse" == code) return QuestionnaireResponse
      if ("RelatedPerson" == code) return RelatedPerson
      if ("RequestGroup" == code) return RequestGroup
      if ("ResearchDefinition" == code) return ResearchDefinition
      if ("ResearchElementDefinition" == code) return ResearchElementDefinition
      if ("ResearchStudy" == code) return ResearchStudy
      if ("ResearchSubject" == code) return ResearchSubject
      if ("RiskAssessment" == code) return RiskAssessment
      if ("RiskEvidenceSynthesis" == code) return RiskEvidenceSynthesis
      if ("Schedule" == code) return Schedule
      if ("SearchParameter" == code) return SearchParameter
      if ("ServiceRequest" == code) return ServiceRequest
      if ("Slot" == code) return Slot
      if ("Specimen" == code) return Specimen
      if ("SpecimenDefinition" == code) return SpecimenDefinition
      if ("StructureDefinition" == code) return StructureDefinition
      if ("StructureMap" == code) return StructureMap
      if ("Subscription" == code) return Subscription
      if ("Substance" == code) return Substance
      if ("SubstanceNucleicAcid" == code) return SubstanceNucleicAcid
      if ("SubstancePolymer" == code) return SubstancePolymer
      if ("SubstanceProtein" == code) return SubstanceProtein
      if ("SubstanceReferenceInformation" == code) return SubstanceReferenceInformation
      if ("SubstanceSourceMaterial" == code) return SubstanceSourceMaterial
      if ("SubstanceSpecification" == code) return SubstanceSpecification
      if ("SupplyDelivery" == code) return SupplyDelivery
      if ("SupplyRequest" == code) return SupplyRequest
      if ("Task" == code) return Task
      if ("TerminologyCapabilities" == code) return TerminologyCapabilities
      if ("TestReport" == code) return TestReport
      if ("TestScript" == code) return TestScript
      if ("ValueSet" == code) return ValueSet
      if ("VerificationResult" == code) return VerificationResult
      if ("VisionPrescription" == code) return VisionPrescription
      throw FHIRException("Unknown resource type$code")
    }
  }
}
