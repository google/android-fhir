/*
 * Copyright 2022 Google LLC
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
package com.google.android.fhir.workflow.testing.plandefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MetadataResource;
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal;

public class MockFhirDal implements FhirDal {

    private final Map<String, IBaseResource> cacheById = new HashMap<String, IBaseResource>();
    private final Map<String, List<IBaseResource>> cacheByURL = new HashMap<String, List<IBaseResource>>();
    private final Map<String, List<IBaseResource>> cacheByType = new HashMap<String, List<IBaseResource>>();

    private String toKey(IIdType resource) {
        return resource.getResourceType() + "/" + resource.getIdPart();
    }

    private void insertOrUpdate(Map<String, List<IBaseResource>> list, String key, IBaseResource element) {
        if (list.containsKey(key))
            list.get(key).add(element);
        else
            list.put(key, new ArrayList<>(Collections.singletonList(element)));
    }

    private void putIntoCache(IBaseResource resource) {
        cacheById.put(toKey(resource.getIdElement()), resource);
        insertOrUpdate(cacheByType, resource.getIdElement().getResourceType(), resource);
     
        if (resource instanceof MetadataResource) {
            insertOrUpdate(cacheByURL, ((MetadataResource)resource).getUrl(), resource);
        }
    }

    public void addAll(IBaseResource resource) {
        if (resource == null) return;

        if (resource instanceof Bundle) {
            ((Bundle) resource).getEntry().forEach(entry -> {
                addAll(entry.getResource());
            });
        } else {
            putIntoCache(resource);
        }
    }
    
    @Override
    public IBaseResource read(IIdType id) {
        return cacheById.get(toKey(id));
    }

    @Override
    public void create(IBaseResource resource) {}

    @Override
    public void update(IBaseResource resource) {}

    @Override
    public void delete(IIdType id) {}

    @Override
    public Iterable<IBaseResource> search(String resourceType) {
        return cacheByType.get(resourceType);
    }

    @Override
    public Iterable<IBaseResource> searchByUrl(String resourceType, String url) {
        return cacheByURL.get(url).stream()
            .filter(resource -> resourceType.equals(resource.getIdElement().getResourceType()))
            .collect(Collectors.toList());
    }
    
}
