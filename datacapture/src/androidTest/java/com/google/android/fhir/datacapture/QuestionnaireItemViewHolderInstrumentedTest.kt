package com.google.android.fhir.datacapture

import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.views.QuestionnaireItemCheckBoxGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemViewHolderInstrumentedTest {
  private val parent = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
  private val viewHolder = QuestionnaireItemCheckBoxGroupViewHolderFactory.create(parent)

  @Test
  fun hiddenExtension_true_shouldHideQuestionnaireItemView() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          addExtension().apply {
            url = EXTENSION_HIDDEN_URL
            setValue(BooleanType(true))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.isGone).isTrue()
  }
}