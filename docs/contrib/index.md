# Setup

## Android Studio

Use Android Studio 4.2+.

## Node.js

Install Node.js (e.g. [via package manager](https://nodejs.org/en/download/package-manager/)) which is needed for the `prettier` plugin we use to format `XML` files.

## Kotlin style

The codebase follows [google-java-format](https://github.com/google/google-java-format) instead of the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) because google-java-format is strict and deterministic, and therefore removes formatting as a concern for developers altogether.

If you would like Android Studio to help format your code, follow these steps to set up your Android Studio:

1. Install and configure the [ktfmt plugin](https://github.com/facebookincubator/ktfmt) in Android Studio by following these steps:
    1. Go to Android Studio's `Settings` (or `Preferences`), select the `Plugins` category, click the `Marketplace` tab, search for the `ktfmt` plugin, and click the `Install` button
    1. In Android Studio's `Settings` (or `Preferences`), go to `Editor` → `ktfmt Settings`, tick `Enable ktfmt`, change the `Code style` to `Google (Internal)`, and click `OK`
1. Indent 2 spaces. In Android Studio's `Settings` (or `Preferences`), go to `Editor` → `Code Style` → `Kotlin` → `Tabs and Indents`, set `Tab size`, `Indent` and `Continuation indent` to `2`, and click `OK`.
1. Use single name import sorted lexigraphically. In Android Studio's `Settings` (or `Preferences`), go to `Editor` → `Code Style` → `Kotlin` → `Imports`, in `Top-level Symbols` and `Java statics and Enum Members` sections select `Use single name import` option, remove all the rules in `Packages to Use Imports with '*'` and `Import Layout` sections and click `OK`.

Now you can go to `Code` → `Reformat code`, or press `Ctrl+Alt+L` (`⌘+⌥+L` for Mac) to automatically format code in Android Studio.

Note that you don't have to do any of these. You could rely on spotless to format any code you want to push. For details see below.

## XML style

We use [prettier](https://prettier.io/)'s [XML plugin](https://github.com/prettier/plugin-xml) to format the XML code. At the moment we have not discovered an Android Studio style configuration that would produce the same result. As a result, please run `./gradlew spotlessApply` to format the XML files.

# Development

## Common Library

The _common_ library module contains code that is shared across other modules e.g. _engine, datacapture_ . During development you might want to make updates to the _common_ module and test them on the dependant library.

To make developing/testing these type of updates more efficient, it is recommended to replace the `implementation(Dependencies.androidFhirCommon)` dependency configuration with `implementation(project(":common"))` in the `build.gradle.kts` file. Then once the PR with the changes to _common_ is merged in and the artifact published, you can revert.

Remember to update the `Versions.androidFhirCommon` variable with the correct/new version of the published artifact in the `Dependencies.kt` file

# Testing

## Unit Tests

### Naming Conventions

We follow the following naming convention for our test cases:

```
methodName_conditionUnderTest_expectedBehavior
```

For example:

```
isNumberEven_one_shouldReturnFalse
isNumberEven_two_shouldReturnTrue
```

## Instrumentation Tests

### UI Automation Testing

**For UI automation, “Page object framework” is used, Below is the structure of framework:**

**Structure:**

**Pages:**
Here all screens objects ,identifiers and functions are defined

**Tests:**
All tests for all screens are written here

**Testdata:**
Data which is used for testing mentioned here

**Setup QA tests:**

Precondition:

1. To launch App and its activity create file under tests folder(Right click pages->New -> Kotlin class/File(BaseTest):

 ```
open class BaseTest {
  @get:Rule
  val activityRule: ActivityScenarioRule<MainActivity> =
    ActivityScenarioRule(MainActivity::class.java)
}
```

Write testcase:

1. Right click pages->New -> Kotlin class/File(AddPatientPage.kt)
2. On newly created file, objects ,identifiers and functions can be defined
3. Define object and identifiers as below:

```
private val pageName = "Add Patient"
private val addPatientButton = R.id.add_patient
```

4. To write functions:

```
fun validate_page() {

}
```

5. To write verification of text

```
onView(allOf(withText(R.string.add_patient), withText(pageName))).check(matches(isDisplayed()))
```

6. To create Tests page for writing tests same as mentioned in step 1 (AddPatientTest.kt)
7. To access function from (AddPatientPage.kt), create its object:

```
class AddPatientTest() : BaseTest() {
private val addPatientPage: AddPatientPage = AddPatientPage()
}
```

8. Access function through object:

```
fun shouldBeAbleToValidatePage() {
addPatientPage.validate_page()
}
```

9. To run tests:

precondition:
device or emulator should be connected

Right click on test file created in step 6 -> Run 'AddPatientTest'

### Screenshot Testing

**To write Screenshot Tests**:

1. To Launch Activity:

```
@get:Rule var activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

  val grantPermissionRule: GrantPermissionRule =
    GrantPermissionRule.grant(
      android.Manifest.permission.READ_EXTERNAL_STORAGE,
      android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
```

2. To write functions:

```
fun testScreenshotEntireActivity() {

}
```

3. Launch activity through "activityrule":

`val activity = activityTestRule.launchActivity(null))
`

4. get view of element:

`
val view = activityTestRule.activity.findViewById<TextView>(R.id.search_src_text)
`

5. Record view through snapactivity:

`
snapActivity(activity).setName("Registered Patient List").record()
`

6. You can set name to the view and record that view:

`
snap(view).setName("sample_view_test").record()
`

**To Run Screenshot Tasks below:**

`clean<App Variant>Screenshots` - Clean last generated screenshot report

`pull<App Variant>Screenshots` - Pull screenshots from your device

`record<App Variant>ScreenshotTest` - Installs and runs screenshot tests, then records their output for later verification

`run<App Variant>ScreenshotTest` - Installs and runs screenshot tests, then generates a report

`verify<App Variant>ScreenshotTest` - Installs and runs screenshot tests, then verifies their output against previously recorded screenshots

**To run through ./gradlew below is the command:**

`$ ./gradlew runDebugAndroidTestScreenshotTest`

To run screenshot test for specific application:

`$ ./gradlew Demo:runDebugAndroidTestScreenshotTest`

**To directory run screenshot test using android studio:**

 Right click either the test folder or the test file in Android Studio and click 'Run Tests in ...':

## Spotless

We use Spotless to maintain the Java/Kotlin coding style in the codebase. Run the following command to check the codebase:

```
./gradlew spotlessCheck
```

and run the following command to apply fixes to the violations:

```
./gradlew spotlessApply
```

## License Headers

Spotless maintains the license headers for Kotlin files. Use addlicense to maintain license headers in other files:

```
addlicense -c "Google LLC" -l apache .
```

## Jacoco Test Report

### Run locally

To run the task locally:

```
$./gradlew jacocoTestReport
```

To run the task locally for a specific module:

```
$./gradlew :<module>:jacocoTestReport
```

The Jacoco test coverage report will be in the folder `<module>/build/reports/jacoco/jacocoTestReport`.

## Ruler

We use [Ruler](https://github.com/spotify/ruler) to generate reports on the APK size of our demo/catalog app. This allows us to track increases in our SDK's library size.

To generate these reports, run the `analyzeReleaseBundle` task on the project you are interested in. For example:

```
./gradlew :demo:analyzeReleaseBundle
```

The task will print a path to an HTML report, which is human-readable, and a JSON report, which can be used for automation.

# Code Reviews

PRs can only be merged if all of the following requirements are met:

* Require approval from [code owners](https://github.com/google/android-fhir/blob/master/CODEOWNERS)
* Require status checks to pass before merging
* Require branches to be up to date before merging

# Use unreleased GitHub build (for development/testing only)

Each GitHub build of the project also contains a maven repository. You can access unreleased features by downloading it and including it in your project. To acquire the maven repository, go the the [actions](https://github.com/google/android-fhir/actions) page, and click on the build you want. In the build `artifacts` dropdown, you will find a `maven-repository.zip` file. After downloading and extracting it to a local folder, you can add it to your gradle setup via:

```
repositories {
  maven {
    url "file:///<path to the unzipped folder>"
  }
}
```

These artifacts are versioned as a combination of the current version + buildid (e.g. `0.1.0-alpha01-build_123`). You can find the version in the zip file by checking the contents of it and update your build file to match that version.

# Common build problems

1. Build failed java heap space

   Set `org.gradle.jvmargs=-Xmx2048m` in project gradle.properties . If it still fails you can further increase the memory.

2. More than one file was found with OS independent path 'mozilla/public-suffix-list.txt'.

    Add this line to the packagingOptions in the build.gradle of you app

    `packagingOptions {
            exclude 'mozilla/public-suffix-list.txt'
        }`

===

naming convention for view snake_case

how to run tests

# Working with libraries

This section defines the process one goes through when making changes to any of the dependent libraries on the FHIR SDK. An example of a library is the [Workflow Library](../use/WFL/index.md).

**Step 1:**

* Make changes, updates, edits on the library's codebase. <br/>
* Make sure to update the [Releases.kt](https://github.com/google/android-fhir/blob/master/buildSrc/src/main/kotlin/Releases.kt) configuration file with a new _Version/Artifact ID_

**Step 2:**

* Make a PR (pull request) with the above changes, request a code review

**Step 3:**

* When PR is reviewed and merged - request for the artifact to be published on maven

**Step 4:**

* Update your/any dependent PR (PR using the library) with the new _Artifact ID_ and make/trigger the CI

**NB:** For a specific example on working with FHIR SDK's Common Library during development, see [Common Library](#common-library).
