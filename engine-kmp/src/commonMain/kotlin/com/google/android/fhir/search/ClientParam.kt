/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir.search

/**
 * KMP-compatible replacement for HAPI FHIR's `IParam` / `*ClientParam` hierarchy. Provides
 * compile-time safety for search parameter types.
 */
sealed class ClientParam(val paramName: String)

class StringClientParam(paramName: String) : ClientParam(paramName)

class DateClientParam(paramName: String) : ClientParam(paramName)

class NumberClientParam(paramName: String) : ClientParam(paramName)

class TokenClientParam(paramName: String) : ClientParam(paramName)

class ReferenceClientParam(paramName: String) : ClientParam(paramName)

class QuantityClientParam(paramName: String) : ClientParam(paramName)

class UriClientParam(paramName: String) : ClientParam(paramName)
