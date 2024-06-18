# Workflow Library

The [Workflow](https://build.fhir.org/workflow.html) Library provides decision support logic and analytics in clinical workflows. Authors of digital guidelines develop reports ([Measure](https://build.fhir.org/measure.html)) and individualized clinical procedures ([PlanDefinition](https://build.fhir.org/plandefinition.html)) to be run on FHIR Databases. Both assets rely on [FHIR Libraries](https://build.fhir.org/library.html) that can be written in [Clinical Quality Language](https://cql.hl7.org/) or [FhirPath](https://hl7.org/fhir/fhirpath.html). To execute such workflows, this library implements the two major FHIR operations, Measure [$Evaluate](https://build.fhir.org/operation-measure-evaluate-measure.html) and Plan Definition [$Apply](https://build.fhir.org/plandefinition-operation-apply.html), as well as the lower-level Library [$Evaluate](https://build.fhir.org/ig/HL7/cqf-recommendations/OperationDefinition-cpg-library-evaluate.html) operation:

1. The [Measure](https://build.fhir.org/measure.html) resource is a reporting-definition tool that supports the evaluation of clinical measures, such as the percentage of patients who received a recommended screening or the number of patients with a specific condition who received appropriate treatment. The output of this operation will be a Report resource, which includes the results of the evaluation. The Report resource will include details about the individual measures that were evaluated, the scores for those measures, and any relevant supporting information. It may also include references to other resources that are relevant to the evaluation, such as observations or procedures that were used to calculate the measure scores. These calculations can be used to assess the performance of a healthcare provider or organization and can help identify areas for improvement.

1. The [PlanDefinition](https://build.fhir.org/plandefinition.html) resource describes a plan or protocol for the care of a given patient. This could include a treatment plan for a specific condition, a discharge plan for a hospitalized patient, or a care plan for managing chronic illness. The output of this operation will be a CarePlan resource, which represents the plan that has been tailored and applied to the specific patient or group of patients. The CarePlan resource will include details about the actions that are part of the plan, the timing and frequency of those actions, and any relevant supporting information. It may also include references to other resources that are relevant to the plan, such as observations or procedures that are part of the plan. The Apply operator can be used to determine the specific actions or interventions that should be taken as part of a care plan, based on the patient's current status and other relevant factors. For example, it could be used to determine which medications a patient should be prescribed or to identify any necessary referrals to other healthcare providers.

1. The [Library](https://build.fhir.org/library.html) resource describes a container for clinical knowledge assets. One of these assets is a shareable library of clinical logic, written in Clinical Quality Language (CQL). Users of the Workflow library can call an evaluation operator directly from the Library resource and run individual expressions at will. The output will be Parameters resource with the results of each expression evaluated. This operator should be used when the use case does not fit into a PlanDefinition or a Measure Evaluate.

It's recommended that these 3 types of resources are authored within the scope of a [FHIR IG](https://www.hl7.org/fhir/implementationguide.html). The IG can then be published online and imported by the Android SDK. To import an IG, Android SDK users must simply copy the required files from the IG package into the `assets` folder and parse those files using the regular FHIR Parser.

The workflow library is dependent on the [Engine library](../FEL/index.md). Operations execute over the dataset saved in the FhirEngine instance.

Future features of the library will provide support for Tasking and other Workflow related requirements

## Next Steps

* [Getting Started](Getting-Started.md)
* Guides
  * [Generate a Care Plan](Generate-A-Care-Plan.md)
  * [Evaluate a Measure](Evaluate-a-Measure.md)
  * [Evaluate a Library](Evaluate-a-Library.md)
  * [Compile CQL](Compile-and-Execute-CQL.md)

## Data safety

This library does not collect or share any personal or sensitive [user data](https://developer.android.com/guide/topics/data/collect-share) with any third party libraries or SDKs.
