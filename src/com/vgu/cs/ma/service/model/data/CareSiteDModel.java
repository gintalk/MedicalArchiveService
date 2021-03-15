package com.vgu.cs.ma.service.model.data;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 14/03/2021
 */

import com.vgu.cs.common.logger.VLogger;
import com.vgu.cs.engine.dal.CareSiteDal;
import com.vgu.cs.engine.entity.CareSiteEntity;
import org.apache.logging.log4j.Logger;

public class CareSiteDModel {

    public static final CareSiteDModel INSTANCE = new CareSiteDModel();
    private static final Logger LOGGER = VLogger.getLogger(CareSiteDModel.class);
    private final CareSiteDal DAL;

    private CareSiteDModel() {
        DAL = CareSiteDal.INSTANCE;
    }

    public CareSiteEntity getCareSite(int careSiteId) {
        return DAL.get(careSiteId);
    }
}
