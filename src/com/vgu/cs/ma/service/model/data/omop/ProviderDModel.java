package com.vgu.cs.ma.service.model.data.omop;

import com.vgu.cs.engine.dal.ProviderDal;
import com.vgu.cs.engine.entity.omop.ProviderEntity;

public class ProviderDModel {

    public static final ProviderDModel INSTANCE = new ProviderDModel();
    private final ProviderDal DAL;

    private ProviderDModel() {
        DAL = ProviderDal.INSTANCE;
    }

    public ProviderEntity getProvider(int providerId) {
        return DAL.get(providerId);
    }
}
