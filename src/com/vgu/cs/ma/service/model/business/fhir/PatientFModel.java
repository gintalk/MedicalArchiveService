package com.vgu.cs.ma.service.model.business.fhir;

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.dhis2.model.TrackedEntityInstance;
import com.vgu.cs.engine.entity.omop.ConceptEntity;
import com.vgu.cs.engine.entity.omop.PersonEntity;
import com.vgu.cs.ma.service.model.business.omop.LocationOModel;
import com.vgu.cs.ma.service.model.business.omop.ProviderOModel;
import com.vgu.cs.ma.service.model.data.omop.ConceptDModel;
import com.vgu.cs.ma.service.model.data.omop.FhirOmopCodeMapDModel;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.StringType;

import java.util.Calendar;
import java.util.Collections;

/**
 * <p>
 * Information about an individual or animal receiving health care services.
 * </p>
 * <p>
 * The class <code>PatientFModel</code> constructs <code>Patient</code>> from any record in the OMOP-compliant
 * table <code>person</code>.
 * </p>
 * <p>
 * The <code>PatientFModel</code> class contains one single public method accepting a <code>PersonEntity</code>, which
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

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Public
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Patient constructFhir(PersonEntity person) {
        Patient patient = new Patient();

        _addIdentifier(patient, person.person_id, person.person_source_value);
        _addBirthDate(patient, person);
        _addGeneralPractitionerReference(patient, person);
        _addGender(patient, person);
        _addAddress(patient, person);
        _addRaceExtension(patient, person);
        _addEthnicityExtension(patient, person);

        return patient;
    }

    public Patient constructFhir(TrackedEntityInstance tei) {
        Patient patient = new Patient();

        _addIdentifier(patient, ConvertUtils.toInteger(tei.getUniqueIdAttribute().getValue()), tei.getId());
        _addName(patient, tei.getFirstNameAttribute().getValue(), tei.getLastNameAttribute().getValue());
        _addAddress(patient, tei.getAddressAttribute().getValue(), tei.getCityAttribute().getValue());

        return patient;
    }

    public String getUsCoreRaceUrl() {
        return US_CORE_RACE_URL;
    }

    public String getUsCoreEthnicityUrl() {
        return US_CORE_ETHNICITY_URL;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Corresponding FHIR field: Patient.identifier
     * An identifier for this patient. It is assumed that every person with a different unique identifier is in fact a
     * different person and should be treated independently. PERSON.person_source_value is used to link back to persons
     * in the source data.
     */
    private void _addIdentifier(Patient patient, int personId, String personSourceValue) {
        Identifier identifier = new Identifier();
        patient.addIdentifier(identifier);

        identifier.setValue(String.valueOf(personId));
        if (!StringUtils.isNullOrEmpty(personSourceValue)) {
            identifier.setId(personSourceValue);
        }
    }

    /**
     * Corresponding FHIR field: Patient.birthDate
     * The date of birth for the individual.
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
     * Patient's nominated primary care provider. PERSON.provider_id refers to the last known primary care provider.
     */
    private void _addGeneralPractitionerReference(Patient patient, PersonEntity person) {
        if (person.provider_id <= 0) {
            return;
        }
        patient.addGeneralPractitioner(ProviderOModel.INSTANCE.getReference(person.provider_id));
    }

    /**
     * Corresponding FHIR field: Patient.gender
     * PERSON.gender_concept_id is meant to capture the biological sex at birth of the Person.
     *
     * @see <a href="https://www.hl7.org/fhir/valueset-administrative-gender.html">Available values for gender</a>
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
     * An address for the individual. PERSON.location_id refers to the physical address of the person. This field
     * should capture the last known location of the person.
     */
    private void _addAddress(Patient patient, PersonEntity person) {
        patient.addAddress(LocationOModel.INSTANCE.getAddress(person.location_id));
    }

    private void _addAddress(Patient patient, String addressLine, String city) {
        Address address = new Address();
        address.setLine(Collections.singletonList(new StringType(addressLine)));
        address.setCity(city);

        patient.addAddress(address);
    }

    /**
     * Corresponding FHIR field: Patient.name
     * A name associated with the patient
     */
    private void _addName(Patient patient, String firstName, String lastName) {
        HumanName name = new HumanName();
        name.setFamily(lastName);
        name.setGiven(Collections.singletonList(new StringType(firstName)));

        patient.addName(name);
    }

    /**
     * Corresponding FHIR field: Patient.extension: us-core-race
     * PERSON.race_concept_id captures race or ethnic background of the person. PERSON.race_source_value is used to
     * store the race of the person from the source data and should be used for reference only.
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
     * PERSON.ethnicity_concept_id captures Ethnicity as defined by the Office of Management and Budget (OMB) of the US
     * Government. PERSON.ethnicity_source_value is used to store the ethnicity of the person from the source data and
     * should be used for reference only.
     *
     * @see <a href="https://www.hl7.org/fhir/us/core/StructureDefinition-us-core-ethnicity.html>Structure Definition: us-core-ethnicity</a>
     */
    private void _addEthnicityExtension(Patient patient, PersonEntity person) {
        ConceptEntity ethnicityConcept = ConceptDModel.INSTANCE.getConcept(person.ethnicity_concept_id);

        Coding ethnicityCoding;
        if (ethnicityConcept == null) {
            ethnicityCoding = FhirOmopCodeMapDModel.INSTANCE.getFhirCodingFromOmopSourceValue(person.ethnicity_source_value);
        } else {
            ethnicityCoding = FhirOmopCodeMapDModel.INSTANCE.getFhirCodingFromOmopConcept(ethnicityConcept.concept_id);
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
