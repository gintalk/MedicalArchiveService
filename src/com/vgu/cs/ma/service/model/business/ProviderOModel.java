package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.engine.entity.ProviderEntity;
import com.vgu.cs.ma.service.model.data.ProviderDModel;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;

public class ProviderOModel extends FhirOmopModel {

    public static final ProviderOModel INSTANCE = new ProviderOModel();

    private ProviderOModel() {

    }

    public Reference getReference(int providerId) {
        Reference reference = new Reference(new IdType("Practitioner", String.valueOf(providerId)));

        ProviderEntity provider = ProviderDModel.INSTANCE.getProvider(providerId);
        if (provider == null) {
            return reference;
        }
        reference.setDisplay(provider.provider_name);

        return reference;
    }
}
