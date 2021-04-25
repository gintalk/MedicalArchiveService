/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 04/03/2021
 */

import com.vgu.cs.engine.entity.CareSiteEntity;
import com.vgu.cs.engine.entity.VisitOccurrenceEntity;
import com.vgu.cs.ma.service.model.business.fhir.EncounterFModel;
import com.vgu.cs.ma.service.model.business.fhir.LocationFModel;
import com.vgu.cs.ma.service.model.business.omop.CareSiteOModel;
import com.vgu.cs.ma.service.model.business.omop.VisitOccurrenceOModel;
import com.vgu.cs.ma.service.model.data.CareSiteDModel;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import com.vgu.cs.ma.service.model.data.VisitOccurrenceDModel;

public class Logic {
    
    public static void main(String[] args) {
        CareSiteEntity omop = CareSiteDModel.INSTANCE.getCareSite(1);
        System.out.println(omop);
        System.out.println(CareSiteOModel.INSTANCE.constructOmop(LocationFModel.INSTANCE.constructFhir(omop)));
    
//        System.out.println(ConceptDModel.INSTANCE.getConcept(8562));
//        System.out.println(ConceptDModel.INSTANCE.getConcept(8536));
//        System.out.println(ConceptDModel.INSTANCE.getConcept(44818517));
        
        System.exit(0);
    }
}
