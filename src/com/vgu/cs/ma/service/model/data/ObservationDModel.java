package com.vgu.cs.ma.service.model.data;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 25/04/2021
 */

import com.vgu.cs.engine.dal.ObservationDal;
import com.vgu.cs.engine.entity.ObservationEntity;

public class ObservationDModel {
    
    public static final ObservationDModel INSTANCE = new ObservationDModel();
    
    private ObservationDModel() {
    
    }
    
    public ObservationEntity getObservation(int observationId) {
        return ObservationDal.INSTANCE.get(observationId);
    }
}
