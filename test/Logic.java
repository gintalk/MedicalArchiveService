/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 04/03/2021
 */

import com.vgu.cs.engine.entity.ConditionOccurrenceEntity;
import com.vgu.cs.ma.service.model.business.fhir.ConditionFModel;
import com.vgu.cs.ma.service.model.business.omop.ConditionOccurrenceOModel;
import com.vgu.cs.ma.service.model.data.ConditionOccurrenceDModel;

public class Logic {
    
    public static void main(String[] args) {
        ConditionOccurrenceEntity omop = ConditionOccurrenceDModel.INSTANCE.getConditionOccurrence(4);
        System.out.println(omop);
        System.out.println(ConditionOccurrenceOModel.INSTANCE.constructOMOP(ConditionFModel.INSTANCE.constructFhir(omop)));
    }
}
