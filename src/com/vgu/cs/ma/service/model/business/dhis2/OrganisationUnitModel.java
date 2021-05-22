package com.vgu.cs.ma.service.model.business.dhis2;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 22/05/2021
 */

import com.vgu.cs.engine.entity.dhis2.model.OrganisationUnit;
import org.hl7.fhir.dstu3.model.Location;

public class OrganisationUnitModel {

    public static final OrganisationUnitModel INSTANCE = new OrganisationUnitModel();

    private OrganisationUnitModel() {

    }

    public OrganisationUnit constructDhis2(Location location) {
        OrganisationUnit orgUnit = new OrganisationUnit();

        _setId(orgUnit, location);
        _setName(orgUnit, location);


        return orgUnit;
    }

    private void _setId(OrganisationUnit orgUnit, Location location) {
        orgUnit.setId(location.getId());
    }

    private void _setName(OrganisationUnit orgUnit, Location location) {
        orgUnit.setName(location.getName());
    }
}
