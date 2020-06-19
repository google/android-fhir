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

import ca.uhn.fhir.context.BaseRuntimeChildDefinition;
import ca.uhn.fhir.context.RuntimeResourceDefinition;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBase;
import org.opencds.cqf.cql.model.R4FhirModelResolver;

/** An {@link R4FhirModelResolver} on Android. */
class AndroidR4FhirModelResolver extends R4FhirModelResolver {
  /**
   * A prefix that is incorrectly included on Android due to the inconsistency of JSON
   * deserialization.
   */
  public static final String NAMESPACE_URI_PREFIX = "{http://hl7.org/fhir}";

  public AndroidR4FhirModelResolver() {
    super();
  }

  @Override
  public Object getContextPath(String contextType, String targetType) {
    if (targetType == null || contextType == null) {
      return null;
    }

    if (contextType != null
        && !(contextType.equals("Unspecified") || contextType.equals("Population"))) {
      if (targetType != null && contextType.equals(targetType)) {
        return "id";
      }

      // Workaround for the issue of target type incorrectly including a namespace URI prefix.
      // TODO: remove this.
      if (targetType.startsWith(NAMESPACE_URI_PREFIX)) {
        targetType = targetType.substring(NAMESPACE_URI_PREFIX.length());
      }

      RuntimeResourceDefinition resourceDefinition =
          this.fhirContext.getResourceDefinition(targetType);
      Object theValue = this.createInstance(contextType);
      Class<? extends IBase> type = (Class<? extends IBase>) theValue.getClass();

      List<BaseRuntimeChildDefinition> children = resourceDefinition.getChildren();
      for (BaseRuntimeChildDefinition child : children) {

        String path = this.innerGetContextPath(child, type);
        if (path != null) {
          return path;
        }
      }
    }

    return null;
  }
}
