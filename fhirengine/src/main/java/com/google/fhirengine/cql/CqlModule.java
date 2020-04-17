package com.google.fhirengine.cql;

import org.opencds.cqf.cql.data.DataProvider;
import org.opencds.cqf.cql.execution.LibraryLoader;
import org.opencds.cqf.cql.retrieve.RetrieveProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

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
}
