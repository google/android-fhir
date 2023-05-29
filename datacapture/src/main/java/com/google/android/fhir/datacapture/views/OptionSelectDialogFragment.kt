/*
 * Copyright 2022 Google LLC
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
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.use
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.views.factories.OptionSelectOption
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemDialogSelectViewModel
import com.google.android.fhir.datacapture.views.factories.SelectedOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.launch

internal class OptionSelectDialogFragment(
  private val title: CharSequence,
  private val config: Config,
  private val selectedOptions: SelectedOptions
) : DialogFragment() {

  /** Configures this [OptionSelectDialogFragment]. */
  data class Config(
    /** Whether multi-select will be enabled or not. */
    val multiSelect: Boolean,
    /** Whether the "other" option will be exposed for free-form text input. */
    val otherOptionsAllowed: Boolean,
  )

  private val viewModel: QuestionnaireItemDialogSelectViewModel by activityViewModels()

  private val questionLinkId: String by lazy { arguments?.getString(KEY_QUESTION_LINK_ID)!! }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    isCancelable = false

    val themeId =
      requireContext().obtainStyledAttributes(R.styleable.QuestionnaireTheme).use {
        it.getResourceId(
          // Use the custom questionnaire theme if it is specified
          R.styleable.QuestionnaireTheme_questionnaire_theme,
          // Otherwise, use the default questionnaire theme
          R.style.Theme_Questionnaire
        )
      }

    val dialogThemeContext = ContextThemeWrapper(requireContext(), themeId)
    val view = LayoutInflater.from(dialogThemeContext).inflate(R.layout.multi_select_dialog, null)

    val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    recyclerView.addItemDecoration(
      MarginItemDecoration(
        marginVertical = resources.getDimensionPixelOffset(R.dimen.option_item_margin_vertical),
        marginHorizontal = resources.getDimensionPixelOffset(R.dimen.option_item_margin_horizontal)
      )
    )

    val adapter = OptionSelectAdapter(multiSelectEnabled = config.multiSelect)
    recyclerView.adapter = adapter
    adapter.submitList(selectedOptions.toOptionRows())

    val dialog =
      MaterialAlertDialogBuilder(requireContext()).setView(view).create().apply {
        setOnShowListener {
          dialog?.window?.let {
            // Android: EditText in Dialog doesn't pull up soft keyboard
            // https://stackoverflow.com/a/9118027
            it.clearFlags(
              WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            )
            // Adjust the dialog after the keyboard is on so that OK-CANCEL buttons are visible.
            // SOFT_INPUT_ADJUST_RESIZE is deprecated and the suggested alternative
            // setDecorFitsSystemWindows is available api level 30 and above.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
              it.setDecorFitsSystemWindows(false)
            } else {
              it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
          }
        }
      }

    view.findViewById<TextView>(R.id.dialog_title).text = title
    view.findViewById<MaterialButton>(R.id.positive_button).setOnClickListener {
      saveSelections(adapter.currentList)
      dialog.dismiss()
    }
    view.findViewById<MaterialButton>(R.id.negative_button).setOnClickListener { dialog.dismiss() }

    return dialog
  }

  /** Saves the current selections in the RecyclerView into the ViewModel. */
  private fun saveSelections(currentList: List<OptionSelectRow>) {
    lifecycleScope.launch {
      viewModel.updateSelectedOptions(
        questionLinkId,
        SelectedOptions(
          options = currentList.filterIsInstance<OptionSelectRow.Option>().map { it.option },
          otherOptions =
            currentList.filterIsInstance<OptionSelectRow.OtherEditText>().map { it.currentText },
        )
      )
    }
  }

  /** Converts the initial ViewModel state into rows to display in the RecyclerView adapter. */
  private fun SelectedOptions.toOptionRows(): List<OptionSelectRow> {
    val rows = mutableListOf<OptionSelectRow>()
    options.forEach { rows += OptionSelectRow.Option(it) }
    if (otherOptions.isEmpty()) {
      // No other options selected; just show an "Other" option if the config allows it
      if (config.otherOptionsAllowed) {
        rows += OptionSelectRow.OtherRow(selected = false)
      }
    } else {
      // Other options were selected; show the "Other" option (selected) and any of those custom
      // options in EditTexts
      rows += OptionSelectRow.OtherRow(selected = true)
      if (!config.multiSelect) {
        check(otherOptions.size == 1) {
          "Multiple 'Other' options selected in single-select mode: $otherOptions"
        }
      }
      rows += otherOptions.map { OptionSelectRow.OtherEditText.fromText(it) }
    }
    return rows.sanitizeOtherOptionRows(multiSelectEnabled = config.multiSelect)
  }

  companion object {
    const val KEY_QUESTION_LINK_ID = "key-question-link-id"
  }
}

private class OptionSelectAdapter(val multiSelectEnabled: Boolean) :
  ListAdapter<OptionSelectRow, OptionSelectViewHolder>(DIFF_CALLBACK) {
  lateinit var recyclerView: RecyclerView
  override fun getItemViewType(position: Int): Int =
    when (getItem(position)) {
      is OptionSelectRow.Option,
      is OptionSelectRow.OtherRow ->
        if (multiSelectEnabled) Types.OPTION_MULTI else Types.OPTION_SINGLE
      is OptionSelectRow.OtherEditText -> Types.OTHER_EDIT_TEXT
      OptionSelectRow.OtherAddAnother -> Types.OTHER_ADD_ANOTHER
    }.ordinal

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionSelectViewHolder =
    when (Types.values()[viewType]) {
      Types.OPTION_SINGLE -> OptionSelectViewHolder.OptionSingle(parent)
      Types.OPTION_MULTI -> OptionSelectViewHolder.OptionMulti(parent)
      Types.OTHER_EDIT_TEXT -> OptionSelectViewHolder.OtherEditText(parent)
      Types.OTHER_ADD_ANOTHER -> OptionSelectViewHolder.OtherAddAnother(parent)
    }

  override fun onBindViewHolder(holder: OptionSelectViewHolder, position: Int) {
    when (val item = getItem(position)) {
      is OptionSelectRow.Option -> {
        val compoundButton =
          if (multiSelectEnabled) {
            (holder as OptionSelectViewHolder.OptionMulti).checkbox
          } else {
            (holder as OptionSelectViewHolder.OptionSingle).radioButton
          }
        compoundButton.text = item.option.displayString
        compoundButton.setCompoundDrawablesRelative(
          item.option.item.itemAnswerOptionImage(compoundButton.context),
          null,
          null,
          null
        )
        compoundButton.setOnCheckedChangeListener(null)
        compoundButton.isChecked = item.option.selected
        compoundButton.setOnCheckedChangeListener { _, checked ->
          submitSelectedChange(position = holder.adapterPosition, selected = checked)
        }
      }
      is OptionSelectRow.OtherRow -> {
        val compoundButton =
          if (multiSelectEnabled) {
            (holder as OptionSelectViewHolder.OptionMulti).checkbox
          } else {
            (holder as OptionSelectViewHolder.OptionSingle).radioButton
          }
        compoundButton.setText(R.string.open_choice_other)
        compoundButton.setOnCheckedChangeListener(null)
        compoundButton.isChecked = item.selected
        compoundButton.setOnCheckedChangeListener { _, checked ->
          submitSelectedChange(position = holder.adapterPosition, selected = checked)
          // Scroll down the recyclerview to show the Add another answer button on the screen.
          if (checked) {
            recyclerView.smoothScrollToPosition(this@OptionSelectAdapter.itemCount)
          }
        }
      }
      is OptionSelectRow.OtherEditText -> {
        holder as OptionSelectViewHolder.OtherEditText
        holder.delete.visibility = if (multiSelectEnabled) View.VISIBLE else View.GONE
        holder.delete.setOnClickListener {
          val newList = currentList.filterIndexed { index, _ -> index != holder.adapterPosition }
          submitList(newList)
        }
        holder.editText.setText(item.startingText)
        holder.currentItem = item
      }
      OptionSelectRow.OtherAddAnother -> {
        holder as OptionSelectViewHolder.OtherAddAnother
        holder.addAnother.setOnClickListener {
          // Add another blank OtherEditText right before this button
          val newList =
            currentList.toMutableList().also {
              it.add(holder.adapterPosition, OptionSelectRow.OtherEditText.fromText(""))
            }
          submitList(newList)
          // Scroll down the recyclerview to show the Add another answer button on the screen.
          recyclerView.smoothScrollToPosition(this@OptionSelectAdapter.itemCount)
        }
      }
    }
  }

  /**
   * Sets the item at [position] to selected = [selected].
   *
   * Also ensures that the state is cleaned up to a valid state (eg, removing "Other" editable rows
   * if "Other" was just deselected, or adding them if "Other" was just selected).
   */
  private fun submitSelectedChange(position: Int, selected: Boolean) {
    val newList: List<OptionSelectRow> =
      currentList
        .mapIndexed { index, row ->
          if (index == position) {
            // This is the row we are changing
            row.withSelectedState(selected = selected)!!
          } else {
            // This is some other row
            if (multiSelectEnabled) {
              // In multi-select mode, the other rows don't need to change
              row
            } else {
              // In single-select mode, we need to disable all of the other rows
              row.withSelectedState(selected = false) ?: row
            }
          }
        }
        .sanitizeOtherOptionRows(multiSelectEnabled = multiSelectEnabled)
    submitList(newList)
  }

  private fun OptionSelectRow.withSelectedState(selected: Boolean): OptionSelectRow? =
    when (this) {
      is OptionSelectRow.Option -> copy(option = option.copy(selected = selected))
      is OptionSelectRow.OtherRow -> copy(selected = selected)
      OptionSelectRow.OtherAddAnother,
      is OptionSelectRow.OtherEditText -> null
    }

  private enum class Types {
    OPTION_SINGLE,
    OPTION_MULTI,
    OTHER_EDIT_TEXT,
    OTHER_ADD_ANOTHER,
  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    this.recyclerView = recyclerView
  }
}

private fun List<OptionSelectRow>.sanitizeOtherOptionRows(
  multiSelectEnabled: Boolean
): List<OptionSelectRow> {
  var sanitized = this
  // Now that we've set the selected states properly, we need to make sure that the "Other" rows
  // are showing in their correct state
  val shouldShowOtherRows = sanitized.any { it is OptionSelectRow.OtherRow && it.selected }
  if (shouldShowOtherRows) {
    if (multiSelectEnabled) {
      // In multi-select with Other enabled, we need the last row to be an AddAnother button
      if (sanitized.last() !is OptionSelectRow.OtherAddAnother) {
        sanitized = sanitized + OptionSelectRow.OtherAddAnother
      }
    } else {
      // In single-select with Other enabled, the last row should just be an OtherEditText
      if (sanitized.last() !is OptionSelectRow.OtherEditText) {
        sanitized = sanitized + OptionSelectRow.OtherEditText.fromText("")
      }
    }
  } else {
    // We should not show the "Other" edit-texts or Add Another buttons, so return a sub-list with
    // those items dropped
    sanitized =
      sanitized.dropLastWhile {
        when (it) {
          // don't drop these
          is OptionSelectRow.Option,
          is OptionSelectRow.OtherRow -> false
          // drop these
          is OptionSelectRow.OtherEditText,
          OptionSelectRow.OtherAddAnother -> true
        }
      }
  }
  return sanitized
}

private sealed class OptionSelectRow {
  /** A predefined option. */
  data class Option(val option: OptionSelectOption) : OptionSelectRow()

  /**
   * "Other" option. Only shown if [OptionSelectDialogFragment.Config.otherOptionsAllowed] is true.
   */
  data class OtherRow(val selected: Boolean) : OptionSelectRow()

  /** Text boxes for user to enter "Other" options in. Only shown when [OtherRow] is selected. */
  data class OtherEditText(val id: Int, val startingText: String) : OptionSelectRow() {
    /**
     * We track the text as the user edits it in a separate variable, so that DiffUtil doesn't pick
     * up the changes the user makes while editing and send the adapter into an infinite loop.
     */
    var currentText: String = startingText

    companion object {
      fun fromText(text: String) =
        OtherEditText(
          id = idProvider.getAndIncrement(),
          startingText = text,
        )

      private val idProvider = AtomicInteger(0)
    }
  }

  /** "Add Another" other field button. Only used in multi-select when [OtherRow] is selected. */
  object OtherAddAnother : OptionSelectRow()
}

private sealed class OptionSelectViewHolder(parent: ViewGroup, layout: Int) :
  RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false)) {
  /** Radio button option. */
  class OptionSingle(parent: ViewGroup) :
    OptionSelectViewHolder(parent, R.layout.option_item_single) {
    val radioButton: RadioButton = itemView.findViewById(R.id.radio_button)
  }

  /** Checkbox option. */
  class OptionMulti(parent: ViewGroup) :
    OptionSelectViewHolder(parent, R.layout.option_item_multi) {
    val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
  }

  /**
   * Freeform option, only shown if [OptionSelectDialogFragment.Config.otherOptionsAllowed] is true.
   */
  class OtherEditText(parent: ViewGroup) :
    OptionSelectViewHolder(parent, R.layout.option_item_other_text) {
    val editText: EditText = itemView.findViewById(R.id.edit_text)
    val delete: View = itemView.findViewById(R.id.delete_button)

    var currentItem: OptionSelectRow.OtherEditText? = null

    init {
      editText.doAfterTextChanged {
        val text = it?.toString().orEmpty()
        currentItem?.currentText = text
      }
    }
  }

  /**
   * Freeform option, only shown if [OptionSelectDialogFragment.Config.otherOptionsAllowed] is true.
   */
  class OtherAddAnother(parent: ViewGroup) :
    OptionSelectViewHolder(parent, R.layout.option_item_other_add_another) {
    val addAnother: Button = itemView.findViewById(R.id.add_another)
  }
}

private val DIFF_CALLBACK =
  object : DiffUtil.ItemCallback<OptionSelectRow>() {
    override fun areItemsTheSame(l: OptionSelectRow, r: OptionSelectRow): Boolean =
      when (l) {
        is OptionSelectRow.Option ->
          r is OptionSelectRow.Option && l.option.displayString == r.option.displayString
        is OptionSelectRow.OtherEditText -> r is OptionSelectRow.OtherEditText && l.id == r.id

        // These two types should only have 1 instance present in the whole RecyclerView, so they
        // only need an instanceof check.
        is OptionSelectRow.OtherRow -> r is OptionSelectRow.OtherRow
        OptionSelectRow.OtherAddAnother -> r == OptionSelectRow.OtherAddAnother
      }

    override fun areContentsTheSame(l: OptionSelectRow, r: OptionSelectRow): Boolean = l == r
  }
