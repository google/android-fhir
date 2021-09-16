/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.datacapture.views

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.R

internal class OptionSelectDialogFragment(
  val title: String,
  val options: List<SelectableOption>,
  val config: OptionsDialogConfig,
) : DialogFragment() {
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    isCancelable = false

    val view =
      LayoutInflater.from(requireContext())
        .inflate(R.layout.questionnaire_item_option_select_dialog, null)

    val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
    recyclerView.layoutManager = LinearLayoutManager(requireContext())

    val adapter: ListAdapter<SelectableOption, *> =
      when (config.mode) {
        OptionsDialogConfig.Mode.SINGLE_SELECT -> SingleSelectAdapter()
        OptionsDialogConfig.Mode.MULTI_SELECT -> MultiSelectAdapter()
      }
    recyclerView.adapter = adapter
    adapter.submitList(options)

    return AlertDialog.Builder(requireContext())
      .setTitle(title)
      .setView(view)
      .setPositiveButton(android.R.string.ok) { _, _ ->
        val items = adapter.currentList
        val selectedIndices =
          items
            .mapIndexedNotNull { index, element -> if (element.selected) index else null }
            .toIntArray()
        setFragmentResult(
          RESULT_REQUEST_KEY,
          bundleOf(
            RESULT_BUNDLE_KEY_SELECTED_INDICES to selectedIndices,
          ),
        )
      }
      .setNegativeButton(android.R.string.cancel) { _, _ -> }
      .setCancelable(false)
      .create()
  }

  companion object {
    const val RESULT_REQUEST_KEY = "multi-select-result-request-key"
    const val RESULT_BUNDLE_KEY_SELECTED_INDICES = "multi-select-bundle-key-selected-indices"
  }
}

private class SingleSelectAdapter :
  ListAdapter<SelectableOption, SingleSelectItemViewHolder>(SELECTABLE_OPTION_DIFF_CALLBACK) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleSelectItemViewHolder =
    SingleSelectItemViewHolder(
      LayoutInflater.from(parent.context)
        .inflate(R.layout.questionnaire_item_single_select_item, parent, false)
    )

  override fun onBindViewHolder(holder: SingleSelectItemViewHolder, position: Int) {
    val item = getItem(position)
    holder.radioButton.text = item.name
    holder.radioButton.isChecked = item.selected
    holder.radioButton.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        // When this item becomes checked, we need to uncheck all elements not at this index
        submitList(
          currentList.mapIndexed { index, element -> element.copy(selected = index == position) }
        )
      }
    }
  }
}

private class SingleSelectItemViewHolder(root: View) : RecyclerView.ViewHolder(root) {
  val radioButton: RadioButton = root.findViewById(R.id.radio_button)
}

private class MultiSelectAdapter :
  ListAdapter<SelectableOption, MultiSelectItemViewHolder>(SELECTABLE_OPTION_DIFF_CALLBACK) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectItemViewHolder =
    MultiSelectItemViewHolder(
      LayoutInflater.from(parent.context)
        .inflate(R.layout.questionnaire_item_multi_select_item, parent, false)
    )

  override fun onBindViewHolder(holder: MultiSelectItemViewHolder, position: Int) {
    val item = getItem(position)
    holder.checkbox.text = item.name
    holder.checkbox.isChecked = item.selected
    holder.checkbox.setOnCheckedChangeListener { _, checked ->
      submitList(currentList.modifyElementAt(index = position) { it.copy(selected = checked) })
    }
  }
}

private val SELECTABLE_OPTION_DIFF_CALLBACK =
  object : DiffUtil.ItemCallback<SelectableOption>() {
    override fun areItemsTheSame(l: SelectableOption, r: SelectableOption): Boolean =
      l.name == r.name

    override fun areContentsTheSame(l: SelectableOption, r: SelectableOption): Boolean = l == r
  }

private class MultiSelectItemViewHolder(root: View) : RecyclerView.ViewHolder(root) {
  val checkbox: CheckBox = root.findViewById(R.id.checkbox)
}

/** Replaces the element at [index] with the result of running [block] on it. */
private inline fun <T> List<T>.modifyElementAt(index: Int, block: (T) -> T): List<T> {
  return mapIndexed { currentIndex, currentElement ->
    if (index == currentIndex) block(currentElement) else currentElement
  }
}
