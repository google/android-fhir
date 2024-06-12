## hapi libs starts
-keep class ca.uhn.fhir.** { *; }
-keep class org.hl7.fhir.**  { *; }
# Used by HapiWorkerContext (fhirpath engine in QuestionnaireViewModel)
-keep class com.github.benmanes.caffeine.cache.**  { *; }
## hapi libs ends
