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

package com.google.android.fhir.cqlreference

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.cqlreference.FhirApplication.Companion.fhirEngine
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import org.hl7.fhir.r4.model.Resource
import org.opencds.cqf.cql.execution.EvaluationResult

class CqlLoadActivity : AppCompatActivity() {
	lateinit var fhirEngine: FhirEngine
	lateinit var cqlLibraryUrlInput: EditText
	lateinit var fhirResourceUrlInput: EditText
	lateinit var libraryInput: EditText
	lateinit var contextInput: EditText
	lateinit var expressionInput: EditText
	lateinit var evaluationResultTextView: TextView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_cql_load)

		fhirEngine = fhirEngine(this)
		cqlLibraryUrlInput = findViewById(R.id.cql_text_input)
		fhirResourceUrlInput = findViewById(R.id.fhir_resource_url_input)
		libraryInput = findViewById(R.id.library_input)
		contextInput = findViewById(R.id.context_input)
		expressionInput = findViewById(R.id.expression_input)
		evaluationResultTextView = findViewById(R.id.evaluate_result)

		val viewModel =
			ViewModelProvider(this, CqlLoadActivityViewModelFactory(fhirEngine))
				.get(CqlActivityViewModel::class.java)

		val loadCqlLibButton = findViewById<Button>(R.id.load_cql_lib_button)
		loadCqlLibButton.setOnClickListener {
			DownloadFhirResource().execute(cqlLibraryUrlInput.text.toString())
		}

		val downloadFhirResourceButton = findViewById<Button>(R.id.download_fhir_resource_button)
		downloadFhirResourceButton.setOnClickListener {
			DownloadFhirResource().execute(fhirResourceUrlInput.text.toString())
		}

		val evaluateButton = findViewById<Button>(R.id.evaluate_button)
		evaluateButton.setOnClickListener {
			EvaluateAncLibrary()
				.execute(
					libraryInput.text.toString(),
					contextInput.text.toString(),
					expressionInput.text.toString()
				)
		}
	}

	private inner class DownloadFhirResource : AsyncTask<String?, String?, Void?>() {
		override fun doInBackground(vararg strings: String?): Void? {
			var result: String? = ""
			var stream: InputStream? = null
			var resource: Resource? = null
			try {
				stream = URL(strings[0]).content as InputStream
				val reader = BufferedReader(InputStreamReader(stream))
				var line: String? = ""
				while (line != null) {
					result += line
					line = reader.readLine()
				}
				val fhirContext = FhirContext.forR4()
				resource = fhirContext.newJsonParser().parseResource(result) as Resource
				fhirEngine.save<Resource>(resource)
				Snackbar.make(
					cqlLibraryUrlInput,
					"Loaded " + resource.resourceType.name + " with ID " + resource.id,
					Snackbar.LENGTH_SHORT
				)
					.show()
			} catch (e: IOException) {
				e.printStackTrace()
				Snackbar.make(cqlLibraryUrlInput, "Something went wrong...", Snackbar.LENGTH_SHORT).show()
			}
			return null
		}
	}

	private inner class EvaluateAncLibrary : AsyncTask<String?, String?, EvaluationResult?>() {
		override fun doInBackground(vararg strings: String?): EvaluationResult? {
			return fhirEngine.evaluateCql(strings[0]!!, strings[1]!!, strings[2]!!)
		}

		override fun onPostExecute(result: EvaluationResult?) {
			val stringBuilder = StringBuilder()
			if (result?.libraryResults?.values != null)
				for (libraryResult in result.libraryResults.values) {
					for ((key, value) in libraryResult.expressionResults) {
						stringBuilder.append("$key -> ")
						if (value == null) {
							stringBuilder.append("null")
						} else if (MutableList::class.java.isAssignableFrom(value.javaClass)) {
							for (listItem in value as List<*>) {
								stringBuilder.append(
									FhirContext.forR4().newJsonParser().encodeResourceToString(listItem as Resource?)
								)
							}
						} else if (Resource::class.java.isAssignableFrom(value.javaClass)) {
							stringBuilder.append(
								FhirContext.forR4().newJsonParser().encodeResourceToString(value as Resource)
							)
						} else {
							stringBuilder.append(value.toString())
						}
					}
				}
			evaluationResultTextView.text = stringBuilder.toString()
		}
	}
}
