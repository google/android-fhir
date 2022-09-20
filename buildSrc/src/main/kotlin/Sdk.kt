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

object Sdk {
  const val compileSdk = 31
  const val targetSdk = 31

  /**
   * # Issues that can be resolved with Desugaring
   *
   * Uses of java.time here, by HAPI 6 and by Caffeine require MinSDK 26 or Desugaring, which
   * provides a workable subset of java.time and a fixed ConcurrentHashMap for caching solutions.
   *
   * # Issues that cannot be resolved with Desugaring but can be fixed by minSDK 26
   *
   * CQLEngine 2.0, HAPI-fhir-base 5 and some versions (2.13.2) of Jackson require
   * java.lang.reflect.Method.getParameterCount() which is not available before 26. We have
   * overridden those Jackson versions with corrected versions, but it might come back. The use of
   * HAPI's PropertyModifyingHelper will crash the app on runtime. Make sure to heavily test
   * different JSONs and XMLs to cover rare need for getParameterCount()
   * - Discussion: https://github.com/FasterXML/jackson-databind/issues/3412
   *
   * java.nio.file, which is now frequently used by HAPI, is not available before 26. This doesn't
   * break our test cases yet, but with NPM Package Manager being based on filesystem, we can expect
   * heavy use of this new API in the near future.
   * - Keep an eye on: https://github.com/hapifhir/hapi-fhir/search?q=java.nio.file
   *
   * # Issues that cannot be resolved by us.
   *
   * Caffeine 3.0 is not compatible with Android anymore due to the use of System.getLogger which is
   * not provided by either desuraring nor newer versions of the SDK. For now, we have to force
   * Caffeine ~2.9
   * - Discussion here: https://github.com/ben-manes/caffeine/issues/763
   * - The solution is to use Guava in the future: https://github.com/hapifhir/hapi-fhir/pull/3977
   *
   * CQLTranslator is graciously forcing the use of Woodstox for now. The default XmlFactory
   * provided by Jackson calls a Streaming API for XML (StAX) function that does not exist on
   * Android: javax.xml.stream.XMLInputFactory.newFactory
   * - Discussion here: https://github.com/cqframework/clinical_quality_language/issues/768
   */
  const val minSdk = 26
}
