# Android FHIR SDK

[![master](https://github.com/google/android-fhir/workflows/CI/badge.svg?branch=master)](https://github.com/google/android-fhir/actions?query=workflow%3ACI) [![codecov](https://codecov.io/gh/google/android-fhir/branch/master/graph/badge.svg?token=PDSC4WRDTQ)](https://codecov.io/gh/google/android-fhir/branch/master) [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![project chat](https://img.shields.io/badge/zulip-join_chat-brightgreen.svg)](https://chat.fhir.org/#narrow/stream/276344-android)

The Android FHIR SDK is a set of Kotlin libraries for building offline-capable, mobile-first
healthcare applications using the [HL7® FHIR® standard](https://www.hl7.org/fhir/) on Android. It
aims to accelerate the adoption of FHIR by making it easy to incorporate FHIR into new and existing
mobile applications.

## Requirements

The SDK supports Android 21 (lollipop) and above. Android Studio 4.0 or above is required for
[Java 8 library desugaring](https://developer.android.com/studio/preview/features#j8-desugar).

## Libraries

The SDK contains the following libraries:

| Library              | Latest release                                                                                                                                                                                                                    | Code                                                                  | Wiki                                                                                | Summary                                                                             |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------- | ----------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------- |
| FHIR Engine Library  | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/engine/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:engine)             | [code](https://github.com/google/android-fhir/tree/master/engine)     | [wiki](https://github.com/google/android-fhir/wiki/FHIR-Engine-Library)             | Store and manage FHIR resources locally on Android and synchronize with FHIR server |
| Data Capture Library | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/data-capture/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:data-capture) | [code](https://github.com/google/android-fhir/tree/master/datacapture)| [wiki](https://github.com/google/android-fhir/wiki/Structured-Data-Capture-Library) | Collect, validate, and process healthcare data on Android                           |
| Workflow Library     | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/workflow/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:workflow)         | [code](https://github.com/google/android-fhir/tree/master/workflow)   | [wiki](https://github.com/google/android-fhir/wiki/Workflow-Library)                | Provide decision support and analytics in clinical workflow on Android              |

## Demo apps

This repository also contains the following demo apps:

| Demo app                            | Code                                                               | Wiki                                                                                            |
| ----------------------------------- | ------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------- |
| FHIR Engine Demo App                | [code](https://github.com/google/android-fhir/tree/master/demo)    | [wiki](https://github.com/google/android-fhir/wiki/FHIR-Engine-Library#demo-app)                |
| Structured Data Capture Catalog App | [code](https://github.com/google/android-fhir/tree/master/catalog) | [wiki](https://github.com/google/android-fhir/wiki/Structured-Data-Capture-Library#catalog-app) |

**These applications are provided for demo purposes only. Do NOT use in production.**

## Contributing

The SDK is being developed by a consortium of application developers. We welcome contributions.
Please
see [How to Contribute](https://github.com/google/android-fhir/blob/master/docs/contributing.md)
and [Contributing](https://github.com/google/android-fhir/wiki/Contributing) for more information.

## Feedback

If you want to provide any feedback, discuss use cases, raise feature requests, or simply want to
get involved, please use the
[Discussions](https://github.com/google/android-fhir/discussions) section, or email us
at <android-fhir-sdk-feedback@google.com>.

## Disclaimer

This is not an officially supported Google product.

This product is not intended to be a medical device.

HL7®, and FHIR® are the registered trademarks of Health Level Seven International and their use of
these trademarks does not constitute an endorsement by HL7.