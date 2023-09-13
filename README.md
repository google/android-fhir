# Android FHIR SDK

[![master](https://github.com/google/android-fhir/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/google/android-fhir/actions/workflows/build.yml) [![master](https://storage.googleapis.com/android-fhir-build-badges/build.svg)](https://storage.googleapis.com/android-fhir-build-badges/build.html) [![codecov](https://codecov.io/gh/google/android-fhir/branch/master/graph/badge.svg?token=PDSC4WRDTQ)](https://codecov.io/gh/google/android-fhir/branch/master) [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![project chat](https://img.shields.io/badge/zulip-join_chat-brightgreen.svg)](https://chat.fhir.org/#narrow/stream/276344-android)

The Android FHIR SDK is a set of Kotlin libraries for building offline-capable, mobile-first
healthcare applications using the [HL7® FHIR® standard](https://www.hl7.org/fhir/) on Android. It
aims to accelerate the adoption of FHIR by making it easy to incorporate FHIR into new and existing
mobile applications.

## Libraries

The SDK contains the following libraries:

| Library              | Latest release                                                                                                                                                                                                                    | Code                                                                  | Wiki                                                                                | Min SDK                    | Summary                                                                                                                                                    |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------- | ----------------------------------------------------------------------------------- |----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Data Capture Library | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/data-capture/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:data-capture) | [code](https://github.com/google/android-fhir/tree/master/datacapture)| [wiki](https://github.com/google/android-fhir/wiki/Structured-Data-Capture-Library) | Android 7.0 (API level 24) | Collect, validate, and process healthcare data on Android                                                                                                  |
| FHIR Engine Library  | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/engine/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:engine)             | [code](https://github.com/google/android-fhir/tree/master/engine)     | [wiki](https://github.com/google/android-fhir/wiki/FHIR-Engine-Library)             | Android 7.0 (API level 24) | Store and manage FHIR resources locally on Android and synchronize with FHIR server                                                                        |
| Workflow Library     | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/workflow/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:workflow)         | [code](https://github.com/google/android-fhir/tree/master/workflow)   | [wiki](https://github.com/google/android-fhir/wiki/Workflow-Library)                | Android 8.0 (API level 26) | Provide decision support and analytics in clinical workflow on Android including implementation of specific FHIR operations ($measure_evaluate and $apply) |

## Demo apps

This repository also contains the following demo apps:

| Demo app                            | Code                                                               | Wiki                                                                                            |
| ----------------------------------- | ------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------- |
| FHIR Engine Demo App                | [code](https://github.com/google/android-fhir/tree/master/demo)    | [wiki](https://github.com/google/android-fhir/wiki/FHIR-Engine-Library#demo-app)                |
| Structured Data Capture Catalog App | [code](https://github.com/google/android-fhir/tree/master/catalog) | [wiki](https://github.com/google/android-fhir/wiki/Structured-Data-Capture-Library#catalog-app) |

**These applications are provided for demo purposes only. Do NOT use in production.**

## Contributing

The development of the SDK began as a collaborative effort between Google, [The World Health Organization](https://www.who.int/), and [Ona](https://ona.io/). Since then, a consortium of application developers have been contributing to the project.

To contribute to the project, please see [Contributing](https://github.com/google/android-fhir/wiki/Contributing) to get started.

## Feedback and getting help

You can create a [GitHub issue](https://github.com/google/android-fhir/issues) for bugs and feature requests.

In case you find any security bug, please do NOT create a Github issue. Instead, email us at <android-fhir-sdk-feedback@google.com>.

If you want to provide any feedback or discuss use cases you can: 
* email us at <android-fhir-sdk-feedback@google.com>
* start a [GitHub discussion](https://github.com/google/android-fhir/discussions)
* start a new topic in [android](https://chat.fhir.org/#narrow/stream/276344-android), [questionnaire](https://chat.fhir.org/#narrow/stream/179255-questionnaire), [implementers](https://chat.fhir.org/#narrow/stream/179166-implementers), or [WHO SMART guidelines](https://chat.fhir.org/#narrow/stream/310477-who-smart-guidelines) stream in the [FHIR Zulip chat](https://chat.fhir.org/)

## Disclaimer
This product is not intended to be a medical device.

HL7®, and FHIR® are the registered trademarks of Health Level Seven International and their use of
these trademarks does not constitute an endorsement by HL7.
