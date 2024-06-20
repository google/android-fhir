# Customize how a Questionnaire is displayed

The Structured Data Capture Library provides default UI components and styles to
render FHIR Questionnaire resources based on
[Material Design](https://material.io/design) and the
[Structured Data Capture Implementation Guide](https://build.fhir.org/ig/HL7/sdc/).
In practice, you may want to customize how questionnaires look, and this guide
explains the two primary ways to do so:

* Adjust the existing components to match the style of the rest of your
    application
* Create new components to collect information in non-standard ways, e.g.
    connected devices

## Customize styles

The Structured Data Capture Library includes a default
[theme](https://developer.android.com/guide/topics/ui/look-and-feel/themes)
[`Theme.Questionnaire`](https://github.com/google/android-fhir/blob/master/datacapture/src/main/res/values/styles.xml#L36)
which defines a number of custom attributes used to style questionnaires. For
example, `questionnaireQuestionTextStyle` defines the style for all question
text, and changing it affects all rendered questionnaires. The custom attributes
are:

* `groupHeaderTextAppearanceQuestionnaire`
* `questionnaireQuestionTextStyle`
* `questionnaireSubtitleTextStyle`
* `questionnaireRadioButtonStyle`
* `questionnaireCheckBoxStyle`
* `questionnaireDropDownTextStyle`
* `questionnaireDropdownLayoutStyle`
* `questionnaireTextInputLayoutStyle`
* `questionnaireTextInputEditTextStyle`
* `questionnaireChipStyle`
* `questionnaireDialogTitleStyle`
* `questionnaireDialogButtonStyle`
* `questionnaireAddAnotherAnswerButtonStyle`
* `questionnaireErrorTextStyle`
* `questionnaireButtonStyle`
* `questionnaireSubmitButtonStyle`

To customize the styles used to render questionnaires, create a theme with
different values for the custom attributes. A simple way to do this is to extend
the `Theme.Questionnaire` theme and override the attributes you want to change.

For example, to change the appearance of question text, open your project's
`res/values/styles.xml` file and add a new theme that extends
`Theme.Questionnaire`, then set the `headerTextAppearanceQuestionnaire`
attribute to your preferred value:

```xml
<style name="Theme.MyQuestionnaire" parent="Theme.Questionnaire">
    <item name="questionnaireQuestionTextStyle">
        @style/TextAppearance.MaterialComponents.Subtitle2
    </item>
</style>
```

Next, edit your application's default theme, typically in the
`res/values/themes.xml` file, and add the attribute `questionnaire_theme` set to
the new theme you just created:

```xml
<style name="Theme.MyApplication" parent="Theme.Material3.DayNight.NoActionBar">
    ...
    <item name="questionnaire_theme">@style/Theme.MyQuestionnaire</item>
</style>
```

## Custom questionnaire components

The Structured Data Capture Library uses
[custom UI components](https://github.com/google/android-fhir/tree/master/datacapture/src/main/java/com/google/android/fhir/datacapture/views)
to render questions in a questionnaire. There are predefined components for most
question item types so you do not need to do anything special for most
questions. However, if you want to render a question in a way not described in
the FHIR standard of the SDC implementation guide, you can create a custom
component.

### 1. Create a factory of custom ViewHolderFactoryMatchersProvider
Develop a class that implements the [QuestionnaireItemViewHolderFactoryMatchersProviderFactory](https://github.com/google/android-fhir/blob/ced8527a5481972591615ad4364487e89130fb6e/datacapture/src/main/java/com/google/android/fhir/datacapture/QuestionnaireFragment.kt#L563) interface. This class acts as a registry for associating custom widgets with their corresponding view holder factories. ([example](https://github.com/google/android-fhir/blob/master/catalog/src/main/java/com/google/android/fhir/catalog/ContribQuestionnaireItemViewHolderFactoryMatchersProviderFactory.kt))

As part of this you need to define:-

1. factory: The custom component factory to use.
2. matches: A predicate function which, given a
    [`Questionnaire.QuestionnaireItemComponent`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Questionnaire.QuestionnaireItemComponent.html), returns true if the factory should apply to that item.

### 2. Register the above factory with datacapture library via DataCaptureConfig
Within your [DataCaptureConfig](https://github.com/google/android-fhir/blob/ced8527a5481972591615ad4364487e89130fb6e/datacapture/src/main/java/com/google/android/fhir/datacapture/DataCaptureConfig.kt#L35) object, assign the custom factory matchers provider to the `questionnaireItemViewHolderFactoryMatchersProviderFactory` property. This step makes your provider available for resolving view holders during questionnaire rendering. ([example](https://github.com/google/android-fhir/blob/ced8527a5481972591615ad4364487e89130fb6e/catalog/src/main/java/com/google/android/fhir/catalog/CatalogApplication.kt#L42C5-L47C8))

### 3. Configure the QuestionnaireFragment Builder
When constructing your QuestionnaireFragment using the builder pattern, utilize the `setCustomQuestionnaireItemViewHolderFactoryMatchersProvider` method. Provide the appropriate string identifier to specify which custom widgets to use. ([example](https://github.com/google/android-fhir/blob/ced8527a5481972591615ad4364487e89130fb6e/catalog/src/main/java/com/google/android/fhir/catalog/DemoQuestionnaireFragment.kt#L142C13-L150C23))

## Localize questionnaires

When rendering your questionnaire, the library will look for the
[translation extension](https://www.hl7.org/fhir/R4/languages.html##ext), and if
the lang element matches the application default locale, will use the value of
the content element of the extension instead of the text element of the
questionnaire item. You can also use the
<code>[Locale.setDefault()](https://developer.android.com/reference/java/util/Locale#setDefault\(java.util.Locale\))</code>
method to manually set the locale to check against.
