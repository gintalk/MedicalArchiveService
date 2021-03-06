package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 06/03/2021
 */

import org.hl7.fhir.dstu3.model.Identifier;

public class ProviderModel {

    public static final ProviderModel INSTANCE = new ProviderModel();
    private final String PROVIDER_IDENTIFIER_SYSTEM;

    private ProviderModel(){
        PROVIDER_IDENTIFIER_SYSTEM = "https://www.licensed-pratitioner.com/practitioner-id-lookup";
    }

    public Identifier getIdentifier(int providerId){
        return new Identifier().setSystem(PROVIDER_IDENTIFIER_SYSTEM).setValue(String.valueOf(providerId));
    }
}
