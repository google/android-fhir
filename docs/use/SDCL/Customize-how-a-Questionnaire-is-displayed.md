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

### Create a custom component

In order to create a custom component:

1. Create a layout for the custom component
    ([example](https://github.com/google/android-fhir/blob/master/catalog/src/main/res/layout/custom_number_picker_layout.xml)).
2. Create a class for your component that implements
    `QuestionnaireItemViewHolderFactory`
    ([example](https://github.com/google/android-fhir/blob/master/catalog/src/main/java/com/google/android/fhir/catalog/CustomNumberPickerFactory.kt)).
    In that class:
    1. Pass the layout resource for your custom component in the constructor of
        your custom factory.
    2. Override the `getQuestionnaireItemViewHolderDelegate()` function. It
        must return a `QuestionnaireItemViewHolderDelegate` which implements the
        following functions:
3. `init`: a delegate function for the `init` function of
    `RecyclerView.ViewHolder`
4. `bind`: a delegate function for the `bind` function of
    `RecyclerView.ViewHolder`
5. `displayValidationResult`: displays the validation result for the answer(s)
    provided by the user
6. `setReadOnly`: configures the UI based on the read-only status of the
    questionnaire item

### Apply a custom component to questions

Now that you have defined the custom widget and its behavior, it is time to
configure the Structured Data Capture Library in order for the custom widget to
be applied to the appropriate questions.

1. Create a QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher
    ([example](https://github.com/google/android-fhir/blob/master/catalog/src/main/java/com/google/android/fhir/catalog/CustomQuestionnaireFragment.kt#L26))
    that defines:
    1. factory: The custom component factory to use.
    2. matches: A predicate function which, given a
    [`Questionnaire.QuestionnaireItemComponent`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Questionnaire.QuestionnaireItemComponent.html),
    returns true if the factory should apply to that item.
2. Create a custom implementation of `QuestionnaireFragment` that
    overrides `getCustomQuestionnaireItemViewHolderFactoryMatchers`
    ([example](https://github.com/google/android-fhir/blob/master/catalog/src/main/java/com/google/android/fhir/catalog/CustomQuestionnaireFragment.kt#L22)).
    It should return a list that contains your
    `QuestionnaireItemViewHolderFactoryMatcher`.
3. When rendering your questionnaire, use your custom implementation of
    <code>QuestionnaireFragment</code> instead.

## Localize questionnaires

When rendering your questionnaire, the library will look for the
[translation extension](http://hl7.org/fhir/extension-translation.html), and if
the lang element matches the application default locale, will use the value of
the content element of the extension instead of the text element of the
questionnaire item. You can also use the
<code>[Locale.setDefault()](https://developer.android.com/reference/java/util/Locale#setDefault\(java.util.Locale\))</code>
method to manually set the locale to check against.
