package com.google.android.fhir.workflow;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.MeasureReport;
import org.hl7.fhir.r4.model.Parameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opencds.cqf.fhir.api.Repository;
import org.opencds.cqf.fhir.cr.measure.MeasureEvaluationOptions;
import org.opencds.cqf.fhir.cr.measure.common.MeasureEvalType;
import org.opencds.cqf.fhir.cr.measure.common.SubjectProvider;
import org.opencds.cqf.fhir.cr.measure.r4.R4MeasureProcessor;

import java.util.List;

public class FhirEngineR4MeasureProcessor extends R4MeasureProcessor {
    public FhirEngineR4MeasureProcessor(Repository repository, MeasureEvaluationOptions measureEvaluationOptions) {
        super(repository, measureEvaluationOptions);
    }

    public FhirEngineR4MeasureProcessor(Repository repository, MeasureEvaluationOptions measureEvaluationOptions, SubjectProvider subjectProvider) {
        super(repository, measureEvaluationOptions, subjectProvider);
    }

    public MeasureReport evaluateMeasure(Measure measure, String periodStart, String periodEnd, String reportType, List<String> subjectIds, IBaseBundle additionalData, Parameters parameters, MeasureEvalType evalType) {
        return super.evaluateMeasure(measure, periodStart, periodEnd, reportType, subjectIds, additionalData, parameters, evalType);
    }
}
