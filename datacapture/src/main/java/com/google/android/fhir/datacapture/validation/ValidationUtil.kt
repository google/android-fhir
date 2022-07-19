package com.google.android.fhir.datacapture.validation

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport
import com.google.android.fhir.datacapture.CQF_CALCULATED_EXPRESSION_URL
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.utils.FHIRPathEngine

object ValidationUtil {

    private val fhirPathEngine: FHIRPathEngine =
        with(FhirContext.forCached(FhirVersionEnum.R4)) {
            FHIRPathEngine(HapiWorkerContext(this, DefaultProfileValidationSupport(this)))
        }

    internal fun getExtensionValue(extension: Extension): Type {
        var result: Type? = null
        if (extension.value.hasExtension()) {
            extension.value.extension.firstOrNull()?.let {
                fhirPathEngine
                    .evaluate(extension, (it.value as Expression).expression)
                    .firstOrNull()
                    ?.let { result = it as Type }
            }
        } else {
            result = extension.value
        }
        return result!!
    }

    internal fun processCQLExtension(
        questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent,
        it: Extension
    ): Type? {
        return if (it.value.hasExtension()) {
            it.value.extension.firstOrNull { it.url == CQF_CALCULATED_EXPRESSION_URL }?.let {
                val expression = (it.value as Expression).expression
                fhirPathEngine.evaluate(questionnaireItemComponent, expression).firstOrNull()?.let { it as Type }
            }
        } else {
            it.value as Type
        }
    }
}