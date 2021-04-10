package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.DrugExposureEntity;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.model.business.omop.VisitOccurrenceOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtil;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.MedicationRequest.MedicationRequestDispenseRequestComponent;

import java.util.Date;

/**
 * <p>
 * A record of a medication that is being consumed by a patient. A MedicationStatement may indicate that the patient
 * may be taking the medication now or has taken the medication in the past or will be taking the medication in the future.
 * </p>
 * <p>
 * The class <code>MedicationStatementFModel</code> constructs <code>MedicationStatement</code>> from any record in the
 * OMOP-compliant table <code>drug_exposure</code>.
 * </p>
 * <p>
 * The <code>MedicationStatementFModel</code> class contains one single public method accepting a
 * <code>DrugExposureEntity</code>, which represents a record in <code>drug_exposure</code>, and returns a
 * FHIR-compliant <code>MedicationStatement</code>.
 * </p>
 *
 * @author namnh16 on 05/03/2021
 * @see <a href="http://build.fhir.org/ig/HL7/cdmh/profiles.html#omop-to-fhir-mappings">OMOP to FHIR mappings</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#DRUG_EXPOSURE">OMOP DRUG_EXPOSURE</a>
 * @see <a href="https://www.hl7.org/fhir/medicationstatement.html">FHIR Medication Statement</a>
 */
public class MedicationStatementFModel {

    public static final MedicationStatementFModel INSTANCE = new MedicationStatementFModel();

    private MedicationStatementFModel() {

    }

    public MedicationStatement constructFhir(DrugExposureEntity drugExposure) {
        MedicationStatement medStatement = new MedicationStatement();

        _addId(medStatement, drugExposure);
        _addBasedOn(medStatement, drugExposure);
        _addContext(medStatement, drugExposure);
        _addDrugExtension(medStatement, drugExposure);
        _addSubjectReference(medStatement, drugExposure);
        _addMedicationCodeableConcept(medStatement, drugExposure);
        _addEffectivePeriod(medStatement, drugExposure);
        _addMedicationCodeableConcept(medStatement, drugExposure);

        return medStatement;
    }

    /**
     * Corresponding FHIR field: MedicationStatement.id
     * DRUG_EXPOSURE.drug_exposure_id is the unique key given to records of drug dispensing or administrations for a person.
     */
    private void _addId(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        medStatement.setId(new IdType(drugExposure.drug_exposure_id));
    }

    /**
     * Corresponding FHIR field: MedicationStatement.basedOn(MedicationRequest)
     * This is a work-in-progress.
     */
    private void _addBasedOn(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        MedicationRequest medRequest = new MedicationRequest();

        MedicationRequestDispenseRequestComponent dispenseRequest = new MedicationRequestDispenseRequestComponent();
        dispenseRequest.setNumberOfRepeatsAllowedElement(new PositiveIntType(drugExposure.refills));
        SimpleQuantity quantity = new SimpleQuantity();
        quantity.setValue(drugExposure.quantity);
        dispenseRequest.setQuantity(quantity);
        Duration duration = new Duration();
        duration.setValue(drugExposure.days_supply);
        dispenseRequest.setExpectedSupplyDuration(duration);
        medRequest.setDispenseRequest(dispenseRequest);
    }

    /**
     * Corresponding FHIR field: MedicationStatement.context
     * Encounter/Episode associated with MedicationStatement. DRUG_EXPOSURE.visit_occurrence_id identifies the Visit
     * during which the drug was prescribed, administered or dispensed.
     */
    private void _addContext(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        medStatement.setContext(VisitOccurrenceOModel.INSTANCE.getReference(drugExposure.visit_occurrence_id));
    }

    /**
     * Corresponding FHIR field: MedicationStatement.Extension (Proposed Name: raw-value : CodeableConcept)
     * DRUG_EXPOSURE.drug_source_value houses the verbatim value from the source data representing the drug exposure
     * that occurred.
     */
    private void _addDrugExtension(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        CodeableConcept drugCodeable = CodeableConceptUtil.fromText(drugExposure.drug_source_value);
        if (drugCodeable == null) {
            return;
        }

        Extension rawValueExtension = new Extension();
        rawValueExtension.setProperty("raw-value", drugCodeable);

        medStatement.addExtension(rawValueExtension);
    }

    /**
     * Corresponding FHIR field: MedicationStatement.medication
     * What medication was taken. DRUG_EXPOSURE.drug_concept_id is the standard concept mapped from the source concept
     * id which represents a drug product or molecule otherwise introduced to the body.
     */
    private void _addMedicationCodeableConcept(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        CodeableConcept medicationCodeable = CodeableConceptUtil.fromConceptId(drugExposure.drug_concept_id);
        if (medicationCodeable == null) {
            return;
        }
        medStatement.setMedication(medicationCodeable);
    }

    /**
     * Corresponding FHIR field: MedicationStatement.subject
     * Who is/was taking the medication. DRUG_EXPOSURE.person_id is the PERSON_ID of the PERSON for whom the drug
     * dispensing or administration is recorded. This may be a system generated code.
     */
    private void _addSubjectReference(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        medStatement.setSubject(PersonOModel.INSTANCE.getReference(drugExposure.person_id));
    }

    /**
     * Corresponding FHIR field: MedicationStatement.effectivePeriod
     * The date/time or interval when the medication is/was/will be taken. DRUG_EXPOSURE.drug_exposure_start_datetime
     * is used to determine the start date of the drug record. DRUG_EXPOSURE.drug_exposure_end_datetime denotes the day
     * the drug exposure ended for the patient.
     */
    private void _addEffectivePeriod(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        Period effectivePeriod = medStatement.getEffectivePeriod();

        Date start = DateTimeUtils.parseDateOrDateTime(drugExposure.drug_exposure_start_date, drugExposure.drug_exposure_start_datetime);
        if (start != null) {
            effectivePeriod.setStart(start);
        }

        Date end = DateTimeUtils.parseDateOrDateTime(drugExposure.drug_exposure_end_date, drugExposure.drug_exposure_end_datetime);
        if (end != null) {
            effectivePeriod.setEnd(end);
        }
    }
}
