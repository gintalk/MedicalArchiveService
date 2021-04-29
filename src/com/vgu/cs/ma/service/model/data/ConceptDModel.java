package com.vgu.cs.ma.service.model.data;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 06/03/2021
 */

import com.vgu.cs.engine.dal.ConceptDal;
import com.vgu.cs.engine.entity.ConceptEntity;

public class ConceptDModel {
    
    public static final ConceptDModel INSTANCE = new ConceptDModel();
    
    private ConceptDModel() {
    
    }
    
    public ConceptEntity getConcept(int conceptId) {
        if(conceptId <= 0){
            return null;
        }
        return ConceptDal.INSTANCE.get(conceptId);
    }
    
    public int getGenderConceptId(String genderConceptName) {
        ConceptEntity genderConcept = ConceptDal.INSTANCE.getByDomainIdAndConceptName("Gender", genderConceptName);
        if (genderConcept == null) {
            return 0;
        }
        
        return genderConcept.concept_id;
    }
}
