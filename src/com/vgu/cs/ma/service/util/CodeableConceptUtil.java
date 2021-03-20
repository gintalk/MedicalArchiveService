package com.vgu.cs.ma.service.util;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 19/03/2021
 */

import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.ConceptEntity;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import com.vgu.cs.ma.service.model.data.FhirOmopVocabularyMapDModel;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;

public class CodeableConceptUtil {

    public static CodeableConcept fromConceptId(int conceptId) {
        ConceptEntity concept = ConceptDModel.INSTANCE.getConcept(conceptId);
        if (concept == null) {
            return null;
        }

        Coding coding = new Coding();
        coding.setCode(concept.concept_code);
        coding.setDisplay(concept.concept_name);

        String fhirUrlSystem = FhirOmopVocabularyMapDModel.INSTANCE.getFhirUrlSystem(concept.vocabulary_id);
        if (!StringUtils.isNullOrEmpty(fhirUrlSystem)) {
            coding.setSystem(fhirUrlSystem);
        }

        CodeableConcept codeable = new CodeableConcept();
        codeable.addCoding(coding);

        return codeable;
    }

    public static CodeableConcept fromText(String text) {
        return new CodeableConcept().setText(text);
    }
}
