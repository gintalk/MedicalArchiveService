package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 03/03/2021
 */

import com.vgu.cs.common.logger.VLogger;
import com.vgu.cs.engine.entity.PersonEntity;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Patient;

public class OMOPModel {

    public static final OMOPModel INSTANCE = new OMOPModel();
    private static final Logger LOGGER = VLogger.getLogger(OMOPModel.class);
    private final PatientModel PATIENT_MODEL;

    private OMOPModel() {
        PATIENT_MODEL = PatientModel.INSTANCE;
    }

    public Patient toFhir(PersonEntity person) {
        Patient patient = new Patient();

        patient.addIdentifier(PATIENT_MODEL.getIdentifier(person.person_id));
        patient.addGeneralPractitioner(PATIENT_MODEL.getGeneralPractitionerReference(person.provider_id));
        patient.setGender(PATIENT_MODEL.getGender(person.gender_concept_id));
        patient.setBirthDate(PATIENT_MODEL.getBirthdate(person.year_of_birth, person.month_of_birth, person.day_of_birth));
        patient.addAddress(PATIENT_MODEL.getAddress(person.location_id));
        patient.addExtension(PATIENT_MODEL.getRaceExtension(person.race_concept_id));
        patient.addExtension(PATIENT_MODEL.getEthnicityExtension(person.ethnicity_concept_id));

        return patient;
    }
}
