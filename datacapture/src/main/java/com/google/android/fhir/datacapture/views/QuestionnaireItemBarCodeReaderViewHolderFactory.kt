package com.google.android.fhir.datacapture.views

import android.R.attr.button
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentResultListener
import com.google.android.fhir.datacapture.R
import com.google.mlkit.md.LiveBarcodeScanningFragment


internal object QuestionnaireItemBarCodeReaderViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_bar_code_reader_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var textQuestion: TextView
      private lateinit var barcodeTextView: TextView
      private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix)
        textQuestion = itemView.findViewById(R.id.question)
        barcodeTextView = itemView.findViewById(R.id.textInputEditText)
          itemView.findViewById<View>(R.id.textInputLayout).setOnClickListener {

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
                              barcodeTextView.text = result.getString("result")

                              val black = context.getColor(R.color.black)
                              barcodeTextView.setTextColor(black)

                              var drawable = ContextCompat.getDrawable(context, R.drawable.ic_barcode)
                              drawable = DrawableCompat.wrap(drawable!!)
                              DrawableCompat.setTint(drawable.mutate(), black)
                              drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                              barcodeTextView.setCompoundDrawables(drawable, null, null, null)

                              itemView.findViewById<TextView>(R.id.tv_rescan).visibility = View.VISIBLE
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
