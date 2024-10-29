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

package com.google.android.fhir.demo.helpers

import android.icu.text.SimpleDateFormat
import java.util.*
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient

object PatientCreationHelper {

  private fun createPatient(
    firstName: String,
    lastName: String,
    birthDate: String,
    gender: Enumerations.AdministrativeGender,
    phoneNumber: String,
    city: String,
    country: String,
    isActive: Boolean,
  ): Patient {
    val patient = Patient()

    // Set UUID as patient ID
    val patientId = UUID.randomUUID().toString()
    patient.id = patientId

    // Set patient name
    val name = HumanName().setFamily(lastName).addGiven(firstName)
    patient.addName(name)

    // Set patient birth date
    val dob = SimpleDateFormat("yyyy-MM-dd").parse(birthDate)
    patient.birthDate = dob

    // Set patient gender
    patient.gender = gender

    // Set patient telecom (phone)
    val telecom = ContactPoint()
    telecom.system = ContactPoint.ContactPointSystem.PHONE
    telecom.value = phoneNumber
    patient.addTelecom(telecom)

    // Set patient address
    val address = Address()
    address.city = city
    address.country = country
    patient.addAddress(address)

    // Set active status
    patient.active = isActive

    return patient
  }

  fun createSamplePatients(): List<Patient> {
    val patients = mutableListOf<Patient>()

    // Patient 1
    patients.add(
      createPatient(
        firstName = "John",
        lastName = "Doe",
        birthDate = "1990-01-01",
        gender = Enumerations.AdministrativeGender.MALE,
        phoneNumber = "1234567890",
        city = "New York",
        country = "USA",
        isActive = true,
      ),
    )

    // Patient 2
    patients.add(
      createPatient(
        firstName = "Jane",
        lastName = "Smith",
        birthDate = "1985-05-15",
        gender = Enumerations.AdministrativeGender.FEMALE,
        phoneNumber = "0987654321",
        city = "Los Angeles",
        country = "USA",
        isActive = true,
      ),
    )

    // Patient 3
    patients.add(
      createPatient(
        firstName = "Emily",
        lastName = "Johnson",
        birthDate = "1978-11-12",
        gender = Enumerations.AdministrativeGender.FEMALE,
        phoneNumber = "2345678901",
        city = "Chicago",
        country = "USA",
        isActive = true,
      ),
    )

    // Patient 4
    patients.add(
      createPatient(
        firstName = "Michael",
        lastName = "Brown",
        birthDate = "1982-04-07",
        gender = Enumerations.AdministrativeGender.MALE,
        phoneNumber = "3456789012",
        city = "Houston",
        country = "USA",
        isActive = false,
      ),
    )

    // Patient 5
    patients.add(
      createPatient(
        firstName = "Sophia",
        lastName = "Davis",
        birthDate = "1995-08-22",
        gender = Enumerations.AdministrativeGender.FEMALE,
        phoneNumber = "4567890123",
        city = "Phoenix",
        country = "USA",
        isActive = true,
      ),
    )

    // Patient 6
    patients.add(
      createPatient(
        firstName = "Liam",
        lastName = "Wilson",
        birthDate = "2001-12-30",
        gender = Enumerations.AdministrativeGender.MALE,
        phoneNumber = "5678901234",
        city = "Dallas",
        country = "USA",
        isActive = true,
      ),
    )

    // Patient 7
    patients.add(
      createPatient(
        firstName = "Olivia",
        lastName = "Martinez",
        birthDate = "1989-03-17",
        gender = Enumerations.AdministrativeGender.FEMALE,
        phoneNumber = "6789012345",
        city = "Miami",
        country = "USA",
        isActive = false,
      ),
    )

    // Patient 8
    patients.add(
      createPatient(
        firstName = "Noah",
        lastName = "Garcia",
        birthDate = "1975-07-05",
        gender = Enumerations.AdministrativeGender.MALE,
        phoneNumber = "7890123456",
        city = "San Francisco",
        country = "USA",
        isActive = true,
      ),
    )

    // Patient 9
    patients.add(
      createPatient(
        firstName = "Ava",
        lastName = "Anderson",
        birthDate = "1998-02-27",
        gender = Enumerations.AdministrativeGender.FEMALE,
        phoneNumber = "8901234567",
        city = "Seattle",
        country = "USA",
        isActive = true,
      ),
    )

    // Patient 10
    patients.add(
      createPatient(
        firstName = "Ethan",
        lastName = "Harris",
        birthDate = "1993-09-10",
        gender = Enumerations.AdministrativeGender.MALE,
        phoneNumber = "9012345678",
        city = "Boston",
        country = "USA",
        isActive = true,
      ),
    )

    // Add more patients as needed

    return patients
  }
}
