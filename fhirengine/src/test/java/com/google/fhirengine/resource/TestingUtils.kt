package com.google.fhirengine.resource

import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.r4.model.Resource
import org.junit.Assert.assertEquals
import javax.inject.Inject


/** Utilities for testing.  */
class TestingUtils @Inject constructor(private val iParser: IParser) {

    /** Asserts that the `expected` and the `actual` FHIR resources are equal.  */
    fun assertResourceEquals(expected: Resource?, actual: Resource?) {
        assertEquals(iParser.encodeResourceToString(expected), iParser.encodeResourceToString(actual))
    }

}
