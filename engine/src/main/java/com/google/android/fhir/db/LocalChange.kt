package com.google.android.fhir.db

import com.google.android.fhir.db.impl.entities.LocalChangeEntity
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
  //TODO extract Type enum from LocalChangeEntity and make LocalChangeEntity as internal class
  val type: LocalChangeEntity.Type,
  /**
   * json string with local changes
   */
  val payload: String,
  /**
   * last udated vesrion for resource
   */
  val versionId: String? = null)

