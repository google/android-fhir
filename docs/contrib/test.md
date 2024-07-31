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

    ```kotlin
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

    ```kotlin
    private val pageName = "Add Patient"
    private val addPatientButton = R.id.add_patient
    ```

4. To write functions:

    ```kotlin
    fun validate_page() {

    }
    ```

5. To write verification of text

    ```kotlin
    onView(allOf(withText(R.string.add_patient), withText(pageName))).check(matches(isDisplayed()))
    ```

6. To create Tests page for writing tests same as mentioned in step 1 (AddPatientTest.kt)
7. To access function from (AddPatientPage.kt), create its object:

    ```kotlin
    class AddPatientTest() : BaseTest() {
    private val addPatientPage: AddPatientPage = AddPatientPage()
    }
    ```

8. Access function through object:

    ```kotlin
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

    ```kotlin
    @get:Rule var activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

      val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
          android.Manifest.permission.READ_EXTERNAL_STORAGE,
          android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    ```

2. To write functions:

    ```kotlin
    fun testScreenshotEntireActivity() {

    }
    ```

3. Launch activity through "activityrule":

    ```kotlin
    val activity = activityTestRule.launchActivity(null))
    ```

4. get view of element:

    ```kotlin
    val view = activityTestRule.activity.findViewById<TextView>(R.id.search_src_text)
    ```

5. Record view through snapactivity:

    ```kotlin
    snapActivity(activity).setName("Registered Patient List").record()
    ```

6. You can set name to the view and record that view:

    ```kotlin
    snap(view).setName("sample_view_test").record()
    ```

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

## Jacoco Test Report

### Run locally

To run the task locally:

```sh
$./gradlew jacocoTestReport
```

To run the task locally for a specific module:

```sh
$./gradlew :<module>:jacocoTestReport
```

The Jacoco test coverage report will be in the folder `<module>/build/reports/jacoco/jacocoTestReport`.

## Ruler

We use [Ruler](https://github.com/spotify/ruler) to generate reports on the APK size of our demo/catalog app. This allows us to track increases in our SDK's library size.

To generate these reports, run the `analyzeReleaseBundle` task on the project you are interested in. For example:

```sh
./gradlew :demo:analyzeReleaseBundle
```

The task will print a path to an HTML report, which is human-readable, and a JSON report, which can be used for automation.
