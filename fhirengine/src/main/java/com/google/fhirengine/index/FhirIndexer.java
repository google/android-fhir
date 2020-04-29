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

package com.google.fhirengine.index;

import org.hl7.fhir.r4.model.Resource;

/**
 * The interface that handles the indexing of FHIR resources.
 * <p>
 * Note: this interface does not handle the actual storage of the indices.
 */
public interface FhirIndexer {
  /**
   * Returns the values to index for a FHIR {@code resource} in the local storage.
   *
   * @param <R> The resource type which should be a subtype of {@link Resource}.
   */
  <R extends Resource> ResourceIndices index(R resource);
}
