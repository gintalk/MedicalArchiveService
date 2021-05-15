package com.vgu.cs.ma.service.model.data.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 25/04/2021
 */

import com.vgu.cs.engine.dal.MeasurementDal;
import com.vgu.cs.engine.entity.omop.MeasurementEntity;

public class MeasurementDModel {
    
    public static final MeasurementDModel INSTANCE = new MeasurementDModel();
    
    private MeasurementDModel(){
    
    }
    
    public MeasurementEntity getMeasurement(int measurementId){
        return MeasurementDal.INSTANCE.get(measurementId);
    }
}
