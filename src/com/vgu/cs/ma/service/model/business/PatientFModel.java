package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 05/03/2021
 */

import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.dal.LocationDal;
import com.vgu.cs.engine.entity.ConceptEntity;
import com.vgu.cs.engine.entity.LocationEntity;
import com.vgu.cs.engine.entity.PersonEntity;
import com.vgu.cs.engine.entity.ProviderEntity;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import com.vgu.cs.ma.service.model.data.ProviderDModel;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;

import java.util.Calendar;
import java.util.Date;

public class PatientFModel extends FhirOmopModel {

    public static final PatientFModel INSTANCE = new PatientFModel();
    private final ProviderDModel PROVIDER_DM;
    private final String PATIENT_IDENTIFIER_SYSTEM;
    private final String US_CORE_RACE_URL;
    private final String US_CORE_ETHNICITY_URL;

    private PatientFModel() {
        super();

        PROVIDER_DM = ProviderDModel.INSTANCE;
        PATIENT_IDENTIFIER_SYSTEM = "https://www.some-mrn-center.com/mrn-lookup/v1.2";
        US_CORE_RACE_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race";
        US_CORE_ETHNICITY_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity";
    }

    public Patient constructFhir(PersonEntity person) {
        Patient patient = new Patient();

        Identifier identifierFromSourceValue = getIdentifierFromSourceValue(person.person_source_value);
        if (identifierFromSourceValue != null) {
            patient.addIdentifier(identifierFromSourceValue);
        }

        Date birthDate = getBirthdate(person.year_of_birth, person.month_of_birth, person.day_of_birth);
        if (birthDate != null) {
            patient.setBirthDate(birthDate);
        }

        Reference generalPractitioner = getGeneralPractitionerReference(person.provider_id);
        if (generalPractitioner != null) {
            patient.addGeneralPractitioner(generalPractitioner);
        }

        patient.setGender(getGender(person.gender_concept_id));

        Address address = getAddress(person.location_id);
        if (address != null) {
            patient.addAddress(address);
        }

        Extension raceExtension = getRaceExtension(person.race_concept_id, person.race_source_value);
        if (raceExtension != null) {
            patient.addExtension(raceExtension);
        }

        Extension ethnicityExtension = getEthnicityExtension(person.ethnicity_concept_id, person.ethnicity_source_value);
        if (ethnicityExtension != null) {
            patient.addExtension(ethnicityExtension);
        }

        return patient;
    }

    public Identifier getIdentifierFromSourceValue(String personSourceValue) {
        if (StringUtils.isNullOrEmpty(personSourceValue)) {
            return null;
        }

        Identifier identifier = new Identifier();

        String[] sourceValues = personSourceValue.trim().split("\\^");
        if (sourceValues.length == 1) {
            identifier.setValue(sourceValues[0]);
        } else {
            String fhirSystem = FO_VOCAB_DM.getFhirSystemName(sourceValues[0]);

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
                identifier.setType(typeCodeable);

                for (int i = 2; i < sourceValues.length; i++) {
                    valueBuilder.append(sourceValues[i]);
                }
            } else {
                if ("NONE".equalsIgnoreCase(fhirSystem)) {
                    identifier.setSystem(fhirSystem);
                }
                valueBuilder.append(sourceValues[1]);
            }
            identifier.setValue(valueBuilder.toString());
        }

        return identifier;
    }

    public Reference getGeneralPractitionerReference(int providerId) {
        if (providerId < 0) {
            return null;
        }

        ProviderEntity provider = PROVIDER_DM.getProvider(providerId);
        if (provider == null) {
            return null;
        }

        Reference reference = new Reference(new IdType(providerId));
        if (!StringUtils.isNullOrEmpty(provider.provider_name)) {
            reference.setDisplay(provider.provider_name);
        }
        return reference;
    }

    public AdministrativeGender getGender(int genderConceptId) {
        ConceptEntity genderConcept = ConceptDModel.INSTANCE.getConcept(genderConceptId);
        if (genderConcept == null) {
            return AdministrativeGender.NULL;
        }

        return AdministrativeGender.fromCode(genderConcept.concept_name.toLowerCase());
    }

    public Date getBirthdate(int yearOfBirth, int monthOfBirth, int dayOfBirth) {
        if (yearOfBirth < 0 || monthOfBirth < 0 || dayOfBirth < 0) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearOfBirth);
        calendar.set(Calendar.MONTH, monthOfBirth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfBirth);

        return calendar.getTime();
    }

    public Address getAddress(int locationId) {
        return LocationOModel.INSTANCE.getAddress(locationId);
    }

    public Extension getRaceExtension(int raceConceptId, String raceSourceValue) {
        ConceptEntity raceConcept = ConceptDModel.INSTANCE.getConcept(raceConceptId);
        if (raceConcept == null && StringUtils.isNullOrEmpty(raceSourceValue)) {
            return null;
        }

        Coding raceCoding;
        if (raceConcept == null) {
            raceCoding = FO_CODE_DM.getFhirCodingFromOmopSourceValue(raceSourceValue);
        } else {
            raceCoding = FO_CODE_DM.getFhirCodingFromOmopConcept(raceConceptId);
        }

        Extension ombCatExtension = new Extension();
        ombCatExtension.setUrl("ombCategory");
        ombCatExtension.setValue(raceCoding);

        Extension textExtension = new Extension();
        textExtension.setUrl("text");
        textExtension.setValue(new StringType(raceCoding.getDisplay()));

        Extension raceExtension = new Extension();
        raceExtension.setUrl(US_CORE_RACE_URL).addExtension(ombCatExtension).addExtension(textExtension);

        return raceExtension;
    }

    public Extension getEthnicityExtension(int ethnicityConceptId, String ethnicitySourceValue) {
        ConceptEntity ethnicityConcept = ConceptDModel.INSTANCE.getConcept(ethnicityConceptId);
        if (ethnicityConcept == null && StringUtils.isNullOrEmpty(ethnicitySourceValue)) {
            return null;
        }

        Coding ethnicityCoding;
        if (ethnicityConcept == null) {
            ethnicityCoding = FO_CODE_DM.getFhirCodingFromOmopSourceValue(ethnicitySourceValue);
        } else {
            ethnicityCoding = FO_CODE_DM.getFhirCodingFromOmopConcept(ethnicityConceptId);
        }

        Extension ombCatExtension = new Extension();
        ombCatExtension.setUrl("ombCategory");
        ombCatExtension.setValue(ethnicityCoding);

        Extension textExtension = new Extension();
        textExtension.setUrl("text");
        textExtension.setValue(new StringType(ethnicityCoding.getDisplay()));

        Extension ethnicityExtension = new Extension();
        ethnicityExtension.setUrl(US_CORE_ETHNICITY_URL).addExtension(ombCatExtension).addExtension(textExtension);

        return ethnicityExtension;
    }
}
