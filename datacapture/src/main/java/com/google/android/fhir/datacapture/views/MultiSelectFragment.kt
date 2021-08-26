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
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.R

internal class MultiSelectFragment(
  val title: String,
  val options: List<MultiSelectOption>,
) : DialogFragment() {
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    isCancelable = false

    val view =
      LayoutInflater.from(requireContext())
        .inflate(R.layout.questionnaire_item_multi_select_dialog, null)

    val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
    recyclerView.layoutManager = LinearLayoutManager(requireContext())

    val adapter = MultiSelectAdapter().also { recyclerView.adapter = it }
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

private class MultiSelectAdapter :
  ListAdapter<MultiSelectOption, MultiSelectAdapter.SelectionItemViewHolder>(DIFF_CALLBACK) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionItemViewHolder =
    SelectionItemViewHolder(
      LayoutInflater.from(parent.context)
        .inflate(R.layout.questionnaire_item_multi_select_item, parent, false)
    )

  override fun onBindViewHolder(holder: SelectionItemViewHolder, position: Int) {
    val item = getItem(position)
    holder.checkbox.text = item.name
    holder.checkbox.isChecked = item.selected
    holder.checkbox.setOnCheckedChangeListener { _, checked ->
      submitList(currentList.modifyElementAt(index = position) { it.copy(selected = checked) })
    }
  }

  private class SelectionItemViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    val checkbox: CheckBox = root.findViewById(R.id.checkbox)
  }

  companion object {
    private val DIFF_CALLBACK =
      object : DiffUtil.ItemCallback<MultiSelectOption>() {
        override fun areItemsTheSame(l: MultiSelectOption, r: MultiSelectOption): Boolean =
          l.name == r.name

        override fun areContentsTheSame(l: MultiSelectOption, r: MultiSelectOption): Boolean =
          l == r
      }
  }
}

/** Replaces the element at [index] with the result of running [block] on it. */
private inline fun <T> List<T>.modifyElementAt(index: Int, block: (T) -> T): List<T> {
  return mapIndexed { currentIndex, currentElement ->
    if (index == currentIndex) block(currentElement) else currentElement
  }
}
