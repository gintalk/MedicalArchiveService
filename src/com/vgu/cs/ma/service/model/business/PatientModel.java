package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 05/03/2021
 */

import com.vgu.cs.engine.dal.LocationDal;
import com.vgu.cs.engine.entity.ConceptEntity;
import com.vgu.cs.engine.entity.LocationEntity;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import org.hl7.fhir.dstu3.model.*;

import java.util.Calendar;
import java.util.Date;

public class PatientModel {

    public static final PatientModel INSTANCE = new PatientModel();
    private final ProviderModel PROVIDER_MODEL;
    private final String PATIENT_IDENTIFIER_SYSTEM;
    private final String US_CORE_RACE_URL;
    private final String US_CORE_ETHNICITY_URL;

    private PatientModel() {
        PROVIDER_MODEL = ProviderModel.INSTANCE;
        PATIENT_IDENTIFIER_SYSTEM = "https://www.some-mrn-center.com/mrn-lookup/v1.2";
        US_CORE_RACE_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race";
        US_CORE_ETHNICITY_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity";
    }

    public Identifier getIdentifier(int personId) {
        return new Identifier().setSystem(PATIENT_IDENTIFIER_SYSTEM).setValue(String.valueOf(personId));
    }

    public Reference getGeneralPractitionerReference(int providerId) {
        return new Reference().setIdentifier(PROVIDER_MODEL.getIdentifier(providerId));
    }

    public Enumerations.AdministrativeGender getGender(int genderConceptId) {
        ConceptEntity genderConcept = ConceptDModel.INSTANCE.getConcept(genderConceptId);
        if (genderConcept == null) {
            return Enumerations.AdministrativeGender.NULL;
        }

        if ("MALE".equalsIgnoreCase(genderConcept.concept_name)) {
            return Enumerations.AdministrativeGender.MALE;
        }
        if ("FEMALE".equalsIgnoreCase(genderConcept.concept_name)) {
            return Enumerations.AdministrativeGender.FEMALE;
        }
        return Enumerations.AdministrativeGender.OTHER;
    }

    public Date getBirthdate(int yearOfBirth, int monthOfBirth, int dayOfBirth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearOfBirth);
        calendar.set(Calendar.MONTH, monthOfBirth);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfBirth);

        return calendar.getTime();
    }

    public Address getAddress(int locationId) {
        LocationEntity location = LocationDal.INSTANCE.get(locationId);

        Address address = new Address();
        address.addLine(location.address_1 + " " + location.address_2);
        address.setCity(location.city);
        address.setState(location.state);
        address.setPostalCode(location.zip);
        address.setCountry(location.county);

        return address;
    }

    public Extension getRaceExtension(int raceConceptId) {
        ConceptEntity raceConcept = ConceptDModel.INSTANCE.getConcept(raceConceptId);

        Extension extension = new Extension();
        extension.setUrl(US_CORE_RACE_URL);
        extension.setValue(new StringType(raceConcept.concept_name));

        return extension;
    }

    public Extension getEthnicityExtension(int ethnicityConceptId) {
        ConceptEntity ethnicityConcept = ConceptDModel.INSTANCE.getConcept(ethnicityConceptId);

        Extension extension = new Extension();
        extension.setUrl(US_CORE_ETHNICITY_URL);
        extension.setValue(new StringType(ethnicityConcept.concept_name));

        return extension;
    }
}
