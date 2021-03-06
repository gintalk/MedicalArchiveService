/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 04/03/2021
 */

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.vgu.cs.engine.dal.PersonDal;
import com.vgu.cs.engine.entity.PersonEntity;
import com.vgu.cs.ma.service.model.business.OMOPModel;
import org.hl7.fhir.dstu3.model.Patient;

public class Logic {

    public static void main(String[] args) {
        FhirContext fhirContext = FhirContext.forDstu3();
        IParser parser = fhirContext.newJsonParser();

        PersonEntity person = PersonDal.INSTANCE.get(1);

        Patient patient = OMOPModel.INSTANCE.toFhir(person);
        System.out.println(parser.encodeResourceToString(patient));
    }
}
