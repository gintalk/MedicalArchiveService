package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 10/03/2021
 */

import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;

public class LocationModel {

    public static LocationModel INSTANCE = new LocationModel();
    private final String US_CORE_LOCATION_URL;

    private LocationModel() {
        US_CORE_LOCATION_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-location";
    }

    public Identifier getIdentifier(int id) {
        return new Identifier().setSystem(US_CORE_LOCATION_URL).setValue(String.valueOf(id));
    }

    public Reference getReference(int id) {
        return new Reference().setIdentifier(getIdentifier(id));
    }
}
