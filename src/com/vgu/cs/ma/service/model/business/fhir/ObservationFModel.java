package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.MeasurementEntity;
import com.vgu.cs.engine.entity.ObservationEntity;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.model.business.omop.ProviderOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtil;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Observation.ObservationReferenceRangeComponent;

import java.util.Date;

public class ObservationFModel {

    public static final ObservationFModel INSTANCE = new ObservationFModel();

    private ObservationFModel() {

    }

    public Observation constructFhir(ObservationEntity oObservation) {
        Observation fObservation = new Observation();

        _addId(fObservation, oObservation.observation_id);
        _addQualifierExtension(fObservation, oObservation);
        _addValue(fObservation, oObservation.unit_concept_id, oObservation.value_as_number, oObservation.value_as_string, oObservation.value_as_concept_id);
        _addPerformerReference(fObservation, oObservation.provider_id);
        _addSubjectReference(fObservation, oObservation.person_id);
        _addCode(fObservation, oObservation.observation_concept_id);
        _addEffectiveDateTime(fObservation, oObservation.observation_date, oObservation.observation_datetime);
        _addCategory(fObservation, oObservation);

        return fObservation;
    }

    public Observation constructFhir(MeasurementEntity measurement) {
        Observation observation = new Observation();

        _addId(observation, measurement.measurement_id);
        _addValue(observation, measurement.unit_concept_id, measurement.value_as_number, "", measurement.value_as_concept_id);
        _addReferenceRange(observation, measurement);
        _addPerformerReference(observation, measurement.provider_id);
        _addMeasurementExtension(observation, measurement);
        _addSubjectReference(observation, measurement.person_id);
        _addCode(observation, measurement.measurement_concept_id);
        _addEffectiveDateTime(observation, measurement.measurement_date, measurement.measurement_datetime);
        _addSource(observation, measurement);

        return observation;
    }

    private void _addId(Observation fObservation, int id) {
        fObservation.setId(new IdType(id));
    }

    private void _addQualifierExtension(Observation fObservation, ObservationEntity oObservation) {
        CodeableConcept qualifierCodeable = CodeableConceptUtil.fromConceptId(oObservation.qualifier_concept_id);
        if (qualifierCodeable == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(oObservation.qualifier_source_value)) {
            qualifierCodeable.setId(oObservation.qualifier_source_value);
        }

        Extension qualifierExtension = new Extension();
        qualifierExtension.setProperty("decision-aid-alert", qualifierCodeable);

        fObservation.addExtension(qualifierExtension);
    }

    private void _addValue(Observation fObservation, int unitConceptId, double valueAsNumber, String valueAsString, int valueAsConceptId) {
        CodeableConcept unitCodeable = CodeableConceptUtil.fromConceptId(unitConceptId);
        if (unitCodeable == null) {
            return;
        }

        Quantity quantity = new Quantity();
        quantity.setSystem(unitCodeable.getCodingFirstRep().getSystem());
        quantity.setUnit(unitCodeable.getCodingFirstRep().getDisplay());
        quantity.setCode(unitCodeable.getCodingFirstRep().getCode());

        fObservation.setValue(quantity);

        CodeableConcept valueCodeable = CodeableConceptUtil.fromConceptId(valueAsConceptId);
        if (valueCodeable != null) {
            fObservation.setValue(valueCodeable);
        } else if (!StringUtils.isNullOrEmpty(valueAsString)) {
            fObservation.setValue(new StringType(valueAsString));
        } else {
            fObservation.getValueQuantity().setValue(valueAsNumber);
        }
    }

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

    private void _addPerformerReference(Observation fObservation, int providerId) {
        fObservation.addPerformer(ProviderOModel.INSTANCE.getReference(providerId));
    }

    private void _addMeasurementExtension(Observation observation, MeasurementEntity measurement) {
        CodeableConcept codeable = CodeableConceptUtil.fromText(measurement.measurement_source_value);
        if (codeable == null) {
            return;
        }

        Extension rawValueExtension = new Extension();
        rawValueExtension.setProperty("raw-value", codeable);

        observation.addExtension(rawValueExtension);
    }

    private void _addSubjectReference(Observation fObservation, int personId) {
        fObservation.setSubject(PersonOModel.INSTANCE.getReference(personId));
    }

    private void _addCode(Observation fObservation, int conceptId) {
        CodeableConcept codeable = CodeableConceptUtil.fromConceptId(conceptId);
        if (codeable == null) {
            return;
        }
        fObservation.setCode(codeable);
    }

    private void _addEffectiveDateTime(Observation fObservation, String date, String dateTime) {
        Date dateOrDateTime = DateTimeUtils.parseDateOrDateTime(date, dateTime);
        if (dateOrDateTime == null) {
            return;
        }
        fObservation.setEffective(new DateTimeType(dateOrDateTime));
    }

    private void _addCategory(Observation fObservation, ObservationEntity oObservation) {
        CodeableConcept categoryCodeable = CodeableConceptUtil.fromConceptId(oObservation.observation_type_concept_id);
        if (categoryCodeable == null) {
            return;
        }
        fObservation.addCategory(categoryCodeable);
    }

    private void _addSource(Observation observation, MeasurementEntity measurement) {
        CodeableConcept measurementTypeCodeable = CodeableConceptUtil.fromConceptId(measurement.measurement_type_concept_id);
        if (measurementTypeCodeable == null) {
            return;
        }
        observation.setMeta(new Meta().addSecurity(measurementTypeCodeable.getCodingFirstRep()));
    }
}
