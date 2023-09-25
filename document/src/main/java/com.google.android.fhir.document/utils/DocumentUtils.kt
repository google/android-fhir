package com.google.android.fhir.library.utils

import android.content.Context
import com.google.android.fhir.document.dataClasses.IPSDocument
import com.google.android.fhir.document.dataClasses.Title
import org.hl7.fhir.r4.model.AllergyIntolerance
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Composition
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Medication
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

class DocumentUtils {

  fun getSectionsFromDoc(doc: IPSDocument) {
    val bundle = doc.document
    val composition =
      bundle.entry?.firstOrNull { it.resource.resourceType == ResourceType.Composition }?.resource as Composition
    doc.titles = composition.section.map { Title(it.title, ArrayList()) } as ArrayList<Title>
  }

  fun shouldExcludeResource(title: String, resource: Resource): Boolean {
    val code = resource.hasCode().second
    return (title == "History of Past Illness" && code == "active") || ((title == "Active Problems" || title == "Allergies and Intolerances") && code != "active")
  }

  fun getSearchingCondition(resource: String, resourceType: String): Boolean {
    return when (resource) {
      "Allergies and Intolerances" -> resourceType == "AllergyIntolerance"
      "Medication" -> resourceType == "Medication"
      "Active Problems", "History of Past Illness" -> resourceType == "Condition"
      "Immunizations" -> resourceType == "Immunization"
      "Results" -> resourceType == "Observation"
      "Plan of Treatment" -> false // inside div
      // titles have to change
      "procedure history" -> false
      "medical devices" -> false
      else -> false
    }
  }

  fun readFileFromAssets(context: Context, filename: String): String {
    return context.assets.open(filename).bufferedReader().use {
      it.readText()
    }
  }

  fun getCodings(res: Resource): MutableList<Coding>? {
    return res.hasCode().first?.coding
  }
}

fun Resource.hasCode(): Pair<CodeableConcept?, String?> {
  return when (this) {
    is AllergyIntolerance -> Pair(code, clinicalStatus.coding.firstOrNull()?.code)
    is Condition -> Pair(code, clinicalStatus.coding.firstOrNull()?.code)
    is Medication -> Pair(code, null)
    is Observation -> Pair(code, null)
    is Immunization -> Pair(vaccineCode, "")
    else -> Pair(null, null)
  }
}