package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.MeasurementEntity;
import com.vgu.cs.engine.entity.ObservationEntity;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.model.business.omop.ProviderOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Observation.ObservationReferenceRangeComponent;

import java.util.Date;

/**
 * <p>
 * Measurements and simple assertions made about a patient, device or other subject.
 * </p>
 * <p>
 * The class <code>ObservationFModel</code> constructs <code>Observation</code>> from any record in the OMOP-compliant
 * table <code>observation</code> or <code>measurement</code>.
 * </p>
 * <p>
 * The <code>ObservationFModel</code> class contains two public methods accepting either a <code>ObservationEntity</code>
 * or a <code>MeasurementEntity</code>, and returns a FHIR-compliant <code>Observation</code>.
 * </p>
 *
 * @author namnh16 on 05/03/2021
 * @see <a href="http://build.fhir.org/ig/HL7/cdmh/profiles.html#omop-to-fhir-mappings">OMOP to FHIR mappings</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#OBSERVATION">OMOP OBSERVATION</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#MEASUREMENT">OMOP MEASUREMENT</a>
 * @see <a href="https://www.hl7.org/fhir/observation.html">FHIR Observation</a>
 */
public class ObservationFModel {
    
    public static final ObservationFModel INSTANCE = new ObservationFModel();
    
    private ObservationFModel() {
    
    }
    
    public Observation constructFhir(ObservationEntity oObservation) {
        Observation fObservation = new Observation();
        
        _addId(fObservation, oObservation.observation_id);
        _addQualifierExtension(fObservation, oObservation);
        _addValue(fObservation, oObservation.unit_concept_id, oObservation.unit_source_value, oObservation.value_as_number, oObservation.value_as_string, oObservation.value_as_concept_id);
        _addPerformerReference(fObservation, oObservation.provider_id);
        _addSubjectReference(fObservation, oObservation.person_id);
        _addCode(fObservation, oObservation.observation_concept_id, oObservation.observation_source_value);
        _addEffectiveDateTime(fObservation, oObservation.observation_date, oObservation.observation_datetime);
        _addCategory(fObservation, oObservation);
        
        return fObservation;
    }
    
    public Observation constructFhir(MeasurementEntity measurement) {
        Observation observation = new Observation();
        
        _addId(observation, measurement.measurement_id);
        _addValue(observation, measurement.unit_concept_id, measurement.unit_source_value, measurement.value_as_number, "", measurement.value_as_concept_id);
        _addReferenceRange(observation, measurement);
        _addPerformerReference(observation, measurement.provider_id);
        _addMeasurementExtension(observation, measurement);
        _addSubjectReference(observation, measurement.person_id);
        _addCode(observation, measurement.measurement_concept_id, measurement.measurement_source_value);
        _addEffectiveDateTime(observation, measurement.measurement_date, measurement.measurement_datetime);
        _addSource(observation, measurement);
        
        return observation;
    }
    
    /**
     * Corresponding FHIR field: Observation.id
     */
    private void _addId(Observation fObservation, int id) {
        fObservation.setId(new IdType(id));
    }
    
    /**
     * Corresponding FHIR field: Observation.Extension (Proposed Name: decision-aid-alert : CodeableConcept)
     * OBSERVATION.qualifier_concept_id contains all attributes specifying the clinical fact further, such as as
     * degrees, severities, drug-drug interaction alerts etc. OBSERVATION.qualifier_source_value houses the verbatim
     * value from the source data representing the qualifier of the Observation that occurred.
     */
    private void _addQualifierExtension(Observation fObservation, ObservationEntity oObservation) {
        CodeableConcept qualifierCodeable = CodeableConceptUtils.fromConceptIdAndSourceValue(oObservation.qualifier_concept_id, oObservation.qualifier_source_value);
        
        Extension qualifierExtension = new Extension();
        qualifierExtension.setUserData("name", "decision-aid-alert");
        qualifierExtension.setValue(qualifierCodeable);
        
        fObservation.addExtension(qualifierExtension);
    }
    
    /**
     * Corresponding FHIR field: Observation.value[x], Observation.valueString, Observation.valueCodeableConcept
     * Actual result
     */
    private void _addValue(Observation fObservation, int unitConceptId, String unitSourceValue, double valueAsNumber, String valueAsString, int valueAsConceptId) {
        CodeableConcept unitCodeable = CodeableConceptUtils.fromConceptIdAndSourceValue(unitConceptId, unitSourceValue);
        if (unitCodeable != null) {
            Quantity quantity = new Quantity();
            quantity.setSystem(unitCodeable.getCodingFirstRep().getSystem());
            quantity.setUnit(unitCodeable.getCodingFirstRep().getDisplay());
            quantity.setCode(unitCodeable.getCodingFirstRep().getCode());
            quantity.setId(unitConceptId + "." + unitSourceValue);
            quantity.setValue(valueAsNumber);
            fObservation.setValue(quantity);
            
            return;
        }
        
        CodeableConcept valueCodeable = CodeableConceptUtils.fromConceptId(valueAsConceptId);
        if (valueCodeable != null) {
            fObservation.setValue(valueCodeable);
            return;
        }
        
        fObservation.setValue(new StringType(valueAsString));
    }
    
    /**
     * Corresponding FHIR field: Observation.referenceRange
     * Provides guide for interpretation
     */
    private void _addReferenceRange(Observation observation, MeasurementEntity measurement) {
        ObservationReferenceRangeComponent component = new ObservationReferenceRangeComponent();
        
        SimpleQuantity low = new SimpleQuantity();
        low.setValue(measurement.range_low);
        component.setLow(low);
        
        SimpleQuantity high = new SimpleQuantity();
        high.setValue(measurement.range_high);
        component.setHigh(high);
        
        observation.addReferenceRange(component);
    }
    
    /**
     * Corresponding FHIR field: Observation.performer
     * Who is responsible for the observation.
     */
    private void _addPerformerReference(Observation fObservation, int providerId) {
        fObservation.addPerformer(ProviderOModel.INSTANCE.getReference(providerId));
    }
    
    /**
     * Corresponding FHIR field: Observation.Extension (Proposed Name: raw-value : CodeableConcept)
     * MEASUREMENT.measure_source_value houses the verbatim value from the source data representing the Measurement
     * that occurred.
     */
    private void _addMeasurementExtension(Observation observation, MeasurementEntity measurement) {
        CodeableConcept codeable = CodeableConceptUtils.fromText(measurement.measurement_source_value);
        if (codeable == null) {
            return;
        }
        
        Extension rawValueExtension = new Extension();
        rawValueExtension.setProperty("raw-value", codeable);
        
        observation.addExtension(rawValueExtension);
    }
    
    /**
     * Corresponding FHIR field: Observation.subject
     * Who and/or what the observation is about.
     */
    private void _addSubjectReference(Observation fObservation, int personId) {
        fObservation.setSubject(PersonOModel.INSTANCE.getReference(personId));
    }
    
    /**
     * Corresponding FHIR field: Observation.code
     * Type of observation (code / type).
     *
     * @see <a href="https://www.hl7.org/fhir/valueset-observation-codes.html">Available values for observation code</a>
     */
    private void _addCode(Observation fObservation, int conceptId, String sourceValue) {
        CodeableConcept codeable = CodeableConceptUtils.fromConceptIdAndSourceValue(conceptId, sourceValue);
        if (codeable == null) {
            return;
        }
        fObservation.setCode(codeable);
    }
    
    /**
     * Corresponding FHIR field: Observation.effectiveDateTime
     * Clinically relevant time/time-period for observation.
     */
    private void _addEffectiveDateTime(Observation fObservation, String date, String dateTime) {
        Date dateOrDateTime = DateTimeUtils.parseDateOrDateTime(date, dateTime);
        if (dateOrDateTime == null) {
            return;
        }
        fObservation.setEffective(new DateTimeType(dateOrDateTime));
    }
    
    /**
     * Corresponding FHIR field: Observation.category
     * Classification of type of observation. OBSERVATION.observation_type_concept_id can be used to determine the
     * provenance of the Observation record, as in whether the measurement was from an EHR system, insurance claim,
     * registry, or other sources.
     *
     * @see <a href="https://www.hl7.org/fhir/valueset-observation-category.html">Available values for observation category</a>
     */
    private void _addCategory(Observation fObservation, ObservationEntity oObservation) {
        CodeableConcept categoryCodeable = CodeableConceptUtils.fromConceptId(oObservation.observation_type_concept_id);
        if (categoryCodeable == null) {
            return;
        }
        fObservation.addCategory(categoryCodeable);
    }
    
    /**
     * Corresponding FHIR field: Observation.meta.source
     * MEASUREMENT.measurement_type_concept_id can be used to determine the provenance of the Measurement record, as in
     * whether the measurement was from an EHR system, insurance claim, registry, or other sources.
     */
    private void _addSource(Observation observation, MeasurementEntity measurement) {
        CodeableConcept measurementTypeCodeable = CodeableConceptUtils.fromConceptId(measurement.measurement_type_concept_id);
        if (measurementTypeCodeable == null) {
            return;
        }
        observation.setMeta(new Meta().addSecurity(measurementTypeCodeable.getCodingFirstRep()));
    }
}
