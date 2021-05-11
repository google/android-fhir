package com.google.android.fhir.datacapture.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.FragmentResultListener
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.R
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.md.LiveBarcodeScanningFragment

internal object QuestionnaireItemBarCodeReaderViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_bar_code_reader_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var textQuestion: TextView
      private lateinit var textInputEditText: TextInputEditText
      private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix)
        textQuestion = itemView.findViewById(R.id.question)
        textInputEditText = itemView.findViewById(R.id.textInputEditText)
        textInputEditText.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
          // Do not show the date picker dialog when losing focus.
          if (!hasFocus) return@setOnFocusChangeListener

          // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
          // and again in TextInputEditText during layout inflation. As a result, it is
          // necessary to access the base context twice to retrieve the application object
          // from the view's context.
          val context = itemView.context.tryUnwrapContext()!!

          context.supportFragmentManager.setFragmentResultListener(
            "result",
            context,
            object : FragmentResultListener {
              override fun onFragmentResult(requestKey: String, result: Bundle) {
                textInputEditText.setText(result.getString("result"))
              }
            }
          )

          LiveBarcodeScanningFragment().show(context.supportFragmentManager, "TAG")
        }
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {}
    }
}

class PickRingtone : ActivityResultContract<Unit, String>() {
  override fun createIntent(context: Context, unit: Unit) =
    Intent(context, LiveBarcodeScanningFragment::class.java)

  override fun parseResult(resultCode: Int, result: Intent?): String? {
    if (resultCode != Activity.RESULT_OK) {
      return null
    }
    return result?.getParcelableExtra("result")
  }
}
