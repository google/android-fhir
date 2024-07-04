/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.workflow.activity

import org.hl7.fhir.r4.model.Resource

fun interface StartPlan<R : Resource> {
  suspend fun startPlan(init: R.() -> Unit): EndPlan<R>
}

// Would set proposal as completed
fun interface EndPlan<R : Resource> {
  suspend fun endPlan(init: R.() -> Unit): StartOrder<R>
}

fun interface StartOrder<R : Resource> {
  suspend fun startOrder(init: R.() -> Unit): EndOrder<R>
}

// Would set plan as completed
interface EndOrder<R : Resource> {
  suspend fun endOrder(init: R.() -> Unit): StartPerform<R>
}

interface StartPerform<R : Resource> {
  suspend fun <E : Resource> startPerform(klass: Class<E>, init: R.() -> Unit): EndPerform<E>
}

// Would set order as completed
interface EndPerform<E : Resource> {
  suspend fun endPerform(init: E.() -> Unit)
}
