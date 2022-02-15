/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.sync.bundle

import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import org.hl7.fhir.r4.model.Bundle

typealias ResourceBundleAndAssociatedLocalChangeTokens = Pair<Bundle, List<LocalChangeToken>>

/**
 * Generates pairs of Transaction [Bundle] and [LocalChangeToken]s associated with the resources
 * present in the transaction bundle.
 */
internal class TransactionBundleGenerator(
  private val createRequest: HttpVerbBasedBundleEntryComponent,
  private val updateRequest: HttpVerbBasedBundleEntryComponent,
  private val deleteRequest: HttpVerbBasedBundleEntryComponent,
  private val localChangeProvider: LocalChangeProvider
) {

  suspend fun generate(): List<ResourceBundleAndAssociatedLocalChangeTokens> {
    return localChangeProvider.getLocalChanges().filter { it.isNotEmpty() }.map {
      generateBundle(it)
    }
  }

  private fun generateBundle(
    localChanges: List<SquashedLocalChange>
  ): ResourceBundleAndAssociatedLocalChangeTokens {
    return Bundle().apply {
      type = Bundle.BundleType.TRANSACTION
      localChanges.forEach {
        this.addEntry(
          getHttpVerbBasedBundleEntryComponentForLocalChangeType(it.localChange.type).getEntry(it)
        )
      }
    } to localChanges.map { it.token }
  }

  private fun getHttpVerbBasedBundleEntryComponentForLocalChangeType(type: LocalChangeEntity.Type) =
    when (type) {
      LocalChangeEntity.Type.INSERT -> createRequest
      LocalChangeEntity.Type.UPDATE -> updateRequest
      LocalChangeEntity.Type.DELETE -> deleteRequest
    }
}

/**
 * Splits up all the local changes into individual change lists to be included in particular
 * [Bundle]s.
 */
internal interface LocalChangeProvider {
  suspend fun getLocalChanges(): List<List<SquashedLocalChange>>
}

/**
 * Tells [TransactionBundleGenerator] that all the local changes should be part of a single [Bundle]
 * transaction.
 */
internal class DefaultLocalChangeProvider(private val localChanges: List<SquashedLocalChange>) :
  LocalChangeProvider {
  override suspend fun getLocalChanges(): List<List<SquashedLocalChange>> = listOf(localChanges)
}
