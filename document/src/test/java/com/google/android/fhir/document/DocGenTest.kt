package com.google.android.fhir.document

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.IPSDocument
import com.google.android.fhir.document.dataClasses.SHLData
import com.google.android.fhir.document.fileExamples.file
import com.google.android.fhir.document.fileExamples.res1
import com.google.android.fhir.document.fileExamples.res2
import com.google.android.fhir.document.fileExamples.res3
import com.google.android.fhir.document.fileExamples.res5
import org.hl7.fhir.r4.model.AllergyIntolerance
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Bundle.BundleType
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Medication
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
  private val fileBundle = parser.parseResource(file) as Bundle

  private val list = listOf(
    parser.parseResource(res1) as Resource,
    parser.parseResource(res2) as Resource,
    parser.parseResource(res3) as Resource,
    parser.parseResource(res3) as Resource,
    parser.parseResource(res5) as Resource
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

  // @Test
  // fun mapCanBeCreatedWithDataForEachTitleInImmunization() {
  //   val doc = IPSDocument(immunizationBundle)
  //   val data = docGenerator.getDataFromDoc(doc)
  //   println(data)
  // }

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
      bundle.entry.first { it.resource.resourceType == ResourceType.AllergyIntolerance }.resource as AllergyIntolerance
    assert(allergy.code.coding.first().code == "no-allergy-info")
  }

  @Test
  fun emptyMedicationResourceHasCorrectCode() {
    val doc = docGenerator.generateIPS(emptyList())
    val bundle = doc.document
    val medication =
      bundle.entry.first { it.resource.resourceType == ResourceType.Medication }.resource as Medication
    assert(medication.code.coding.first().code == "no-medication-info")
  }

  @Test
  fun emptyProblemResourceHasCorrectCode() {
    val doc = docGenerator.generateIPS(emptyList())
    val bundle = doc.document
    val problem =
      bundle.entry.first { it.resource.resourceType == ResourceType.Condition }.resource as Condition
    assert(problem.code.coding.first().code == "no-problem-info")
  }

  @Test
  fun anImmunizationCanBeAddedToIPS() {
    val doc = docGenerator.generateIPS(listOf(Immunization()))
    val bundle = doc.document
    assert(bundle.entry.any { it.resource.resourceType == ResourceType.Immunization })
  }

}