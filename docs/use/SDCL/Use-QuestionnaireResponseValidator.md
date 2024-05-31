# Use `QuestionnaireResponseValidator`

The Structured Data Capture Library provides an API to validate a FHIR QuestionnaireResponse resource against the FHIR Questionnaire resource it is answering.

```kotlin
val validationResult = QuestionnaireResponseValidator.validate(questionnaire, questionnaireResponse, context)
```
