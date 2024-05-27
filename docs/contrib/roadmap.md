# Roadmap

The [product roadmap board](https://github.com/google/android-fhir/projects/7) is where you can learn about what features are being working on, what stage they're in, and release timelines. Have any questions or comments about items on the roadmap or want to know how to get involved? Please use the [discussions section](https://github.com/google/android-fhir/discussions), or email us at <android-fhir-sdk-feedback@google.com>.

## Guide to the roadmap

Every item pre-fixed with [Roadmap] relates to one or more issues on individual feature area project boards, with a label that indicates each of the following:

* A **release phase** that describes the next expected phase of the roadmap item. See below for a guide to release phases.
* A **feature area** that indicates the specific library to which the item belongs. For a list of feature areas, see below.

## Release phases

Release phases indicate the stages that the product or feature goes through, from early testing to general availability.

* **alpha**: Primarily for testing and feedback: Features still under heavy development, and subject to change. Not for production use, and no documentation provided.
* **beta**: Available in full or limited capacity: Features mostly complete and documented.
* **ga**: Generally available: Confidence within that community that this is ready for production use. Implementer able to provide support or leverage open source community

Some features will be in exploratory stages, and have no timeframe available. These are included in the roadmap for early feedback. These are marked as follows:

* **in design**: Feature in discovery phase. We have decided to build this feature, but are still figuring out how.
* **exploring**: Feature under consideration. We are considering building this feature, and gathering feedback on it or doing R&D.

## Roadmap stages

The roadmap is arranged on a [project board](https://github.com/google/android-fhir/projects/7) to give a sense for how far out each item is on the horizon (broken down by quarters). **This is just indicative at this stage and is subject to change**, especially further out on the timeline. There is also a specific column for exploratory R&D items where no current timeline has been decided.

## Feature areas

The SDK is designed in a modular fashion to enable developers to layer in a rich set of features for building FHIR capable mobile solutions. The feature areas, which are packaged as separate libraries are defined as follows:

* **FHIR Engine:** includes APIs for managing FHIR resources (using FHIR protos), interacting with the storage layer (sqllite db), searching and syncing
* **Data Capture Library:** Implementation of the FHIR SDC specification for interacting with Questionnaires includes population and extraction
* **Workflow Library:** Provides specific operations for generating CarePlans and indicators relevant to implementers working with [WHO SMART Guidelines content](https://www.who.int/news/item/18-02-2021-from-paper-to-digital-pathway-who-launches-first-smart-guidelines)

## Feedback and getting help

Bugs and feature requests can be filed with [Github issues](https://github.com/google/android-fhir/issues). See the section on [How to Contribute](https://github.com/google/android-fhir/blob/master/docs/contributing.md) first

If you want to provide any feedback or discuss use cases you can:

* Email us at <android-fhir-sdk-feedback@google.com>
* For general Android FHIR SDK discussion, join the FHIR Zulip chat for [Android](https://chat.fhir.org/#narrow/stream/276344-android)
* For WHO SMART Guidelines topics, see the FHIR Zulip chat for [WHO SMART Guidelines](https://chat.fhir.org/#narrow/stream/310477-who-smart-guidelines)
