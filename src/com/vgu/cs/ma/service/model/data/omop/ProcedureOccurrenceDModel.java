package com.vgu.cs.ma.service.model.data.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 29/04/2021
 */

import com.vgu.cs.engine.dal.ProcedureOccurrenceDal;
import com.vgu.cs.engine.entity.omop.ProcedureOccurrenceEntity;

public class ProcedureOccurrenceDModel {

    public static final ProcedureOccurrenceDModel INSTANCE = new ProcedureOccurrenceDModel();

    private ProcedureOccurrenceDModel() {

    }

    public ProcedureOccurrenceEntity getProcedureOccurrence(int procedureOccurrenceId) {
        return ProcedureOccurrenceDal.INSTANCE.get(procedureOccurrenceId);
    }
}
