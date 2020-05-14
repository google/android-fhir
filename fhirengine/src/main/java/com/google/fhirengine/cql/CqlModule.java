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

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import org.opencds.cqf.cql.data.DataProvider;
import org.opencds.cqf.cql.execution.LibraryLoader;
import org.opencds.cqf.cql.model.ModelResolver;
import org.opencds.cqf.cql.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.terminology.TerminologyProvider;

/** Dagger module for CQL Engine. */
@Module
public abstract class CqlModule {
  @Binds
  abstract LibraryLoader bindLibraryLoader(FhirEngineLibraryLoader libraryLoader);

  @Binds
  abstract RetrieveProvider bindRetrieveProvider(FhirEngineRetrieveProvider retrieveProvider);

  @Binds
  @IntoMap
  @StringKey("http://hl7.org/fhir")
  abstract DataProvider bindDataProvider(FhirEngineDataProvider dataProvider);

  /**
   * Binds the data provider for an empty namespace URI. This is a workaround for an inconsistency
   * issue on Android where the namespace URI is incorrectly interpreted as part of the type name,
   * leaving the namespace URI empty.
   */
  @Binds
  @IntoMap
  @StringKey("")
  abstract DataProvider bindDefaultDataProvider(FhirEngineDataProvider dataProvider);

  @Binds
  abstract TerminologyProvider bindTerminologyProvider(
      FhirEngineTerminologyProvider terminologyProvider);

  @Binds
  abstract ModelResolver bindModelResolver(AndroidR4FhirModelResolver modelResolver);
}
