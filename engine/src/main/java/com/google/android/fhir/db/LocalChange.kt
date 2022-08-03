package com.google.android.fhir.db

import com.google.android.fhir.db.impl.dao.LocalChangeToken
import org.hl7.fhir.r4.model.Resource

/**
 * Data class for squashed local changes for resource
 */
data class LocalChange(
  /**The [ResourceType] */
  val resourceType: String,
  /** The resource id [Resource.id]*/
  val resourceId: String,
  /**
   * last updated timestamp
   */
  val timestamp: String = "",
  /**
   * Type of local change like insert, delete, etc
   */
  val type: LocalChangeType,
  /**
   * json string with local changes
   */
  val payload: String,
  /**
   * last udated vesrion for resource
   */
  val versionId: String? = null,
  /**
   *This token value must be explicitly applied when list of local changes are squashed and [LocalChange] class instance is created.
   */
  var token: LocalChangeToken)
