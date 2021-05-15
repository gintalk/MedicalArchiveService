package com.vgu.cs.ma.service.model.data.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 14/03/2021
 */

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
