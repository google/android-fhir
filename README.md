# Android FHIR SDK (Early Access) [![master](https://github.com/google/android-fhir/workflows/CI/badge.svg?branch=master)](https://github.com/google/android-fhir/actions?query=workflow%3ACI) [![codecov](https://codecov.io/gh/google/android-fhir/branch/master/graph/badge.svg?token=PDSC4WRDTQ)](https://codecov.io/gh/google/android-fhir)
The Android FHIR SDK (the SDK) is an Android library for building offline-capable, mobile-first
healthcare applications using [FHIR](https://www.hl7.org/fhir/) resources on Android. The overall
goal is to simplify the process of incorporating support for FHIR into new or existing mobile
solutions and to accelerate the adoption of FHIR standards as part of broader interoperability
efforts in healthcare.

## Status
This is currently in **Early Access** for Developers ONLY and is NOT production-ready. **Do NOT use
in production**.

## Usage 
The SDK is designed to support Android 21 (lollipop) and above. Android Studio 4.0 is required for
[Java 8 library desugaring](https://developer.android.com/studio/preview/features#j8-desugar).

## Libraries
The repository is organised into two main libraries, *engine* and *structured data capture*.

### FHIR engine library  [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/engine/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:engine)
This is the main library for building a mobile based clinical data repository using FHIR. It
provides:
- SQLite database for FHIR resources
- APIs for accessing (create, read, update, and delete) FHIR resources in the database
- APIs for searching FHIR resources in the database
- Sync API for synchronization of resources with a FHIR server

To use this library in your Android application, see [FHIR Engine Library User's Guide](https://github.com/google/android-fhir/wiki/FHIR-Engine-Library-User's-Guide).

### Structured data capture (SDC) library  [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/data-capture/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:data-capture)
This library is a partial implementation of the
[Structured Data Capture FHIR IG](http://build.fhir.org/ig/HL7/sdc/) on Android. It includes the UI
components and APIs to capture healthcare data using FHIR questionnaires. 

To use this library in your Android application, see 
[Structured Data Capture Library User's Guide](https://github.com/google/android-fhir/wiki/Structured-Data-Capture-Library-User's-Guide).

## Sample Applications
Two sample applications are provided that demonstrate different features of the SDK. 

**These applications are provided for demonstration purposes only and are not for use in production.
Do NOT use in production.**

### Reference Application
This is the initial demo application for showcasing core SDK features and CQL proof of concept. The
demo uses synthea generated data that has been loaded into the HAPI FHIR server public demo site.

To run this application, clone the codebase and run the `reference` module. 

### SDC Gallery App
This app includes a number of pre-loaded sample FHIR Questionnaires. 

It uses the SDC library to create data capture forms using FHIR questionnaires. For supported
widgets, features and known bugs, see the latest release notes.

To run this application, clone the codebase and run the `datacapturegallery` module. 

## Contributing
The SDK is being developed by a consortium of application developers. We welcome contributions.
Please see
[How to Contribute](https://github.com/google/android-fhir/blob/master/docs/contributing.md) and
[Developer's Guide](https://github.com/google/android-fhir/wiki/Developer's-Guide) for more
information.

## Feedback
If you want to provide any feedback, discuss use cases, raise feature requests, or simply want to
get involved, please use the [Discussions](https://github.com/google/android-fhir/discussions)
section, or email us at <android-fhir-sdk-feedback@google.com>.

## Disclaimer
This is not an officially supported Google product.

This product is not intended to be a medical device.
