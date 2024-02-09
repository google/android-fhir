package com.google.android.fhir.workflow;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.MeasureReport;
import org.opencds.cqf.fhir.api.Repository;
import org.opencds.cqf.fhir.cr.measure.MeasureEvaluationOptions;
import org.opencds.cqf.fhir.cr.measure.common.MeasureEvalType;
import org.opencds.cqf.fhir.cr.measure.common.SubjectProvider;
import org.opencds.cqf.fhir.cr.measure.r4.R4MeasureProcessor;
import org.opencds.cqf.fhir.cr.measure.r4.R4RepositorySubjectProvider;
import org.opencds.cqf.fhir.utility.repository.FederatedRepository;
import org.opencds.cqf.fhir.utility.repository.InMemoryFhirRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class FhirEngineR4MeasureProcessor extends R4MeasureProcessor {
    private static String FIELD_SUBJECT_PROVIDER="subjectProvider";
    private Repository repository;

    public FhirEngineR4MeasureProcessor(Repository repository, MeasureEvaluationOptions measureEvaluationOptions) {
        super(repository, measureEvaluationOptions);
        this.repository = repository;
    }

    public FhirEngineR4MeasureProcessor(Repository repository, MeasureEvaluationOptions measureEvaluationOptions, SubjectProvider subjectProvider) {
        super(repository, measureEvaluationOptions, subjectProvider);
        this.repository = repository;
    }

    @Override
    public MeasureReport evaluateMeasure(Measure measure, String periodStart, String periodEnd, String reportType, List<String> subjectIds, IBaseBundle additionalData, MeasureEvalType evalType) {
        var actualRepo = this.repository;
        if (additionalData != null) {
            actualRepo = new FederatedRepository(
                    this.repository, new InMemoryFhirRepository(this.repository.fhirContext(), additionalData));
        }

        SubjectProvider subjectProvider = getSubjectProvider();
        var subjects = subjectProvider.getSubjects(actualRepo, evalType, subjectIds).collect(Collectors.toList());
        return super.evaluateMeasure( measure,  periodStart,  periodEnd,  reportType, subjects,  additionalData,  evalType) ;
    }


    /***
     * We have two constructors that could result in different subject providers. So for this field we will use reflection
     * @return [SubjectProvider] the SubjectProvider
     */
    public SubjectProvider getSubjectProvider(){
        SubjectProvider subjectProvider;
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField(FIELD_SUBJECT_PROVIDER);
            field.setAccessible(true);
            subjectProvider = (SubjectProvider) field.get(this);
        }catch (Exception e){
            subjectProvider = null;
        }
        return subjectProvider;
    }
}
