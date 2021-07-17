package com.vgu.cs.ma.service.model.data.omop;

import com.vgu.cs.engine.dal.VisitOccurrenceDal;
import com.vgu.cs.engine.entity.omop.VisitOccurrenceEntity;

public class VisitOccurrenceDModel {
    
    public static final VisitOccurrenceDModel INSTANCE = new VisitOccurrenceDModel();
    
    private VisitOccurrenceDModel() {
    
    }
    
    public VisitOccurrenceEntity getVisitOccurrence(int visitOccurrenceId) {
        return VisitOccurrenceDal.INSTANCE.get(visitOccurrenceId);
    }
}
