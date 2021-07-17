package com.vgu.cs.ma.service.model.business.dhis2;

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.dhis2.model.TrackedEntityInstance;
import org.apache.commons.text.WordUtils;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.Date;

public class TrackedEntityInstanceModel {

    public static final TrackedEntityInstanceModel INSTANCE = new TrackedEntityInstanceModel();

    private TrackedEntityInstanceModel() {

    }

    public TrackedEntityInstance constructDhis2(Patient patient) {
        TrackedEntityInstance tei = new TrackedEntityInstance();

        _setId(tei, patient);
        _setBirthDate(tei, patient);
        _setName(tei, patient);
        _setGender(tei, patient);
        _setAddress(tei, patient);

        return tei;
    }

    private void _setId(TrackedEntityInstance tei, Patient patient) {
        Identifier identifier = patient.getIdentifierFirstRep();
        if (identifier == null) {
            return;
        }

        tei.setTrackedEntityInstance(identifier.getId());
        tei.setUniqueIdAttribute(identifier.getValue());
    }

    private void _setBirthDate(TrackedEntityInstance tei, Patient patient) {
        Date birthDate = patient.getBirthDate();
        if (birthDate == null) {
            return;
        }

        tei.setBirthDateAttribute(DateTimeUtils.getDateString(birthDate));
    }

    private void _setName(TrackedEntityInstance tei, Patient patient) {
        HumanName name = patient.getNameFirstRep();
        if (name == null) {
            return;
        }

        if (name.hasGiven()) {
            tei.setFirstNameAttribute(name.getGiven().get(0).getValue());
        }
        tei.setLastNameAttribute(name.getFamily());
    }

    private void _setGender(TrackedEntityInstance tei, Patient patient) {
        Enumerations.AdministrativeGender gender = patient.getGender();
        if (gender == null) {
            return;
        }

        tei.setGenderAttribute(WordUtils.capitalizeFully(gender.getDisplay()));
    }

    private void _setAddress(TrackedEntityInstance tei, Patient patient) {
        Address address = patient.getAddressFirstRep();
        if (address == null) {
            return;
        }

        if (address.hasLine()) {
            tei.setAddressAttribute(address.getLine().get(0).toString());
        }
        tei.setCityAttribute(address.getCity());
        tei.setZipCodeAttribute(address.getPostalCode());
    }
}
