package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 21/04/2021
 */

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.ConditionOccurrenceEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Extension;

import java.util.Date;

public class ConditionOccurrenceOModel {
    
    public static final ConditionOccurrenceOModel INSTANCE = new ConditionOccurrenceOModel();
    
    private ConditionOccurrenceOModel() {
    
    }
    
    /**
     * Unretained fields include:
     * - condition_start_datetime
     * - condition_end_datetime
     * - visit_occurrence_id
     * - condition_source_concept_id
     * - condition_status_source_value
     * - condition_status_concept_id
     */
    public ConditionOccurrenceEntity constructOmop(Condition condition) {
        ConditionOccurrenceEntity conditionOccurrence = new ConditionOccurrenceEntity();
        
        _setId(condition, conditionOccurrence);
        _setProviderId(condition, conditionOccurrence);
        _setPersonId(condition, conditionOccurrence);
        _setConceptIdAndSourceValue(condition, conditionOccurrence);
        _setDate(condition, conditionOccurrence);
        _setConditionType(condition, conditionOccurrence);
        _setStopReason(condition, conditionOccurrence);
        
        return conditionOccurrence;
    }
    
    /**
     * CONDITION_OCCURRENCE.condition_occurrence_id = Condition.id
     */
    private void _setId(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        conditionOccurrence.condition_occurrence_id = ConvertUtils.toInteger(condition.getId());
    }
    
    /**
     * CONDITION_OCCURRENCE.provider_id = Condition.asserter.id
     */
    private void _setProviderId(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        conditionOccurrence.provider_id = ConvertUtils.toInteger(condition.getAsserter().getId());
    }
    
    /**
     * CONDITION_OCCURRENCE.person_id = Condition.subject.id
     */
    private void _setPersonId(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        conditionOccurrence.person_id = ConvertUtils.toInteger(condition.getSubject().getId());
    }
    
    /**
     * Maps Condition.code to CONDITION_OCCURRENCE's condition_concept_id and condition_source_value
     */
    private void _setConceptIdAndSourceValue(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        CodeableConcept conditionCodeable = condition.getCode();
        if (conditionCodeable == null) {
            return;
        }
        
        conditionOccurrence.condition_concept_id = CodeableConceptUtils.getConceptId(conditionCodeable);
        conditionOccurrence.condition_source_value = conditionCodeable.getId();
    }
    
    /**
     * Maps:
     * - Condition.onset to CONDITION_OCCURRENCE.condition_start_date
     * - Condition.abatement to CONDITION_OCCURRENCE.condition_end_date
     */
    private void _setDate(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        Date onsetDate = condition.getOnsetDateTimeType().getValue();
        if (onsetDate != null) {
            conditionOccurrence.condition_start_date = DateTimeUtils.getDateString(onsetDate);
        }
        
        Date abatementDate = condition.getAbatementDateTimeType().getValue();
        if (abatementDate != null) {
            conditionOccurrence.condition_end_date = DateTimeUtils.getDateString(abatementDate);
        }
    }
    
    /**
     * Maps Condition.Extension (Proposed Name: raw-value : CodeableConcept) to CONDITION_OCCURRENCE.condition_type_concept_id
     */
    private void _setConditionType(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        CodeableConcept conditionTypeCodeable = null;
        for (Extension extension : condition.getExtension()) {
            if (!"raw-value".equals(extension.getUserString("name"))) {
                continue;
            }
            
            conditionTypeCodeable = (CodeableConcept) extension.getValue();
            break;
        }
        if (conditionTypeCodeable == null) {
            return;
        }
        
        conditionOccurrence.condition_type_concept_id = CodeableConceptUtils.getConceptId(conditionTypeCodeable);
    }
    
    /**
     * Maps Condition.Extension (Proposed Name: abatement-reason : CodeableConcept) to CONDITION_OCCURRENCE.stop_reason
     */
    private void _setStopReason(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        CodeableConcept abatementReasonCodeable = null;
        for (Extension extension : condition.getExtension()) {
            if (!"abatement-reason".equals(extension.getUserString("name"))) {
                continue;
            }
            
            abatementReasonCodeable = (CodeableConcept) extension.getValue();
            break;
        }
        if (abatementReasonCodeable == null) {
            return;
        }
        
        conditionOccurrence.stop_reason = CodeableConceptUtils.getText(abatementReasonCodeable);
    }
}
