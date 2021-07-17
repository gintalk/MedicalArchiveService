package com.vgu.cs.ma.service.model.data.omop;

import com.vgu.cs.common.logger.VLogger;
import com.vgu.cs.engine.dal.LocationDal;
import com.vgu.cs.engine.entity.omop.LocationEntity;
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
