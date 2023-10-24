package com.google.android.fhir.configurablecare.care

import com.google.android.fhir.testing.jsonParser
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.Task

class TestRequestHandler: RequestHandler {

  companion object {
    // fun evaluateProposal(
    //   proposalQuestionnaireResponse: QuestionnaireResponse,
    //   proposedRequest: Resource,
    // ): {
    //
    //   // val choice =
    //   // // This is specifically for the app-based Questionnaire
    //   // {"resourceType":"QuestionnaireResponse","item":[{"linkId":"3888885591343","text":"Do you approve this proposal?","answer":[{"valueBoolean":false}]},{"linkId":"8599813785588","text":"Reason for rejection"}]}
    //
    //   println("Proposal QR: ${jsonParser.encodeResourceToString(proposalQuestionnaireResponse)}")
    //   // parse QR to get decision - this is specifically for the app-based Questionnaire
    //   val decision = proposalQuestionnaireResponse.item.filter { it -> it.linkId == "approval-choice" }.first().answer.first().valueBooleanType.value
    //   val reason = proposalQuestionnaireResponse.item.filter { it -> it.linkId == "reason" }.first().answer.first().valueStringType.value
    //
    //   for (item in proposalQuestionnaireResponse.item) {
    //     if (item.linkId == "approval-choice") {
    //
    //     }
    //   }
    //
    //   return true
    // }
  }

  override fun acceptProposedRequest(request: Resource): Boolean {
    // RequestManager.acceptRequest(request)
    if (request is Task)
      request.status = Task.TaskStatus.ACCEPTED
    return true
  }
}