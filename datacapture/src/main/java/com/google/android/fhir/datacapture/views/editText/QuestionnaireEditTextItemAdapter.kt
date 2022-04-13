/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.datacapture.views.editText
//
// import android.text.Editable
// import android.view.LayoutInflater
// import android.view.View
// import android.view.ViewGroup
// import android.widget.Button
// import androidx.core.widget.doAfterTextChanged
// import androidx.recyclerview.widget.DiffUtil
// import androidx.recyclerview.widget.ListAdapter
// import androidx.recyclerview.widget.RecyclerView
// import com.google.android.fhir.datacapture.R
// import com.google.android.fhir.datacapture.validation.ValidationResult
// import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderDelegate
// import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
// import com.google.android.material.textfield.TextInputEditText
// import org.hl7.fhir.r4.model.QuestionnaireResponse
// import org.hl7.fhir.r4.model.StringType
//
// class QuestionnaireEditTextItemAdapter(
// val rawInputType: Int,
// val isSingleLine: Boolean,
// private val questionnaireItemViewHolderMatchers:
// List<QuestionnaireItemViewItem> =
// emptyList()
// ) : ListAdapter<QuestionnaireItemViewItem,
// QuestionnaireEditTextItemAdapter.QuestionnaireEditTextItemViewHolder>(DiffCallback) {
//
//    open class QuestionnaireEditTextItemViewHolder(
//    itemView: View,
//    val rawInputType: Int,
//    val isSingleLine: Boolean, override var questionnaireItemViewItem: QuestionnaireItemViewItem,
//    ) : RecyclerView.ViewHolder(itemView), QuestionnaireItemViewHolderDelegate {
//        private var textInputEditText: TextInputEditText =
// itemView.findViewById(R.id.text_input_edit_text)
//        private var removeView: Button = itemView.findViewById(R.id.btn_remove_view)
//        override fun init(itemView: View) {
//        }
//
//        override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem, position: Int) {
//            textInputEditText.setRawInputType(rawInputType)
//            textInputEditText.isSingleLine = isSingleLine
//            textInputEditText.doAfterTextChanged { editable: Editable? ->
//                questionnaireItemViewItem.singleAnswerOrNull = getValue(editable.toString())
//                onAnswerChanged(textInputEditText.context)
//            }
//
//
//            removeView.setOnClickListener {
//                run {
//
//                }
//            }
//        }
//
//        override fun displayValidationResult(validationResult: ValidationResult) {
//        }
//
//        override fun setReadOnly(isReadOnly: Boolean) {
//        }
//
//        fun getValue(
//        text: String
//        ): QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent? {
//            return text.let {
//                if (it.isEmpty()) {
//                    null
//                } else {
//                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
//                    .setValue(StringType(it))
//                }
//            }
//        }
//    }
//
//    /**
//     * @param viewType the integer value of the [QuestionnaireItemViewHolderType] used to render
// the
//     * [QuestionnaireItemViewItem].
//     */
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
// QuestionnaireEditTextItemViewHolder {
//        val inflater =
// LayoutInflater.from(parent.context).inflate(R.layout.questionnaire_item_edit_text_view_row,
// parent, false)
//        return QuestionnaireEditTextItemViewHolder(inflater.rootView, rawInputType, isSingleLine,
// currentList.first())
//    }
//
//    override fun onBindViewHolder(holder: QuestionnaireEditTextItemViewHolder, position: Int) {
//        holder.bind(getItem(position), position)
//    }
//
//    fun addItem(questionnaireItemViewItem: QuestionnaireItemViewItem, position: Int) {
//        val list = currentList
//        list.add(position + 1, questionnaireItemViewItem)
//        submitList(list)
//        this.notifyItemInserted(position + 1)
//    }
//
//    fun removeItem(position: Int) {
//        val list = currentList
//        list.removeAt(position)
//        submitList(list)
//        this.notifyItemRemoved(position)
//    }
//
// }
//
//
// internal object DiffCallback : DiffUtil.ItemCallback<QuestionnaireItemViewItem>() {
//    override fun areItemsTheSame(
//    oldItem: QuestionnaireItemViewItem,
//    newItem: QuestionnaireItemViewItem
//    ) = oldItem == newItem
//
//    override fun areContentsTheSame(
//    oldItem: QuestionnaireItemViewItem,
//    newItem: QuestionnaireItemViewItem
//    ) = oldItem.equalsDeep(newItem)
// }
