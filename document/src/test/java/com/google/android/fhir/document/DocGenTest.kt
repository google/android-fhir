/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.document

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.IPSDocument
import com.google.android.fhir.document.dataClasses.SHLData
import com.google.android.fhir.testing.readFromFile
import org.hl7.fhir.r4.model.AllergyIntolerance
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Bundle.BundleType
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Medication
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class DocGenTest {

  private val docGenerator = DocumentGenerator()
  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val fileBundle = readFromFile(Bundle::class.java, "/bundleMinimal.json")

  private val res1 = readFromFile(Observation::class.java, "/observationResource.json")
  private val res2 = readFromFile(Condition::class.java, "/conditionResource.json")
  private val res3 = readFromFile(AllergyIntolerance::class.java, "/allergyResource.json")
  private val res4 = readFromFile(Immunization::class.java, "/immunizationResource.json")
  private val res5 = readFromFile(Medication::class.java, "/medicationResource.json")

  private val immunizationBundle = readFromFile(Bundle::class.java, "/immunizationBundle.json")

  private val list =
    listOf(
      res1 as Resource,
      res2 as Resource,
      res3 as Resource,
      res3 as Resource,
      res4 as Resource,
      res5 as Resource,
    )

  @Test
  fun testDocGen() {
    val doc = docGenerator.generateIPS(list)
    println(parser.encodeResourceToString(doc.document))
    assertEquals(doc.document.type, BundleType.DOCUMENT)
  }

  @Test
  fun testSHLDataCanBeInitialised() {
    val doc = docGenerator.generateIPS(list)
    val shlData = SHLData(doc)
    shlData.label = "abc"
    assertEquals(shlData.label, "abc")
  }

  @Test
  fun mapCanBeCreatedWithDataForEachTitle() {
    val doc = IPSDocument(fileBundle)
    val data = docGenerator.getDataFromDoc(doc)
    println(data)
  }

  @Test
  fun mapCanBeCreatedWithDataForEachTitleInImmunization() {
    val doc = IPSDocument(immunizationBundle)
    val data = docGenerator.getDataFromDoc(doc)
    println(data)
  }

  @Test
  fun anIPSDocRequiresPAMs() {
    val doc = docGenerator.generateIPS(emptyList())
    val bundle = doc.document
    assert(bundle.entry.any { it.resource.resourceType == ResourceType.AllergyIntolerance })
    assert(bundle.entry.any { it.resource.resourceType == ResourceType.Medication })
    assert(bundle.entry.any { it.resource.resourceType == ResourceType.Condition })
  }

  @Test
  fun emptyAllergyResourceHasCorrectCode() {
    val doc = docGenerator.generateIPS(emptyList())
    val bundle = doc.document
    val allergy =
      bundle.entry.first { it.resource.resourceType == ResourceType.AllergyIntolerance }.resource
        as AllergyIntolerance
    assert(allergy.code.coding.first().code == "no-allergy-info")
  }

  @Test
  fun emptyMedicationResourceHasCorrectCode() {
    val doc = docGenerator.generateIPS(emptyList())
    val bundle = doc.document
    val medication =
      bundle.entry.first { it.resource.resourceType == ResourceType.Medication }.resource
        as Medication
    assert(medication.code.coding.first().code == "no-medication-info")
  }

  @Test
  fun emptyProblemResourceHasCorrectCode() {
    val doc = docGenerator.generateIPS(emptyList())
    val bundle = doc.document
    val problem =
      bundle.entry.first { it.resource.resourceType == ResourceType.Condition }.resource
        as Condition
    assert(problem.code.coding.first().code == "no-problem-info")
  }

  @Test
  fun anImmunizationCanBeAddedToIPS() {
    val doc = docGenerator.generateIPS(listOf(Immunization()))
    val bundle = doc.document
    assert(bundle.entry.any { it.resource.resourceType == ResourceType.Immunization })
  }
}
