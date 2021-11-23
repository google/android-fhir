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

package com.google.android.fhir.reference.testData

class AddPatientTestData {

  val phoneNumber: String = "1234567890"
  val gender: String = "Male"
  val city: String = "NAIROBI"
  val country: String = "KE"
  val isActive: String = "Is Active?"

  fun firstName(): String {
    return generateString()
  }

  fun familyName(): String {
    return generateString()
  }

  private fun generateString(): String {

    val charPool: List<Char> = ('a'..'z') + ('A'..'Z')
    val outputStrLength = (5..10).shuffled().first()

    return (1..outputStrLength)
      .map { kotlin.random.Random.nextInt(0, charPool.size) }
      .map(charPool::get)
      .joinToString("")
  }
}
