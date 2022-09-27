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

package com.google.android.fhir.db.impl.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Fts4
import com.google.android.fhir.index.entities.StringIndex

@Fts4(contentEntity = StringIndexEntity::class)
@Entity
internal data class StringIndexEntityFts(@Embedded(prefix = "index_") val index: StringIndex)
