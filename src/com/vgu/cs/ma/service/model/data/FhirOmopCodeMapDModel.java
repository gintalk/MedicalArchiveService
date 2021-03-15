package com.vgu.cs.ma.service.model.data;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 14/03/2021
 */

import com.vgu.cs.common.logger.VLogger;
import com.vgu.cs.engine.dal.FhirOmopCodeMapDal;
import com.vgu.cs.engine.entity.FhirOmopCodeMapEntity;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Coding;

public class FhirOmopCodeMapDModel {

    public static final FhirOmopCodeMapDModel INSTANCE = new FhirOmopCodeMapDModel();
    private static final Logger LOGGER = VLogger.getLogger(FhirOmopCodeMapDModel.class);
    private final FhirOmopCodeMapDal DAL;

    private FhirOmopCodeMapDModel() {
        DAL = FhirOmopCodeMapDal.INSTANCE;
    }

    public Coding getFhirCodingFromOmopSourceValue(String sourceValue) {
        FhirOmopCodeMapEntity entity = DAL.getFromFhirDisplay(sourceValue);
        if (entity == null) {
            return null;
        }

        return new Coding().setSystem(entity.fhir_system).setCode(entity.fhir_code).setDisplay(entity.fhir_display);
    }

    public Coding getFhirCodingFromOmopConcept(int conceptId) {
        FhirOmopCodeMapEntity entity = DAL.getFromOmopConceptId(conceptId);
        if (entity == null) {
            return null;
        }

        return new Coding().setSystem(entity.fhir_system).setCode(entity.fhir_code).setDisplay(entity.fhir_display);
    }
}
