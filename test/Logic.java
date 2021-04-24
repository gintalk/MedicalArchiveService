/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 04/03/2021
 */

import com.vgu.cs.engine.entity.VisitOccurrenceEntity;
import com.vgu.cs.ma.service.model.business.fhir.EncounterFModel;
import com.vgu.cs.ma.service.model.business.omop.VisitOccurrenceOModel;
import com.vgu.cs.ma.service.model.data.VisitOccurrenceDModel;

public class Logic {
    
    public static void main(String[] args) {
        VisitOccurrenceEntity omop = VisitOccurrenceDModel.INSTANCE.getVisitOccurrence(1);
        System.out.println(omop);
        omop.discharge_to_concept_id = 1;
        System.out.println(VisitOccurrenceOModel.INSTANCE.constructOMOP(EncounterFModel.INSTANCE.constructFhir(omop)));
    }
}
