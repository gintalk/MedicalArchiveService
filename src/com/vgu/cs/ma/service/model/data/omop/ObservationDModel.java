package com.vgu.cs.ma.service.model.data.omop;

import com.vgu.cs.engine.dal.ObservationDal;
import com.vgu.cs.engine.entity.omop.ObservationEntity;

public class ObservationDModel {
    
    public static final ObservationDModel INSTANCE = new ObservationDModel();
    
    private ObservationDModel() {
    
    }
    
    public ObservationEntity getObservation(int observationId) {
        return ObservationDal.INSTANCE.get(observationId);
    }
}
