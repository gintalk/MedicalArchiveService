/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 04/03/2021
 */

import com.vgu.cs.engine.entity.PersonEntity;
import com.vgu.cs.ma.service.model.business.fhir.PatientFModel;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.model.data.PersonDModel;
import org.hl7.fhir.dstu3.model.Patient;

public class Logic {
    
    public static void main(String[] args) {
        PersonEntity person = PersonDModel.INSTANCE.getPerson(1);
        System.out.println(person);
        
        Patient patient = PatientFModel.INSTANCE.constructFhir(person);
        person = PersonOModel.INSTANCE.constructOMOP(patient);
        System.out.println(person);
        
        System.exit(0);
    }
}
