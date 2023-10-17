package com.google.android.fhir.workflow.testing;

import org.opencds.cqf.fhir.api.Repository;
import org.opencds.cqf.fhir.utility.repository.IGFileStructureRepository;
import org.opencds.cqf.fhir.utility.repository.IGLayoutMode;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;

public class TestRepositoryFactory {
    private TestRepositoryFactory() {
        // intentionally empty
    }

    public static Repository createRepository(FhirContext fhirContext, String path) {
        return createRepository(fhirContext, path, IGLayoutMode.TYPE_PREFIX);
    }

    public static Repository createRepository(
            FhirContext fhirContext, String path, IGLayoutMode layoutMode) {
        return new IGInputStreamStructureRepository(
            fhirContext,
            path,
            layoutMode,
            EncodingEnum.JSON
        );
    }
}