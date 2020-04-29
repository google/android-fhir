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

package com.google.fhirengine.index.impl;

import android.os.Build;

import com.google.common.truth.Truth;
import com.google.fhirengine.index.CodeIndex;
import com.google.fhirengine.index.ReferenceIndex;
import com.google.fhirengine.index.ResourceIndices;
import com.google.fhirengine.index.StringIndex;
import com.google.fhirengine.resource.ResourceModule;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

/** Unit tests for {@link FhirIndexerImpl}. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class FhirIndexerImplTest {
  private static final String TEST_CODE_SYSTEM_1 = "http://openmrs.org/concepts";
  private static final String TEST_CODE_VALUE_1 = "1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

  private static final String TEST_PATIENT_1_ID = "test_patient_1";
  private static final Patient TEST_PATIENT_1;

  static {
    TEST_PATIENT_1 = new Patient();
    TEST_PATIENT_1.setId(TEST_PATIENT_1_ID);
    TEST_PATIENT_1.addName(new HumanName().addGiven("Tom"));
  }

  private static final String TEST_OBSERVATION_1_ID = "test_observation_1";
  private static final Observation TEST_OBSERVATION_1;

  static {
    TEST_OBSERVATION_1 = new Observation();
    TEST_OBSERVATION_1.setId(TEST_OBSERVATION_1_ID);
    TEST_OBSERVATION_1.setSubject(new Reference().setReference("Patient/" + TEST_PATIENT_1_ID));
    TEST_OBSERVATION_1
        .setCode(new CodeableConcept()
            .addCoding(new Coding().setSystem(TEST_CODE_SYSTEM_1).setCode(TEST_CODE_VALUE_1)));
  }

  @Inject
  FhirIndexerImpl fhirIndexer;

  @Singleton
  @Component(modules = {FhirIndexerModule.class, ResourceModule.class})
  public interface TestComponent {
    void inject(com.google.fhirengine.index.impl.FhirIndexerImplTest fhirIndexerImplTest);
  }

  @Before
  public void setUp() throws Exception {
    DaggerFhirIndexerImplTest_TestComponent.builder().build().inject(this);
  }

  @Test
  public void index_patient_shouldIndexGivenName() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_PATIENT_1);
    Truth.assertThat(resourceIndices.getStringIndices())
        .contains(new StringIndex("given", "Patient.name.given", "Tom"));
  }

  @Test
  public void index_observation_shouldIndexSubject() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_OBSERVATION_1);
    Truth.assertThat(resourceIndices.getReferenceIndices())
        .contains(new ReferenceIndex("subject", "Observation.subject", "Patient/" + TEST_PATIENT_1_ID));
  }

  @Test
  public void index_observation_shouldIndexCode() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_OBSERVATION_1);
    Truth.assertThat(resourceIndices.getCodeIndices())
        .contains(new CodeIndex("code", "Observation.code", TEST_CODE_SYSTEM_1, TEST_CODE_VALUE_1));
  }

  // TODO: improve the tests.
}