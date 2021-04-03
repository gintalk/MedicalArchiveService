package com.vgu.cs.ma.service.model.business.fhir;

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.ConceptEntity;
import com.vgu.cs.engine.entity.PersonEntity;
import com.vgu.cs.ma.service.model.business.omop.LocationOModel;
import com.vgu.cs.ma.service.model.business.omop.ProviderOModel;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import com.vgu.cs.ma.service.model.data.FhirOmopCodeMapDModel;
import com.vgu.cs.ma.service.model.data.FhirOmopVocabularyMapDModel;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;

import java.util.Calendar;

/**
 * <p>
 * The class <code>PatientFModel</code> constructs <code>Patient</code>> from any record in the OMOP-compliant
 * table <code>person</code>
 * </p>
 * <p>
 * The <code>Patient</code> class contains one single public method accepting a <code>PersonEntity</code>, which
 * represents a record in <code>person</code>, and returns a FHIR-compliant <code>Patient</code>.
 * </p>
 *
 * @author namnh16 on 05/03/2021
 * @see <a href="http://build.fhir.org/ig/HL7/cdmh/profiles.html#omop-to-fhir-mappings">OMOP to FHIR mappings</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#PERSON">OMOP PERSON</a>
 * @see <a href="https://www.hl7.org/fhir/patient.html">FHIR Patient</a>
 */
public class PatientFModel {

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

    /**
     * Corresponding FHIR field: Patient.identifier
     * Contain Person.person_id and the original system in which the ID can be used to identify the patient
     */
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
            String fhirSystem = FhirOmopVocabularyMapDModel.INSTANCE.getFhirUrlSystem(sourceValues[0]);

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

    /**
     * Corresponding FHIR field: Patient.birthDate
     * If Person.birth_datetime is present, parse it as "yyyy/MM/dd HH:mm:ss". Otherwise, use Java Calendar to
     * construct a date from Person.year_of_birth, Person.month_of_birth and Person.day_of_birth
     */
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

    /**
     * Corresponding FHIR field: Patient.generalPractitioner
     * Retrieve a reference to the person who is medically responsible for the patient from Person.provider_id
     */
    private void _addGeneralPractitionerReference(Patient patient, PersonEntity person) {
        patient.addGeneralPractitioner(ProviderOModel.INSTANCE.getReference(person.provider_id));
    }

    /**
     * Corresponding FHIR field: Patient.gender
     * Compute a gender preset from Person.gender_concept_id
     */
    private void _addGender(Patient patient, PersonEntity person) {
        ConceptEntity genderConcept = ConceptDModel.INSTANCE.getConcept(person.gender_concept_id);
        if (genderConcept == null) {
            patient.setGender(AdministrativeGender.NULL);
        } else {
            patient.setGender(AdministrativeGender.fromCode(genderConcept.concept_name.toLowerCase()));
        }
    }

    /**
     * Corresponding FHIR field: Patient.address
     * Rely on <code>LocationOModel</code> to construct address data from Person.location_id
     */
    private void _addAddress(Patient patient, PersonEntity person) {
        patient.addAddress(LocationOModel.INSTANCE.getAddress(person.location_id));
    }

    /**
     * Corresponding FHIR field: Patient.extension: us-core-race
     *
     * @see <a href="https://www.hl7.org/fhir/us/core/StructureDefinition-us-core-race.html">Structure Definition: us-core-race</a>
     */
    private void _addRaceExtension(Patient patient, PersonEntity person) {
        ConceptEntity raceConcept = ConceptDModel.INSTANCE.getConcept(person.race_concept_id);

        Coding raceCoding;
        if (raceConcept == null) {
            raceCoding = FhirOmopCodeMapDModel.INSTANCE.getFhirCodingFromOmopSourceValue(person.race_source_value);
        } else {
            raceCoding = FhirOmopCodeMapDModel.INSTANCE.getFhirCodingFromOmopConcept(person.race_concept_id);
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

    /**
     * Corresponding FHIR field: Patient.extension: us-core-ethnicity
     *
     * @see <a href="https://www.hl7.org/fhir/us/core/StructureDefinition-us-core-ethnicity.html>Structure Definition: us-core-ethnicity</a>
     */
    private void _addEthnicityExtension(Patient patient, PersonEntity person) {
        ConceptEntity ethnicityConcept = ConceptDModel.INSTANCE.getConcept(person.ethnicity_concept_id);

        Coding ethnicityCoding;
        if (ethnicityConcept == null) {
            ethnicityCoding = FhirOmopCodeMapDModel.INSTANCE.getFhirCodingFromOmopSourceValue(person.ethnicity_source_value);
        } else {
            ethnicityCoding = FhirOmopCodeMapDModel.INSTANCE.getFhirCodingFromOmopConcept(person.ethnicity_concept_id);
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
