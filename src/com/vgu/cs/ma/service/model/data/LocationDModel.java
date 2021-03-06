package com.vgu.cs.ma.service.model.data;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 06/03/2021
 */

import com.vgu.cs.common.logger.VLogger;
import com.vgu.cs.engine.dal.LocationDal;
import com.vgu.cs.engine.entity.LocationEntity;
import org.apache.logging.log4j.Logger;

public class LocationDModel {

    public static final LocationDModel INSTANCE = new LocationDModel();
    private static final Logger LOGGER = VLogger.getLogger(LocationDModel.class);

    private LocationDModel() {

    }

    public LocationEntity getLocation(int locationId) {
        return LocationDal.INSTANCE.get(locationId);
    }
}
