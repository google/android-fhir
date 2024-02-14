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

package com.google.android.fhir.document.render

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.fhir.document.IPSDocument
import com.google.android.fhir.document.R
import org.hl7.fhir.r4.model.AllergyIntolerance
import org.hl7.fhir.r4.model.Composition
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Medication
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

class IPSRenderer(private val document: IPSDocument?) {

  private fun createTextView(context: Context, text: String): TextView {
    return TextView(context).apply {
      this.text = text
      setPadding(8, 8, 8, 8)
      gravity = Gravity.START
    }
  }

  private fun createSeparator(context: Context): View {
    val separator = View(context)
    separator.layoutParams =
      TableRow.LayoutParams(
        TableRow.LayoutParams.MATCH_PARENT,
        5,
      )
    separator.setBackgroundColor(Color.BLACK)
    return separator
  }

  private fun createHorizontalScrollView(
    context: Context,
    textView: TextView,
  ): HorizontalScrollView {
    return HorizontalScrollView(context).apply { addView(textView) }
  }

  fun render(
    context: Context,
  ) {
    val patientTable = (context as AppCompatActivity).findViewById<TableLayout>(R.id.patientTable)
    val documentTable = context.findViewById<TableLayout>(R.id.documentTable)
    val immunizationTable = context.findViewById<TableLayout>(R.id.immunizationTable)
    val allergiesTable = context.findViewById<TableLayout>(R.id.allergiesTable)
    val resultsTable = context.findViewById<TableLayout>(R.id.resultsTable)
    val medicationTable = context.findViewById<TableLayout>(R.id.medicationTable)
    val problemsTable = context.findViewById<TableLayout>(R.id.problemsTable)

    val allergiesSection = context.findViewById<LinearLayout>(R.id.allergiesSection)
    val problemSection = context.findViewById<LinearLayout>(R.id.problemSection)
    val medicationSection = context.findViewById<LinearLayout>(R.id.medicationSection)
    val immunizationSection = context.findViewById<LinearLayout>(R.id.immunizationSection)
    val resultsSection = context.findViewById<LinearLayout>(R.id.resultsSection)

    allergiesSection.visibility = View.GONE
    problemSection.visibility = View.GONE
    medicationSection.visibility = View.GONE
    immunizationSection.visibility = View.GONE
    resultsSection.visibility = View.GONE

    val entries = document?.document?.entry
    entries?.firstOrNull()?.let { firstEntry ->
      if (firstEntry.resource.resourceType == ResourceType.Composition) {
        val documentText = "Summary Date: ${(firstEntry.resource as Composition).dateElement.value}"
        val docRow = createRowWithTextViewAndScrollView(context, documentText)
        documentTable.addView(docRow)
        val separator = createSeparator(context)
        documentTable.addView(separator)
      }
    }
    entries?.forEach { entry ->
      when (entry.resource.resourceType) {
        ResourceType.Immunization -> {
          val date = (entry.resource as Immunization).occurrenceDateTimeType.valueAsString
          val vaccine = (entry.resource as Immunization).vaccineCode.coding[0].display
          val row = TableRow(context)
          val dateTextView = createTextView(context, date)
          val vaccineHorizontalTextView = createTextView(context, vaccine)
          val horizontalScrollView = createHorizontalScrollView(context, vaccineHorizontalTextView)
          row.addView(dateTextView)
          row.addView(horizontalScrollView)
          row.setBackgroundColor(Color.LTGRAY)
          immunizationTable.addView(row)
          val separator = createSeparator(context)
          immunizationTable.addView(separator)
          immunizationSection.visibility = View.VISIBLE
        }
        ResourceType.Patient -> {
          val patient = (entry.resource as Patient)
          val patientText =
            "Name: ${patient.name.first().givenAsSingleString} ${patient.name.first().family} \nBirth Date: ${patient.birthDateElement.valueAsString}"
          val row = createRowWithTextViewAndScrollView(context, patientText)
          patientTable.addView(row)
          val separator = createSeparator(context)
          patientTable.addView(separator)
        }
        ResourceType.AllergyIntolerance -> {
          val allergyEntry = (entry.resource as AllergyIntolerance)
          val allergy = allergyEntry.code.coding[0].display
          val code = allergyEntry.clinicalStatus.coding.firstOrNull()?.code
          println(code)
          if (code == "active" || allergyEntry.code.coding[0].code == "no-allergy-info") {
            val categories = allergyEntry.category.joinToString(" - ") { it.valueAsString }
            val allergyText =
              "${allergyEntry.type?.name ?: ""} - $categories - Criticality: ${allergyEntry.criticality?.name ?: "undefined"}\n$allergy (${allergyEntry.code.coding[0].code})"
            val row = createRowWithTextViewAndScrollView(context, allergyText)
            allergiesTable.addView(row)
            val separator = createSeparator(context)
            allergiesTable.addView(separator)
            allergiesSection.visibility = View.VISIBLE
          }
        }
        ResourceType.Observation -> {
          val observation = entry.resource as Observation
          val date = observation.effectiveDateTimeType.valueAsString
          val resultName = observation.code.coding.firstOrNull()?.display
          val value =
            if (observation.hasValueCodeableConcept()) {
              observation.valueCodeableConcept.coding.firstOrNull()?.display
            } else {
              "${observation.valueQuantity.value}${observation.valueQuantity.unit}"
            }
          val category =
            (entry.resource as Observation).category.firstOrNull()?.coding?.firstOrNull()?.code
          if (resultName != null) {
            val resultsDisplay =
              "Name: $resultName \nDate/Time: $date \nValue: $value\nCategory: $category"
            val row = createRowWithTextViewAndScrollView(context, resultsDisplay)
            resultsTable.addView(row)
            val separator = createSeparator(context)
            resultsTable.addView(separator)
            resultsSection.visibility = View.VISIBLE
          }
        }
        ResourceType.Medication -> {
          val medication = (entry.resource as Medication).code.coding
          val medicationDisplays =
            medication.joinToString("\n") { "${it.display} (${it.code}) (${it.system})" }
          val row = createRowWithTextViewAndScrollView(context, medicationDisplays)
          medicationTable.addView(row)
          val separator = createSeparator(context)
          medicationTable.addView(separator)
          medicationSection.visibility = View.VISIBLE
        }
        ResourceType.Condition -> {
          val problem = (entry.resource as Condition).code.coding
          if (
            (entry.resource as Condition).clinicalStatus.coding.firstOrNull()?.code == "active" ||
              problem[0].code == "no-problem-info"
          ) {
            if (problem != null) {
              val conditionDisplay =
                problem.joinToString("\n") { "${it.display} (${it.code}) \n(${it.system})" }
              val row = createRowWithTextViewAndScrollView(context, conditionDisplay)
              problemsTable.addView(row)
              val separator = createSeparator(context)
              problemsTable.addView(separator)
              problemSection.visibility = View.VISIBLE
            }
          }
        }
        else -> {}
      }
    }
    addGapToTable(immunizationTable, 30)
    addGapToTable(allergiesTable, 30)
    addGapToTable(problemsTable, 30)
    addGapToTable(resultsTable, 30)
    addGapToTable(medicationTable, 30)
    addGapToTable(patientTable, 30)
    addGapToTable(documentTable, 30)
  }

  private fun createRowWithTextViewAndScrollView(
    context: Context,
    text: String,
  ): TableRow {
    val row = TableRow(context)
    val textView = createTextView(context, text)
    val horizontalScrollView = createHorizontalScrollView(context, textView)
    row.addView(horizontalScrollView)
    row.setBackgroundColor(Color.LTGRAY)
    row.gravity = Gravity.START
    return row
  }

  private fun addGapToTable(table: TableLayout, gapHeight: Int) {
    val gapRow = TableRow(table.context)
    val params =
      TableRow.LayoutParams(
        TableRow.LayoutParams.MATCH_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT,
      )
    gapRow.layoutParams = params
    gapRow.minimumHeight = gapHeight
    table.addView(gapRow)
  }
}
