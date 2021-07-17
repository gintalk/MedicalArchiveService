package com.vgu.cs.ma.service.model.data.omop;

import com.vgu.cs.common.logger.VLogger;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.dal.FhirOmopVocabularyMapDal;
import com.vgu.cs.engine.entity.omop.FhirOmopVocabularyMapEntity;
import org.apache.logging.log4j.Logger;

public class FhirOmopVocabularyMapDModel {

    public static final FhirOmopVocabularyMapDModel INSTANCE = new FhirOmopVocabularyMapDModel();
    private static final Logger LOGGER = VLogger.getLogger(FhirOmopVocabularyMapDModel.class);
    private final FhirOmopVocabularyMapDal DAL;

    private FhirOmopVocabularyMapDModel() {
        DAL = FhirOmopVocabularyMapDal.INSTANCE;
    }

    public String getFhirUrlSystem(String omopVocabularyId) {
        FhirOmopVocabularyMapEntity entity = DAL.get(omopVocabularyId);
        if (entity == null) {
            return "";
        }

        if (StringUtils.isNullOrEmpty(entity.fhir_url_system)) {
            return entity.other_system;
        }
        return entity.fhir_url_system;
    }
}
