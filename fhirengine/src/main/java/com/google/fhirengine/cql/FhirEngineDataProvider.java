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

import com.google.fhirengine.db.Database;
import org.opencds.cqf.cql.data.CompositeDataProvider;
import org.opencds.cqf.cql.model.ModelResolver;
import org.opencds.cqf.cql.retrieve.RetrieveProvider;

/**
 * FHIR Engine's implementation of a {@link org.opencds.cqf.cql.data.DataProvider} which provides
 * the {@link org.opencds.cqf.cql.execution.CqlEngine} required data to complete CQL evaluation.
 */
public class FhirEngineDataProvider extends CompositeDataProvider {
  public FhirEngineDataProvider(ModelResolver modelResolver, RetrieveProvider retrieveProvider) {
    super(modelResolver, retrieveProvider);
  }

  // TODO internal after moving to kotlin
  public static class Factory {
    public static FhirEngineDataProvider create(Database database) {
      return new FhirEngineDataProvider(
          new AndroidR4FhirModelResolver(), new FhirEngineRetrieveProvider(database));
    }
  }
}
