/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.google.android.fhir.demo.helpers.PatientUiState
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Enumerations

class CrudOperationFragment : Fragment() {
  private val crudOperationViewModel: CrudOperationViewModel by viewModels()
  private var patientLogicalId: String? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    return inflater.inflate(R.layout.fragment_crud_layout, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpActionBar()
    setHasOptionsMenu(true)
    initializeUi()
    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        crudOperationViewModel.patientUiState.collect { patientUiState ->
          patientUiState?.let {
            patientLogicalId = it.patientId
            when (
              requireView().findViewById<RadioGroup>(R.id.radioGroupCrud).checkedRadioButtonId
            ) {
              R.id.rbCreate -> {
                Toast.makeText(requireContext(), "Patient is saved", Toast.LENGTH_SHORT).show()
              }
              R.id.rbRead -> displayPatientDetails(it)
              R.id.rbUpdate -> {
                if (it.isReadOperation) {
                  displayPatientDetails(it)
                } else {
                  Toast.makeText(requireContext(), "Patient is updated", Toast.LENGTH_SHORT).show()
                }
              }
              R.id.rbDelete -> {
                requireView().findViewById<EditText>(R.id.etId).text.clear()
                Toast.makeText(requireContext(), "Patient is deleted", Toast.LENGTH_SHORT).show()
              }
            }
          }
        }
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      else -> false
    }
  }

  private fun setUpActionBar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = requireContext().getString(R.string.crud_operations)
      setDisplayHomeAsUpEnabled(true)
    }
  }

  private fun initializeUi() {
    setupRadioGroupChangeListener()
    requireView().findViewById<RadioGroup>(R.id.radioGroupCrud).check(R.id.rbCreate)

    requireView().findViewById<Button>(R.id.btnSubmit).setOnClickListener {
      when (requireView().findViewById<RadioGroup>(R.id.radioGroupCrud).checkedRadioButtonId) {
        R.id.rbCreate -> {
          createPatient()
        }
        R.id.rbRead -> {
          //
        }
        R.id.rbUpdate -> {
          updatePatient()
        }
        R.id.rbDelete -> {
          deletePatient()
        }
      }
    }
  }

  private fun setupRadioGroupChangeListener() {
    requireView().findViewById<RadioGroup>(R.id.radioGroupCrud).setOnCheckedChangeListener {
      _,
      checkedId,
      ->
      when (checkedId) {
        R.id.rbCreate -> {
          configureFieldsForCreate()
        }
        R.id.rbRead -> {
          patientLogicalId?.let {
            if (it.isNotEmpty()) {
              crudOperationViewModel.getPatientById(it)
            }
          }
          configureFieldsForRead()
        }
        R.id.rbUpdate -> {
          patientLogicalId?.let {
            if (it.isNotEmpty()) {
              crudOperationViewModel.getPatientById(it)
            }
          }
          configureFieldsForUpdate()
        }
        R.id.rbDelete -> {
          configureFieldsForDelete()
        }
      }
    }
  }

  private fun createPatient() {
    val patientId = requireView().findViewById<EditText>(R.id.etId).text.toString()
    val firstName = requireView().findViewById<EditText>(R.id.etFirstName).text.toString().trim()
    // Validation: Check if first name is blank
    if (firstName.isBlank()) {
      Toast.makeText(requireContext(), "First name is blank", Toast.LENGTH_SHORT).show()
      return
    }
    crudOperationViewModel.createPatient(patientId, firstName)
  }

  private fun updatePatient() {
    val patientId = requireView().findViewById<EditText>(R.id.etId).text.toString()
    if (patientId.isBlank()) {
      Toast.makeText(requireContext(), "Please create a patient first.", Toast.LENGTH_SHORT).show()
      return
    }
    val firstName = requireView().findViewById<EditText>(R.id.etFirstName).text.toString().trim()
    val lastName = requireView().findViewById<EditText>(R.id.etLastName).text.toString().trim()
    val birthDate = requireView().findViewById<EditText>(R.id.etBirthDate).text.toString().trim()

    // Gender selection
    val selectedGenderId =
      requireView().findViewById<RadioGroup>(R.id.radioGroupGender).checkedRadioButtonId
    val gender =
      when (selectedGenderId) {
        R.id.rbMale -> Enumerations.AdministrativeGender.MALE
        R.id.rbFemale -> Enumerations.AdministrativeGender.FEMALE
        R.id.rbOther -> Enumerations.AdministrativeGender.OTHER
        else -> null
      }

    // Active status
    val isActive = requireView().findViewById<CheckBox>(R.id.checkBoxActive).isChecked

    // Validation (ensure firstName is not blank)
    if (firstName.isBlank()) {
      Toast.makeText(requireContext(), "First name is required.", Toast.LENGTH_SHORT).show()
      return
    }

    crudOperationViewModel.updatePatient(
      patientId = patientId,
      firstName = firstName,
      lastName = lastName,
      birthDate = birthDate,
      gender = gender ?: Enumerations.AdministrativeGender.UNKNOWN,
      isActive = isActive,
    )
  }

  private fun deletePatient() {
    val patientId = requireView().findViewById<EditText>(R.id.etId).text.toString()
    if (patientId.isBlank()) {
      Toast.makeText(requireContext(), "Please create a patient first.", Toast.LENGTH_SHORT).show()
      return
    }
    crudOperationViewModel.deletePatient(patientId)
  }

  private fun configureFieldsForCreate() {
    requireView().findViewById<EditText>(R.id.etId).setText(crudOperationViewModel.getPatientId())

    requireView().findViewById<TextInputLayout>(R.id.tilId).visibility = View.VISIBLE
    requireView().findViewById<TextInputLayout>(R.id.tilFirstName).visibility = View.VISIBLE
    requireView().findViewById<EditText>(R.id.etFirstName).isEnabled = true
    requireView().findViewById<TextInputLayout>(R.id.tilLastName).visibility = View.GONE
    //    requireView().findViewById<TextInputLayout>(R.id.tilBirthDate).visibility = View.GONE
    requireView().findViewById<TextView>(R.id.tvGenderLabel).visibility = View.GONE
    requireView().findViewById<RadioGroup>(R.id.radioGroupGender).visibility = View.GONE
    requireView().findViewById<CheckBox>(R.id.checkBoxActive).visibility = View.GONE
    requireView().findViewById<Button>(R.id.btnSubmit).visibility = View.VISIBLE
  }

  private fun configureFieldsForRead() {
    if (patientLogicalId.isNullOrEmpty()) {
      requireView().findViewById<EditText>(R.id.etId).text.clear()
      Toast.makeText(requireContext(), "Please create a patient first.", Toast.LENGTH_SHORT).show()
    }

    val visibility =
      if (patientLogicalId.isNullOrEmpty()) {
        View.GONE
      } else {
        View.VISIBLE
      }

    requireView().findViewById<EditText>(R.id.etId).setText(patientLogicalId)
    requireView().findViewById<TextInputLayout>(R.id.tilId).visibility = visibility
    requireView().findViewById<TextInputLayout>(R.id.tilFirstName).visibility = visibility
    requireView().findViewById<TextInputLayout>(R.id.tilLastName).visibility = visibility
    //    requireView().findViewById<TextInputLayout>(R.id.tilBirthDate).visibility = visibility
    requireView().findViewById<TextView>(R.id.tvGenderLabel).visibility = visibility
    requireView().findViewById<RadioGroup>(R.id.radioGroupGender).visibility = visibility
    requireView().findViewById<CheckBox>(R.id.checkBoxActive).visibility = visibility

    // Configure fields as editable or non-editable based on the operation
    requireView().findViewById<EditText>(R.id.etFirstName).isEnabled = false
    requireView().findViewById<EditText>(R.id.etLastName).isEnabled = false
    //    requireView().findViewById<EditText>(R.id.etBirthDate).apply {
    //      isEnabled = false // BirthDate is always non-editable
    //    }

    requireView().findViewById<RadioGroup>(R.id.radioGroupGender).isEnabled = false
    requireView().findViewById<RadioButton>(R.id.rbMale).isEnabled = false
    requireView().findViewById<RadioButton>(R.id.rbFemale).isEnabled = false
    requireView().findViewById<RadioButton>(R.id.rbOther).isEnabled = false

    requireView().findViewById<CheckBox>(R.id.checkBoxActive).isEnabled = false
    requireView().findViewById<Button>(R.id.btnSubmit).visibility = View.GONE
  }

  private fun configureFieldsForUpdate() {
    if (patientLogicalId.isNullOrEmpty()) {
      requireView().findViewById<EditText>(R.id.etId).text.clear()
      Toast.makeText(requireContext(), "Please create a patient first.", Toast.LENGTH_SHORT).show()
    }

    val visibility =
      if (patientLogicalId.isNullOrEmpty()) {
        View.GONE
      } else {
        View.VISIBLE
      }

    requireView().findViewById<EditText>(R.id.etId).setText(patientLogicalId)
    requireView().findViewById<TextInputLayout>(R.id.tilId).visibility = visibility
    requireView().findViewById<TextInputLayout>(R.id.tilFirstName).visibility = visibility
    requireView().findViewById<TextInputLayout>(R.id.tilLastName).visibility = visibility
    //    requireView().findViewById<TextInputLayout>(R.id.tilBirthDate).visibility = visibility
    requireView().findViewById<TextView>(R.id.tvGenderLabel).visibility = visibility
    requireView().findViewById<RadioGroup>(R.id.radioGroupGender).visibility = visibility
    requireView().findViewById<CheckBox>(R.id.checkBoxActive).visibility = visibility

    // Configure fields as editable or non-editable based on the operation
    requireView().findViewById<EditText>(R.id.etFirstName).isEnabled = true
    requireView().findViewById<EditText>(R.id.etLastName).isEnabled = true
    //    requireView().findViewById<EditText>(R.id.etBirthDate).apply {
    //      isEnabled = false // BirthDate is always non-editable
    //    }
    requireView().findViewById<RadioGroup>(R.id.radioGroupGender).isEnabled = true
    requireView().findViewById<RadioButton>(R.id.rbMale).isEnabled = true
    requireView().findViewById<RadioButton>(R.id.rbFemale).isEnabled = true
    requireView().findViewById<RadioButton>(R.id.rbOther).isEnabled = true
    requireView().findViewById<CheckBox>(R.id.checkBoxActive).isEnabled = true
    requireView().findViewById<Button>(R.id.btnSubmit).visibility = visibility
  }

  private fun configureFieldsForDelete() {
    if (patientLogicalId.isNullOrEmpty()) {
      requireView().findViewById<EditText>(R.id.etId).text.clear()
      Toast.makeText(requireContext(), "Please create a patient first.", Toast.LENGTH_SHORT).show()
    }

    val visibility =
      if (patientLogicalId.isNullOrEmpty()) {
        View.GONE
      } else {
        View.VISIBLE
      }

    requireView().findViewById<EditText>(R.id.etId).setText(patientLogicalId)
    requireView().findViewById<TextInputLayout>(R.id.tilId).visibility = visibility
    requireView().findViewById<TextInputLayout>(R.id.tilFirstName).visibility = View.GONE

    requireView().findViewById<TextInputLayout>(R.id.tilLastName).visibility = View.GONE
    //    requireView().findViewById<TextInputLayout>(R.id.tilBirthDate).visibility = View.GONE
    requireView().findViewById<TextView>(R.id.tvGenderLabel).visibility = View.GONE
    requireView().findViewById<RadioGroup>(R.id.radioGroupGender).visibility = View.GONE
    requireView().findViewById<CheckBox>(R.id.checkBoxActive).visibility = View.GONE
    requireView().findViewById<Button>(R.id.btnSubmit).visibility = visibility
  }

  private fun displayPatientDetails(patientUiState: PatientUiState) {
    // Set ID field text
    requireView().findViewById<EditText>(R.id.etId).apply { setText(patientUiState.patientId) }

    // Set First Name field text
    requireView().findViewById<EditText>(R.id.etFirstName).apply {
      setText(patientUiState.firstName)
    }

    // Set Last Name field text
    requireView().findViewById<EditText>(R.id.etLastName).apply { setText(patientUiState.lastName) }

    // Set Birth Date field text
    requireView().findViewById<EditText>(R.id.etBirthDate).apply {
      setText(patientUiState.birthDate)
    }

    // Set Gender field selection based on patient gender
    requireView().findViewById<RadioGroup>(R.id.radioGroupGender).apply {
      val genderRadioButtonId =
        when (patientUiState.gender) {
          Enumerations.AdministrativeGender.MALE -> R.id.rbMale
          Enumerations.AdministrativeGender.FEMALE -> R.id.rbFemale
          Enumerations.AdministrativeGender.OTHER -> R.id.rbOther
          else -> null
        }
      genderRadioButtonId?.let { check(it) }
    }

    // Set Active status checkbox based on patient state
    requireView().findViewById<CheckBox>(R.id.checkBoxActive).apply {
      isChecked = patientUiState.isActive
    }
  }
}
