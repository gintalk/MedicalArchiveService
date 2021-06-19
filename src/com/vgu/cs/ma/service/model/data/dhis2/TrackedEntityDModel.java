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
import java.util.List;
import java.util.Map;

public class TrackedEntityDModel extends Dhis2BaseDModel {

    public static final TrackedEntityDModel INSTANCE = new TrackedEntityDModel();

    private TrackedEntityDModel() {

    }

    /**
     * Paging is not yet supported, only the first 50 results are returned
     *
     * @param orgUnitId: ID of the responsible Organisation Unit
     * @return List of Tracked Entity Instances associated with the Organisation Unit identified by <code>orgUnitId</code>
     */
    public List<TrackedEntityInstance> getTrackedEntityInstances(String orgUnitId) {
        Map<String, String> query = new HashMap<>();
        query.put("ou", orgUnitId);

        return getJsonList("trackedEntityInstances", query, TrackedEntityInstances.class).getTrackedEntityInstances();
    }

    /**
     * @param tei: ID of the Tracked Entity Instance
     * @return The Tracked Entity Instance identified by <code>tei</code>
     */
    public TrackedEntityInstance getTrackedEntityInstance(String tei, String programId) {
        Map<String, String> query = new HashMap<>();
        query.put("trackedEntityInstance", tei);
        query.put("program", programId);

        TrackedEntityInstances instances = getJsonList("trackedEntityInstances", query, TrackedEntityInstances.class);
        if (instances == null || CollectionUtils.isNullOrEmpty(instances.getTrackedEntityInstances())) {
            return null;
        }

        return instances.getTrackedEntityInstances().get(0);
    }
}
