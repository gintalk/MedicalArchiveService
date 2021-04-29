package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.ConceptEntity;
import com.vgu.cs.engine.entity.ProviderEntity;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Practitioner.PractitionerQualificationComponent;

/**
 * <p>
 * A person who is directly or indirectly involved in the provisioning of healthcare.
 * </p>
 * <p>
 * The class <code>PractitionerFModel</code> constructs <code>Practitioner</code>> from any record in the OMOP-compliant
 * table <code>provider</code>.
 * </p>
 * <p>
 * The <code>PractitionerFModel</code> class contains one single public method accepting a <code>ProviderEntity</code>, which
 * represents a record in <code>provider</code>, and returns a FHIR-compliant <code>Practitioner</code>.
 * </p>
 *
 * @author namnh16 on 05/03/2021
 * @see <a href="http://build.fhir.org/ig/HL7/cdmh/profiles.html#omop-to-fhir-mappings">OMOP to FHIR mappings</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#PROVIDER">OMOP PROVIDER</a>
 * @see <a href="https://www.hl7.org/fhir/practitioner.html">FHIR Practitioner</a>
 */
public class PractitionerFModel {

    public static final PractitionerFModel INSTANCE = new PractitionerFModel();

    private PractitionerFModel() {

    }

    public Practitioner constructFhir(ProviderEntity provider) {
        Practitioner practitioner = new Practitioner();

        _addId(practitioner, provider);
        _addName(practitioner, provider);
        _addIdentifier(practitioner, provider);
        _addQualification(practitioner, provider);
        _addBirthDate(practitioner, provider);
        _addGender(practitioner, provider);

        return practitioner;
    }

    /**
     * Corresponding FHIR field: Practitioner.id
     * It is assumed that every provider with a different unique identifier is in fact a different person and should be
     * treated independently.
     */
    private void _addId(Practitioner practitioner, ProviderEntity provider) {
        practitioner.setId(new IdType(provider.provider_id));
    }

    /**
     * Corresponding FHIR field: Practitioner.name
     * The name(s) associated with the practitioner.
     */
    private void _addName(Practitioner practitioner, ProviderEntity provider) {
        practitioner.addName(new HumanName().setText(provider.provider_name));
    }

    /**
     * Corresponding FHIR field: Practitioner.identifier
     * An identifier for the person as this agent. PROVIDER.npi is the National Provider Number issued to health care
     * providers in the US by the Centers for Medicare and Medicaid Services (CMS).
     */
    private void _addIdentifier(Practitioner practitioner, ProviderEntity provider) {
        Identifier identifier  = new Identifier();
        identifier.setValue(provider.npi);
        identifier.setId(provider.provider_source_value);
        
        practitioner.addIdentifier(identifier);
    }

    /**
     * Corresponding FHIR field: Practitioner.qualification
     * Certification, licenses, or training pertaining to the provision of care. PROVIDER.dea is the identifier issued
     * by the DEA, a US federal agency, that allows a provider to write prescriptions for controlled substances.
     */
    private void _addQualification(Practitioner practitioner, ProviderEntity provider) {
        practitioner.addQualification(new PractitionerQualificationComponent(CodeableConceptUtils.fromText(provider.dea)));
    }

    /**
     * Corresponding FHIR field: Practitioner.birthDate
     * The date on which the practitioner was born.
     */
    private void _addBirthDate(Practitioner practitioner, ProviderEntity provider) {
        practitioner.setBirthDate(DateTimeUtils.parse("YYYY", String.valueOf(provider.year_of_birth)));
    }

    /**
     * Corresponding FHIR field: Practitioner.gender
     * PROVIDER.gender_concept_id represents the recorded gender of the provider in the source data.
     *
     * @see <a href="https://www.hl7.org/fhir/valueset-administrative-gender.html">Available values for gender</a>
     */
    private void _addGender(Practitioner practitioner, ProviderEntity provider) {
        ConceptEntity genderConcept = ConceptDModel.INSTANCE.getConcept(provider.gender_concept_id);
        if (genderConcept == null) {
            practitioner.setGender(Enumerations.AdministrativeGender.NULL);
        } else {
            practitioner.setGender(Enumerations.AdministrativeGender.fromCode(genderConcept.concept_name.toLowerCase()));
        }
    }
}
