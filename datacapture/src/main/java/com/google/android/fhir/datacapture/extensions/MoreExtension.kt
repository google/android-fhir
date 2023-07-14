package com.google.android.fhir.datacapture.extensions

import com.google.android.fhir.datacapture.mapping.ITEM_INITIAL_EXPRESSION_URL
import org.hl7.fhir.r4.model.Extension

internal val Extension.isRunOnceExpression: Boolean
  get() = this.url == ITEM_INITIAL_EXPRESSION_URL || this.url == EXTENSION_ITEM_POPULATE_CONTEXT_URL
