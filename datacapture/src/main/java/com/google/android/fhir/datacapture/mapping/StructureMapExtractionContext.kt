package com.google.android.fhir.datacapture.mapping

import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.utils.StructureMapUtilities

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 13-01-2022.
 */
data class StructureMapExtractionContext(val structureMapProvider: (suspend (String, IWorkerContext) -> StructureMap?), val transformSupportServices: StructureMapUtilities.ITransformerServices? = null)
