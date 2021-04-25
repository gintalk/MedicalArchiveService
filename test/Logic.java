/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 04/03/2021
 */

import com.vgu.cs.engine.entity.CareSiteEntity;
import com.vgu.cs.engine.entity.LocationEntity;
import com.vgu.cs.engine.entity.VisitOccurrenceEntity;
import com.vgu.cs.ma.service.model.business.fhir.EncounterFModel;
import com.vgu.cs.ma.service.model.business.fhir.LocationFModel;
import com.vgu.cs.ma.service.model.business.omop.CareSiteOModel;
import com.vgu.cs.ma.service.model.business.omop.LocationOModel;
import com.vgu.cs.ma.service.model.business.omop.VisitOccurrenceOModel;
import com.vgu.cs.ma.service.model.data.CareSiteDModel;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import com.vgu.cs.ma.service.model.data.LocationDModel;
import com.vgu.cs.ma.service.model.data.VisitOccurrenceDModel;

public class Logic {
    
    public static void main(String[] args) {
        LocationEntity omop = LocationDModel.INSTANCE.getLocation(1);
        System.out.println(omop);
        System.out.println(LocationOModel.INSTANCE.constructOmop(LocationFModel.INSTANCE.constructFhir(omop)));
        
        System.exit(0);
    }
}
