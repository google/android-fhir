package com.google.android.fhir.workflow

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.knowledge.FhirNpmPackage
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.knowledge.LocalFhirNpmPackageMetadata
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Resource
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
class SmartImmunizationTest {
    @get:Rule
    val fhirEngineProviderRule = FhirEngineProviderTestRule()

    private val fhirContext = FhirContext.forR4()

    private lateinit var fhirEngine: FhirEngine
    private lateinit var fhirOperator: FhirOperator

    private val loader = TestBundleLoader(fhirContext)

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val knowledgeManager = KnowledgeManager.create(context = context, inMemory = true)

    @Before
    fun setUp() = runBlockingOnWorkerThread {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
        fhirEngine = FhirEngineProvider.getInstance(context)
        fhirOperator = FhirOperator(fhirContext, fhirEngine, knowledgeManager)

        // Installing ANC CDS to the IGManager
        val rootDirectory = File(javaClass.getResource("/smart-imm/ig/")!!.file)

        println(rootDirectory)

        knowledgeManager.install(
            FhirNpmPackage(
                "who.fhir.immunization",
                "1.0.0",
                "https://github.com/WorldHealthOrganization/smart-immunizations",
            ),
            rootDirectory,
        )
    }

    @Test
    fun findCarePlan() = runBlockingOnWorkerThread {
        val planDef = knowledgeManager.loadResources(
            resourceType = "PlanDefinition",
            id = "IMMZD2DTMeasles"
        ).firstOrNull()

        loader.loadFile(
            "/smart-imm/tests/IMMZ-Patient-NoVaxeninfant-f/Patient/Patient-IMMZ-Patient-NoVaxeninfant-f.json",
            ::importToFhirEngine,
        )
        loader.loadFile(
            "/smart-imm/tests/IMMZ-Patient-NoVaxeninfant-f/Observation/Observation-birthweightnormal-NoVaxeninfant-f.json",
            ::importToFhirEngine,
        )

        assertThat(planDef).isNotNull()

        val carePlan = fhirOperator.generateCarePlan(
            planDefinitionId = "IMMZD2DTMeasles",
            patientId = "IMMZ-Patient-NoVaxeninfant-f",
        )

        println(FhirContext.forR4Cached().newJsonParser().encodeResourceToString(carePlan))

        assertThat(carePlan).isNotNull()
    }

    private suspend fun importToFhirEngine(resource: Resource) {
        fhirEngine.create(resource)
    }
}