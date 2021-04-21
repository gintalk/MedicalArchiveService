package com.vgu.cs.ma.service.model.data;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 21/04/2021
 */

import com.vgu.cs.engine.dal.DeathDal;
import com.vgu.cs.engine.entity.DeathEntity;

public class DeathDModel {
    
    public static final DeathDModel INSTANCE = new DeathDModel();
    
    private DeathDModel() {
    
    }
    
    public DeathEntity getDeath(int personId) {
        return DeathDal.INSTANCE.get(personId);
    }
}
