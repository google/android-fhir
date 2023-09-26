package com.google.android.fhir.document

import  ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.IPSDocument
import com.google.android.fhir.document.fileExamples.file
import com.google.android.fhir.document.fileExamples.immunizationBundleString
import com.google.android.fhir.document.utils.DocumentGeneratorUtils
import com.google.android.fhir.library.utils.DocumentUtils
import org.hl7.fhir.r4.model.AllergyIntolerance
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Medication
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DocumentTest {

  private val docGenerator = DocumentGenerator()
  private val docUtils = DocumentUtils()
  private val docGenUtils = DocumentGeneratorUtils()
  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  private val fileBundle = parser.parseResource(file) as Bundle
  private val immunizationBundle = parser.parseResource(immunizationBundleString) as Bundle

  @Test
  fun getTitlesFromMinBundleDoc() {
    val doc = IPSDocument(fileBundle)
    docUtils.getSectionsFromDoc(doc)
    assertEquals(3, doc.titles.size)
  }

  @Test
  fun getTitlesFromImmunizationBundle() {
    val doc = IPSDocument(immunizationBundle)
    docUtils.getSectionsFromDoc(doc)
    println(doc.titles)
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

  @Test
  fun activeAllergyReturnsCorrectTitle() {
    val allergy = AllergyIntolerance()
    allergy.clinicalStatus.coding.add(Coding().apply {
      code = "active"
    })
    val title = docGenUtils.getResourceTitle(allergy)
    assertEquals(title, "Allergies and Intolerances")
  }

  @Test
  fun pastAllergyReturnsCorrectTitle() {
    val allergy = AllergyIntolerance()
    allergy.clinicalStatus.coding.add(Coding().apply {
      code = "remission"
    })
    val title = docGenUtils.getResourceTitle(allergy)
    assertEquals(title, "History of Past Illness")
  }

  @Test
  fun activeProblemReturnsCorrectTitle() {
    val problem = Condition()
    problem.clinicalStatus.coding.add(Coding().apply {
      code = "active"
    })
    val title = docGenUtils.getResourceTitle(problem)
    assertEquals(title, "Active Problems")
  }

  @Test
  fun pastProblemReturnsCorrectTitle() {
    val problem = Condition()
    problem.clinicalStatus.coding.add(Coding().apply {
      code = "remission"
    })
    val title = docGenUtils.getResourceTitle(problem)
    assertEquals(title, "History of Past Illness")
  }

}