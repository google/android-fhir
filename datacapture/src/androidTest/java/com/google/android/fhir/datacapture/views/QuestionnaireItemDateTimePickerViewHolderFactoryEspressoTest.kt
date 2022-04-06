package com.google.android.fhir.datacapture.views

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemDateTimePickerViewHolderFactoryEspressoTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule<TestActivity>(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder
  @Before
  fun setup() {
    activityScenarioRule.getScenario().onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = QuestionnaireItemDateTimePickerViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun shouldSetFirstDateInputThenTimeInput() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}

    runOnUI { viewHolder.bind(questionnaireItemView) }

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.date_input_edit_text).text.toString()
      )
      .isEmpty()
    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.time_input_edit_text).text.toString()
      )
      .isEmpty()

    onView(withId(R.id.date_input_edit_text)).perform(ViewActions.click())
    onView(withText("OK")).perform(ViewActions.click())
    onView(withId(R.id.time_input_edit_text)).perform(ViewActions.click())
    onView(withText("OK")).perform(ViewActions.click())

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.date_input_edit_text).text.toString()
      )
      .isNotEmpty()
    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.time_input_edit_text).text.toString()
      )
      .isNotEmpty()
  }

  @Test
  fun shouldSetFirstTimeInputThenDateInput() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}

    runOnUI { viewHolder.bind(questionnaireItemView) }

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.date_input_edit_text).text.toString()
      )
      .isEmpty()
    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.time_input_edit_text).text.toString()
      )
      .isEmpty()

    onView(withId(R.id.time_input_edit_text)).perform(ViewActions.click())
    onView(withText("OK")).perform(ViewActions.click())
    onView(withId(R.id.date_input_edit_text)).perform(ViewActions.click())
    onView(withText("OK")).perform(ViewActions.click())

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.date_input_edit_text).text.toString()
      )
      .isNotEmpty()
    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.time_input_edit_text).text.toString()
      )
      .isNotEmpty()
  }

  /**
   * Method to run code snippet on UI/main thread
   */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.getScenario().onActivity { activity -> action() }
  }

  /**
   * Method to set content view for test activity
   */
  private fun setTestLayout(view: View) {
    activityScenarioRule.getScenario().onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }
}
