# What is the Structured Data Capture Library?

The Structured Data Capture Library lets you easily build Android applications that capture and process healthcare data using the [FHIR Questionnaire](https://www.hl7.org/fhir/questionnaire.html).

## Key Capabilities

* **Render FHIR Questionnaires** Displays the questionnaire for you in a [Fragment](https://developer.android.com/guide/fragments) using consistent, [Material](https://material.io/)-based components
* **Standards-based form responses** Answers are consistently formatted as a [FHIR QuestionnaireResponse](http://www.hl7.org/fhir/questionnaireresponse.html)
* **Validate answers** Validate individual fields during user entry or entire questionnaire responses
* **Extract additional FHIR resources** Convert the QuestionnaireResponse to other FHIR resources to populate a FHIR-based health record
* **Powerful form controls** Advanced rendering and form behaviors such as skip-logic, pagination, runtime validation and multi-language support based on the [HL7 Structured Data Capture](http://build.fhir.org/ig/HL7/sdc/) implementation guide
* **Custom look and feel** Supports custom themes or creating your own question widgets

## Why would you use the Structured Data Capture Library?

Forms to collect data are a very common component of healthcare applications today. When building a typical Android application you need to design and code the business logic and UI for each form you create. This quickly becomes unsustainable for the many different forms used in a modern healthcare environment.

With the Structured Data Capture Library, forms are defined using a [FHIR Questionnaire](https://www.hl7.org/fhir/questionnaire.html) and rendered for you. This separates form design from application development, allowing forms to be easily added, shared, and updated without building UI layouts, and healthcare data experts can author questionnaires without any knowledge of Android development.

The Structured Data Capture Library also simplifies the process of creating FHIR resources based on form answers and using existing FHIR data to pre-populate forms. These two processes make data collected using FHIR Questionnaires more meaningful and useful in clinical workflows and enable more advanced use cases.

## How does the Structured Data Capture Library work?

There are three main public APIs in the Structured Data Capture Library:

* [QuestionnaireFragment](Use-QuestionnaireFragment.md): the main class for rendering questionnaires
* [ResourceMapper](Use-ResourceMapper.md): handles data extraction and questionnaire population
* [QuestionnaireResponseValidator](Use-QuestionnaireResponseValidator.md): validates questionnaire responses against questionnaires

You can also browse the pages in the navigation sidebar for more information.
