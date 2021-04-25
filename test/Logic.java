/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 04/03/2021
 */

import ca.uhn.fhir.context.FhirContext;
import com.vgu.cs.engine.entity.*;
import com.vgu.cs.ma.service.model.business.fhir.EncounterFModel;
import com.vgu.cs.ma.service.model.business.fhir.LocationFModel;
import com.vgu.cs.ma.service.model.business.fhir.MedicationStatementFModel;
import com.vgu.cs.ma.service.model.business.fhir.ObservationFModel;
import com.vgu.cs.ma.service.model.business.omop.*;
import com.vgu.cs.ma.service.model.data.*;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;

public class Logic {
    
    public static void main(String[] args) {
        MeasurementEntity omop = MeasurementDModel.INSTANCE.getMeasurement(2);
        System.out.println(omop);
        System.out.println(MeasurementOModel.INSTANCE.constructOmop(ObservationFModel.INSTANCE.constructFhir(omop)));
    
//        FhirContext fhirContext = FhirContext.forDstu3();
//        System.out.println(fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(ObservationFModel.INSTANCE.constructFhir(ObservationDModel.INSTANCE.getObservation(1))));
        
//        Type type = new StringType();
//        System.out.println(type.fhirType());
        
        System.exit(0);
    }
}
