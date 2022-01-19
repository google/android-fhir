package com.google.android.fhir.datacapture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class QuestionnaireResponseDisplayFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    inflater.context.obtainStyledAttributes(R.styleable.QuestionnaireTheme).use {
      val themeId =
        it.getResourceId(
          // Use the custom questionnaire theme if it is specified
          R.styleable.QuestionnaireTheme_questionnaire_theme,
          // Otherwise, use the default questionnaire theme
          R.style.Theme_Questionnaire
        )
      return inflater
        .cloneInContext(ContextThemeWrapper(inflater.context, themeId))
        .inflate(R.layout.questionnaire_response_display_fragment, container, false)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

    val backButton = view.findViewById<Button>(R.id.back_button)
    backButton.setOnClickListener {}

    val submitButton = view.findViewById<Button>(R.id.submit_button)
    submitButton.setOnClickListener {}

    val editButton = view.findViewById<Button>(R.id.edit_button)
    editButton.setOnClickListener {}

    val questionnaireNameText = view.findViewById<TextView>(R.id.questionnaire_name_text)
    questionnaireNameText.text = ""

    val adapter = QuestionnaireResponseItemAdapter()

    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(view.context)
  }

  companion object {
    /**
     * A JSON encoded string extra for a questionnaire. This should only be used for questionnaires
     * with size at most 512KB. For large questionnaires, use [EXTRA_QUESTIONNAIRE_JSON_URI].
     *
     * This is required unless [EXTRA_QUESTIONNAIRE_JSON_URI] is provided.
     *
     * If this and [EXTRA_QUESTIONNAIRE_JSON_URI] are provided, [EXTRA_QUESTIONNAIRE_JSON_URI] takes
     * precedence.
     */
    const val EXTRA_QUESTIONNAIRE_JSON_STRING = "questionnaire"
    /**
     * A [Uri] extra for streaming a JSON encoded questionnaire.
     *
     * This is required unless [EXTRA_QUESTIONNAIRE_JSON_STRING] is provided.
     *
     * If this and [EXTRA_QUESTIONNAIRE_JSON_STRING] are provided, this extra takes precedence.
     */
    const val EXTRA_QUESTIONNAIRE_JSON_URI = "questionnaire-uri"
    /** A JSON encoded string extra for a prefilled questionnaire response. */
    const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING = "questionnaire-response"
  }
}
