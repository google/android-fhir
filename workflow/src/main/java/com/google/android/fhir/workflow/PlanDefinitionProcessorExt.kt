package com.google.android.fhir.workflow

import ca.uhn.fhir.context.FhirContext
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.Resource
import org.opencds.cqf.cql.evaluator.activitydefinition.r4.ActivityDefinitionProcessor
import org.opencds.cqf.cql.evaluator.expression.ExpressionEvaluator
import org.opencds.cqf.cql.evaluator.library.LibraryProcessor
import org.opencds.cqf.cql.evaluator.plandefinition.r4.OperationParametersParser
import org.opencds.cqf.cql.evaluator.plandefinition.r4.PlanDefinitionProcessor

class PlanDefinitionProcessorExt( val fhirContext: FhirContext,
                                  val fhirEngineDal: FhirEngineDal,
                                  val libraryProcessor: LibraryProcessor,
                                  val expressionEvaluator: ExpressionEvaluator,
                                  val activityDefinitionProcessor: ActivityDefinitionProcessor,
                                  val operationParametersParser: OperationParametersParser): PlanDefinitionProcessor( fhirContext,
    fhirEngineDal,
    libraryProcessor,
    expressionEvaluator,
    activityDefinitionProcessor,
    operationParametersParser) {

    override fun resolveContained(resource: DomainResource, id: String): Resource {
        return super.resolveContained(resource, id)
    }
}