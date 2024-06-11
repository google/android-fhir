/*
 * Copyright 2022-2023 Google LLC
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

package codegen

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.hl7.fhir.r4.model.Bundle

@CacheableTask
abstract class GenerateSearchParamsTask : DefaultTask() {
  @OutputDirectory val srcOutputDir = project.objects.directoryProperty()

  @OutputDirectory val testOutputDir = project.objects.directoryProperty()

  @TaskAction
  fun generateCode() {
    val bundle =
      GenerateSearchParamsTask::class.java.getResourceAsStream("/search-parameters.json").use {
        checkNotNull(it) { "Failed to get search-parameters.json" }
        FhirContext.forCached(FhirVersionEnum.R4)
          .newJsonParser()
          .parseResource(Bundle::class.java, it)
      }
    val srcOut = srcOutputDir.asFile.get()
    srcOut.deleteRecursively()
    srcOut.mkdirs()
    val testOut = testOutputDir.asFile.get()
    testOut.deleteRecursively()
    testOut.mkdirs()
    SearchParameterRepositoryGenerator.generate(
      bundle = bundle,
      outputPath = srcOut,
      testOutputPath = testOut,
    )
  }
}
