package com.google.fhirengine;

import android.content.Context;

import com.google.fhirengine.db.impl.DatabaseModule;
import com.google.fhirengine.impl.FhirEngineModule;
import com.google.fhirengine.index.impl.FhirIndexerModule;
import com.google.fhirengine.resource.ResourceModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/** Dagger component for creating {@link FhirEngine}. */
@Singleton
@Component(modules = {FhirEngineModule.class, FhirIndexerModule.class, DatabaseModule.class,
    ResourceModule.class})
public interface FhirEngineComponent {

  FhirEngine getFhirEngine();

  @Component.Builder
  interface Builder {
    @BindsInstance
    Builder context(Context context);

    FhirEngineComponent build();
  }
}
