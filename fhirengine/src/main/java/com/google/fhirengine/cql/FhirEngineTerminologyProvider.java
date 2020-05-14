/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.fhirengine.cql;

import javax.inject.Inject;
import org.opencds.cqf.cql.runtime.Code;
import org.opencds.cqf.cql.terminology.CodeSystemInfo;
import org.opencds.cqf.cql.terminology.TerminologyProvider;
import org.opencds.cqf.cql.terminology.ValueSetInfo;

/** Fhir Engine's implementation of {@link TerminologyProvider}. */
public class FhirEngineTerminologyProvider implements TerminologyProvider {

  @Inject
  public FhirEngineTerminologyProvider() {}

  @Override
  public boolean in(Code code, ValueSetInfo valueSet) {
    return false;
  }

  @Override
  public Iterable<Code> expand(ValueSetInfo valueSet) {
    return null;
  }

  @Override
  public Code lookup(Code code, CodeSystemInfo codeSystem) {
    return null;
  }
}
