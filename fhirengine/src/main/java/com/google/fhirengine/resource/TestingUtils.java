package com.google.fhirengine.resource;

import org.hl7.fhir.r4.model.Resource;

import javax.inject.Inject;

import ca.uhn.fhir.parser.IParser;

import static org.junit.Assert.assertEquals;

/** Utilities for testing. */
public class TestingUtils {
  private final IParser iParser;

  @Inject
  public TestingUtils(IParser iParser) {
    this.iParser = iParser;
  }

  /** Asserts that the {@code expected} and the {@code actual} FHIR resources are equal. */
  public void assertResourceEquals(Resource expected, Resource actual) {
    assertEquals(iParser.encodeResourceToString(expected),
        iParser.encodeResourceToString(actual));
  }
}
