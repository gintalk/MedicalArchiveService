package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 03/03/2021
 */

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import com.vgu.cs.common.logger.VLogger;
import com.vgu.cs.ma.service.model.data.FhirOmopCodeMapDModel;
import com.vgu.cs.ma.service.model.data.FhirOmopVocabularyMapDModel;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;

public abstract class FhirOmopModel {

    private static final Logger LOGGER = VLogger.getLogger(FhirOmopModel.class);
    protected final FhirOmopVocabularyMapDModel FO_VOCAB_DM;
    protected final FhirOmopCodeMapDModel FO_CODE_DM;
    private final FhirContext CONTEXT;
    private final FhirValidator VALIDATOR;

    protected FhirOmopModel() {
        CONTEXT = FhirContext.forDstu3();
        VALIDATOR = CONTEXT.newValidator().registerValidatorModule(new FhirInstanceValidator(CONTEXT));
        FO_VOCAB_DM = FhirOmopVocabularyMapDModel.INSTANCE;
        FO_CODE_DM = FhirOmopCodeMapDModel.INSTANCE;
    }

    public FhirContext getFhirContext() {
        return CONTEXT;
    }

    public boolean validateFhirInstance(IBaseResource instance) {
        ValidationResult result = VALIDATOR.validateWithResult(instance);
        if (!result.isSuccessful()) {
            for (SingleValidationMessage message : result.getMessages()) {
                LOGGER.error(message);
            }
        }

        return result.isSuccessful();
    }
}
