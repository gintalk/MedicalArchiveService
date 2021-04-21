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
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.*;

import java.util.Date;

/**
 * <p>
 * An action that is or was performed on or for a patient. This can be a physical intervention like an operation, or
 * less invasive like long term services, counseling, or hypnotherapy.
 * </p>
 * <p>
 * The class <code>ProcedureFModel</code> constructs <code>Procedure</code>> from any record in the OMOP-compliant
 * table <code>procedure_occurrence</code> or <code>device_exposure</code>.
 * </p>
 * <p>
 * The <code>ProcedureFModel</code> class contains two public methods accepting either a <code>ProcedureOccurrenceEntity</code> or a
 * <code>DeviceExposureEntity</code>, and returns a FHIR-compliant <code>Procedure</code>.
 * </p>
 *
 * @author namnh16 on 05/03/2021
 * @see <a href="http://build.fhir.org/ig/HL7/cdmh/profiles.html#omop-to-fhir-mappings">OMOP to FHIR mappings</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#PROCEDURE_OCCURRENCE">OMOP PROCEDURE_OCCURRENCE</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#DEVICE_EXPOSURE">OMOP DEVICE_EXPOSURE</a>
 * @see <a href="https://www.hl7.org/fhir/procedure.html">FHIR Procedure</a>
 */
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

    /**
     * Corresponding FHIR field: Procedure.id
     * External identifiers for this procedure.
     */
    private void _addId(Procedure procedure, int id) {
        procedure.setId(new IdType(id));
    }

    /**
     * Corresponding FHIR field: Procedure.subject
     * Who the procedure was performed on.
     */
    private void _addSubjectReference(Procedure procedure, int personId) {
        procedure.setSubject(PersonOModel.INSTANCE.getReference(personId));
    }

    /**
     * Corresponding FHIR field: Procedure.code
     * Identification of the procedure. PROCEDURE_OCCURRENCE.procedure_concept_id is the standard concept mapped from
     * the source value which represents a procedure. PROCEDURE_OCCURRENCE.procedure_source_value houses the verbatim
     * value from the source data representing the procedure that occurred.
     *
     * @see <a href="https://www.hl7.org/fhir/valueset-procedure-code.html">Available values for procedure code</a>
     */
    private void _addCode(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        CodeableConcept procedureCodeable = CodeableConceptUtils.fromConceptId(procedureOccurrence.procedure_concept_id);
        if (procedureCodeable == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(procedureOccurrence.procedure_source_value)) {
            procedureCodeable.setId(procedureOccurrence.procedure_source_value);
        }

        procedure.setCode(procedureCodeable);
    }

    /**
     * Corresponding FHIR field: Procedure.performedDateTime
     * When the procedure was performed.
     */
    private void _addPerformedDate(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        Date dateOrDateTime = DateTimeUtils.parseDateOrDateTime(procedureOccurrence.procedure_date, procedureOccurrence.procedure_datetime);
        if (dateOrDateTime == null) {
            return;
        }
        procedure.setPerformed(new DateTimeType(dateOrDateTime));
    }

    /**
     * Corresponding FHIR field: Procedure.performedPeriod
     * When the procedure was performed.
     */
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

    /**
     * Corresponding FHIR field: Procedure.Extension (Proposed Name: source-data-type : CodeableConcept)
     * PROCEDURE_OCCURRENCE.procedure_type_concept_id  can be used to determine the provenance of the Procedure record,
     * as in whether the procedure was from an EHR system, insurance claim, registry, or other sources.
     */
    private void _addTypeExtension(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        CodeableConcept typeCodeable = CodeableConceptUtils.fromConceptId(procedureOccurrence.procedure_type_concept_id);
        if (typeCodeable == null) {
            return;
        }

        Extension typeExtension = new Extension();
        typeExtension.setProperty("source-data-type", typeCodeable);

        procedure.addExtension(typeExtension);
    }

    /**
     * Corresponding FHIR field: Procedure.Extension (Proposed Name: raw-value : CodeableConcept)
     * DEVICE_EXPOSURE.device_type_concept_id denotes the provenance of the record, as in whether the record is from
     * administrative claims or EHR.
     */
    private void _addTypeExtension(Procedure procedure, DeviceExposureEntity deviceExposure) {
        CodeableConcept typeCodeable = CodeableConceptUtils.fromConceptId(deviceExposure.device_type_concept_id);
        if (typeCodeable == null) {
            return;
        }

        Extension typeExtension = new Extension();
        typeExtension.setProperty("raw-value", typeCodeable);

        procedure.addExtension(typeExtension);
    }

    /**
     * Corresponding FHIR field: Procedure.Extension (Proposed Name: num-of-procedures : CodeableConcept)
     */
    private void _addQuantity(Procedure procedure, int quantity) {
        CodeableConcept quantityCodeable = new CodeableConcept();
        quantityCodeable.setText(String.valueOf(quantity));

        Extension quantityExtension = new Extension();
        quantityExtension.setProperty("num-of-procedures", quantityCodeable);

        procedure.addExtension(quantityExtension);
    }

    /**
     * Corresponding FHIR field: Procedure.performer.actor
     * The people who performed the procedure.
     */
    private void _addPerformerActor(Procedure procedure, int providerId) {
        Reference actorReference = ProviderOModel.INSTANCE.getReference(providerId);
        procedure.addPerformer(new Procedure.ProcedurePerformerComponent().setActor(actorReference));
    }
}
