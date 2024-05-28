# Getting Started

This page describes how to configure an Android Studio project to use the
Structured Data Capture Library, and simple examples of how to use the library.

This guide is intended for developers who are familiar with basic Android
development with Kotlin, and proficient in working with
[FHIR](http://hl7.org/fhir/) concepts and resources.

## Dependencies

The Structured Data Capture Library is available through
[Google's Maven repository](https://maven.google.com/web/index.html), which
should be already configured for Android projects created in Android Studio 3.0
and higher.

Add the following dependencies to your module's app-level `build.gradle` file,
typically `app/build.gradle`:

```gradle
dependencies {
  // ...

  implementation 'com.google.android.fhir:data-capture:1.0.0'
  implementation 'androidx.fragment:fragment-ktx:1.5.5'
}
```

The minimum API level supported is 24. The library also requires
[Android Gradle Plugin](https://developer.android.com/studio/releases/gradle-plugin)
version 4.0.0 or later for
[Java 8+ API desugaring support](https://developer.android.com/studio/write/java8-support#library-desugaring).

The following examples assume you already have a
[FHIR questionnaire](https://www.hl7.org/fhir/questionnaire.html) as a JSON
file. If you need one, the FHIR specification has
[several examples](https://www.hl7.org/fhir/questionnaire-examples.html) like
[this one](https://www.hl7.org/fhir/questionnaire-example-f201-lifelines.json).

## Display a Questionnaire

`QuestionnaireFragment` is the interface for working with
[FHIR questionnaires](https://www.hl7.org/fhir/questionnaire.html), and also a
[fragment](https://developer.android.com/guide/fragments) for rendering them.
See the QuestionnaireFragment guide for details. To render a questionnaire in
your app, follow these steps:

1. Add a `FragmentContainerView` to your activity's layout to contain the
    Questionnaire.

    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <androidx.constraintlayout.widget.ConstraintLayout
    ... >

    <androidx.fragment.app.FragmentContainerView
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@+id/fragment_container_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>
    ```

1. Create a bundle with the JSON questionnaire content for the fragment, in
    this case as a `String` read from a JSON file stored in the `assets` folder.

    ```kotlin
    // Small questionnaires can be read directly as a string, larger ones
    // should be passed as a URI instead.
    val questionnaireJsonString =
    application.assets.open("questionnaire.json")
        .bufferedReader().use { it.readText() }

    val bundle = bundleOf( QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to
    questionnaireJsonString )
    ```

1. Set the fragment to the `FragmentContainerView`.

    ```kotlin
    if (savedInstanceState == null) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<QuestionnaireFragment>(R.id.fragment_container_view, args = bundle)
        }
    }
    ```

## Get a QuestionnaireResponse

The `getQuestionnaireResponse` function of `QuestionnaireFragment` returns the
current form answers in a
[FHIR QuestionnaireResponse](http://www.hl7.org/fhir/questionnaireresponse.html),
specifically as an instance of the
[HAPI FHIR Structures data model's QuestionnaireResponse](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/QuestionnaireResponse.html).
This provides a
[convenient interface](https://hapifhir.io/hapi-fhir/docs/model/working_with_resources.html)
to work with the data:

```kotlin
val fragment: QuestionnaireFragment =
    supportFragmentManager.findFragmentById(R.id.fragment_container_view) as QuestionnaireFragment
val questionnaireResponse = fragment.getQuestionnaireResponse()

// For example, convert the whole response to a string
val questionnaireResponseString =  context.newJsonParser().encodeResourceToString(questionnaireResponse)
Log.d(
    "response", questionnaireResponseString
)
```

This could be in a callback for the user after pressing a Submit button.

## Extract FHIR Resources from a QuestionnaireResponse

`ResourceMapper` converts data between a QuestionnaireResponse and other FHIR
resources by implementing data
[extraction](http://build.fhir.org/ig/HL7/sdc/extraction.html) and
[population](http://build.fhir.org/ig/HL7/sdc/populate.html) from the
[HL7 Structured Data Capture Implementation Guide](http://build.fhir.org/ig/HL7/sdc/).
See the ResourceMapper guide for more information.

The Structured Data Capture Library supports two mechanisms for data extraction:
[Definition-based extraction](http://hl7.org/fhir/uv/sdc/extraction.html#definition-based-extraction)
and
[StuctureMap-based extraction](http://hl7.org/fhir/uv/sdc/extraction.html#structuremap-based-extraction).

Both methods require a `Questionnaire` and corresponding
`QuestionnaireResponse`:

```kotlin
val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

val myQuestionnaire =
    jsonParser.parseResource(questionnaireJson) as Questionnaire

// also need the QuestionnaireResponse, probably like this
val myQuestionnaireResponse = fragment.getQuestionnaireResponse()
```

For Definition-based extraction, just pass the `Questionnaire` and
`QuestionnaireResponse` to `ResourceMapper.extract()`.

```kotlin
lifecycle.coroutineScope.launch {
    val bundle =
        ResourceMapper.extract(questionnaire, questionnaireResponse)
}
```

StructureMap-based Extraction also requires a `StructureMapExtractionContext`
that contains the StructureMap. For example, if your StructureMap is written in
the [FHIR Mapping Language](http://hl7.org/fhir/R4/mapping-language.html):

```kotlin
val mappingStr = "map ..."
lifecycle.coroutineScope.launch {
    val bundle =
        ResourceMapper.extract(
            myQuestionnaire,
            myQuestionnaireResponse,
            StructureMapExtractionContext(context = applicationContext) { _, worker ->
                StructureMapUtilities(worker).parse(mappingStr, "")
            },
        )
}
```

The `extract` function is a suspend function, so these examples must be called
from an appropriate
[coroutine](https://developer.android.com/kotlin/coroutines).

## Further reading

* [Use QuestionnaireFragment](Use-QuestionnaireFragment.md)
* [Use ResourceMapper](Use-ResourceMapper.md)
* [Customize how a Questionnaire is displayed](Customize-how-a-Questionnaire-is-displayed.md)
* [Reference external data](Reference-external-data.md)
