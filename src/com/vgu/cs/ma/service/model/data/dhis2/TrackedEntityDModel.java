package com.vgu.cs.ma.service.model.data.dhis2;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 13/05/2021
 */

import com.vgu.cs.common.util.CollectionUtils;
import com.vgu.cs.engine.entity.dhis2.model.TrackedEntityInstance;
import com.vgu.cs.engine.entity.dhis2.model.TrackedEntityInstances;

import java.util.HashMap;
import java.util.Map;

public class TrackedEntityDModel extends Dhis2BaseModel {

    public static final TrackedEntityDModel INSTANCE = new TrackedEntityDModel();

    private TrackedEntityDModel() {

    }

    public TrackedEntityInstances getTrackedEntityInstances(String orgUnitId) {
        Map<String, String> query = new HashMap<>();
        query.put("ou", orgUnitId);

        return get("trackedEntityInstances", query, TrackedEntityInstances.class);
    }

    public TrackedEntityInstance getTrackedEntityInstance(String tei){
        Map<String, String> query = new HashMap<>();
        query.put("trackedEntityInstance", tei);

        TrackedEntityInstances instances = get("trackedEntityInstances", query, TrackedEntityInstances.class);
        if(instances == null || CollectionUtils.isNullOrEmpty(instances.trackedEntityInstances)){
            return null;
        }

        return instances.trackedEntityInstances.get(0);
    }
}
