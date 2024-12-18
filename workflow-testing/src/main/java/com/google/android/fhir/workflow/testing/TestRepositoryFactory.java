package com.google.android.fhir.workflow.testing;

import org.opencds.cqf.fhir.api.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;

public class TestRepositoryFactory {
    public static Repository createRepository(FhirContext fhirContext, String path) {
        return new IGInputStreamStructureRepository(
                fhirContext,
                path,
            EncodingEnum.JSON
        );
    }
}