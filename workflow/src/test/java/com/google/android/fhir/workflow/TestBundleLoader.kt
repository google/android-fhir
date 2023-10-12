package com.google.android.fhir.workflow

import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.workflow.testing.CqlBuilder
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import java.io.File
import java.io.InputStream
import java.lang.IllegalArgumentException
import kotlin.reflect.KSuspendFunction1

class TestBundleLoader(private val fhirContext: FhirContext = FhirContext.forR4()) {
    private val jsonParser = fhirContext.newJsonParser()
    private val xmlParser = fhirContext.newXmlParser()

    suspend fun loadFile(
        path: String,
        importFunction: KSuspendFunction1<Resource, Unit>
    ) {
        val resource =
            if (path.endsWith(suffix = ".xml")) {
                xmlParser.parseResource(open(path)) as Resource
            } else if (path.endsWith(".json")) {
                jsonParser.parseResource(open(path)) as Resource
            } else if (path.endsWith(".cql")) {
                toFhirLibrary(open(path))
            } else {
                throw IllegalArgumentException("Only xml and json and cql files are supported")
            }
        loadResource(resource, importFunction)
    }

    private suspend fun loadResource(
        resource: Resource,
        importFunction: KSuspendFunction1<Resource, Unit>,
    ) {
        when (resource.resourceType) {
            ResourceType.Bundle -> loadBundle(resource as Bundle, importFunction)
            else -> importFunction(resource)
        }
    }

    private suspend fun loadBundle(
        bundle: Bundle,
        importFunction: KSuspendFunction1<Resource, Unit>,
    ) {
        for (entry in bundle.entry) {
            val resource = entry.resource
            loadResource(resource, importFunction)
        }
    }

    private fun toFhirLibrary(cql: InputStream): Library {
        return CqlBuilder.compileAndBuild(cql)
    }

    private fun open(path: String) = javaClass.getResourceAsStream(path)!!

    fun readResourceAsString(path: String) = open(path).readBytes().decodeToString()
}