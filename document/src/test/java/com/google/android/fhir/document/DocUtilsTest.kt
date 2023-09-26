package com.google.android.fhir.document;

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.IPSDocument
import com.google.android.fhir.document.fileExamples.file
import com.google.android.fhir.library.utils.DocumentUtils
import org.hl7.fhir.r4.model.Bundle
import org.junit.Assert
import org.junit.Test;

class DocUtilsTest {

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val fileBundle = parser.parseResource(file) as Bundle
  private val immunizationBundleContent = this::class.java.classLoader?.getResource("immunizationBundle.json")?.readText()
  private val immunizationBundle = parser.parseResource(immunizationBundleContent) as Bundle
  private val docUtils = DocumentUtils()


  @Test
  fun getTitlesFromMinBundleDoc() {
    val doc = IPSDocument(fileBundle)
    docUtils.getSectionsFromDoc(doc)
    Assert.assertEquals(3, doc.titles.size)
  }

  @Test
  fun getTitlesFromImmunizationBundle() {
    val doc = IPSDocument(immunizationBundle)
    docUtils.getSectionsFromDoc(doc)
    Assert.assertEquals(7, doc.titles.size)
  }

}
