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

package com.google.fhirengine;

import android.content.Context;

import com.google.fhirengine.cql.CqlModule;
import com.google.fhirengine.db.impl.DatabaseModule;
import com.google.fhirengine.impl.FhirEngineModule;
import com.google.fhirengine.index.impl.FhirIndexerModule;
import com.google.fhirengine.resource.ResourceModule;
import com.google.fhirengine.search.impl.SearchModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/** Dagger component for creating {@link FhirEngine}. */
@Singleton
@Component(modules = {FhirEngineModule.class, FhirIndexerModule.class, DatabaseModule.class,
    CqlModule.class, ResourceModule.class})
public interface FhirEngineComponent {

  FhirEngine getFhirEngine();

  @Component.Builder
  interface Builder {
    @BindsInstance
    Builder context(Context context);

    FhirEngineComponent build();
  }
}
