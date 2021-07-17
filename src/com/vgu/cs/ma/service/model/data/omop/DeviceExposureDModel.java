package com.vgu.cs.ma.service.model.data.omop;

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
