/*
 * Copyright 2023 Google LLC
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
package com.google.android.fhir;

import junit.framework.AssertionFailedError;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

/**
 * JUnit Rule to run detect duplicate entries on the classpath.
 *
 * Such classpath duplicates often cause confusing and hard to debug problems, and
 * can also "bloat" the file size of an application package like an  Android APK.
 * They are caused by "unclean" dependencies, and typically solved with exclusions etc.
 *
 * Usage: <pre>public static {@literal @}ClassRule ClasspathHellDuplicatesCheckRule
 *       dupes = new ClasspathHellDuplicatesCheckRule();</pre>
 *
 * This code originated in https://github.com/opendaylight/infrautils/tree/master/testutils/src/main/java/org/opendaylight/infrautils/testutils,
 * where it was originally inspired by but then improved upon https://jhades.github.io,
 * was then re-used in https://github.com/apache/fineract/pull/803/files for https://issues.apache.org/jira/browse/FINERACT-919, and has now
 * been contributed here in https://github.com/google/android-fhir as well. See also https://github.com/classgraph/classgraph/issues/256 and https://github.com/classgraph/classgraph/wiki/Code-examples#find-all-duplicate-class-definitions-in-the-classpath-or-module-path
 *
 * @author Michael Vorburger.ch
 */
public class ClasspathHellDuplicatesCheckRule implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {
        checkClasspath();
        return base;
    }

    protected void checkClasspath() {
        Map<String, List<String>> duplicates = new HashMap<>();

        // To debug this scanner, use ClassGraph().verbose()
        // We intentionally do not use .classFilesOnly(), or .nonClassFilesOnly(), to check both
        try (ScanResult scanResult = new ClassGraph().scan()) {
            for (Map.Entry<String, ResourceList> dupe : scanResult.getAllResources().findDuplicatePaths()) {
                String resourceName = dupe.getKey();
                if (!isHarmlessDuplicate(resourceName)) {
                    boolean addIt = true;
                    List<String> jars = dupe.getValue().stream().map(resource -> resource.getURL().toExternalForm()).collect(Collectors.toList());
                    for (String jar : jars) {
                        if (skipJAR(jar)) {
                            addIt = false;
                            break;
                        }
                    }
                    if (addIt) {
                        duplicates.put(resourceName, jars);
                    }
                }
            }
        }

        if (!duplicates.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : duplicates.entrySet()) {
                sb.append(entry.getKey());
                sb.append('\n');
                for (String location : entry.getValue()) {
                    sb.append("    ");
                    sb.append(location);
                    sb.append('\n');
                }
            }
            throw new AssertionFailedError(duplicates.size() + " Classpath duplicates detected:\n" + sb.toString());
        }
    }

    private boolean skipJAR(String jarPath) {
        // ./gradlew test finds classes from the Gradle Wrapper (which don't show up in-IDE), exclude those
        return jarPath.contains("/.gradle/wrapper/dists/")
                // TODO https://github.com/google/android-fhir/issues/2308
                || jarPath.contains("-runtime.jar!/androidx/")
                ;
    }

    protected boolean isHarmlessDuplicate(String resourcePath) {
        // list initially from org.jhades.reports.DuplicatesReport
        return resourcePath.equals("META-INF/MANIFEST.MF")
                || resourcePath.equals("META-INF/INDEX.LIST")
                || resourcePath.equals("META-INF/ORACLE_J.SF")
                || resourcePath.toUpperCase().startsWith("META-INF/ASL")
                || resourcePath.toUpperCase().startsWith("META-INF/NOTICE")
                || resourcePath.toUpperCase().startsWith("META-INF/LICENSE")
                || resourcePath.toUpperCase().startsWith("LICENSE")
                || resourcePath.toUpperCase().startsWith("LICENSE/NOTICE")
                || resourcePath.endsWith(".txt")
                || resourcePath.endsWith("LICENSE")
                || resourcePath.endsWith("license.html")
                || resourcePath.endsWith("about.html")
                || resourcePath.endsWith("readme.html")
                // Groovy is groovy
                || resourcePath.startsWith("META-INF/groovy-release-info.properties")
                // Java 9 modules
                || resourcePath.endsWith("module-info.class")
                || resourcePath.contains("findbugs")
                // Kotlin related stuff which seems to be normal expected duplicates
                || resourcePath.equals("META-INF/common_debug.kotlin_module")
                || resourcePath.equals("META-INF/common_release.kotlin_module")
                || resourcePath.equals("META-INF/common_debugUnitTest.kotlin_module")
                || resourcePath.equals("META-INF/common_releaseUnitTest.kotlin_module")
                ;
    }
}
