package com.vgu.cs.ma.service.model.data.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 24/04/2021
 */

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
