# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
## hapi libs starts
-keep class ca.uhn.fhir.** { *; }
-keep class org.hl7.fhir.r4.hapi.ctx.*  { *; }
-keep class org.hl7.fhir.r4.**  { *; }
-keep class org.hl7.fhir.utilities.**  { *; }
-keep class org.hl7.fhir.exceptions.*  { *; }
-keep class org.hl7.fhir.instance.model.api.*  { *; }
# Used by HapiWorkerContext (fhirpath engine in QuestionnaireViewModel)
-keep class com.github.benmanes.caffeine.cache.**  { *; }
## hapi libs ends

## sqlcipher starts
# see (https://github.com/sqlcipher/android-database-sqlcipher/tree/master#proguard)
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }
## sqlcipher endss

## retrofit starts
# see (https://github.com/square/retrofit/issues/3539)
-keep class com.google.android.fhir.sync.remote.RemoteFhirService { *; }
## retrofit ends