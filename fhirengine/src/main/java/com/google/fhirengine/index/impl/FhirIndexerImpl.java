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

package com.google.fhirengine.index.impl;

import android.text.TextUtils;
import android.util.Log;
import ca.uhn.fhir.model.api.annotation.SearchParamDefinition;

import com.google.fhirengine.index.FhirIndexer;
import com.google.fhirengine.index.ReferenceIndex;
import com.google.fhirengine.index.ResourceIndices;
import com.google.fhirengine.index.StringIndex;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;

/** Implementation of {@link FhirIndexer}. */
public class FhirIndexerImpl implements FhirIndexer {

  /** The prefix of getter methods for retrieving field values. */
  private static final String GETTER_PREFIX = "get";

  /** The regular expression for the separator */
  private static final String SEPARATOR_REGEX = "\\.";

  /** The string representing the string search parameter type. */
  private static final String SEARCH_PARAM_DEFINITION_TYPE_STRING = "string";

  /** The string representing the reference search parameter type. */
  private static final String SEARCH_PARAM_DEFINITION_TYPE_REFERENCE = "reference";

  /** The string representing the code search parameter type. */
  private static final String SEARCH_PARAM_DEFINITION_TYPE_CODE = "token";

  /** Tag for logging. */
  private static final String TAG = "FhirIndexerImpl";

  public static final String DOT_NOTATION_REGEX = "^[a-zA-Z0-9\\.]+$";

  @Inject
  public FhirIndexerImpl() {}

  @Override
  public <R extends Resource> ResourceIndices index(R resource) {
    return extractIndexValues(resource);
  }

  /** Extracts the values to be indexed for {@code resource}. */
  private <R extends Resource> ResourceIndices extractIndexValues(R resource) {
    ResourceIndices resourceIndices =
        new ResourceIndices(resource.getResourceType(), resource.getId());
    for (Field f : resource.getClass().getFields()) {
      SearchParamDefinition searchParamDefinition = f.getAnnotation(SearchParamDefinition.class);
      if (searchParamDefinition != null) {
        String path = searchParamDefinition.path();
        String type = searchParamDefinition.type();
        if (type.equals(SEARCH_PARAM_DEFINITION_TYPE_STRING) && hasDotNotationOnly(path)) {
          for (String value : getStringValues(getValuesForPath(resource, path))) {
            resourceIndices.addStringIndex(
                StringIndex.create(
                    searchParamDefinition.name(), searchParamDefinition.path(), value));
          }
        }

        if (type.equals(SEARCH_PARAM_DEFINITION_TYPE_REFERENCE) && hasDotNotationOnly(path)) {
          for (Reference reference : getReferenceValues(getValuesForPath(resource, path))) {
            String referenceString = reference.getReference();
            if (!TextUtils.isEmpty(referenceString)) {
              resourceIndices.addReferenceIndex(
                  ReferenceIndex.create(
                      searchParamDefinition.name(), searchParamDefinition.path(), referenceString));
            }
          }
        }

        if (type.equals(SEARCH_PARAM_DEFINITION_TYPE_CODE) && hasDotNotationOnly(path)) {
          for (Coding code : getCodeValues(getValuesForPath(resource, path))) {
            String system = code.getSystem();
            String value = code.getCode();
            if (!TextUtils.isEmpty(system) && !TextUtils.isEmpty(value)) {
              resourceIndices.addCodeIndex(
                  CodeIndex.create(
                      searchParamDefinition.name(), searchParamDefinition.path(), system, value));
            }
          }
        }

        // TODO: Implement number, date, token, reference, composite, quantity, URI, and special
        // search parameter types.
      }
    }
    return resourceIndices;
  }

  /** Returns the list of values corresponding to the {@code path} in the {@code resource}. */
  private static List<Object> getValuesForPath(Resource resource, String path) {
    String[] paths = path.split(SEPARATOR_REGEX);
    if (paths.length <= 1) {
      return null;
    }

    List<Object> objects = new ArrayList<>();
    objects.add(resource);
    for (int i = 1; i < paths.length; i++) {
      objects = getFieldValues(objects, paths[i]);
    }
    return objects;
  }

  /**
   * Returns the list of field values for {@code fieldName} in each of the {@code objects}.
   *
   * <p>If the field is a {@link List}, the list will be expanded and each element of the {@link
   * List} will be added to the returned value.
   */
  private static List<Object> getFieldValues(List<Object> objects, String fieldName) {
    List<Object> fieldValues = new ArrayList<>();
    for (Object o : objects) {
      Object fieldValue = null;
      try {
        fieldValue = o.getClass().getMethod(getGetterName(fieldName)).invoke(o);
        if (fieldValue == null) {
          continue;
        }
      } catch (InvocationTargetException e) {
        Log.w(TAG, e);
      } catch (NoSuchMethodException e) {
        Log.w(TAG, e);
      } catch (IllegalAccessException e) {
        Log.w(TAG, e);
      }
      // If the field is a list, extract elements of the list. Otherwise, use the field value
      // directly.
      if (List.class.isAssignableFrom(fieldValue.getClass())) {
        fieldValues.addAll((List) fieldValue);
      } else {
        fieldValues.add(fieldValue);
      }
    }
    return fieldValues;
  }

  /**
   * Returns the representative string values for the list of {@code objects}.
   *
   * <p>If an object in the list is a Java {@link String}, the returned list will contain the value
   * of the Java {@link String}. If an object in the list is a FHIR {@link StringType}, the returned
   * list will contain the value of the FHIR {@link StringType}. If an object in the list matches a
   * server defined search type (HumanName, Address, etc), the returned list will contain the string
   * value representative of the type.
   */
  private List<String> getStringValues(List<Object> objects) {
    List<String> stringValues = new ArrayList<>();
    for (Object object : objects) {
      if (String.class.isAssignableFrom(object.getClass())) {
        stringValues.add((String) object);
        continue;
      }
      if (StringType.class.isAssignableFrom(object.getClass())) {
        stringValues.add(((StringType) object).getValue());
        continue;
      }

      // TODO: Implement the server defined search parameters. According to
      // https://www.hl7.org/fhir/searchparameter-registry.html, name, device name, and address are
      // defined by the server (the FHIR Engine library in this case).
    }
    return stringValues;
  }

  /** Returns the reference values for the list of {@code objects}. */
  private List<Reference> getReferenceValues(List<Object> objects) {
    List<Reference> references = new ArrayList<>();
    for (Object object : objects) {
      if (Reference.class.isAssignableFrom(object.getClass())) {
        references.add((Reference) object);
      }
    }
    return references;
  }

  /** Returns the code values for the list of {@code objects}. */
  private List<Coding> getCodeValues(List<Object> objects) {
    List<Coding> codes = new ArrayList<>();
    for (Object object : objects) {
      if (CodeableConcept.class.isAssignableFrom(object.getClass())) {
        codes.addAll(((CodeableConcept) object).getCoding());
      }
    }
    return codes;
  }

  /** Returns the name of the method to retrieve the field {@code fieldName}. */
  private static String getGetterName(String fieldName) {
    return GETTER_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }

  /**
   * Returns whether the given path only uses a dot notation with no additional expressions such as
   * where() or exists().
   */
  private static boolean hasDotNotationOnly(String path) {
    return path.matches(DOT_NOTATION_REGEX);
  }
}
