package com.google.android.fhir.document

import  ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.IPSDocument
import com.google.android.fhir.document.fileExamples.file
// import com.google.android.fhir.document.fileExamples.immunizationBundleString
import org.hl7.fhir.r4.model.AllergyIntolerance
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Medication
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DocumentTest {

  private val docGenerator = DocumentGenerator()
  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val fileBundle = parser.parseResource(file) as Bundle
  // val immunizationBundle = readFromFile(Bundle::class.java, "immunizationBundle.json")


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