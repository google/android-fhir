package com.google.fhirengine.cql;

import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.elm.execution.ObjectFactoryEx;
import org.opencds.cqf.cql.execution.LibraryLoader;

import javax.inject.Inject;
import javax.xml.namespace.QName;

/**
 * FHIR Engine's implementation of {@link LibraryLoader} that loads a CQL/ELM library for the {@link
 * org.opencds.cqf.cql.execution.CqlEngine} to use.
 */
public class FhirEngineLibraryLoader implements LibraryLoader {

  @Inject
  public FhirEngineLibraryLoader() {
  }

  @Override
  public Library load(VersionedIdentifier libraryIdentifier) {
    ObjectFactoryEx objectFactoryEx = new ObjectFactoryEx();
    return objectFactoryEx.createLibrary()
        .withIdentifier(objectFactoryEx.createVersionedIdentifier().withId("ANCFHIRDummy")
            .withVersion("0.1.0"))
        .withSchemaIdentifier(
            objectFactoryEx.createVersionedIdentifier().withId("urn:hl7-org:elm")
                .withVersion("r1"))
        .withUsings(
            objectFactoryEx.createLibraryUsings().withDef(
                objectFactoryEx.createUsingDef().withLocalIdentifier("System")
                    .withUri("urn:hl7-org:elm-types:r1")
            )
                .withDef(
                    objectFactoryEx.createUsingDef().withLocalIdentifier("FHIR")
                        .withUri("http://hl7.org/fhir").withVersion("4.0.0")
                )
        )
        .withStatements(
            objectFactoryEx.createLibraryStatements().withDef(
                objectFactoryEx.createExpressionDef()
                    .withName("Patient")
                    .withContext("Patient")
                    .withExpression(
                        objectFactoryEx.createSingletonFrom()
                            .withOperand(
                                objectFactoryEx.createRetrieve()
                                    .withDataType(
                                        new QName("http://hl7.org/fhir", "Patient", "fhir")
                                    )
                            )
                    )
            )
                .withDef(
                    objectFactoryEx.createExpressionDef()
                        .withName("Observations")
                        .withContext("Patient")
                        .withExpression(
                            objectFactoryEx.createRetrieve()
                                .withDataType(
                                    new QName("http://hl7.org/fhir", "Observation", "fhir"))
                        )
                )
        );
  }
}
