package com.google.fhirengine.index;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import ca.uhn.fhir.model.api.annotation.SearchParamDefinition;

/**
 * Encapsulation of a FHIR resource (its resource type and ID) and all the indices that are
 * extracted from the resource.
 * <p>
 * For example, for a {@link org.hl7.fhir.r4.model.Patient}, this class should include the patient's
 * resource type, ID, and all the field values of the patient that should be indexed such as name,
 * email address.
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

  public ResourceIndices(ResourceType resourceType, String id) {
    this.resourceType = resourceType;
    this.id = id;
  }

  /** Returns the {@link ResourceType} of the resource. */
  public ResourceType getResourceType(){
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

  /** Returns the string indices. */
  public ImmutableList<StringIndex> getStringIndices() {
    return ImmutableList.copyOf(stringIndices);
  }

  /** Returns the reference indices. */
  public ImmutableList<ReferenceIndex> getReferenceIndices() {
    return ImmutableList.copyOf(referenceIndices);
  }
}
