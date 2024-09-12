package com.google.android.fhir.workflow.activity.phase.request

import com.google.android.fhir.workflow.activity.phase.Phase
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import org.opencds.cqf.fhir.api.Repository

class ProposalPhase<R : CPGRequestResource<*>>(repository: Repository, r: R) :
  BaseRequestPhase<R>(repository, r) {
  override fun getPhaseName() = Phase.PhaseName.PROPOSAL
}
