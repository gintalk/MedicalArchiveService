package com.vgu.cs.ma.service.util;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 19/03/2021
 */

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.omop.ConceptEntity;
import com.vgu.cs.ma.service.model.data.omop.ConceptDModel;
import com.vgu.cs.ma.service.model.data.omop.FhirOmopVocabularyMapDModel;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;

public class CodeableConceptUtils {
    
    public static CodeableConcept fromConceptId(int conceptId) {
        return fromConceptIdAndSourceValue(conceptId, "");
    }
    
    public static CodeableConcept fromConceptIdAndSourceValue(int conceptId, String sourceValue) {
        CodeableConcept codeable = new CodeableConcept();
        codeable.setId(sourceValue);
        
        if (conceptId <= 0) {
            if (StringUtils.isNullOrEmpty(sourceValue)) {
                return null;
            }
            return codeable;
        }
        
        Coding coding = new Coding();
        coding.setId(String.valueOf(conceptId));
        codeable.addCoding(coding);
        
        ConceptEntity concept = ConceptDModel.INSTANCE.getConcept(conceptId);
        if (concept == null) {
            return codeable;
        }
        
        coding.setCode(concept.concept_code);
        coding.setDisplay(concept.concept_name);
        
        String fhirUrlSystem = FhirOmopVocabularyMapDModel.INSTANCE.getFhirUrlSystem(concept.vocabulary_id);
        if (!StringUtils.isNullOrEmpty(fhirUrlSystem)) {
            coding.setSystem(fhirUrlSystem);
        }
        
        return codeable;
    }
    
    public static CodeableConcept fromText(String text) {
        return new CodeableConcept().setText(text);
    }
    
    public static int getConceptId(CodeableConcept codeable) {
        Coding coding = codeable.getCodingFirstRep();
        if (coding == null) {
            return 0;
        }
        
        return ConvertUtils.toInteger(coding.getId());
    }
    
    public static String getText(CodeableConcept codeable) {
        return codeable.getText();
    }
}
