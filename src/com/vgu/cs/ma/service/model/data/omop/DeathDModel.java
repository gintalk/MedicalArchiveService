package com.vgu.cs.ma.service.model.data.omop;

import com.vgu.cs.engine.dal.DeathDal;
import com.vgu.cs.engine.entity.omop.DeathEntity;

public class DeathDModel {
    
    public static final DeathDModel INSTANCE = new DeathDModel();
    
    private DeathDModel() {
    
    }
    
    public DeathEntity getDeath(int personId) {
        return DeathDal.INSTANCE.get(personId);
    }
}
