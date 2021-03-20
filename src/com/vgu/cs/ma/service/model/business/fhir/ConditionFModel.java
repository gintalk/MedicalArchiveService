package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 19/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.ConditionOccurrenceEntity;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.model.business.omop.ProviderOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtil;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;

import java.util.Date;

public class ConditionFModel  {

    public static final ConditionFModel INSTANCE = new ConditionFModel();

    private ConditionFModel() {

    }

    public Condition constructFhir(ConditionOccurrenceEntity conditionOccurrence) {
        Condition condition = new Condition();

        _addId(condition, conditionOccurrence);
        _addAsserterReference(condition, conditionOccurrence);
        _addClinicalStatus(condition, conditionOccurrence);
        _addSubjectReference(condition, conditionOccurrence);
        _addCode(condition, conditionOccurrence);
        _addOnsetDateTime(condition, conditionOccurrence);
        _addAbatementDateTime(condition, conditionOccurrence);
        _addRawValueExtension(condition, conditionOccurrence);
        _addAbatementReasonExtension(condition, conditionOccurrence);

        return condition;
    }

    private void _addId(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        condition.setId(new IdType(String.valueOf(conditionOccurrence.condition_occurrence_id)));
    }

    private void _addAsserterReference(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        condition.setAsserter(ProviderOModel.INSTANCE.getReference(conditionOccurrence.provider_id));
    }

    // TODO
    private void _addClinicalStatus(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        condition.setClinicalStatus(ConditionClinicalStatus.ACTIVE);
    }

    private void _addSubjectReference(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        condition.setSubject(PersonOModel.INSTANCE.getReference(conditionOccurrence.person_id));
    }

    private void _addCode(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        CodeableConcept conditionCodeable = CodeableConceptUtil.fromConceptId(conditionOccurrence.condition_concept_id);
        if (conditionCodeable == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(conditionOccurrence.condition_source_value)) {
            conditionCodeable.setId(conditionOccurrence.condition_source_value);
        }
        condition.setCode(conditionCodeable);
    }

    private void _addOnsetDateTime(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        Date dateOrDatetime = DateTimeUtils.parseDateOrDateTime(conditionOccurrence.condition_start_date, conditionOccurrence.condition_start_datetime);
        if (dateOrDatetime == null) {
            return;
        }
        condition.setOnset(new DateTimeType(dateOrDatetime));
    }

    private void _addAbatementDateTime(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        Date dateOrDatetime = DateTimeUtils.parseDateOrDateTime(conditionOccurrence.condition_end_date, conditionOccurrence.condition_end_datetime);
        if (dateOrDatetime == null) {
            return;
        }
        condition.setAbatement(new DateTimeType(dateOrDatetime));
    }

    private void _addRawValueExtension(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        CodeableConcept conditionTypeCodeable = CodeableConceptUtil.fromConceptId(conditionOccurrence.condition_type_concept_id);
        if (conditionTypeCodeable == null) {
            return;
        }

        Extension rawValueExtension = new Extension();
        rawValueExtension.setProperty("raw-value", conditionTypeCodeable);

        condition.addExtension(rawValueExtension);
    }

    private void _addAbatementReasonExtension(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        CodeableConcept stopReasonCodeable = CodeableConceptUtil.fromText(conditionOccurrence.stop_reason);
        if (stopReasonCodeable == null) {
            return;
        }

        Extension abatementReasonExtension = new Extension();
        abatementReasonExtension.setProperty("abatement-reason", stopReasonCodeable);

        condition.addExtension(abatementReasonExtension);
    }
}
