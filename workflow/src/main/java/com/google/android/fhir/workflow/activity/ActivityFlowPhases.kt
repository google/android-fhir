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

import com.google.android.fhir.workflow.activity.event.CPGEventResource
import com.google.android.fhir.workflow.activity.request.CPGRequestResource

fun interface StartPlan<R : CPGRequestResource<*>, E : CPGEventResource<*>> {
  suspend fun startPlan(init: R.() -> Unit): EndPlan<R, E>
}

// Would set proposal as completed
fun interface EndPlan<R : CPGRequestResource<*>, E : CPGEventResource<*>> {
  suspend fun endPlan(init: R.() -> Unit): StartOrder<R, E>
}

fun interface StartOrder<R : CPGRequestResource<*>, E : CPGEventResource<*>> {
  suspend fun startOrder(init: R.() -> Unit): EndOrder<R, E>
}

// Would set plan as completed
interface EndOrder<R : CPGRequestResource<*>, E : CPGEventResource<*>> {
  suspend fun endOrder(init: R.() -> Unit): StartPerform<R, E>
}

interface StartPerform<R : CPGRequestResource<*>, E : CPGEventResource<*>> {
  suspend fun <D : E> startPerform(klass: Class<D>, init: R.() -> Unit): EndPerform<D>
}

// Would set order as completed
interface EndPerform<E : CPGEventResource<*>> {
  suspend fun endPerform(init: E.() -> Unit)
}
