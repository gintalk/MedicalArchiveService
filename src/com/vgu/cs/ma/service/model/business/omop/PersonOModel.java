package com.vgu.cs.ma.service.model.business.omop;

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.omop.PersonEntity;
import com.vgu.cs.ma.service.model.business.fhir.PatientFModel;
import com.vgu.cs.ma.service.model.data.omop.ConceptDModel;
import org.hl7.fhir.dstu3.model.*;

import java.util.Date;

public class PersonOModel {
    
    public static final PersonOModel INSTANCE = new PersonOModel();
    
    private PersonOModel() {
    
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Public
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public PersonEntity constructOmop(Patient patient) {
        PersonEntity person = new PersonEntity();
        
        _setIdAndSourceValue(patient, person);
        _setBirthDate(patient, person);
        _setProviderId(patient, person);
        _setGender(patient, person);
        _setAddress(patient, person);
        _setRace(patient, person);
        _setEthnicity(patient, person);
        
        return person;
    }
    
    public Reference getReference(int personId) {
        Reference reference = new Reference(new IdType("Patient", String.valueOf(personId)));
        reference.setId(String.valueOf(personId));
        
        return reference;
    }
    
    public int getIdFromReference(Reference reference) {
        if(reference == null){
            return 0;
        }
        return ConvertUtils.toInteger(reference.getId());
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * PERSON.person_id = Patient.identifier.value
     * PERSON.person_source_value = Patient.identifier.id
     */
    private void _setIdAndSourceValue(Patient patient, PersonEntity person) {
        Identifier identifier = patient.getIdentifierFirstRep();
        if (identifier == null) {
            return;
        }
        
        person.person_id = ConvertUtils.toInteger(identifier.getValue());
        person.person_source_value = identifier.getId();
    }
    
    /**
     * Maps Patient.birthDate to PERSON's year_of_birth, month_of_birth, day_of_birth
     */
    private void _setBirthDate(Patient patient, PersonEntity person) {
        Date birthDate = patient.getBirthDate();
        if (birthDate == null) {
            return;
        }
        
        person.year_of_birth = DateTimeUtils.getYear(birthDate);
        person.month_of_birth = DateTimeUtils.getMonth(birthDate);
        person.day_of_birth = DateTimeUtils.getDayOfMonth(birthDate);
    }
    
    /**
     * PERSON.provider_id = Patient.generalPractitioner[0].id
     */
    private void _setProviderId(Patient patient, PersonEntity person) {
        Reference generalPractitionerReference = patient.getGeneralPractitionerFirstRep();
        if (generalPractitionerReference == null || StringUtils.isNullOrEmpty(generalPractitionerReference.getId())) {
            return;
        }
        
        person.provider_id = ConvertUtils.toInteger(generalPractitionerReference.getId());
    }
    
    /**
     * Maps Patient.gender to PERSON.gender_concept_id
     */
    private void _setGender(Patient patient, PersonEntity person) {
        Enumerations.AdministrativeGender gender = patient.getGender();
        if (gender == null || gender == Enumerations.AdministrativeGender.NULL) {
            person.gender_concept_id = 0;
        } else {
            person.gender_concept_id = ConceptDModel.INSTANCE.getGenderConceptId(gender.name());
        }
    }
    
    /**
     * Person.location_id = Patient.address[0].id
     */
    private void _setAddress(Patient patient, PersonEntity person) {
        Address address = patient.getAddressFirstRep();
        if (address == null) {
            person.location_id = 0;
        } else {
            person.location_id = ConvertUtils.toInteger(address.getId());
        }
    }
    
    /**
     * Maps Patient.extension (us-core-race) to PERSON's race_concept_id and race_source_value
     */
    private void _setRace(Patient patient, PersonEntity person) {
        Extension ombCatExtension = null;
        for (Extension extension : patient.getExtension()) {
            if (!PatientFModel.INSTANCE.getUsCoreRaceUrl().equals(extension.getUrl())) {
                continue;
            }
            
            for (Extension innerExtension : extension.getExtension()) {
                if ("ombCategory".equals(innerExtension.getUrl())) {
                    ombCatExtension = innerExtension;
                    break;
                }
            }
            if (ombCatExtension != null) {
                break;
            }
        }
        if (ombCatExtension == null) {
            return;
        }
        
        Coding raceCoding = (Coding) ombCatExtension.getValue();
        if (raceCoding == null) {
            return;
        }
        
        person.race_concept_id = ConvertUtils.toInteger(raceCoding.getId());
        person.race_source_value = raceCoding.getDisplay();
    }
    
    /**
     * Maps Patient.extension (us-core-ethnicity) to PERSON's ethnicity_concept_id and ethnicity_source_value
     */
    private void _setEthnicity(Patient patient, PersonEntity person) {
        Extension ombCatExtension = null;
        for (Extension extension : patient.getExtension()) {
            if (!PatientFModel.INSTANCE.getUsCoreEthnicityUrl().equals(extension.getUrl())) {
                continue;
            }
            
            for (Extension innerExtension : extension.getExtension()) {
                if ("ombCategory".equals(innerExtension.getUrl())) {
                    ombCatExtension = innerExtension;
                    break;
                }
            }
            if (ombCatExtension != null) {
                break;
            }
        }
        if (ombCatExtension == null) {
            return;
        }
        
        Coding raceCoding = (Coding) ombCatExtension.getValue();
        if (raceCoding == null) {
            return;
        }
        
        person.ethnicity_concept_id = ConvertUtils.toInteger(raceCoding.getId());
        person.ethnicity_source_value = raceCoding.getDisplay();
    }
}
