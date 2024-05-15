# Use `ResourceMapper`

Mapping FHIR Questionnaire Responses to other FHIR resources (and back) allows
the structured data capture process to be more tightly integrated with clinical
workflows.

For example, if your application has a questionnaire for new patient
registration, your ultimate goal may be to create a
[FHIR Patient resource](https://www.hl7.org/fhir/patient.html) based on the
answers provided to use in your application. Or, if your application has a
questionnaire for entering test results, you could create a FHIR Observation
resource. The process of mapping a FHIR QuestionnaireResponse to one or more
other FHIR resources is called
[extraction](http://hl7.org/fhir/uv/sdc/extraction.html).

On the other hand, you may want to reduce data entry by loading values from
existing FHIR resources into your questionnaire. For example, if a questionnaire
asks for a patient's name and age, you can pre-populate that information from an
existing FHIR Patient resource. The process of mapping one or more FHIR
resources to a FHIR QuestionnaireResponse is called
[population](http://hl7.org/fhir/uv/sdc/populate.html).

This section shows how to use the
[`ResourceMapper`](https://github.com/google/android-fhir/blob/master/datacapture/src/main/java/com/google/android/fhir/datacapture/mapping/ResourceMapper.kt)
class from the Structured Data Capture Library to perform extraction and
population.

## Extract FHIR resources from a questionnaire response

The Structured Data Capture Library supports two mechanisms for data extraction:
[Definition-based extraction](http://hl7.org/fhir/uv/sdc/extraction.html#definition-based-extraction)
and
[StuctureMap-based extraction](http://hl7.org/fhir/uv/sdc/extraction.html#structuremap-based-extraction).

Both methods of extraction require a HAPI FHIR `Questionnaire` that includes
data extraction FHIR extensions, plus a corresponding `QuestionnaireResponse`
containing the answers to extract. You can use HAPI FHIR's
[`JsonParser.parseResource()`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/parser/JsonParser.html#parseResource\(ca.uhn.fhir.parser.json.JsonLikeStructure\))
from
[`FhirContext.newJsonParser()`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/FhirContext.html#newJsonParser\(\))
to deserialize your JSON content to a questionnaire:

```kotlin
val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

// From a JSON string
val myQuestionnaire =
    jsonParser.parseResource(questionnaireJsonString) as Questionnaire

// From a URI for a JSON file
val myQuestionnaire =
    jsonParser.parseResource(contentResolver.openInputStream(questionnaireUri)) as Questionnaire
```

The `QuestionnaireResponse` you use with data extraction is likely from calling
`QuestionnaireFragment.getQuestionnaireResponse()` after your user fills out a
questionnaire:

```kotlin
val myQuestionnaireResponse = fragment.getQuestionnaireResponse()
```

[Learn more about QuestionnaireFragment](Use-QuestionnaireFragment.md)
and how to collect responses from a questionnaire.

Next, call `ResourceMapper.extract()` to extract the FHIR resources. It returns
a
[HAPI FHIR Bundle](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Bundle.html)
containing one or more FHIR resources as specified by the data extraction FHIR
extensions in the questionnaire. Note that `ResourceMapper.extract()` is a
suspend function, so the following examples must be called from an appropriate
[coroutine](https://developer.android.com/kotlin/coroutines), such as
[`ViewModelScope` if you are using a ViewModel in your app](https://medium.com/androiddevelopers/easy-coroutines-in-android-viewmodelscope-25bffb605471).

### Definition-based extraction

For Definition-based extraction, all the metadata required to perform data
extraction are within the questionnaire, so no additional information is
necessary. Simply pass the `Questionnaire` and `QuestionnaireResponse` to
`ResourceMapper.extract()`:

```kotlin
val bundle = ResourceMapper.extract(questionnaire, questionnaireResponse)
```

### StructureMap-based extraction

In addition to the Questionnaire and QuestionnaireResponse, StructureMap-based
Extraction also requires a `StructureMapExtractionContext` to retrieve a
StructureMap. To create a `StructureMapExtractionContext`, pass your
application's context and provide a `structureMapProvider` lambda function which
returns the `StructureMap`.

The function includes a String parameter which contains the canonical URL for
the Structure Map referenced in the
[Target structure map extension](http://hl7.org/fhir/uv/sdc/StructureDefinition-sdc-questionnaire-targetStructureMap.html)
of the questionnaire, and a HAPI FHIR
[`IWorkerContext`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/context/IWorkerContext.html)
which may be used with other HAPI FHIR classes, like
[`StructureMapUtilities`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/utils/StructureMapUtilities.html).

A StructureMap may be written in the
[FHIR Mapping Language](http://hl7.org/fhir/R4/mapping-language.html) or as a
[StructureMap FHIR resource](http://hl7.org/fhir/R4/structuremap.html).

For example, if your StructureMap is hard-coded and written in the FHIR Mapping
Language:

```kotlin
val mappingStr = "map ..."
val bundle = ResourceMapper.extract(
    myQuestionnaire,
    myQuestionnaireResponse,
    StructureMapExtractionContext(context = applicationContext) { _, worker ->
        StructureMapUtilities(worker).parse(mapping, "")
    },
)
```

As another example, this code uses a StructureMap resource as JSON based on its
URL:

```kotlin
val bundle = ResourceMapper.extract(
    myQuestionnaire,
    myQuestionnaireResponse,
    StructureMapExtractionContext(context = applicationContext) { targetStructureMapUrl, _ ->
        val structureMapJson = getJsonFromUrl(targetStructureMapUrl)
        jsonParser.parseResource(mappingJson) as StructureMap
        )
    },
)
```

## Populate a questionnaire response based on other FHIR resources

The Structured Data Capture Library supports
[expression-based population](http://hl7.org/fhir/uv/sdc/populate.html#expression-based-population).
If a questionnaire implements the
[SDC Questionnaire Populate - Expression profile](http://hl7.org/fhir/uv/sdc/StructureDefinition-sdc-questionnaire-pop-exp.html),
then you can use `ResourceMapper.populate()` to generate a questionnaire
response based on the values in other FHIR resources:

```kotlin
val questionnaireResponse = ResourceMapper.populate(questionnaire, resource1, resource2, ...)
```

In this example, `resource1, resource2, ...` are FHIR resources represented by
instances of
[HAPI FHIR Structures](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/package-summary.html).
To understand which resources you should include, look at the expression-based
population extensions used in the questionnaire, most often the
[Initial Expression extension](https://hl7.org/fhir/uv/sdc/StructureDefinition-sdc-questionnaire-initialExpression.html).

`ResourceMapper.populate()` returns a `QuestionnaireResponse` which can be used
to
[pre-fill a questionnaire with existing answers](Use-QuestionnaireFragment.md#pre-fill-a-questionnaire-with-existing-answers).
