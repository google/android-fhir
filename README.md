# Android FHIR SDK

[![master](https://github.com/google/android-fhir/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/google/android-fhir/actions/workflows/build.yml) [![master](https://github.com/google/android-fhir/actions/workflows/device-tests.yml/badge.svg?branch=master)](https://github.com/google/android-fhir/actions/workflows/device-tests.yml) [![codecov](https://codecov.io/gh/google/android-fhir/branch/master/graph/badge.svg?token=PDSC4WRDTQ)](https://codecov.io/gh/google/android-fhir/branch/master) [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![project chat](https://img.shields.io/badge/zulip-join_chat-brightgreen.svg)](https://chat.fhir.org/#narrow/stream/276344-android)

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
| Data Capture Library | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/data-capture/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:data-capture) | [code](https://github.com/google/android-fhir/tree/master/datacapture)| [wiki](https://github.com/google/android-fhir/wiki/Structured-Data-Capture-Library) | Collect, validate, and process healthcare data on Android                           |
| FHIR Engine Library  | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/engine/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:engine)             | [code](https://github.com/google/android-fhir/tree/master/engine)     | [wiki](https://github.com/google/android-fhir/wiki/FHIR-Engine-Library)             | Store and manage FHIR resources locally on Android and synchronize with FHIR server |
| Workflow Library     | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/workflow/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:workflow)         | [code](https://github.com/google/android-fhir/tree/master/workflow)   | [wiki](https://github.com/google/android-fhir/wiki/Workflow-Library)                | Provide decision support and analytics in clinical workflow on Android including implementation of specific FHIR operations ($measure_evaluate and $apply) |

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

## Feedback and getting help
Bugs and feature requests can be filed with [Github issues](https://github.com/google/android-fhir/issues). See the section on [How to Contribute](https://github.com/google/android-fhir/blob/master/docs/contributing.md) first

If you want to provide any feedback or discuss use cases you can: 
* Email us at <android-fhir-sdk-feedback@google.com>
* For general Android FHIR SDK discussion, join the FHIR Zulip chat for [Android](https://chat.fhir.org/#narrow/stream/276344-android) 
* For WHO SMART Guidelines topics, see the FHIR Zulip chat for [WHO SMART Guidelines](https://chat.fhir.org/#narrow/stream/310477-who-smart-guidelines)

## Disclaimer

This is not an officially supported Google product.

This product is not intended to be a medical device.

HL7®, and FHIR® are the registered trademarks of Health Level Seven International and their use of
these trademarks does not constitute an endorsement by HL7.
