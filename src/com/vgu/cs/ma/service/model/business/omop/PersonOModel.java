package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;

public class PersonOModel {

    public static final PersonOModel INSTANCE = new PersonOModel();

    private PersonOModel() {

    }

    public Reference getReference(int personId) {
        return new Reference(new IdType("Patient", String.valueOf(personId)));
    }
}
