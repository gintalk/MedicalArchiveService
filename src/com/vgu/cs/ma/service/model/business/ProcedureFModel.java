package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.ProcedureOccurrenceEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtil;
import org.hl7.fhir.dstu3.model.*;

import java.util.Date;

public class ProcedureFModel extends FhirOmopModel {

    public static final ProcedureFModel INSTANCE = new ProcedureFModel();

    private ProcedureFModel() {

    }

    public Procedure constructFhir(ProcedureOccurrenceEntity procedureOccurrence) {
        Procedure procedure = new Procedure();

        _addId(procedure, procedureOccurrence);
        _addSubjectReference(procedure, procedureOccurrence);
        _addCode(procedure, procedureOccurrence);
        _addPerformedDate(procedure, procedureOccurrence);
        _addTypeExtension(procedure, procedureOccurrence);
        _addQuantity(procedure, procedureOccurrence);
        _addPerformerActor(procedure, procedureOccurrence);

        return procedure;
    }

    private void _addId(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        procedure.setId(new IdType(procedureOccurrence.procedure_occurrence_id));
    }

    private void _addSubjectReference(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        procedure.setSubject(PersonOModel.INSTANCE.getReference(procedureOccurrence.person_id));
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

    private void _addTypeExtension(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        CodeableConcept typeCodeable = CodeableConceptUtil.fromConceptId(procedureOccurrence.procedure_type_concept_id);
        if (typeCodeable == null) {
            return;
        }

        Extension typeExtension = new Extension();
        typeExtension.setProperty("source-data-type", typeCodeable);

        procedure.addExtension(typeExtension);
    }

    private void _addQuantity(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        CodeableConcept quantityCodeable = new CodeableConcept();
        quantityCodeable.setText(String.valueOf(procedureOccurrence.quantity));

        Extension quantityExtension = new Extension();
        quantityExtension.setProperty("num-of-procedures", quantityCodeable);

        procedure.addExtension(quantityExtension);
    }

    private void _addPerformerActor(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        Reference actorReference = ProviderOModel.INSTANCE.getReference(procedureOccurrence.provider_id);
        procedure.addPerformer(new Procedure.ProcedurePerformerComponent().setActor(actorReference));
    }
}
