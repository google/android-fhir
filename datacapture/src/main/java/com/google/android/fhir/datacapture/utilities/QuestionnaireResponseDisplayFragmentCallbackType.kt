package com.google.android.fhir.datacapture.utilities


enum class QuestionnaireResponseDisplayFragmentCallbackType(val value: String) {
  BACK("back"),
  EDIT("edit"),
  SUBMIT("submit");

  companion object {
    fun fromValue(value: String) = valueOf(value.uppercase())
  }
}
