package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 25/04/2021
 */

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.omop.ObservationEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.*;

import java.util.Date;

public class ObservationOModel {
    
    public static final ObservationOModel INSTANCE = new ObservationOModel();
    
    private ObservationOModel() {
    
    }
    
    /**
     * Unretained fields include:
     * - observation_datetime
     * - visit_occurrence_id
     * - observation_source_concept_id
     */
    public ObservationEntity constructOmop(Observation fObservation) {
        ObservationEntity oObservation = new ObservationEntity();
        
        _setId(fObservation, oObservation);
        _setQualifierConceptIdAndSourceValue(fObservation, oObservation);
        _setValue(fObservation, oObservation);
        _setProviderId(fObservation, oObservation);
        _setPersonId(fObservation, oObservation);
        _setConceptIdAndSourceValue(fObservation, oObservation);
        _setDate(fObservation, oObservation);
        _setTypeConceptId(fObservation, oObservation);
        
        return oObservation;
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * OBSERVATION.observation_id = Observation.id
     */
    private void _setId(Observation fObservation, ObservationEntity oObservation) {
        oObservation.observation_id = ConvertUtils.toInteger(fObservation.getId());
    }
    
    /**
     * Maps Observation.Extension (Proposed Name: decision-aid-alert : CodeableConcept) to OBSERVATION's qualifier_concept_id and qualifier_source_value
     */
    private void _setQualifierConceptIdAndSourceValue(Observation fObservation, ObservationEntity oObservation) {
        CodeableConcept qualifierCodeable = null;
        for (Extension extension : fObservation.getExtension()) {
            if (!"decision-aid-alert".equals(extension.getUserString("name"))) {
                continue;
            }
            
            qualifierCodeable = (CodeableConcept) extension.getValue();
            break;
        }
        if (qualifierCodeable == null) {
            return;
        }
        
        oObservation.qualifier_concept_id = CodeableConceptUtils.getConceptId(qualifierCodeable);
        oObservation.qualifier_source_value = qualifierCodeable.getId();
    }
    
    /**
     * If Observation.value is of type:
     * - CodeableConcept: maps Observation.valueCodeableConcept to OBSERVATION.value_as_concept_id
     * - Quantity: maps Observation.valueQuantity to OBSERVATION's unit_concept_id, unit_source_value, value_as_number
     * - StringType: OBSERVATION.value_as_string = Observation.valueString
     */
    private void _setValue(Observation fObservation, ObservationEntity oObservation) {
        Type value = fObservation.getValue();
        switch (value.fhirType().toUpperCase()) {
            case "CODEABLECONCEPT":
                CodeableConcept valueCodeable = (CodeableConcept) value;
                oObservation.value_as_concept_id = CodeableConceptUtils.getConceptId(valueCodeable);
                
                return;
            case "QUANTITY":
                Quantity valueQuantity = (Quantity) value;
                
                String[] params = valueQuantity.getId().split("\\.");
                oObservation.unit_concept_id = ConvertUtils.toInteger(params[0]);
                oObservation.unit_source_value = params[1];
                oObservation.value_as_number = valueQuantity.getValue().intValue();
                
                return;
            case "STRING":
                StringType valueString = (StringType) value;
                oObservation.value_as_string = valueString.toString();
                
                return;
            default:
        }
    }
    
    /**
     * OBSERVATION.provider_id = Observation.performer[0].id
     */
    private void _setProviderId(Observation fObservation, ObservationEntity oObservation) {
        oObservation.provider_id = ProviderOModel.INSTANCE.getProviderIdFromReference(fObservation.getPerformerFirstRep());
    }
    
    /**
     * OBSERVATION_person_id = Observation.subject.id
     */
    private void _setPersonId(Observation fObservation, ObservationEntity oObservation) {
        oObservation.person_id = PersonOModel.INSTANCE.getIdFromReference(fObservation.getSubject());
    }
    
    /**
     * Maps Observation.code to OBSERVATION's observation_concept_id and observation_source_value
     */
    private void _setConceptIdAndSourceValue(Observation fObservation, ObservationEntity oObservation) {
        CodeableConcept observationCodeable = fObservation.getCode();
        if (observationCodeable == null) {
            return;
        }
        
        oObservation.observation_concept_id = CodeableConceptUtils.getConceptId(observationCodeable);
        oObservation.observation_source_value = observationCodeable.getId();
    }
    
    /**
     * OBSERVATION.observation_date = Observation.effective
     */
    private void _setDate(Observation fObservation, ObservationEntity oObservation) {
        DateTimeType dateTimeType = fObservation.getEffectiveDateTimeType();
        if (dateTimeType == null) {
            return;
        }
        
        Date date = dateTimeType.toCalendar().getTime();
        oObservation.observation_date = DateTimeUtils.getDateString(date);
    }
    
    /**
     * Maps Observation.category[0] to OBSERVATION.observation_type_concept_id
     */
    private void _setTypeConceptId(Observation fObservation, ObservationEntity oObservation) {
        CodeableConcept categoryCodeable = fObservation.getCategoryFirstRep();
        if (categoryCodeable == null) {
            return;
        }
        
        oObservation.observation_type_concept_id = CodeableConceptUtils.getConceptId(categoryCodeable);
    }
}
