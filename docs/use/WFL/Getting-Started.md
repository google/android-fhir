# Getting Started

To get started, you need an Android Studio project first. If you are new to Android, start with the [Build Your First App](https://developer.android.com/training/basics/firstapp) tutorial. The rest of this document assumes familiarity with the fundamentals of Android development.

The Workflow library supports Android 24 and above. Android Studio 4.0 or above is required for [Java 11 library desugaring](https://developer.android.com/studio/write/java11-default-support-table).

This guide is intended for developers who are familiar with basic Android development with Kotlin, and proficient in working with FHIR concepts and resources.

## Add the Workflow Library

Once you have set up your project in Android Studio, set the `minSdk` of the app to `24` and add the Workflow modules to your app's build.gradle file:

```gradle
dependencies {
  implementation("com.google.android.fhir:workflow:0.1.0-alpha03")
}
```

Add the following Packaging Options to avoid compilation errors:

```gradle
android {
  //...
  packagingOptions {
    pickFirst('META-INF/ASL-2.0.txt')
    pickFirst('META-INF/LGPL-3.0.txt')
    pickFirst('META-INF/LICENSE.md')
    pickFirst('META-INF/NOTICE.md')
    pickFirst('META-INF/sun-jaxb.episode')
  }
}
```

Once this is done and you have synchronized your Gradle project, proceed to the next section to start using the library's APIs.

## Initialize and get a FhirOperator

All workflow requests are made through a `FhirOperator` instance, which should be initialized in the `Application.onCreate` function, together with the Engine's initialization. The `FhirOperator` is your main access point to all clinical decision support implementations in the SDK.

The snippet below demonstrates how to pre-load a single operator to your application. Notice how the Workflow module requires a choice of FhirVersion, `R4` in this case.

```kotlin
class MyApplication : Application() {
 private val inSync = LazyThreadSafetyMode.SYNCHRONIZED

 // Only initiate when used for the first time, not when the app is created.
 private val fhirEngine: FhirEngine by lazy(inSync) { FhirEngineProvider.getInstance(this) }
 private val fhirContext: FhirContext by lazy(inSync) { FhirContext.forCached(FhirVersionEnum.R4) }
 private val fhirOperator: FhirOperator by lazy(inSync) { FhirOperator(fhirContext, fhirEngine) }

 override fun onCreate() {
   super.onCreate()

   FhirEngineProvider.init(
     FhirEngineConfiguration(
       enableEncryptionIfSupported = true,
       DatabaseErrorStrategy.RECREATE_AT_OPEN
     )
   )

   thread {
     fhirOperator //Initializes the object in the background.
   }
 }

 companion object {
   fun fhirEngine(context: Context) = (context.applicationContext as MyApplication).fhirEngine
   fun fhirContext(context: Context) = (context.applicationContext as MyApplication).fhirContext
   fun fhirOperator(context: Context) = (context.applicationContext as MyApplication).fhirOperator
 }
}
```

Don't forget to add the custom MyApplication to the AndroidManifest.xml

```xml
<application
   android:name=".MyApplication"
   <!-- ... ->
</application>
```

## Use FhirOperator APIs

**1. Evaluate Individual Subject Matters:**

```kotlin
fhirOperator.evaluateMeasure(
 url = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
 start = "2020-01-01",
 end = "2020-01-31",
 reportType = "subject",
 subject = "charity-otala-1",
 practitioner = "jane",
 lastReceivedOn = null
)
```

**2. Evaluate Population Matters:**

```kotlin
val measureReport : MeasureReport =
 fhirOperator.evaluateMeasure(
   url = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
   start = "2019-01-01",
   end = "2021-12-31",
   reportType = "population",
   subject = null,
   practitioner = "jane",
   lastReceivedOn = null
 )
```

**3. Generate Care Plan for a Patient:**

```kotlin
val plan: CarePlan = fhirOperator.generateCarePlan(
 planDefinitionId = "plandefinition-RuleFilters-1.0.0",
 patientId = "Reportable",
 encounterId = "reportable-encounter"
)
```

See [FhirOperatorTest.kt](https://github.com/google/android-fhir/blob/master/workflow/src/test/java/com/google/android/fhir/workflow/FhirOperatorTest.kt) for more details on how to set up data.
