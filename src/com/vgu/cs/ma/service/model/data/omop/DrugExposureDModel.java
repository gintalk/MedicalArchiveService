package com.vgu.cs.ma.service.model.data.omop;

import com.vgu.cs.engine.dal.DrugExposureDal;
import com.vgu.cs.engine.entity.omop.DrugExposureEntity;

public class DrugExposureDModel {
    
    public static final DrugExposureDModel INSTANCE = new DrugExposureDModel();
    
    private DrugExposureDModel() {
    
    }
    
    public DrugExposureEntity getDrugExposure(int drugExposureId) {
        return DrugExposureDal.INSTANCE.get(drugExposureId);
    }
}
