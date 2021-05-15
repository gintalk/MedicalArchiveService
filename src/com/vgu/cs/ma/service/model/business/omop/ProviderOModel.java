package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.omop.ProviderEntity;
import com.vgu.cs.ma.service.model.data.omop.ConceptDModel;
import com.vgu.cs.ma.service.model.data.omop.ProviderDModel;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;

public class ProviderOModel {
    
    public static final ProviderOModel INSTANCE = new ProviderOModel();
    
    private ProviderOModel() {
    
    }
    
    /**
     * Unretained fields include:
     * - specialty_concept_id
     * - care_site_id
     * - specialty_source_value
     * - specialty_source_concept_id
     * - gender_source_value
     * - gender_source_concept_id
     */
    public ProviderEntity constructOmop(Practitioner practitioner) {
        ProviderEntity provider = new ProviderEntity();
        
        _setId(practitioner, provider);
        _setName(practitioner, provider);
        _setNpiAndSourceValue(practitioner, provider);
        _setDea(practitioner, provider);
        _setYearOfBirth(practitioner, provider);
        _setGender(practitioner, provider);
        
        return provider;
    }
    
    public Reference getReference(int providerId) {
        Reference reference = new Reference(new IdType("Practitioner", String.valueOf(providerId)));
        
        ProviderEntity provider = ProviderDModel.INSTANCE.getProvider(providerId);
        if (provider == null) {
            return reference;
        }
        
        reference.setId(String.valueOf(providerId));
        reference.setDisplay(provider.provider_name);
        
        return reference;
    }
    
    public int getProviderIdFromReference(Reference reference) {
        if (reference == null) {
            return 0;
        }
        return ConvertUtils.toInteger(reference.getId());
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * PROVIDER.provider_id = Practitioner.id
     */
    private void _setId(Practitioner practitioner, ProviderEntity provider) {
        provider.provider_id = ConvertUtils.toInteger(practitioner.getId());
    }
    
    /**
     * PROVIDER.provider_name = Practitioner.name[0].text
     */
    private void _setName(Practitioner practitioner, ProviderEntity provider) {
        provider.provider_name = practitioner.getNameFirstRep().getText();
    }
    
    /**
     * PROVIDER.npi = Practitioner.identifier[0].value
     */
    private void _setNpiAndSourceValue(Practitioner practitioner, ProviderEntity provider) {
        provider.npi = practitioner.getIdentifierFirstRep().getValue();
        provider.provider_source_value = practitioner.getIdentifierFirstRep().getId();
    }
    
    /**
     * PROVIDER.dea = Practitioner.qualification[0].code.text
     */
    private void _setDea(Practitioner practitioner, ProviderEntity provider) {
        provider.dea = practitioner.getQualificationFirstRep().getCode().getText();
    }
    
    /**
     * PROVIDER.year_of_birth = Practitioner.birthDate
     */
    private void _setYearOfBirth(Practitioner practitioner, ProviderEntity provider) {
        provider.year_of_birth = ConvertUtils.toInteger(DateTimeUtils.getString("YYYY", practitioner.getBirthDate()));
    }
    
    /**
     * Maps Practitioner.gender to PROVIDER.gender_concept_id
     */
    private void _setGender(Practitioner practitioner, ProviderEntity provider) {
        Enumerations.AdministrativeGender gender = practitioner.getGender();
        if (gender == null || gender == Enumerations.AdministrativeGender.NULL) {
            provider.gender_concept_id = 0;
        } else {
            provider.gender_concept_id = ConceptDModel.INSTANCE.getGenderConceptId(gender.name());
        }
    }
}
