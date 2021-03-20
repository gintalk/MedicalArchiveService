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

    private void _addId(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        medStatement.setId(new IdType(drugExposure.drug_exposure_id));
    }

    // TODO
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

    private void _addContext(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        medStatement.setContext(VisitOccurrenceOModel.INSTANCE.getReference(drugExposure.visit_occurrence_id));
    }

    private void _addDrugExtension(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        CodeableConcept drugCodeable = CodeableConceptUtil.fromText(drugExposure.drug_source_value);
        if (drugCodeable == null) {
            return;
        }

        Extension rawValueExtension = new Extension();
        rawValueExtension.setProperty("raw-value", drugCodeable);

        medStatement.addExtension(rawValueExtension);
    }

    private void _addMedicationCodeableConcept(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        CodeableConcept medicationCodeable = CodeableConceptUtil.fromConceptId(drugExposure.drug_concept_id);
        if (medicationCodeable == null) {
            return;
        }
        medStatement.setMedication(medicationCodeable);
    }

    private void _addSubjectReference(MedicationStatement medStatement, DrugExposureEntity drugExposure) {
        medStatement.setSubject(PersonOModel.INSTANCE.getReference(drugExposure.person_id));
    }

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
