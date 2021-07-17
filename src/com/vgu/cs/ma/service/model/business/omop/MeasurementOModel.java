package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 25/04/2021
 */

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.omop.MeasurementEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.*;

import java.util.Date;

public class MeasurementOModel {
    
    public static final MeasurementOModel INSTANCE = new MeasurementOModel();
    
    private MeasurementOModel() {
    
    }
    
    /**
     * Unretained fields include:
     * - measurement_datetime
     * - operator_concept_id
     * - visit_occurrence_id
     * - measurement_source_concept_id
     * - value_source_value
     */
    public MeasurementEntity constructOmop(Observation observation) {
        MeasurementEntity measurement = new MeasurementEntity();
        
        _setId(observation, measurement);
        _setValue(observation, measurement);
        _setRange(observation, measurement);
        _setProviderId(observation, measurement);
        _setPersonId(observation, measurement);
        _setConceptIdAndSourceValue(observation, measurement);
        _setDate(observation, measurement);
        _setTypeConceptId(observation, measurement);
        
        return measurement;
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * MEASUREMENT.measurement_id = Observation.id
     */
    private void _setId(Observation observation, MeasurementEntity measurement) {
        measurement.measurement_id = ConvertUtils.toInteger(observation.getId());
    }
    
    /**
     * If Observation.value is of type:
     * - CodeableConcept: maps Observation.valueCodeableConcept to MEASUREMENT.value_as_concept_id
     * - Quantity: maps Observation.valueQuantity to MEASUREMENT's unit_concept_id, unit_source_value, value_as_number
     */
    private void _setValue(Observation observation, MeasurementEntity measurement) {
        Type value = observation.getValue();
        switch (value.fhirType().toUpperCase()) {
            case "CODEABLECONCEPT":
                CodeableConcept valueCodeable = (CodeableConcept) value;
                measurement.value_as_concept_id = CodeableConceptUtils.getConceptId(valueCodeable);
                
                return;
            case "QUANTITY":
                Quantity valueQuantity = (Quantity) value;
                
                String[] params = valueQuantity.getId().split("\\.");
                measurement.unit_concept_id = ConvertUtils.toInteger(params[0]);
                measurement.unit_source_value = params[1];
                measurement.value_as_number = valueQuantity.getValue().intValue();
                
                return;
            default:
        }
    }
    
    /**
     * MEASUREMENT.range_low = Observation.referenceRange.low
     * MEASUREMENT.range_high = Observation.referenceRange.high
     */
    private void _setRange(Observation observation, MeasurementEntity measurement) {
        Observation.ObservationReferenceRangeComponent component = observation.getReferenceRangeFirstRep();
        if (component == null) {
            return;
        }
        
        if (component.getLow() != null) {
            measurement.range_low = component.getLow().getValue().doubleValue();
        }
        if (component.getHigh() != null) {
            measurement.range_high = component.getHigh().getValue().doubleValue();
        }
    }
    
    /**
     * MEASUREMENT.provider_id = Observation.performer[0].id
     */
    private void _setProviderId(Observation observation, MeasurementEntity measurement) {
        measurement.provider_id = ProviderOModel.INSTANCE.getProviderIdFromReference(observation.getPerformerFirstRep());
    }
    
    /**
     * MEASUREMENT.person_id = Observation.subject.id
     */
    private void _setPersonId(Observation observation, MeasurementEntity measurement) {
        measurement.person_id = PersonOModel.INSTANCE.getIdFromReference(observation.getSubject());
    }
    
    /**
     * Maps Observation.code to MEASUREMENT's observation_concept_id and observation_source_value
     */
    private void _setConceptIdAndSourceValue(Observation observation, MeasurementEntity measurement) {
        CodeableConcept observationCodeable = observation.getCode();
        if (observationCodeable == null) {
            return;
        }
        
        measurement.measurement_concept_id = CodeableConceptUtils.getConceptId(observationCodeable);
        measurement.measurement_source_value = observationCodeable.getId();
    }
    
    /**
     * MEASUREMENT.measurement_date = Observation.effective
     */
    private void _setDate(Observation observation, MeasurementEntity measurement) {
        DateTimeType dateTimeType = observation.getEffectiveDateTimeType();
        if (dateTimeType == null) {
            return;
        }
        
        Date date = dateTimeType.toCalendar().getTime();
        measurement.measurement_date = DateTimeUtils.getDateString(date);
    }
    
    /**
     * Maps Observation.meta.security to MEASUREMENT.measurement_type_concept_id
     */
    private void _setTypeConceptId(Observation observation, MeasurementEntity measurement) {
        Meta meta = observation.getMeta();
        if (meta == null) {
            return;
        }
        
        Coding coding = meta.getSecurityFirstRep();
        if (coding == null) {
            return;
        }
        
        measurement.measurement_type_concept_id = ConvertUtils.toInteger(coding.getId());
    }
}
