package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;

public class VisitOccurrenceOModel {

    public static final VisitOccurrenceOModel INSTANCE = new VisitOccurrenceOModel();

    private VisitOccurrenceOModel() {

    }

    public Reference getReference(int visitOccurrenceId) {
        return new Reference(new IdType("Encounter", String.valueOf(visitOccurrenceId)));
    }
}
