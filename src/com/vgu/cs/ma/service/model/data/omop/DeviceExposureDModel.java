package com.vgu.cs.ma.service.model.data.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 08/05/2021
 */

import com.vgu.cs.engine.dal.DeviceExposureDal;
import com.vgu.cs.engine.entity.omop.DeviceExposureEntity;

public class DeviceExposureDModel {

    public static final DeviceExposureDModel INSTANCE = new DeviceExposureDModel();

    private DeviceExposureDModel(){

    }

    public DeviceExposureEntity getDeviceExposure(int deviceExposureId){
        return DeviceExposureDal.INSTANCE.get(deviceExposureId);
    }
}
