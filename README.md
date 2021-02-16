# Android FHIR SDK (Early Access) ![master](https://github.com/google/android-fhir/workflows/CI/badge.svg?branch=master)

The Android FHIR SDK (the SDK) is an Android library for building offline-capable, mobile-first healthcare applications using [FHIR](https://www.hl7.org/fhir/) resources on Android. The overall goal is to simplify the process of incorporating support for FHIR into new or existing mobile solutions and to accelerate the adoption of FHIR standards as part of broader interoperability efforts in healthcare.

*The Android SDK is a community collaboration and contributions to the codebase, feature requests and suggestions about specific use cases are welcomed.* [See feedback section](#feedback)

For developers looking to contribute to the SDK, see the section on [Contributing](#contributing)

## Status

This is currently in **Early Access** for Developers ONLY and is NOT production-ready. **Do NOT use in production**.

## Usage 
The SDK is designed to support Android 21 (lollipop) and above

### Requirements
Android Studio 4.0 is required for [Java 8 library desugaring](https://developer.android.com/studio/preview/features#j8-desugar)

### Spotless
We use [Spotless](https://github.com/diffplug/spotless/tree/master/plugin-gradle) to maintain the
Java/Kotlin coding style in the codebase. Run the following command to check the codebase:

```
./gradlew spotlessCheck
```

and run the following command to apply fixes to the violations:

```
./gradlew spotlessApply
```

### License Headers
Spotless maintains the license headers for Java and Kotlin files. Use
[addlicense](https://github.com/google/addlicense) to maintain license headers in other files:

```
addlicense -c "Google LLC" -l apache .
```
## Details of the repository
The repository is organised into two main libraries, *core* and *structured data capture* as well as sample demo applications for each (see sample applications).

### Core library
This is the main library for building a mobile based clinical data repository using FHIR. It provides:
- SQLite database for FHIR resources
- APIs for accessing (create, read, update, and delete) FHIR resources in the database
- APIs for searching FHIR resources in the database

### Structured data capture (SDC) library  [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/data-capture/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:data-capture)
This library is a partial implementation of the [Structured Data Capture FHIR IG](http://build.fhir.org/ig/HL7/sdc/) on Android. It includes the UI components and APIs to capture healthcare data using FHIR questionnaires. 

The following code snippet renders a questionnaire using a FHIR questionnaire resource and an optional questionnaire response resource.

```
if (savedInstanceState == null) {
    val fragment = QuestionnaireFragment()
    fragment.arguments = bundleOf(
        QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE to questionnaireJsonString
        QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE to
            questionnaireResponseJsonString
    )

    supportFragmentManager.commit {
        add(R.id.container, fragment, QUESTIONNAIRE_FRAGMENT_TAG)
    }
}
```

## Sample Applications
Two sample applications are provided that demonstrate different features of the SDK. 

**These applications are provided for demonstration purposes only and are not for use in production. Do NOT use in production.**

### Clinical Data Repository (CDR) Application
This is the initial demo application for showcasing core SDK features and CQL proof of concept. The demo uses synthea generated data that has been loaded into the HAPI fhir server public demo site.

#### CQL Proof of Concept
Within the CDR demo is an example of evaluating CQL using the [CQL Engine](https://github.com/DBCG/cql_engine).

### SDC Gallery App
This app includes a number of pre-loaded sample FHIR Questionnaires. 

It uses the SDC library to create data capture forms using FHIR questionnaires. For supported widgets, features and known bugs, see the latest release notes.
Contributing

# Contributing
The Android FHIR SDK is being developed by a consortium of application developers. We welcome contributions. Please see the [project boards](https://github.com/google/android-fhir/projects).

## Feedback
If you want to provide any feedback, discuss use cases, raise feature requests, or simply want to get involved, please use the [Discussions](https://github.com/google/android-fhir/discussions) section, or email us at <android-fhir-sdk-feedback@google.com>.

# Disclaimer
This is not an officially supported Google product.

This product is not intended to be a medical device.
