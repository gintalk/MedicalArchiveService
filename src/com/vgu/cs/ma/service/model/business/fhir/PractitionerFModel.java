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
import com.vgu.cs.ma.service.util.CodeableConceptUtil;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Practitioner.PractitionerQualificationComponent;

public class PractitionerFModel  {

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

    private void _addId(Practitioner practitioner, ProviderEntity provider) {
        practitioner.setId(new IdType(provider.provider_id));
    }

    private void _addName(Practitioner practitioner, ProviderEntity provider) {
        practitioner.addName(new HumanName().setText(provider.provider_name));
    }

    private void _addIdentifier(Practitioner practitioner, ProviderEntity provider) {
        practitioner.addIdentifier(new Identifier().setValue(provider.npi));
    }

    private void _addQualification(Practitioner practitioner, ProviderEntity provider) {
        CodeableConcept deaCodeable = CodeableConceptUtil.fromText(provider.dea);
        if (deaCodeable == null) {
            return;
        }
        practitioner.addQualification(new PractitionerQualificationComponent(deaCodeable));
    }

    private void _addBirthDate(Practitioner practitioner, ProviderEntity provider) {
        practitioner.setBirthDate(DateTimeUtils.parse("YYYY", String.valueOf(provider.year_of_birth)));
    }

    private void _addGender(Practitioner practitioner, ProviderEntity provider) {
        ConceptEntity genderConcept = ConceptDModel.INSTANCE.getConcept(provider.gender_concept_id);
        if (genderConcept == null) {
            practitioner.setGender(Enumerations.AdministrativeGender.NULL);
        } else {
            practitioner.setGender(Enumerations.AdministrativeGender.fromCode(genderConcept.concept_name.toLowerCase()));
        }
    }
}
