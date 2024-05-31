# FAQs

1. [What is the Android FHIR SDK?](#what-is-the-android-fhir-sdk)
1. [What is FHIR?](#what-is-fhir)
1. [Why is a FHIR SDK needed? What problem does it solve?](#why-is-a-fhir-sdk-needed-what-problem-does-it-solve)
1. [What are the components of the FHIR SDK?](#what-are-the-components-of-the-fhir-sdk)
1. [What is Google’s role in this project?](#what-is-googles-role-in-this-project)
1. [Who else is involved?](#who-else-is-involved)
1. [What is in/out of scope of the FHIR SDK?](#what-is-inout-of-scope-of-the-fhir-sdk)
1. [Do I have control of the data collected using the SDK?](#do-i-have-control-of-the-data-collected-using-the-sdk)
1. [How is data encrypted?](#how-is-data-encrypted)
1. [How can I build cross-platform applications?](#how-can-i-build-cross-platform-applications)
1. [What applications currently use the SDK?](#what-applications-currently-use-the-sdk)
1. [How to do database inspect in the FHIR demo app?](#how-to-do-database-inspect-in-the-fhir-demo-app)

### What is the Android FHIR SDK?

It is an open source library for Android developers who want to build [FHIR](http://hl7.org/fhir/)-enabled offline-capable, mobile-first healthcare applications.

### What is FHIR?

FHIR (Fast Healthcare Interoperability Resources) is the latest version of the HL7 standard for building modern patient centered healthcare applications - see [HL7 FHIR Specification](http://hl7.org/fhir/)

### Why is a FHIR SDK needed? What problem does it solve?

Today there is no simple way to build a FHIR compliant app on Android that is suited to the needs of LMICs - particularly offline capabilities due to poor connectivity. The FHIR SDK makes it easy for developers to build custom applications without needing to also build and maintain all the underlying components.

### What are the components of the FHIR SDK?

The SDK enables the common components that developers need to build rich, offline-capable, standard-compliant applications. This includes APIs for standard data capture, data access, search and sync as well as encrypted local storage (using SQLite DB).

### What is Google’s role in this project?

We are contributing to the design and development of the open source FHIR SDK

### Who else is involved?

The Android FHIR SDK project was initiated by the WHO together with teams from Android and [ONA](https://ona.io/home/). There is an open consortium of groups convened by the WHO to progress the design and development of the FHIR SDK as part of supporting their [SMART Guidelines work](https://www.who.int/teams/digital-health-and-innovation/smart-guidelineshttp:// "SMART Guidelines work"). To-date developers from ONA, IPRD and Argusoft as well as a number of independent software engineers have contributed to the code-base. It is an open source initiative and we welcome contributions from any developers who want to get involved.

### What is in/out of scope of the FHIR SDK?

In the SDK:

* Implementation of the FHIR specification relevant for mobile health use cases
* Helper methods for common implementation scenarios that are implementer agnostic
* Encryption of data within the SQLite DB at rest (using standard Android encryption)

Not in the SDK:

* Custom implementation requirements: these should be in implementer specific libraries
* Creation of an Authenticated client: assume server side implementation with client side tokens.
* Authorization: rules around what resources a specific user should have access to are applied server side.

### Do I have control of the data collected using the SDK?

The Android FHIR SDK stores data on device in a SQLite database and can be configured to sync the data with a FHIR server specified by the application. The choice of storage (on-prem or cloud based) is up to the individual implementation.

### How is data encrypted?

Data in the SQLite database is **encrypted at rest** using standard Android device encryption (minimum support for Android 5.0). For devices using Android 6.0 or above, additional level of application based encryption is provided by SQLCipher integration. [Read more](use/FEL/Privacy-Security.md#database-encryption)

There is no limitation on supporting **encryption in transit** and this is recommended best practice for all implementers to put in place when syncing data between the Android client and a FHIR server.

### How can I build cross-platform applications?

The Android FHIR SDK is designed for the Android OS. There are currently no plans to support iOS.

### What applications currently use the SDK?

We have an active community of developers that are in the process of building applications using the FHIR SDK.

### How to do database inspect in the FHIR demo app?

The data inside FHIR database are encrypted for safety reason. This is controlled by the flag `enableEncryptionIfSupported` flag in the `FhirEngineConfiguration`. To debug/inspect the database content(for example, in the demo app), developer can temporarily disable the encryption as following:
In [FhirApplication](https://github.com/google/android-fhir/blob/master/demo/src/main/java/com/google/android/fhir/demo/FhirApplication.kt), when initiate the `FhirEngineProvider`, set `enableEncryptionIfSupported` to false. Code example:

```kotlin
class FhirApplication : Application() {
  // Only initiate the FhirEngine when used for the first time, not when the app is created.
  private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

  override fun onCreate() {
    super.onCreate()
    FhirEngineProvider.init(
      FhirEngineConfiguration(enableEncryptionIfSupported = false, RECREATE_AT_OPEN)
    )
    Sync.oneTimeSync<FhirPeriodicSyncWorker>(this)
  }
```

In AndroidStudio, run the `demo` app on a connected android device. Then go to View -> Tool Windows -> App Inspection, click on the Database Inspector tab to inspect the database `resources.db`.

One thing to note: If there is any database exception after disabling encryption, developers can wipe the demo app data either in Settings or via `adb shell pm clear com.google.android.fhir.demo`, and rerun the demo app.
