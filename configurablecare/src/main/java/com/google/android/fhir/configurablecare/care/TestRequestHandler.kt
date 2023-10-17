package com.google.android.fhir.configurablecare.care

import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.Task

class TestRequestHandler: RequestHandler {

  override fun acceptProposedRequest(request: Resource): Boolean {
    // RequestManager.acceptRequest(request)
    if (request is Task)
      request.status = Task.TaskStatus.ACCEPTED
    return true
  }
}