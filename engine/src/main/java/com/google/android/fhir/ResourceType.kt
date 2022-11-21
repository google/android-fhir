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


enum class ResourceType {
  Account,
  ActivityDefinition,
  AdministrableProductDefinition,
  AdverseEvent,
  AllergyIntolerance,
  Appointment,
  AppointmentResponse,
  ArtifactAssessment,
  AuditEvent,
  Basic,
  Binary,
  BiologicallyDerivedProduct,
  BodyStructure,
  Bundle,
  CapabilityStatement,
  CapabilityStatement2,
  CarePlan,
  CareTeam,
  CatalogEntry,
  ChargeItem,
  ChargeItemDefinition,
  Citation,
  Claim,
  ClaimResponse,
  ClinicalImpression,
  ClinicalUseDefinition,
  ClinicalUseIssue,
  CodeSystem,
  Communication,
  CommunicationRequest,
  CompartmentDefinition,
  Composition,
  ConceptMap,
  ConceptMap2,
  Condition,
  ConditionDefinition,
  Consent,
  Contract,
  Coverage,
  CoverageEligibilityRequest,
  CoverageEligibilityResponse,
  DetectedIssue,
  Device,
  DeviceDefinition,
  DeviceDispense,
  DeviceMetric,
  DeviceRequest,
  DeviceUsage,
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
  EvidenceReport,
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
  ImagingSelection,
  ImagingStudy,
  Immunization,
  ImmunizationEvaluation,
  ImmunizationRecommendation,
  ImplementationGuide,
  Ingredient,
  InsurancePlan,
  InventoryReport,
  Invoice,
  Library,
  Linkage,
  List,
  Location,
  ManufacturedItemDefinition,
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
  MedicinalProductDefinition,
  MedicinalProductIndication,
  MedicinalProductIngredient,
  MedicinalProductInteraction,
  MedicinalProductManufactured,
  MedicinalProductPackaged,
  MedicinalProductPharmaceutical,
  MedicinalProductUndesirableEffect,
  MedicationUsage,
  MessageDefinition,
  MessageHeader,
  MolecularSequence,
  NamingSystem,
  NutritionIntake,
  NutritionOrder,
  NutritionProduct,
  Observation,
  ObservationDefinition,
  OperationDefinition,
  OperationOutcome,
  Organization,
  OrganizationAffiliation,
  PackagedProductDefinition,
  Parameters,
  Patient,
  PaymentNotice,
  PaymentReconciliation,
  Permission,
  Person,
  PlanDefinition,
  Practitioner,
  PractitionerRole,
  Procedure,
  Provenance,
  Questionnaire,
  QuestionnaireResponse,
  RegulatedAuthorization,
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
  SubscriptionStatus,
  SubscriptionTopic,
  Substance,
  SubstanceDefinition,
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
        AdministrableProductDefinition -> "administrableproductdefinition"
        AdverseEvent -> "adverseevent"
        AllergyIntolerance -> "allergyintolerance"
        Appointment -> "appointment"
        AppointmentResponse -> "appointmentresponse"
        ArtifactAssessment -> "artifactassessment"
        AuditEvent -> "auditevent"
        Basic -> "basic"
        Binary -> "binary"
        BiologicallyDerivedProduct -> "biologicallyderivedproduct"
        BodyStructure -> "bodystructure"
        Bundle -> "bundle"
        CapabilityStatement -> "capabilitystatement"
        CapabilityStatement2 -> "capabilitystatement2"
        CarePlan -> "careplan"
        CareTeam -> "careteam"
        CatalogEntry -> "catalogentry"
        ChargeItem -> "chargeitem"
        ChargeItemDefinition -> "chargeitemdefinition"
        Citation -> "citation"
        Claim -> "claim"
        ClaimResponse -> "claimresponse"
        ClinicalImpression -> "clinicalimpression"
        ClinicalUseDefinition -> "clinicalusedefinition"
        ClinicalUseIssue -> "clinicaluseissue"
        CodeSystem -> "codesystem"
        Communication -> "communication"
        CommunicationRequest -> "communicationrequest"
        CompartmentDefinition -> "compartmentdefinition"
        Composition -> "composition"
        ConceptMap -> "conceptmap"
        ConceptMap2 -> "conceptmap2"
        Condition -> "condition"
        ConditionDefinition -> "conditiondefinition"
        Consent -> "consent"
        Contract -> "contract"
        Coverage -> "coverage"
        CoverageEligibilityRequest -> "coverageeligibilityrequest"
        CoverageEligibilityResponse -> "coverageeligibilityresponse"
        DetectedIssue -> "detectedissue"
        Device -> "device"
        DeviceDefinition -> "devicedefinition"
        DeviceDispense -> "devicedispense"
        DeviceMetric -> "devicemetric"
        DeviceRequest -> "devicerequest"
        DeviceUsage -> "deviceusage"
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
        EvidenceReport -> "evidencereport"
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
        ImagingSelection -> "imagingselection"
        ImagingStudy -> "imagingstudy"
        Immunization -> "immunization"
        ImmunizationEvaluation -> "immunizationevaluation"
        ImmunizationRecommendation -> "immunizationrecommendation"
        ImplementationGuide -> "implementationguide"
        Ingredient -> "ingredient"
        InsurancePlan -> "insuranceplan"
        InventoryReport -> "inventoryreport"
        Invoice -> "invoice"
        Library -> "library"
        Linkage -> "linkage"
        List -> "list"
        Location -> "location"
        ManufacturedItemDefinition -> "manufactureditemdefinition"
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
        MedicinalProductDefinition -> "medicinalproductdefinition"
        MedicinalProductIndication -> "medicinalproductindication"
        MedicinalProductIngredient -> "medicinalproductingredient"
        MedicinalProductInteraction -> "medicinalproductinteraction"
        MedicinalProductManufactured -> "medicinalproductmanufactured"
        MedicinalProductPackaged -> "medicinalproductpackaged"
        MedicinalProductPharmaceutical -> "medicinalproductpharmaceutical"
        MedicinalProductUndesirableEffect -> "medicinalproductundesirableeffect"
        MedicationUsage -> "medicationusage"
        MessageDefinition -> "messagedefinition"
        MessageHeader -> "messageheader"
        MolecularSequence -> "molecularsequence"
        NamingSystem -> "namingsystem"
        NutritionIntake -> "nutritionintake"
        NutritionOrder -> "nutritionorder"
        NutritionProduct -> "nutritionproduct"
        Observation -> "observation"
        ObservationDefinition -> "observationdefinition"
        OperationDefinition -> "operationdefinition"
        OperationOutcome -> "operationoutcome"
        Organization -> "organization"
        OrganizationAffiliation -> "organizationaffiliation"
        PackagedProductDefinition -> "packagedproductdefinition"
        Parameters -> "parameters"
        Patient -> "patient"
        PaymentNotice -> "paymentnotice"
        PaymentReconciliation -> "paymentreconciliation"
        Permission -> "permission"
        Person -> "person"
        PlanDefinition -> "plandefinition"
        Practitioner -> "practitioner"
        PractitionerRole -> "practitionerrole"
        Procedure -> "procedure"
        Provenance -> "provenance"
        Questionnaire -> "questionnaire"
        QuestionnaireResponse -> "questionnaireresponse"
        RegulatedAuthorization -> "regulatedauthorization"
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
        SubscriptionStatus -> "subscriptionstatus"
        SubscriptionTopic -> "subscriptiontopic"
        Substance -> "substance"
        SubstanceDefinition -> "substancedefinition"
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
    @Throws(Exception::class)
    fun fromCode(code: String): ResourceType {
      if ("Account" == code) return Account
      if ("ActivityDefinition" == code) return ActivityDefinition
      if ("AdministrableProductDefinition" == code) return AdministrableProductDefinition
      if ("AdverseEvent" == code) return AdverseEvent
      if ("AllergyIntolerance" == code) return AllergyIntolerance
      if ("Appointment" == code) return Appointment
      if ("AppointmentResponse" == code) return AppointmentResponse
      if ("ArtifactAssessment" == code) return ArtifactAssessment
      if ("AuditEvent" == code) return AuditEvent
      if ("Basic" == code) return Basic
      if ("Binary" == code) return Binary
      if ("BiologicallyDerivedProduct" == code) return BiologicallyDerivedProduct
      if ("BodyStructure" == code) return BodyStructure
      if ("Bundle" == code) return Bundle
      if ("CapabilityStatement" == code) return CapabilityStatement
      if ("CapabilityStatement2" == code) return CapabilityStatement2
      if ("CarePlan" == code) return CarePlan
      if ("CareTeam" == code) return CareTeam
      if ("CatalogEntry" == code) return CatalogEntry
      if ("ChargeItem" == code) return ChargeItem
      if ("ChargeItemDefinition" == code) return ChargeItemDefinition
      if ("Citation" == code) return Citation
      if ("Claim" == code) return Claim
      if ("ClaimResponse" == code) return ClaimResponse
      if ("ClinicalImpression" == code) return ClinicalImpression
      if ("ClinicalUseDefinition" == code) return ClinicalUseDefinition
      if ("ClinicalUseIssue" == code) return ClinicalUseIssue
      if ("CodeSystem" == code) return CodeSystem
      if ("Communication" == code) return Communication
      if ("CommunicationRequest" == code) return CommunicationRequest
      if ("CompartmentDefinition" == code) return CompartmentDefinition
      if ("Composition" == code) return Composition
      if ("ConceptMap" == code) return ConceptMap
      if ("ConceptMap2" == code) return ConceptMap2
      if ("Condition" == code) return Condition
      if ("ConditionDefinition" == code) return ConditionDefinition
      if ("Consent" == code) return Consent
      if ("Contract" == code) return Contract
      if ("Coverage" == code) return Coverage
      if ("CoverageEligibilityRequest" == code) return CoverageEligibilityRequest
      if ("CoverageEligibilityResponse" == code) return CoverageEligibilityResponse
      if ("DetectedIssue" == code) return DetectedIssue
      if ("Device" == code) return Device
      if ("DeviceDefinition" == code) return DeviceDefinition
      if ("DeviceDispense" == code) return DeviceDispense
      if ("DeviceMetric" == code) return DeviceMetric
      if ("DeviceRequest" == code) return DeviceRequest
      if ("DeviceUsage" == code) return DeviceUsage
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
      if ("EvidenceReport" == code) return EvidenceReport
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
      if ("ImagingSelection" == code) return ImagingSelection
      if ("ImagingStudy" == code) return ImagingStudy
      if ("Immunization" == code) return Immunization
      if ("ImmunizationEvaluation" == code) return ImmunizationEvaluation
      if ("ImmunizationRecommendation" == code) return ImmunizationRecommendation
      if ("ImplementationGuide" == code) return ImplementationGuide
      if ("Ingredient" == code) return Ingredient
      if ("InsurancePlan" == code) return InsurancePlan
      if ("InventoryReport" == code) return InventoryReport
      if ("Invoice" == code) return Invoice
      if ("Library" == code) return Library
      if ("Linkage" == code) return Linkage
      if ("List" == code) return List
      if ("Location" == code) return Location
      if ("ManufacturedItemDefinition" == code) return ManufacturedItemDefinition
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
      if ("MedicinalProductDefinition" == code) return MedicinalProductDefinition
      if ("MedicinalProductIndication" == code) return MedicinalProductIndication
      if ("MedicinalProductIngredient" == code) return MedicinalProductIngredient
      if ("MedicinalProductInteraction" == code) return MedicinalProductInteraction
      if ("MedicinalProductManufactured" == code) return MedicinalProductManufactured
      if ("MedicinalProductPackaged" == code) return MedicinalProductPackaged
      if ("MedicinalProductPharmaceutical" == code) return MedicinalProductPharmaceutical
      if ("MedicinalProductUndesirableEffect" == code) return MedicinalProductUndesirableEffect
      if ("MedicationUsage" == code) return MedicationUsage
      if ("MessageDefinition" == code) return MessageDefinition
      if ("MessageHeader" == code) return MessageHeader
      if ("MolecularSequence" == code) return MolecularSequence
      if ("NamingSystem" == code) return NamingSystem
      if ("NutritionIntake" == code) return NutritionIntake
      if ("NutritionOrder" == code) return NutritionOrder
      if ("NutritionProduct" == code) return NutritionProduct
      if ("Observation" == code) return Observation
      if ("ObservationDefinition" == code) return ObservationDefinition
      if ("OperationDefinition" == code) return OperationDefinition
      if ("OperationOutcome" == code) return OperationOutcome
      if ("Organization" == code) return Organization
      if ("OrganizationAffiliation" == code) return OrganizationAffiliation
      if ("PackagedProductDefinition" == code) return PackagedProductDefinition
      if ("Parameters" == code) return Parameters
      if ("Patient" == code) return Patient
      if ("PaymentNotice" == code) return PaymentNotice
      if ("PaymentReconciliation" == code) return PaymentReconciliation
      if ("Permission" == code) return Permission
      if ("Person" == code) return Person
      if ("PlanDefinition" == code) return PlanDefinition
      if ("Practitioner" == code) return Practitioner
      if ("PractitionerRole" == code) return PractitionerRole
      if ("Procedure" == code) return Procedure
      if ("Provenance" == code) return Provenance
      if ("Questionnaire" == code) return Questionnaire
      if ("QuestionnaireResponse" == code) return QuestionnaireResponse
      if ("RegulatedAuthorization" == code) return RegulatedAuthorization
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
      if ("SubscriptionStatus" == code) return SubscriptionStatus
      if ("SubscriptionTopic" == code) return SubscriptionTopic
      if ("Substance" == code) return Substance
      if ("SubstanceDefinition" == code) return SubstanceDefinition
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
      throw Exception("Unknown resource type $code")
    }
  }
}
