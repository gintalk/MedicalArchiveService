package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.DeviceExposureEntity;
import com.vgu.cs.engine.entity.ProcedureOccurrenceEntity;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.model.business.omop.ProviderOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtil;
import org.hl7.fhir.dstu3.model.*;

import java.util.Date;

public class ProcedureFModel {

    public static final ProcedureFModel INSTANCE = new ProcedureFModel();

    private ProcedureFModel() {

    }

    public Procedure constructFhir(ProcedureOccurrenceEntity procedureOccurrence) {
        Procedure procedure = new Procedure();

        _addId(procedure, procedureOccurrence.procedure_occurrence_id);
        _addSubjectReference(procedure, procedureOccurrence.person_id);
        _addCode(procedure, procedureOccurrence);
        _addPerformedDate(procedure, procedureOccurrence);
        _addTypeExtension(procedure, procedureOccurrence);
        _addQuantity(procedure, procedureOccurrence.quantity);
        _addPerformerActor(procedure, procedureOccurrence.provider_id);

        return procedure;
    }

    public Procedure constructFhir(DeviceExposureEntity deviceExposure) {
        Procedure procedure = new Procedure();

        _addId(procedure, deviceExposure.device_exposure_id);
        _addQuantity(procedure, deviceExposure.quantity);
        _addPerformerActor(procedure, deviceExposure.provider_id);
        _addSubjectReference(procedure, deviceExposure.person_id);
        _addPerformedPeriod(procedure, deviceExposure);
        _addTypeExtension(procedure, deviceExposure);

        return procedure;
    }

    private void _addId(Procedure procedure, int id) {
        procedure.setId(new IdType(id));
    }

    private void _addSubjectReference(Procedure procedure, int personId) {
        procedure.setSubject(PersonOModel.INSTANCE.getReference(personId));
    }

    private void _addCode(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        CodeableConcept procedureCodeable = CodeableConceptUtil.fromConceptId(procedureOccurrence.procedure_concept_id);
        if (procedureCodeable == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(procedureOccurrence.procedure_source_value)) {
            procedureCodeable.setId(procedureOccurrence.procedure_source_value);
        }

        procedure.setCode(procedureCodeable);
    }

    private void _addPerformedDate(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        Date dateOrDateTime = DateTimeUtils.parseDateOrDateTime(procedureOccurrence.procedure_date, procedureOccurrence.procedure_datetime);
        if (dateOrDateTime == null) {
            return;
        }
        procedure.setPerformed(new DateTimeType(dateOrDateTime));
    }

    private void _addPerformedPeriod(Procedure procedure, DeviceExposureEntity deviceExposure) {
        Period period = procedure.getPerformedPeriod();

        Date start = DateTimeUtils.parseDateOrDateTime(deviceExposure.device_exposure_start_date, deviceExposure.device_exposure_start_datetime);
        if (start != null) {
            period.setStart(start);
        }

        Date end = DateTimeUtils.parseDateOrDateTime(deviceExposure.device_exposure_end_date, deviceExposure.device_exposure_end_datetime);
        if (end != null) {
            period.setEnd(end);
        }
    }

    private void _addTypeExtension(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        CodeableConcept typeCodeable = CodeableConceptUtil.fromConceptId(procedureOccurrence.procedure_type_concept_id);
        if (typeCodeable == null) {
            return;
        }

        Extension typeExtension = new Extension();
        typeExtension.setProperty("source-data-type", typeCodeable);

        procedure.addExtension(typeExtension);
    }

    private void _addTypeExtension(Procedure procedure, DeviceExposureEntity deviceExposure) {
        CodeableConcept typeCodeable = CodeableConceptUtil.fromConceptId(deviceExposure.device_type_concept_id);
        if (typeCodeable == null) {
            return;
        }

        Extension typeExtension = new Extension();
        typeExtension.setProperty("raw-value", typeCodeable);

        procedure.addExtension(typeExtension);
    }

    private void _addQuantity(Procedure procedure, int quantity) {
        CodeableConcept quantityCodeable = new CodeableConcept();
        quantityCodeable.setText(String.valueOf(quantity));

        Extension quantityExtension = new Extension();
        quantityExtension.setProperty("num-of-procedures", quantityCodeable);

        procedure.addExtension(quantityExtension);
    }

    private void _addPerformerActor(Procedure procedure, int providerId) {
        Reference actorReference = ProviderOModel.INSTANCE.getReference(providerId);
        procedure.addPerformer(new Procedure.ProcedurePerformerComponent().setActor(actorReference));
    }
}
