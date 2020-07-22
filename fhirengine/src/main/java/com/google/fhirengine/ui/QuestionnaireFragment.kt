package com.google.fhirengine.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.fhirengine.R
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

class QuestionnaireFragment(private val questionnaire: Questionnaire) : Fragment() {

    companion object {
        fun newInstance(questionnaire: Questionnaire) = QuestionnaireFragment(questionnaire)
    }

    private lateinit var viewModel: QuestionnaireViewModel

    internal var onQuestionnaireSubmittedListener: OnQuestionnaireSubmittedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, QuestionnaireViewModelFactory(questionnaire)).get(
                QuestionnaireViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.questionnaire_fragment, container, false)
        view.findViewById<TextView>(R.id.title).text = viewModel.questionnaire.title
        val item = view.findViewById<LinearLayout>(R.id.item)

        viewModel.questionnaire.item.forEach {
            generateAndAttachQuestion(it, item)
        }

        view.findViewById<Button>(R.id.submit).setOnClickListener {
            onQuestionnaireSubmittedListener?.onSubmitted(viewModel.questionnaireResponse)
        }

        return view
    }

    private fun generateAndAttachQuestion(
            questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent,
            viewGroup: ViewGroup) {
        val linkId = questionnaireItemComponent.linkId
        when (questionnaireItemComponent.type) {
            Questionnaire.QuestionnaireItemType.BOOLEAN -> {
                val checkBox = CheckBox(this.context)
                checkBox.text = questionnaireItemComponent.text
                viewGroup.addView(checkBox)
                checkBox.setOnClickListener {
                    val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    answer.setValue(BooleanType(checkBox.isChecked))
                    viewModel.map[linkId]?.answer = listOf(answer)
                }
            }
            Questionnaire.QuestionnaireItemType.STRING -> {
                val questionText = TextView(context)
                questionText.text = questionnaireItemComponent.text
                viewGroup.addView(questionText)
                val input = EditText(context)
                viewGroup.addView(input)
                input.addTextChangedListener(object : TextWatcher{
                    override fun afterTextChanged(s: Editable?) {
                        // do nothing
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                            after: Int) {
                        // do nothing
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int,
                            count: Int) {
                        val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                        answer.setValue(StringType(s.toString()))
                        viewModel.map[linkId]?.answer = listOf(answer)
                    }

                })
            }
            Questionnaire.QuestionnaireItemType.GROUP -> {
                val linearLayout = LinearLayout(context)
                linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                linearLayout.orientation = LinearLayout.VERTICAL
                val title = TextView(context)
                title.text = questionnaireItemComponent.text
                linearLayout.addView(title)
                questionnaireItemComponent.item.forEach { generateAndAttachQuestion(it, linearLayout) }
                viewGroup.addView(linearLayout)
            }
        }
    }

    fun setOnQuestionnaireSubmittedListener(onQuestionnaireSubmittedListener: OnQuestionnaireSubmittedListener) {
        this.onQuestionnaireSubmittedListener = onQuestionnaireSubmittedListener
    }

    interface OnQuestionnaireSubmittedListener {
        fun onSubmitted(questionnaireResponse: QuestionnaireResponse)
    }
}