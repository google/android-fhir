## hapi libs starts
-keep class ca.uhn.fhir.** { *; }
-keep class org.hl7.fhir.**  { *; }
# Used by HapiWorkerContext (fhirpath engine in QuestionnaireViewModel)
-keep class com.github.benmanes.caffeine.cache.**  { *; }
## hapi libs ends

## sqlcipher starts
# see (https://github.com/sqlcipher/android-database-sqlcipher/tree/master#proguard)
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }
## sqlcipher ends

## retrofit starts
# see (https://github.com/square/retrofit/issues/3539)
-keep class com.google.android.fhir.sync.remote.RetrofitHttpService { *; }
## retrofit ends
-ignorewarnings
