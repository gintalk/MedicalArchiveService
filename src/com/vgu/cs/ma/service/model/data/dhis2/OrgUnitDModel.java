package com.vgu.cs.ma.service.model.data.dhis2;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 22/05/2021
 */

import com.vgu.cs.common.util.CollectionUtils;
import com.vgu.cs.engine.entity.dhis2.model.OrganisationUnit;
import com.vgu.cs.engine.entity.dhis2.model.OrganisationUnits;

import java.util.HashMap;
import java.util.Map;

public class OrgUnitDModel extends Dhis2BaseDModel {

    public static final OrgUnitDModel INSTANCE = new OrgUnitDModel();

    private OrgUnitDModel() {

    }

    public OrganisationUnit getOrgUnit(String orgUnitId) {
        Map<String, String> query = new HashMap<>();
        query.put("query", orgUnitId);

        OrganisationUnits orgUnits = getJsonList("organisationUnits", query, OrganisationUnits.class);
        if (orgUnits == null || CollectionUtils.isNullOrEmpty(orgUnits.getOrgUnits())) {
            return null;
        }

        return orgUnits.getOrgUnits().get(0);
    }
}
