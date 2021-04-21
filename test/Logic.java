/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 04/03/2021
 */

import com.vgu.cs.engine.dal.DeathDal;
import com.vgu.cs.engine.dao.DeathDao;
import com.vgu.cs.engine.entity.DeathEntity;
import com.vgu.cs.engine.entity.PersonEntity;
import com.vgu.cs.ma.service.model.business.fhir.AdverseEventFModel;
import com.vgu.cs.ma.service.model.business.fhir.PatientFModel;
import com.vgu.cs.ma.service.model.business.omop.DeathOModel;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.model.data.DeathDModel;
import com.vgu.cs.ma.service.model.data.PersonDModel;
import org.hl7.fhir.dstu3.model.AdverseEvent;
import org.hl7.fhir.dstu3.model.Patient;

public class Logic {
    
    public static void main(String[] args) {
        DeathEntity death = DeathDModel.INSTANCE.getDeath(16);
        System.out.println(death);
    
        AdverseEvent adverseEvent = AdverseEventFModel.INSTANCE.constructFhir(death);
        death = DeathOModel.INSTANCE.constructOMOP(adverseEvent);
        System.out.println(death);
    }
}
