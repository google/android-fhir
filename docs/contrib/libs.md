# Libraries

## External Dependencies

This Android FHIR SDK uses a number of external libraries.

Their exact version numbers are shown on [the Dependency Graph Insights](https://github.com/google/android-fhir/network/dependencies), which is automatically updated.

## Version Upgrades

This section defines the process one goes through when making changes to any of the dependent libraries on the FHIR SDK. An example of a library is the [Workflow Library](../use/WFL/index.md).

**Step 1:**

* Make changes, updates, edits on the library's codebase.
* Make sure to update the [Releases.kt](https://github.com/google/android-fhir/blob/master/buildSrc/src/main/kotlin/Releases.kt) configuration file with a new _Version/Artifact ID_

**Step 2:**

* Make a PR (pull request) with the above changes, request a code review

**Step 3:**

* When PR is reviewed and merged - request for the artifact to be published on maven

**Step 4:**

* Update your/any dependent PR (PR using the library) with the new _Artifact ID_ and make/trigger the CI

## Common Library

The _common_ library module contains code that is shared across other modules e.g. _engine, datacapture_ . During development you might want to make updates to the _common_ module and test them on the dependant library.

To make developing/testing these type of updates more efficient, it is recommended to replace the `implementation(Dependencies.androidFhirCommon)` dependency configuration with `implementation(project(":common"))` in the `build.gradle.kts` file. Then once the PR with the changes to _common_ is merged in and the artifact published, you can revert.

Remember to update the `Versions.androidFhirCommon` variable with the correct/new version of the published artifact in the `Dependencies.kt` file.
