package com.google.android.fhir.configurablecare.util;

import java.lang.reflect.Constructor;
import java.util.List;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.context.IWorkerContext;
import org.hl7.fhir.r4.elementmodel.Manager;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.ResourceFactory;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.utils.StructureMapUtilities;

public class TransformSupportServicesMatchBox implements StructureMapUtilities.ITransformerServices {

    private List<Base> outputs;
    private IWorkerContext context;
    protected static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(
        TransformSupportServices.class);

    public TransformSupportServicesMatchBox(IWorkerContext worker, List<Base> outputs) {
        this.context = worker;
        this.outputs = outputs;
    }

    @Override
    public Base createType(Object appInfo, String name) throws FHIRException {
        try{
            Enumerations.DataType dataType = Enumerations.DataType.fromCode(name);
            return ResourceFactory.createResourceOrType(name);
        }catch (Exception e){
            StructureDefinition sd = context.fetchResource(StructureDefinition.class, name);
            return Manager.build(context, sd);
        }

    }

    @Override
    public Base createResource(Object appInfo, Base res, boolean atRootofTransform) {
        if (atRootofTransform)
            outputs.add(res);
        try{
            Enumerations.FHIRAllTypes fhirType = Enumerations.FHIRAllTypes.fromCode(res.fhirType());
            Constructor<?> constructor = Class.forName(
                "org.hl7.fhir.r4.model."+fhirType.getDisplay()
            ).getConstructor();
            res =(Base) constructor.newInstance();
            return res;
        }catch (Exception e){
            return res;
        }
    }

    @Override
    public Coding translate(Object appInfo, Coding source, String conceptMapUrl) throws FHIRException {
        ConceptMapEngine cme = new ConceptMapEngine(context);
        return cme.translate(source, conceptMapUrl);
    }

    @Override
    public Base resolveReference(Object appContext, String url) throws FHIRException {
        org.hl7.fhir.r4.model.Resource resource = context.fetchResource(org.hl7.fhir.r4.model.Resource.class, url);
        return resource;
//    if (resource != null) {
//      String inStr = FhirContext.forR4Cached().newJsonParser().encodeResourceToString(resource);
//      try {
//        return Manager.parseSingle(context, new ByteArrayInputStream(inStr.getBytes()), FhirFormat.JSON);
//      } catch (IOException e) {
//        throw new FHIRException("Cannot convert resource to element model");
//      }
//    }
//    throw new FHIRException("resolveReference, url not found: " + url);
    }

    @Override
    public List<Base> performSearch(Object appContext, String url) throws FHIRException {
        throw new FHIRException("performSearch is not supported yet");
    }

    @Override
    public void log(String message) {
        log.debug(message);
    }
}