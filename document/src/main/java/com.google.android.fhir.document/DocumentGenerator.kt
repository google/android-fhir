package com.google.android.fhir.document

import android.content.Context
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.IPSDocument
import com.google.android.fhir.document.dataClasses.Title
import com.google.android.fhir.document.interfaces.IPSDocumentGenerator
import com.google.android.fhir.document.utils.DocumentGeneratorUtils
import com.google.android.fhir.library.utils.DocumentUtils
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Resource

class DocumentGenerator : IPSDocumentGenerator {

  private val docGenUtils = DocumentGeneratorUtils()
  private val docUtils = DocumentUtils()
  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  override fun getDataFromDoc(doc: IPSDocument): List<Title> {
    val bundle = doc.document

    for (title in doc.titles) {
      val filteredResources = bundle.entry.map { it.resource }.filter { resource ->
        val resourceType = resource.resourceType.toString()
        docUtils.getSearchingCondition(title.name, resourceType)
      }
      val resourceList = filteredResources.filterNot { docUtils.shouldExcludeResource(title.name, it) }
      val existingTitle = doc.titles.find { it.name == title.name }
      existingTitle?.dataEntries?.addAll(resourceList)
    }
    return doc.titles
  }

  override fun generateIPS(selectedResources: List<Resource>): IPSDocument {
    val composition = docGenUtils.createIPSComposition()
    val sections = docGenUtils.createIPSSections(selectedResources)
    val (missingSections, missingResources) = docGenUtils.checkSections(sections)
    val referenced = mutableListOf<Resource>()

    selectedResources.forEach { resource ->
      val references = findReferences(resource)
      referenced.addAll(references)
    }
    sections.addAll(missingSections)
    composition.section = sections
    val bundle =
      docGenUtils.addResourcesToDoc(composition, selectedResources + referenced, missingResources)
    println(parser.encodeResourceToString(bundle))
    return IPSDocument(bundle)
  }

  private fun findReferences(resource: Resource): List<Resource> {
    val organizationReferences = mutableListOf<Resource>()
    if (resource is Observation) {
      val performerReferences = resource.performer
      performerReferences.forEach { performerReference ->
        if (performerReference.reference.isNotBlank()) {
          val organization = docGenUtils.createOrganizationFromReference(performerReference)
          organizationReferences.add(organization)
        }
      }
    }
    return organizationReferences
  }

  override fun displayOptions(
    context: Context,
    bundle: IPSDocument,
    checkboxes: MutableList<CheckBox>,
    checkboxTitleMap: MutableMap<String, String>
  ): List<Title> {
    docUtils.getSectionsFromDoc(bundle)
    val containerLayout = (context as AppCompatActivity).findViewById<LinearLayout>(R.id.containerLayout)

    return getDataFromDoc(bundle).filter { title ->
      title.dataEntries.any { docUtils.getCodings(it)?.isNotEmpty() == true }
    }.map { title ->
      val headingView = docGenUtils.createHeadingView(context, title.name, containerLayout)
      containerLayout.addView(headingView)

      title.dataEntries.forEach { obj ->
        docUtils.getCodings(obj)?.firstOrNull { it.hasDisplay() }?.let { codingElement ->
          val displayValue = codingElement.display
          val checkBoxItem = docGenUtils.createCheckBox(context, displayValue, containerLayout)
          containerLayout.addView(checkBoxItem)
          checkboxTitleMap[displayValue] = title.name
          checkboxes.add(checkBoxItem)
        }
      }
      title
    }
  }


}