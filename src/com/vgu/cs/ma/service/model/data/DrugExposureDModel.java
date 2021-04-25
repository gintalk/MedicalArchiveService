package com.vgu.cs.ma.service.model.data;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 25/04/2021
 */

import com.vgu.cs.engine.dal.DrugExposureDal;
import com.vgu.cs.engine.entity.DrugExposureEntity;

public class DrugExposureDModel {
    
    public static final DrugExposureDModel INSTANCE = new DrugExposureDModel();
    
    private DrugExposureDModel() {
    
    }
    
    public DrugExposureEntity getDrugExposure(int drugExposureId) {
        return DrugExposureDal.INSTANCE.get(drugExposureId);
    }
}
