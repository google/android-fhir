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

This guide outlines the step-by-step process to create and incorporate custom widgets into FHIR questionnaires using the Android FHIR SDK.

Note: Examples given below are all from the [catalog](https://github.com/google/android-fhir/tree/master/catalog) application in this repository. We recommend you try out the application to see these concepts in action.

1. Create a layout for the Custom Component

   Design an XML layout file to define the visual structure and appearance of your custom widget.

    * Example: See this [example location_widget_view.xml](https://github.com/google/android-fhir/blob/master/contrib/locationwidget/src/main/res/layout/location_widget_view.xml).

2. Create a `QuestionnaireItemViewHolderFactory` class

   Implement a class that extends [QuestionnaireItemViewHolderFactory](https://github.com/google/android-fhir/blob/master/datacapture/src/main/java/com/google/android/fhir/datacapture/views/factories/QuestionnaireItemViewHolderFactory.kt#L35). This class will be responsible for creating and managing the view holder for your custom component.

    * Example: Check out this [example LocationWidgetViewHolderFactory](https://github.com/google/android-fhir/blob/master/contrib/locationwidget/src/main/java/com/google/android/fhir/datacapture/contrib/views/locationwidget/LocationWidgetViewHolderFactory.kt#L29).

   Within this class:

    * Pass the layout resource ID to the constructor.
    * Override `getQuestionnaireItemViewHolderDelegate()` to return a `QuestionnaireItemViewHolderDelegate` implementation. This delegate should include:
        * `init`:  Initializes the `RecyclerView.ViewHolder`.
        * `bind`: Binds data to the `RecyclerView.ViewHolder`.
        * `displayValidationResult`: Displays validation feedback for user input.
        * `setReadOnly`: Configures the UI for read-only mode.

3. Create `QuestionnaireItemViewHolderFactoryMatcher` Objects

   For each custom `ViewHolderFactory`, create a corresponding factory matcher object - [QuestionnaireItemViewHolderFactoryMatcher](https://github.com/google/android-fhir/blob/master/datacapture/src/main/java/com/google/android/fhir/datacapture/QuestionnaireFragment.kt#L538)

    * Example: Refer to these [matcher examples](https://github.com/google/android-fhir/blob/master/catalog/src/main/java/com/google/android/fhir/catalog/ContribQuestionnaireItemViewHolderFactoryMatchersProviderFactory.kt#L38C15-L45C16).

   Each matcher should define:

    * `factory`: The custom `ViewHolderFactory` instance.
    * `matches`: A predicate function that takes a [`Questionnaire.QuestionnaireItemComponent`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Questionnaire.QuestionnaireItemComponent.html) and returns `true` if this factory is suitable for rendering that item.

4. Create a `QuestionnaireItemViewHolderFactoryMatchersProviderFactory`

   Create a factory class that implements this interface. It acts as a central registry for your custom widget associations.

    * Example: This [example factory class](https://github.com/google/android-fhir/blob/master/catalog/src/main/java/com/google/android/fhir/catalog/ContribQuestionnaireItemViewHolderFactoryMatchersProviderFactory.kt) demonstrates this.

5. Register the Factory in `DataCaptureConfig`

   In your `DataCaptureConfig` object, set the `questionnaireItemViewHolderFactoryMatchersProviderFactory` property to your custom factory instance.

    * Example: See how this is done in [this code](https://github.com/google/android-fhir/blob/master/catalog/src/main/java/com/google/android/fhir/catalog/CatalogApplication.kt#L42C5-L47C8).

6. Use the Custom Widget in Your Questionnaire Fragment

   When building your `QuestionnaireFragment`, call the `setCustomQuestionnaireItemViewHolderFactoryMatchersProvider` method on the builder, providing the string identifier associated with your custom widget.

    * Example: This [usage example](https://github.com/google/android-fhir/blob/master/catalog/src/main/java/com/google/android/fhir/catalog/DemoQuestionnaireFragment.kt#L142C13-L150C23) shows how to set up the fragment.


## Localize questionnaires

When rendering your questionnaire, the library will look for the
[translation extension](https://www.hl7.org/fhir/R4/languages.html##ext), and if
the lang element matches the application default locale, will use the value of
the content element of the extension instead of the text element of the
questionnaire item. You can also use the
<code>[Locale.setDefault()](https://developer.android.com/reference/java/util/Locale#setDefault\(java.util.Locale\))</code>
method to manually set the locale to check against.
