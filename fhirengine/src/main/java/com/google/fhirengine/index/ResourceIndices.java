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

package com.google.fhirengine.index;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.ResourceType;

/**
 * Encapsulation of a FHIR resource (its resource type and ID) and all the indices that are
 * extracted from the resource.
 *
 * <p>For example, for a {@link org.hl7.fhir.r4.model.Patient}, this class should include the
 * patient's resource type, ID, and all the field values of the patient that should be indexed such
 * as name, email address.
 */
public class ResourceIndices {
  /** The resource type. */
  private final ResourceType resourceType;

  /** The ID of the resource. */
  private final String id;

  /** The string indices. */
  private final List<StringIndex> stringIndices = new ArrayList<>();

  /** The reference indices. */
  private final List<ReferenceIndex> referenceIndices = new ArrayList<>();

  /** The code indices. */
  private final List<CodeIndex> codeIndices = new ArrayList<>();

  public ResourceIndices(ResourceType resourceType, String id) {
    this.resourceType = resourceType;
    this.id = id;
  }

  /** Returns the {@link ResourceType} of the resource. */
  public ResourceType getResourceType() {
    return resourceType;
  }

  /** Returns the ID of the resource. */
  public String getId() {
    return id;
  }

  /** Adds string values to the index. */
  public void addStringIndex(StringIndex stringIndex) {
    stringIndices.add(stringIndex);
  }

  /** Adds reference values to the index. */
  public void addReferenceIndex(ReferenceIndex referenceIndex) {
    referenceIndices.add(referenceIndex);
  }

  /** Adds code values to the index. */
  public void addCodeIndex(CodeIndex codeIndex) {
    codeIndices.add(codeIndex);
  }

  /** Returns the string indices. */
  public ImmutableList<StringIndex> getStringIndices() {
    return ImmutableList.copyOf(stringIndices);
  }

  /** Returns the reference indices. */
  public ImmutableList<ReferenceIndex> getReferenceIndices() {
    return ImmutableList.copyOf(referenceIndices);
  }

  /** Returns the code indices. */
  public ImmutableList<CodeIndex> getCodeIndices() {
    return ImmutableList.copyOf(codeIndices);
  }
}
