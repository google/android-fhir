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

package com.google.android.fhir.sync.upload

import com.google.android.fhir.LocalChange
import com.google.android.fhir.db.Database

interface LocalChangeFetcher {
  suspend fun hasNext(): Boolean
  suspend fun next(): List<LocalChange>
  suspend fun getProgress(): Double
}

internal class AllChangesLocalChangeFetcher(val database: Database) : LocalChangeFetcher {
  override suspend fun hasNext(): Boolean = database.getAllLocalChanges().isNotEmpty()

  override suspend fun next(): List<LocalChange> = database.getAllLocalChanges()

  override suspend fun getProgress(): Double = database.getAllLocalChanges().size.toDouble()
}

sealed class FetchStrategy {

  class AllChanges(val pageSize: Int) : FetchStrategy()
  object PerResource : FetchStrategy()
  object EarliestChange : FetchStrategy()
}
