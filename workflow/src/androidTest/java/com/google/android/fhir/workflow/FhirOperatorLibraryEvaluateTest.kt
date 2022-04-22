package com.google.android.fhir.workflow

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngineProvider
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class FhirOperatorLibraryEvaluateTest {

    private val fhirEngine = FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
    private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
    private val fhirOperator = FhirOperator(fhirContext, fhirEngine)

    private val json = fhirContext.newJsonParser()

    fun open(assetName: String): InputStream? {
        return javaClass.classLoader?.getResourceAsStream(assetName)
    }

    @Before
    fun setUp() = runBlocking {
        val ids = fhirEngine.create(json.parseResource(open("COVIDImmunizationHistory.json")) as Resource)
        println(ids)
        println("Composition " + fhirEngine.get(ResourceType.Composition, ids.first()))
        println("Organization " + fhirEngine.get(ResourceType.Organization, "#3"))
        println("Immunization " + fhirEngine.get(ResourceType.Immunization, "#2"))
        println("Patient " + fhirEngine.get(ResourceType.Patient, "#1"))

        fhirOperator.loadLibs(json.parseResource(open("COVIDCheck-FHIRLibraryBundle.json")) as Bundle)
    }

    @Test
    fun evaluateCOVIDCheck() {
        val results = fhirOperator.evaluateLibrary(
            "http://localhost/Library/COVIDCheck|1.0.0", "#1",
            setOf("CompletedImmunization", "GetFinalDose", "GetSingleDose",
                "ModernaProtocol", "PfizerProtocol")) as Parameters

        assertEquals(true, results.getParameterBool("CompletedImmunization"))
        assertEquals(false, results.getParameterBool("ModernaProtocol"))
        assertEquals(false, results.getParameterBool("PfizerProtocol"))
    }
}