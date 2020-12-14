package com.google.android.fhir.datacapture.gallery

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class QuestionnaireResponseDialogFragment(val contents: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val view = requireActivity().layoutInflater.inflate(
                R.layout.questionnaire_response_dialog_contents,
                null
            )
            view.findViewById<TextView>(R.id.contents).text = contents

            AlertDialog.Builder(it)
                .setView(view)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TAG = "questionnaire-response-dialog-fragment"
    }
}