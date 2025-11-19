package com.google.android.fhir.datacapture.extensions

import com.google.android.fhir.datacapture.extensions.localizedTextSpanned
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem

/**
 * Appends ' *' to [Questionnaire.QuestionnaireItemComponent.localizedTextSpanned] text if
 * [Questionnaire.QuestionnaireItemComponent.required] is true.
 */
fun appendAsteriskToQuestionText(
    questionnaireViewItem: QuestionnaireViewItem,
): Spanned {
    return SpannableStringBuilder().apply {
        questionnaireViewItem.questionText?.let { append(it) }
        if (
            questionnaireViewItem.questionViewTextConfiguration.showAsterisk &&
            questionnaireViewItem.questionnaireItem.required &&
            !questionnaireViewItem.questionnaireItem.localizedTextSpanned.isNullOrEmpty()
        ) {
            append(context.applicationContext.getString(R.string.space_asterisk))
        }
    }
}
