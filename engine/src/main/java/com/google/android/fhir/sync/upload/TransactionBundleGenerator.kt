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

package com.google.android.fhir.sync.upload

import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChange.Type
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.r4.model.Bundle

typealias ResourceBundleAndAssociatedLocalChangeTokens = Pair<IBaseBundle, List<LocalChangeToken>>

/**
 * Generates pairs of Transaction [Bundle] and [LocalChangeToken]s associated with the resources
 * present in the transaction bundle.
 */
internal open class TransactionBundleGenerator(
  val getBundleEntryComponentGeneratorForLocalChangeType:
    (type: Type) -> HttpVerbBasedBundleEntryComponentGenerator
) {

  fun generate(
    localChanges: List<List<LocalChange>>,
    fhirVersionEnum: FhirVersionEnum
  ): List<ResourceBundleAndAssociatedLocalChangeTokens> {
    return localChanges.filter { it.isNotEmpty() }.map { generateBundle(it, fhirVersionEnum) }
  }

  private fun generateBundle(
    localChanges: List<LocalChange>,
    fhirVersionEnum: FhirVersionEnum
  ): ResourceBundleAndAssociatedLocalChangeTokens =
    when (fhirVersionEnum) {
      FhirVersionEnum.R4 -> {
        Bundle().apply {
          type = Bundle.BundleType.TRANSACTION
          localChanges.forEach {
            this.addEntry(
              getBundleEntryComponentGeneratorForLocalChangeType(it.type)
                .getEntryForR4(it, FhirVersionEnum.R4)
            )
          }
        } to localChanges.map { it.token }
      }
      FhirVersionEnum.R5 -> {
        org.hl7.fhir.r5.model.Bundle().apply {
          type = org.hl7.fhir.r5.model.Bundle.BundleType.TRANSACTION
          localChanges.forEach {
            this.addEntry(
              getBundleEntryComponentGeneratorForLocalChangeType(it.type)
                .getEntryForR5(it, FhirVersionEnum.R5)
            )
          }
        } to localChanges.map { it.token }
      }
      else -> throw FHIRException("Im done")
    }

  companion object Factory {

    fun getDefault() = PutForCreateAndPatchForUpdateBasedTransactionGenerator

    /**
     * Returns a [TransactionBundleGenerator] based on the provided [Bundle.HTTPVerb]s for creating
     * and updating resources. The function may throw an [IllegalArgumentException] if the provided
     * [Bundle.HTTPVerb]s are not supported.
     */
    fun getGenerator(
      httpVerbToUseForCreate: Bundle.HTTPVerb,
      httpVerbToUseForUpdate: Bundle.HTTPVerb
    ): TransactionBundleGenerator {

      return if (httpVerbToUseForCreate == Bundle.HTTPVerb.PUT &&
          httpVerbToUseForUpdate == Bundle.HTTPVerb.PATCH
      ) {
        PutForCreateAndPatchForUpdateBasedTransactionGenerator
      } else {
        throw IllegalArgumentException(
          "Engine currently supports creation using [PUT] and updates using [PATCH]"
        )
      }
    }
  }
}

internal object PutForCreateAndPatchForUpdateBasedTransactionGenerator :
  TransactionBundleGenerator({ type ->
    when (type) {
      Type.INSERT -> HttpPutForCreateEntryComponentGenerator
      Type.UPDATE -> HttpPatchForUpdateEntryComponentGenerator
      Type.DELETE -> HttpDeleteEntryComponentGenerator
    }
  })
