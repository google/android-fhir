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
import com.google.android.fhir.demo.helpers.PatientCreationHelper
import com.google.android.material.tabs.TabLayout
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
    setupUi()
    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        crudOperationViewModel.patientUiState.collect { patientUiState ->
          patientUiState?.let {
            patientLogicalId = it.patientId
            when (it.operationType) {
              OperationType.CREATE -> {
                Toast.makeText(requireContext(), "Patient is saved", Toast.LENGTH_SHORT).show()
              }
              OperationType.READ -> displayPatientDetails(it)
              OperationType.UPDATE -> {
                Toast.makeText(requireContext(), "Patient is updated", Toast.LENGTH_SHORT).show()
              }
              OperationType.DELETE -> {
                // Reset the page as the patient has been deleted.
                clearTextFieldValues()
                configureFieldsForOperation(OperationType.DELETE)
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

  private fun setupUi() {
    setupTabLayoutChangeListener()
    selectTab(TAB_CREATE)
    setupUiByOperationType(OperationType.CREATE)
    requireView().findViewById<Button>(R.id.btnSubmit).setOnClickListener {
      val currentOperationType =
        getOperationTypeByTabPosition(
          requireView().findViewById<TabLayout>(R.id.tabLayoutCrud).selectedTabPosition,
        )
      when (currentOperationType) {
        OperationType.CREATE -> createPatient()
        OperationType.READ -> {
          error("Invalid operation.")
        }
        OperationType.UPDATE -> updatePatient()
        OperationType.DELETE -> deletePatient()
      }
    }
  }

  private fun setupUiByOperationType(operationType: OperationType) {
    when (operationType) {
      OperationType.CREATE -> {
        clearTextFieldValues()
        configureFieldsForOperation(OperationType.CREATE)
        requireView()
          .findViewById<EditText>(R.id.etId)
          .setText(PatientCreationHelper.createPatientId())
      }
      OperationType.READ,
      OperationType.UPDATE,
      OperationType.DELETE, -> {
        patientLogicalId
          ?.takeIf { it.isNotEmpty() }
          ?.let {
            crudOperationViewModel.readPatientById(it)
            requireView().findViewById<EditText>(R.id.etId).setText(it)
          }
        displayCreatePatientMessageIfNeeded()
        configureFieldsForOperation(operationType)
      }
    }
  }

  private fun selectTab(position: Int) {
    val tabLayout = requireView().findViewById<TabLayout>(R.id.tabLayoutCrud)
    val tab = tabLayout.getTabAt(position)
    tab?.select()
  }

  private fun setupTabLayoutChangeListener() {
    val tabLayout = requireView().findViewById<TabLayout>(R.id.tabLayoutCrud)
    tabLayout.addOnTabSelectedListener(
      object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
          val currentOperationType = getOperationTypeByTabPosition(tab?.position ?: TAB_CREATE)
          setupUiByOperationType(currentOperationType)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabReselected(tab: TabLayout.Tab?) {}
      },
    )
  }

  private fun clearTextFieldValues() {
    requireView().findViewById<EditText>(R.id.etId).text.clear()
    requireView().findViewById<EditText>(R.id.etFirstName).text.clear()
    requireView().findViewById<EditText>(R.id.etLastName).text.clear()
    requireView().findViewById<EditText>(R.id.etBirthDate).text.clear()
    requireView().findViewById<RadioButton>(R.id.rbOther).isChecked = true
  }

  private fun configureFieldsForOperation(operationType: OperationType) {
    // Common field visibility and text settings
    val visibility = if (patientLogicalId.isNullOrEmpty()) View.GONE else View.VISIBLE

    requireView().findViewById<TextInputLayout>(R.id.tilId).visibility =
      when (operationType) {
        OperationType.CREATE -> View.VISIBLE
        OperationType.READ,
        OperationType.UPDATE,
        OperationType.DELETE, -> visibility
      }
    requireView().findViewById<TextInputLayout>(R.id.tilFirstName).visibility =
      when (operationType) {
        OperationType.CREATE -> View.VISIBLE
        OperationType.READ,
        OperationType.UPDATE,
        OperationType.DELETE, -> visibility
      }
    requireView().findViewById<TextInputLayout>(R.id.tilLastName).visibility =
      when (operationType) {
        OperationType.CREATE,
        OperationType.DELETE, -> View.GONE
        OperationType.READ,
        OperationType.UPDATE, -> visibility
      }
    requireView().findViewById<TextInputLayout>(R.id.tilBirthDate).visibility =
      when (operationType) {
        OperationType.CREATE,
        OperationType.DELETE, -> View.GONE
        OperationType.READ,
        OperationType.UPDATE, -> visibility
      }
    requireView().findViewById<TextView>(R.id.tvGenderLabel).visibility =
      when (operationType) {
        OperationType.CREATE,
        OperationType.DELETE, -> View.GONE
        OperationType.READ,
        OperationType.UPDATE, -> visibility
      }
    requireView().findViewById<RadioGroup>(R.id.radioGroupGender).visibility =
      when (operationType) {
        OperationType.CREATE,
        OperationType.DELETE, -> View.GONE
        OperationType.READ,
        OperationType.UPDATE, -> visibility
      }
    requireView().findViewById<CheckBox>(R.id.checkBoxActive).visibility =
      when (operationType) {
        OperationType.CREATE,
        OperationType.DELETE, -> View.GONE
        OperationType.READ,
        OperationType.UPDATE, -> visibility
      }
    requireView().findViewById<Button>(R.id.btnSubmit).visibility =
      when (operationType) {
        OperationType.CREATE -> View.VISIBLE
        OperationType.READ -> View.GONE
        OperationType.UPDATE,
        OperationType.DELETE, -> if (patientLogicalId.isNullOrEmpty()) View.GONE else View.VISIBLE
      }
    // Field editability
    val isEditable =
      when (operationType) {
        OperationType.CREATE,
        OperationType.UPDATE, -> true
        else -> false
      }
    requireView().findViewById<EditText>(R.id.etFirstName).isEnabled = isEditable
    requireView().findViewById<EditText>(R.id.etLastName).isEnabled = isEditable
    requireView().findViewById<EditText>(R.id.etBirthDate).isEnabled = isEditable
    requireView().findViewById<RadioGroup>(R.id.radioGroupGender).isEnabled = isEditable
    requireView().findViewById<RadioButton>(R.id.rbMale).isEnabled = isEditable
    requireView().findViewById<RadioButton>(R.id.rbFemale).isEnabled = isEditable
    requireView().findViewById<RadioButton>(R.id.rbOther).isEnabled = isEditable
    requireView().findViewById<CheckBox>(R.id.checkBoxActive).isEnabled = isEditable
  }

  private fun displayPatientDetails(patientUiState: PatientUiState) {
    requireView().findViewById<EditText>(R.id.etId).apply { setText(patientUiState.patientId) }
    requireView().findViewById<EditText>(R.id.etFirstName).apply {
      setText(patientUiState.firstName)
    }
    requireView().findViewById<EditText>(R.id.etLastName).apply {
      setText(patientUiState.lastName ?: "")
    }
    requireView().findViewById<EditText>(R.id.etBirthDate).apply {
      setText(patientUiState.birthDate ?: "")
    }
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
    requireView().findViewById<CheckBox>(R.id.checkBoxActive).apply {
      isChecked = patientUiState.isActive
    }
  }

  private fun displayCreatePatientMessageIfNeeded() {
    if (patientLogicalId.isNullOrEmpty()) {
      requireView().findViewById<EditText>(R.id.etId).text.clear()
      Toast.makeText(requireContext(), "Please create a patient first.", Toast.LENGTH_SHORT).show()
    }
  }

  private fun getOperationTypeByTabPosition(tabPosition: Int): OperationType {
    return when (tabPosition) {
      TAB_CREATE -> {
        OperationType.CREATE
      }
      TAB_READ -> {
        OperationType.READ
      }
      TAB_UPDATE -> {
        OperationType.UPDATE
      }
      TAB_DELETE -> {
        OperationType.DELETE
      }
      else -> {
        error("Invalid tab selection.")
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
    if (!PatientCreationHelper.isBirthdateParsed(birthDate)) {
      Toast.makeText(requireContext(), "Please enter a valid birth date.", Toast.LENGTH_SHORT)
        .show()
      return
    }
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
      patientLogicalId = patientId,
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

  companion object {
    private const val TAB_CREATE = 0
    private const val TAB_READ = 1
    private const val TAB_UPDATE = 2
    private const val TAB_DELETE = 3
  }
}
