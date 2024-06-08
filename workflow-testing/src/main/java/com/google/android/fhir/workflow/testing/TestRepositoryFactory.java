package com.google.android.fhir.workflow.testing;

import org.opencds.cqf.fhir.api.Repository;

import ca.uhn.fhir.context.FhirContext;

public class TestRepositoryFactory {
    private TestRepositoryFactory() {
        // intentionally empty
    }

    public static Repository createRepository(FhirContext fhirContext, String path) {
        return new IGInputStreamStructureRepository(
            fhirContext,
            path
        );
    }
}