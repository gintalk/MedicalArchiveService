package com.vgu.cs.ma.service.model.data;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 21/04/2021
 */

import com.vgu.cs.engine.dal.ConditionOccurrenceDal;
import com.vgu.cs.engine.entity.ConditionOccurrenceEntity;

public class ConditionOccurrenceDModel {
    
    public static final ConditionOccurrenceDModel INSTANCE = new ConditionOccurrenceDModel();
    
    private ConditionOccurrenceDModel() {
    
    }
    
    public ConditionOccurrenceEntity getConditionOccurrence(int conditionOccurrenceId) {
        return ConditionOccurrenceDal.INSTANCE.get(conditionOccurrenceId);
    }
}
