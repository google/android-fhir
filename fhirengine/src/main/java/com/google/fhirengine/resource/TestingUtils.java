// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.fhirengine.resource;

import static org.junit.Assert.assertEquals;

import ca.uhn.fhir.parser.IParser;
import javax.inject.Inject;
import org.hl7.fhir.r4.model.Resource;

/** Utilities for testing. */
public class TestingUtils {
  private final IParser iParser;

  @Inject
  public TestingUtils(IParser iParser) {
    this.iParser = iParser;
  }

  /** Asserts that the {@code expected} and the {@code actual} FHIR resources are equal. */
  public void assertResourceEquals(Resource expected, Resource actual) {
    assertEquals(iParser.encodeResourceToString(expected), iParser.encodeResourceToString(actual));
  }
}
