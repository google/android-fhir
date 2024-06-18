# Use `QuestionnaireFragment`

`QuestionnaireFragment` is the main interface for displaying FHIR Questionnaires
and getting user responses as FHIR QuestionnareResponses. In this section, we
will discuss how to use `QuestionnaireFragment` in more detail. To use
`QuestionnaireFragment` effectively, you should also be familiar with using
[Fragments in Android](https://developer.android.com/guide/fragments).

## Display a Questionnaire in your application

Place a `QuestionnaireFragment` in your activity or fragment whenever you want
to display a questionnaire. First, add a FragmentContainerView to your
activity's layout to contain the rendered Questionnaire, as shown in the
following example:

```xml
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragment_container_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

Next, define the content of the FHIR Questionnaire to render. The Questionnaire
must be JSON-encoded and can be provided as a `String` or a `URI` to a JSON
file. These values are provided in `QuestionnaireFragment.Builder` used to create a
`QuestionnaireFragment`.

The following example reads a file in the `assets/` directory to a `String` which will be passed to the `QuestionnaireFragment`.

```kotlin
val questionnaire: String =
    application.assets.open("questionnaire.json").bufferedReader().use { it.readText() }
```

Providing a `String` is only suitable for Questionnaires that are a few KBs in
size, or
[you may get a TransactionTooLargeException exception](https://developer.android.com/guide/components/activities/parcelables-and-bundles#sdba).
For larger FHIR Questionnaires, use the URI-based option below.

Alternatively, you may use a `URI` pointing to a JSON file containing the
questionnaire. The example below creates a `URI` for a questionnaire file stored
as a raw resource.

```kotlin
val questionnaire: Uri =
 Uri.parse("android.resource://" + this.packageName + "/" + R.raw.questionnaire)
```

How you get or create a `URI` for your questionnaire will depend on your data
source. You cannot get a `URI` for a file in the `assets/` directory. However,
you can use the following code to create a temporary copy and get a `URI` for
it.

```kotlin
/**
* Returns a [Uri] pointing to a temporary file containing the content of the asset file with
* [filename].
*/
private suspend fun createUri(filename: String) =
 withContext(backgroundContext) {
   val application = getApplication<Application>()
   val outputFile = File(application.externalCacheDir, filename)
   application.assets.open(filename).use { inputStream ->
     outputFile.outputStream().use { outputStream -> IOUtils.copy(inputStream, outputStream) }
   }
   Uri.fromFile(outputFile)
 }
```

Use `QuestionnaireFragment.Builder` to create the `QuestionnaireFragment`. You must call one of `setQuestionnaire(questionnaireJson: String)` or
`setQuestionnaire(questionnaireUri: Uri)` in  `QuestionnaireFragment.Builder`. If neither is provided, the system throws an `IllegalStateException` exception. If
both are provided, `setQuestionnaire(questionnaireUri: Uri)` takes precedence and
`setQuestionnaire(questionnaireJson: String)` is ignored.

In a real application, the FHIR Questionnaire JSON can be loaded from a file
(like in the examples above), a database, the network, or even hardcoded,
depending on your use case. If you are using any I/O operations to load the
JSON, it should be done asynchronously such as by using
[coroutines](https://developer.android.com/kotlin/coroutines).

Finally, use
<code>[FragmentManager](https://developer.android.com/guide/fragments/fragmentmanager)</code>
to programmatically set the fragment to the FragmentContainerView.

```kotlin
if (savedInstanceState == null) {
 supportFragmentManager.commit {
   setReorderingAllowed(true)
   add(
     R.id.fragment_container_view,
     QuestionnaireFragment.builder().setQuestionnaire(questionnaire).build(),
     QUESTIONNAIRE_FRAGMENT_TAG
   )
 }
}
```

This example shows the minimal code to
[add a fragment programmatically](https://developer.android.com/guide/fragments/create#add-programmatic),
but Android strongly recommends using the
[Navigation library](https://developer.android.com/guide/navigation) to manage
navigation in a real app.

## Collect a Questionnaire response

Once the user has finished filling out the questionnaire, you can collect the
questionnaire response. Typically you should use `FragmentManager` to find your
questionnaire fragment and then call `getQuestionnaireResponse()`.

```kotlin
val fragment: QuestionnaireFragment =
 supportFragmentManager.findFragmentById(R.id.fragment_container_view) as QuestionnaireFragment
val questionnaireResponse = fragment.getQuestionnaireResponse()
```

The form answers are structured as a
[FHIR QuestionnaireResponse](http://www.hl7.org/fhir/questionnaireresponse.html),
which is a structured set of answers to the questions in the corresponding FHIR
Questionnaire. More specifically, it an instance of
<code>[org.hl7.fhir.r4.model.QuestionnaireResponse](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/QuestionnaireResponse.html)</code>,
a class in the [HAPI FHIR](https://hapifhir.io/)
[Structures library](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/)
which is used as the in-memory data model for FHIR resources in the SDK. This
provides a relatively convenient interface to work with the data:

```kotlin
// Get the first answer for the first item
val questionnaireResponseFirstAnswer =
 questionnaireResponse.item[0].answer[0].value.asStringValue()

// Convert the whole response to a JSON string and send to log
val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
val questionnaireResponseString = jsonParser.encodeResourceToString(questionnaireResponse)
Log.d( "response", questionnaireResponseString)

```

Learn more about
[working with HAPI FHIR resources](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html).

What you do with the questionnaire response depends on your use case:

* If your existing application does not store data using FHIR, you can
    navigate the response's item and answer structure to get and store responses
    in your app's native format.
* If the questionnaire you are using implements
    [form data extraction](http://build.fhir.org/ig/HL7/sdc/extraction.html),
    you can use Structured Data Capture Library's `ResourceMapper` to extract
    additional FHIR resources based on the questionnaire response.

## Pre-fill a questionnaire with existing answers

If you want your questionnaire to start with some answers already filled,
include a questionnaire response in your arguments bundle for your
`QuestionnaireFragment`.

```kotlin
val questionnaireFragment =
 QuestionnaireFragment.builder()
   .setQuestionnaire(questionnaire)
   .setQuestionnaireResponse(questionnaireResponse)
   .build()
```

This may be useful if, for example, you allow a user to edit a form they have
previously submitted, or to pre-fill certain fields where you already know the
value based on another source.

Provide one of `setQuestionnaire(questionnaireJson: String)` or
`setQuestionnaire(questionnaireUri: Uri)` in the QuestionnaireFragment.Builder.
 If both are provided, `setQuestionnaire(questionnaireUri: Uri)` takes precedence and
`setQuestionnaire(questionnaireJson: String)` is ignored.

Similar to questionnaires, do not pass questionnaire responses over a few KBs in
size as a `String` in `setQuestionnaireResponse(questionnaireResponseJson: String)`,
 but instead provide a `URI` using `setQuestionnaireResponse(questionnaireResponseUri: Uri)`.

## Further reading

* [Customize how a questionnaire is displayed](Customize-how-a-Questionnaire-is-displayed.md)
