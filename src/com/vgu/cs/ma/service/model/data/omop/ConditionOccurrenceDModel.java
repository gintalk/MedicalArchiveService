package com.vgu.cs.ma.service.model.data.omop;

import com.vgu.cs.engine.dal.ConditionOccurrenceDal;
import com.vgu.cs.engine.entity.omop.ConditionOccurrenceEntity;

public class ConditionOccurrenceDModel {
    
    public static final ConditionOccurrenceDModel INSTANCE = new ConditionOccurrenceDModel();
    
    private ConditionOccurrenceDModel() {
    
    }
    
    public ConditionOccurrenceEntity getConditionOccurrence(int conditionOccurrenceId) {
        return ConditionOccurrenceDal.INSTANCE.get(conditionOccurrenceId);
    }
}
