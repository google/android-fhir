/*
 * Copyright 2024-2025 Google LLC
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
import android.widget.RadioGroup
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
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Enumerations

class CrudOperationFragment : Fragment() {
  private val crudOperationViewModel: CrudOperationViewModel by viewModels()

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
    setupUiOnScreenLaunch()
    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        crudOperationViewModel.patientUiState.collect { patientUiState ->
          patientUiState?.let {
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
                clearUiFieldValues()
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

  private fun setupUiOnScreenLaunch() {
    setupTabLayoutChangeListener()
    selectTab(TAB_CREATE)
    setupUiForCrudOperation(OperationType.CREATE)

    requireView().findViewById<Button>(R.id.btnSubmit).setOnClickListener {
      val currentOperationType =
        getOperationTypeByTabPosition(
          requireView().findViewById<TabLayout>(R.id.tabLayoutCrud).selectedTabPosition,
        )
      when (currentOperationType) {
        OperationType.CREATE -> createPatient()
        OperationType.READ -> {
          isPatientCreationRequired()
          crudOperationViewModel.readPatientById()
        }
        OperationType.UPDATE -> updatePatient()
        OperationType.DELETE -> deletePatient()
      }
    }
  }

  private fun setupUiForCrudOperation(operationType: OperationType) {
    when (operationType) {
      OperationType.CREATE -> {
        clearUiFieldValues()
        setPatientIdUiFieldText(PatientCreationHelper.createPatientId())
        updateSubmitButtonText(getString(R.string.create))
      }
      OperationType.READ -> {
        clearUiFieldValues()
        setPatientIdUiFieldText(crudOperationViewModel.currentPatientLogicalId)
        updateSubmitButtonText(getString(R.string.read))
      }
      OperationType.UPDATE -> {
        updateSubmitButtonText(getString(R.string.update))
        crudOperationViewModel.readPatientById()
      }
      OperationType.DELETE -> {
        updateSubmitButtonText(getString(R.string.delete))
        crudOperationViewModel.readPatientById()
      }
    }
    configureFieldsForOperation(operationType)
  }

  private fun updateSubmitButtonText(text: String) {
    requireView().findViewById<Button>(R.id.btnSubmit).text = text
  }

  private fun setPatientIdUiFieldText(patientId: String?) {
    requireView().findViewById<EditText>(R.id.etId).setText(patientId)
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
          setupUiForCrudOperation(currentOperationType)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabReselected(tab: TabLayout.Tab?) {}
      },
    )
  }

  private fun clearUiFieldValues() {
    val editTexts =
      listOf(
        R.id.etId,
        R.id.etFirstName,
        R.id.etLastName,
        R.id.etBirthDate,
      )
    editTexts.forEach { editTextId ->
      requireView().findViewById<EditText>(editTextId).apply {
        text.clear()
        clearFocus()
      }
    }
    requireView().findViewById<RadioGroup>(R.id.radioGroupGender).clearCheck()
    requireView().findViewById<CheckBox>(R.id.checkBoxActive).isChecked = false
  }

  private fun configureFieldsForOperation(operationType: OperationType) {
    val isEditable =
      when (operationType) {
        OperationType.CREATE,
        OperationType.UPDATE, -> true
        else -> false
      }
    val views =
      listOf(
        R.id.etFirstName,
        R.id.etLastName,
        R.id.etBirthDate,
        R.id.radioGroupGender,
        R.id.rbMale,
        R.id.rbFemale,
        R.id.rbOther,
        R.id.checkBoxActive,
      )
    views.forEach { viewId -> requireView().findViewById<View>(viewId).isEnabled = isEditable }
  }

  private fun displayPatientDetails(patientUiState: PatientUiState) {
    setPatientIdUiFieldText(patientUiState.patientId)
    requireView().findViewById<EditText>(R.id.etFirstName).apply {
      setText(patientUiState.firstName)
    }
    requireView().findViewById<EditText>(R.id.etLastName).apply {
      setText(patientUiState.lastName ?: "")
    }
    requireView().findViewById<EditText>(R.id.etBirthDate).apply {
      setText(patientUiState.birthDate ?: "")
    }

    val radioGroupGender = requireView().findViewById<RadioGroup>(R.id.radioGroupGender)
    radioGroupGender.clearCheck()
    val genderRadioButtonId =
      when (patientUiState.gender) {
        Enumerations.AdministrativeGender.MALE -> R.id.rbMale
        Enumerations.AdministrativeGender.FEMALE -> R.id.rbFemale
        Enumerations.AdministrativeGender.OTHER -> R.id.rbOther
        else -> null
      }
    genderRadioButtonId?.let { radioGroupGender.check(it) }

    requireView().findViewById<CheckBox>(R.id.checkBoxActive).apply {
      isChecked = patientUiState.isActive
    }
  }

  private fun isPatientCreationRequired(): Boolean {
    if (crudOperationViewModel.currentPatientLogicalId.isNullOrEmpty()) {
      Toast.makeText(requireContext(), "Please create a patient first.", Toast.LENGTH_SHORT).show()
      return true
    }
    return false
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

  private fun getPatientInput(): PatientInput? {
    val firstName = requireView().findViewById<EditText>(R.id.etFirstName).text.toString().trim()
    if (firstName.isBlank()) {
      Toast.makeText(requireContext(), "First name is required.", Toast.LENGTH_SHORT).show()
      return null
    }
    val lastName = requireView().findViewById<EditText>(R.id.etLastName).text.toString().trim()
    val birthDate = requireView().findViewById<EditText>(R.id.etBirthDate).text.toString().trim()
    if (birthDate.isNotEmpty() && !crudOperationViewModel.isBirthDateValid(birthDate)) {
      Toast.makeText(requireContext(), "Please enter a valid birth date.", Toast.LENGTH_SHORT)
        .show()
      return null
    }
    val selectedGenderId =
      requireView().findViewById<RadioGroup>(R.id.radioGroupGender).checkedRadioButtonId
    val gender =
      when (selectedGenderId) {
        R.id.rbMale -> Enumerations.AdministrativeGender.MALE
        R.id.rbFemale -> Enumerations.AdministrativeGender.FEMALE
        R.id.rbOther -> Enumerations.AdministrativeGender.OTHER
        else -> null
      }
    val isActive = requireView().findViewById<CheckBox>(R.id.checkBoxActive).isChecked

    return PatientInput(firstName, lastName, birthDate, gender, isActive)
  }

  private fun createPatient() {
    getPatientInput()?.let {
      crudOperationViewModel.createPatient(
        patientId = requireView().findViewById<EditText>(R.id.etId).text.toString(),
        firstName = it.firstName,
        lastName = it.lastName,
        birthDate = it.birthDate,
        gender = it.gender,
        isActive = it.isActive,
      )
    }
  }

  private fun updatePatient() {
    if (isPatientCreationRequired()) {
      return
    }
    getPatientInput()?.let {
      crudOperationViewModel.updatePatient(
        firstName = it.firstName,
        lastName = it.lastName,
        birthDate = it.birthDate,
        gender = it.gender,
        isActive = it.isActive,
      )
    }
  }

  private fun deletePatient() {
    if (isPatientCreationRequired()) {
      return
    }
    crudOperationViewModel.currentPatientLogicalId?.let { crudOperationViewModel.deletePatient() }
  }

  companion object {
    private const val TAB_CREATE = 0
    private const val TAB_READ = 1
    private const val TAB_UPDATE = 2
    private const val TAB_DELETE = 3
  }
}
