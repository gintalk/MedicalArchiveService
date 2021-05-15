package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 21/04/2021
 */

import com.vgu.cs.common.util.CollectionUtils;
import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.omop.DeathEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.AdverseEvent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.Date;
import java.util.List;

public class DeathOModel {
    
    public static final DeathOModel INSTANCE = new DeathOModel();
    
    private DeathOModel() {
    
    }
    
    /**
     *
     */
    public DeathEntity constructOmop(AdverseEvent adverseEvent) {
        DeathEntity death = new DeathEntity();
        
        _setPersonId(adverseEvent, death);
        _setDeathDate(adverseEvent, death);
        _setDeathType(adverseEvent, death);
        _setCause(adverseEvent, death);
        
        return death;
    }
    
    /**
     * DEATH.person_id = AdverseEvent.subject.id
     */
    private void _setPersonId(AdverseEvent adverseEvent, DeathEntity death) {
        Reference subjectReference = adverseEvent.getSubject();
        if (subjectReference == null || StringUtils.isNullOrEmpty(subjectReference.getId())) {
            return;
        }
        
        death.person_id = ConvertUtils.toInteger(PersonOModel.INSTANCE.getIdFromReference(subjectReference));
    }
    
    /**
     * DEATH.death_date = AdverseEvent.date
     */
    private void _setDeathDate(AdverseEvent adverseEvent, DeathEntity death) {
        Date date = adverseEvent.getDate();
        if (date == null) {
            return;
        }
        
        death.death_date = DateTimeUtils.getDateString(date);
    }
    
    /**
     * Maps AdverseEvent.suspectedEntity.causality.Extension (Proposed Name: cause-code : CodeableConcept) to DEATH.death_type_concept_id
     */
    private void _setDeathType(AdverseEvent adverseEvent, DeathEntity death) {
        List<AdverseEvent.AdverseEventSuspectEntityComponent> components = adverseEvent.getSuspectEntity();
        if (CollectionUtils.isNullOrEmpty(components)) {
            return;
        }
        
        CodeableConcept deathTypeCodeable = null;
        for (AdverseEvent.AdverseEventSuspectEntityComponent component : components) {
            if (!"death-type".equals(component.getUserString("name"))) {
                continue;
            }
            
            Extension deathTypeExtension = component.getExtensionFirstRep();
            if (deathTypeExtension == null) {
                return;
            }
            
            deathTypeCodeable = (CodeableConcept) deathTypeExtension.getValue();
            break;
        }
        if (deathTypeCodeable == null) {
            return;
        }
        
        death.death_type_concept_id = CodeableConceptUtils.getConceptId(deathTypeCodeable);
    }
    
    /**
     * Maps AdverseEvent.suspectedEntity.causality.Extension (Proposed Name: cause-code : CodeableConcept) to DEATH's cause_concept_id and cause_source_value
     */
    private void _setCause(AdverseEvent adverseEvent, DeathEntity death) {
        List<AdverseEvent.AdverseEventSuspectEntityComponent> components = adverseEvent.getSuspectEntity();
        if (CollectionUtils.isNullOrEmpty(components)) {
            return;
        }
        
        CodeableConcept causeCodeable = null;
        for (AdverseEvent.AdverseEventSuspectEntityComponent component : components) {
            if (!"cause".equals(component.getUserString("name"))) {
                continue;
            }
            
            Extension causeExtension = component.getExtensionFirstRep();
            if (causeExtension == null) {
                return;
            }
            
            causeCodeable = (CodeableConcept) causeExtension.getValue();
            break;
        }
        if (causeCodeable == null) {
            return;
        }
        
        death.cause_concept_id = CodeableConceptUtils.getConceptId(causeCodeable);
        death.cause_source_value = causeCodeable.getId();
    }
}
