package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.ObservationEntity;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.model.business.omop.ProviderOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtil;
import org.hl7.fhir.dstu3.model.*;

import java.util.Date;

public class ObservationFModel {

    public static final ObservationFModel INSTANCE = new ObservationFModel();

    private ObservationFModel() {

    }

    public Observation constructFhir(ObservationEntity oObservation) {
        Observation fObservation = new Observation();

        _addId(fObservation, oObservation);
        _addQualifierExtension(fObservation, oObservation);
        _addValue(fObservation, oObservation);
        _addPerformerReference(fObservation, oObservation);
        _addSubjectReference(fObservation, oObservation);
        _addCode(fObservation, oObservation);
        _addEffectiveDateTime(fObservation, oObservation);
        _addCategory(fObservation, oObservation);

        return fObservation;
    }

    private void _addId(Observation fObservation, ObservationEntity oObservation) {
        fObservation.setId(new IdType(oObservation.observation_id));
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

    private void _addValue(Observation fObservation, ObservationEntity oObservation) {
        CodeableConcept unitCodeable = CodeableConceptUtil.fromConceptId(oObservation.unit_concept_id);
        if (unitCodeable == null) {
            return;
        }

        Quantity quantity = new Quantity();
        quantity.setSystem(unitCodeable.getCodingFirstRep().getSystem());
        quantity.setUnit(unitCodeable.getCodingFirstRep().getDisplay());
        quantity.setCode(unitCodeable.getCodingFirstRep().getCode());

        fObservation.setValue(quantity);

        CodeableConcept valueCodeable = CodeableConceptUtil.fromConceptId(oObservation.value_as_concept_id);
        if (valueCodeable != null) {
            fObservation.setValue(valueCodeable);
        } else if (!StringUtils.isNullOrEmpty(oObservation.value_as_string)) {
            fObservation.setValue(new StringType(oObservation.value_as_string));
        } else {
            fObservation.getValueQuantity().setValue(oObservation.value_as_number);
        }
    }

    private void _addPerformerReference(Observation fObservation, ObservationEntity oObservation) {
        fObservation.addPerformer(ProviderOModel.INSTANCE.getReference(oObservation.provider_id));
    }

    private void _addSubjectReference(Observation fObservation, ObservationEntity oObservation) {
        fObservation.setSubject(PersonOModel.INSTANCE.getReference(oObservation.person_id));
    }

    private void _addCode(Observation fObservation, ObservationEntity oObservation) {
        CodeableConcept codeable = CodeableConceptUtil.fromConceptId(oObservation.observation_concept_id);
        if (codeable == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(oObservation.observation_source_value)) {
            fObservation.setId(oObservation.observation_source_value);
        }
        fObservation.setCode(codeable);
    }

    private void _addEffectiveDateTime(Observation fObservation, ObservationEntity oObservation) {
        Date dateOrDateTime = DateTimeUtils.parseDateOrDateTime(oObservation.observation_date, oObservation.observation_datetime);
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
}
