## HAPI Strucutres need to be kept beause
	# 1. Reflection is used in resource extraction as the FHIR Path expression is then translated to field names to locate data elements in FHIR resources.
	# 2. In HAPI Strucures, ClassLoader is used to load classes from different packages that are hardcoded.
-keep class ca.uhn.fhir.** { *; }
-keep class org.hl7.fhir.**  { *; }
# Used by hapi's XmlUtil which is internally used by hapi's FHIRPathEngine.
-keep class com.ctc.wstx.stax.**  { *; }
# Used by HapiWorkerContext (fhirpath engine in QuestionnaireViewModel)
-keep class com.github.benmanes.caffeine.cache.**  { *; }
## hapi libs ends
-ignorewarnings
