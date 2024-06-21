# Welcome to Android FHIR SDK Technical Documentation

The Android FHIR SDK is a set of Kotlin libraries for building offline-capable, mobile-first
healthcare applications using the [HL7® FHIR® standard](https://www.hl7.org/fhir/) on Android. It
aims to accelerate the adoption of FHIR by making it easy to incorporate FHIR into new and existing
mobile applications.

Use the following table for documentation and resources to get started with each library in the SDK.

| Library              | Latest release                                                                                                                                                                                                                    | Code                                                                  | Docs                                                                                | Codelab | Summary                                                                             |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------- | ----------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------- | - |
| Data Capture Library         | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/data-capture/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:data-capture) | [code](https://github.com/google/android-fhir/tree/master/datacapture)| [docs](use/SDCL/index.md) | [codelab](https://github.com/google/android-fhir/tree/master/codelabs/datacapture) | Collects, validates, and processes healthcare data on Android                           |
| FHIR Engine Library          | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/engine/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:engine)       | [code](https://github.com/google/android-fhir/tree/master/engine)     | [docs](use/FEL/index.md)             | [codelab](https://github.com/google/android-fhir/tree/master/codelabs/engine)      | Stores and manages FHIR resources locally on Android and synchronizes with FHIR server |
| Workflow Library             | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/workflow/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:workflow)     | [code](https://github.com/google/android-fhir/tree/master/workflow)   | [docs](use/WFL/index.md)                | |  Provides decision support and analytics in clinical workflow on Android including implementation of specific FHIR operations ($measure_evaluate and $apply) |
| Knowledge Manager Library    | [![Google Maven](https://badgen.net/maven/v/metadata-url/dl.google.com/dl/android/maven2/com/google/android/fhir/knowledge/maven-metadata.xml)](https://maven.google.com/web/index.html?#com.google.android.fhir:knowledge)    | [code](https://github.com/google/android-fhir/tree/master/knowledge)  |        | | Manages knowledge resources locally on Android and supports other libraries with knowledge resources                                               |

## Project information

* [Project roadmap](contrib/roadmap.md)
* [FAQs](faq.md)

## Resources

### FHIR and implementation guides

* [HL7 FHIR](https://www.hl7.org/fhir/)
* [Structured Data Capture](http://hl7.org/fhir/us/sdc/)
* [Clinical Quality Lanugage (CQL)](https://cql.hl7.org/)

### More on mobile health landscape

* [Digital Health Atlas](https://digitalhealthatlas.org/)
* [Global Goods Guidebook](https://digitalsquare.org/global-goods-guidebook)

### Community

* [Zulip FHIR chat](https://chat.fhir.org/) has the following streams that are relevant to this codebase:
  * [#android](https://chat.fhir.org/#narrow/stream/276344-android)
  * [#implementers](https://chat.fhir.org/#narrow/stream/179166-implementers)
  * [#questionnaire](https://chat.fhir.org/#narrow/stream/179255-questionnaire) for the SDC library and gallery
