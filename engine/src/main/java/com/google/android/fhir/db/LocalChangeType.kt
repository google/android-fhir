package com.google.android.fhir.db

/**
 * Enum specifies type of local change
 */
enum class LocalChangeType(val value: Int) {
  INSERT(1), // create a new resource. payload is the entire resource json.
  UPDATE(2), // patch. payload is the json patch.
  DELETE(3); // delete. payload is empty string.

  companion object {
    fun from(input: Int): LocalChangeType = values().first { it.value == input }
  }
}