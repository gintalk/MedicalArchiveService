package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 06/03/2021
 */

import com.vgu.cs.engine.entity.ProviderEntity;
import com.vgu.cs.ma.service.model.data.ProviderDModel;
import org.hl7.fhir.dstu3.model.Identifier;

public class ProviderModel {

    public static final ProviderModel INSTANCE = new ProviderModel();
    private final ProviderDModel D_MODEL;
    private final String PROVIDER_IDENTIFIER_SYSTEM;

    private ProviderModel() {
        D_MODEL = ProviderDModel.INSTANCE;
        PROVIDER_IDENTIFIER_SYSTEM = "https://www.licensed-pratitioner.com/practitioner-id-lookup";
    }

    public ProviderEntity getProvider(int providerId) {
        return D_MODEL.getProvider(providerId);
    }

    public Identifier getIdentifier(int providerId) {
        return new Identifier().setSystem(PROVIDER_IDENTIFIER_SYSTEM).setValue(String.valueOf(providerId));
    }
}
