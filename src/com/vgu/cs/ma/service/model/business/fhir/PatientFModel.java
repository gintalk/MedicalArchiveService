package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 05/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.ConceptEntity;
import com.vgu.cs.engine.entity.PersonEntity;
import com.vgu.cs.ma.service.model.business.omop.LocationOModel;
import com.vgu.cs.ma.service.model.business.omop.ProviderOModel;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;

import java.util.Calendar;

public class PatientFModel  {

    public static final PatientFModel INSTANCE = new PatientFModel();
    private final String US_CORE_RACE_URL;
    private final String US_CORE_ETHNICITY_URL;

    private PatientFModel() {
        super();

        US_CORE_RACE_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race";
        US_CORE_ETHNICITY_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity";
    }

    public Patient constructFhir(PersonEntity person) {
        Patient patient = new Patient();

        _addIdentifier(patient, person);
        _addIdentifier(patient, person);
        _addBirthDate(patient, person);
        _addGeneralPractitionerReference(patient, person);
        _addGender(patient, person);
        _addAddress(patient, person);
        _addRaceExtension(patient, person);
        _addEthnicityExtension(patient, person);

        return patient;
    }

    private void _addIdentifier(Patient patient, PersonEntity person) {
        Identifier idIdentifier = new Identifier();
        idIdentifier.setValue(String.valueOf(person.person_id));
        patient.addIdentifier(idIdentifier);

        if (StringUtils.isNullOrEmpty(person.person_source_value)) {
            return;
        }

        Identifier sourceValueIdentifier = new Identifier();
        String[] sourceValues = person.person_source_value.trim().split("\\^");
        if (sourceValues.length == 1) {
            sourceValueIdentifier.setValue(sourceValues[0]);
        } else {
            String fhirSystem = FO_VOCAB_DM.getFhirUrlSystem(sourceValues[0]);

            StringBuilder valueBuilder = new StringBuilder();
            if (sourceValues.length > 2) {
                String code = sourceValues[1];
                CodeableConcept typeCodeable = new CodeableConcept();
                Coding typeCoding = new Coding();

                if ("NONE".equalsIgnoreCase(fhirSystem)) {
                    typeCoding.setSystem(fhirSystem);
                }
                typeCoding.setCode(code);
                typeCodeable.addCoding(typeCoding);
                sourceValueIdentifier.setType(typeCodeable);

                for (int i = 2; i < sourceValues.length; i++) {
                    valueBuilder.append(sourceValues[i]);
                }
            } else {
                if ("NONE".equalsIgnoreCase(fhirSystem)) {
                    sourceValueIdentifier.setSystem(fhirSystem);
                }
                valueBuilder.append(sourceValues[1]);
            }
            sourceValueIdentifier.setValue(valueBuilder.toString());
        }
        patient.addIdentifier(sourceValueIdentifier);
    }

    private void _addBirthDate(Patient patient, PersonEntity person) {
        if (!StringUtils.isNullOrEmpty(person.birth_datetime)) {
            patient.setBirthDate(DateTimeUtils.parseDatetime(person.birth_datetime));
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, person.year_of_birth);
            calendar.set(Calendar.MONTH, person.month_of_birth - 1);
            calendar.set(Calendar.DAY_OF_MONTH, person.day_of_birth);

            patient.setBirthDate(calendar.getTime());
        }
    }

    private void _addGeneralPractitionerReference(Patient patient, PersonEntity person) {
        patient.addGeneralPractitioner(ProviderOModel.INSTANCE.getReference(person.provider_id));
    }

    private void _addGender(Patient patient, PersonEntity person) {
        ConceptEntity genderConcept = ConceptDModel.INSTANCE.getConcept(person.gender_concept_id);
        if (genderConcept == null) {
            patient.setGender(AdministrativeGender.NULL);
        } else {
            patient.setGender(AdministrativeGender.fromCode(genderConcept.concept_name.toLowerCase()));
        }
    }

    private void _addAddress(Patient patient, PersonEntity person) {
        patient.addAddress(LocationOModel.INSTANCE.getAddress(person.location_id));
    }

    private void _addRaceExtension(Patient patient, PersonEntity person) {
        ConceptEntity raceConcept = ConceptDModel.INSTANCE.getConcept(person.race_concept_id);

        Coding raceCoding;
        if (raceConcept == null) {
            raceCoding = FO_CODE_DM.getFhirCodingFromOmopSourceValue(person.race_source_value);
        } else {
            raceCoding = FO_CODE_DM.getFhirCodingFromOmopConcept(person.race_concept_id);
        }

        Extension ombCatExtension = new Extension();
        ombCatExtension.setUrl("ombCategory");
        ombCatExtension.setValue(raceCoding);

        Extension textExtension = new Extension();
        textExtension.setUrl("text");
        textExtension.setValue(new StringType(raceCoding.getDisplay()));

        Extension raceExtension = new Extension();
        raceExtension.setUrl(US_CORE_RACE_URL).addExtension(ombCatExtension).addExtension(textExtension);

        patient.addExtension(raceExtension);
    }

    private void _addEthnicityExtension(Patient patient, PersonEntity person) {
        ConceptEntity ethnicityConcept = ConceptDModel.INSTANCE.getConcept(person.ethnicity_concept_id);

        Coding ethnicityCoding;
        if (ethnicityConcept == null) {
            ethnicityCoding = FO_CODE_DM.getFhirCodingFromOmopSourceValue(person.ethnicity_source_value);
        } else {
            ethnicityCoding = FO_CODE_DM.getFhirCodingFromOmopConcept(person.ethnicity_concept_id);
        }

        Extension ombCatExtension = new Extension();
        ombCatExtension.setUrl("ombCategory");
        ombCatExtension.setValue(ethnicityCoding);

        Extension textExtension = new Extension();
        textExtension.setUrl("text");
        textExtension.setValue(new StringType(ethnicityCoding.getDisplay()));

        Extension ethnicityExtension = new Extension();
        ethnicityExtension.setUrl(US_CORE_ETHNICITY_URL).addExtension(ombCatExtension).addExtension(textExtension);

        patient.addExtension(ethnicityExtension);
    }
}
