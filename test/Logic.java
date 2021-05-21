/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 04/03/2021
 */

import ca.uhn.fhir.context.FhirContext;
import com.vgu.cs.ma.service.model.business.fhir.LocationFModel;
import com.vgu.cs.ma.service.model.business.fhir.PatientFModel;
import com.vgu.cs.ma.service.model.data.dhis2.EventDModel;
import com.vgu.cs.ma.service.model.data.dhis2.OrgUnitDModel;
import com.vgu.cs.ma.service.model.data.dhis2.TrackedEntityDModel;
import com.vgu.cs.ma.service.model.data.dhis2.UserDModel;

public class Logic {

    public static void main(String[] args) {
//        SpecimenEntity omop = SpecimenDModel.INSTANCE.getSpecimen(1);
//        System.out.println(omop);
//        System.out.println(SpecimenOModel.INSTANCE.constructOmop(SpecimenFModel.INSTANCE.constructFhir(omop)));

        FhirContext fhirContext = FhirContext.forDstu3();
//        System.out.println(fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(ObservationFModel.INSTANCE.constructFhir(ObservationDModel.INSTANCE.getObservation(1))));

//        Type type = new StringType();
//        System.out.println(type.fhirType());

//        System.out.println(TrackedEntityDModel.INSTANCE.getTrackedEntityInstances("DiszpKrYNg8"));
//        System.out.println(TrackedEntityDModel.INSTANCE.getTrackedEntityInstance("Qk90XyD07T9", "ur1Edk5Oe2n"));
//        System.out.println(TrackedEntityDModel.INSTANCE.getTrackedEntityInstance("r6Wc56APuCL", "ur1Edk5Oe2n"));
//        System.out.println(
//                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(
//                        PatientFModel.INSTANCE.constructFhir(TrackedEntityDModel.INSTANCE.getTrackedEntityInstance("Qk90XyD07T9", "ur1Edk5Oe2n"))
//                )
//        );
//        System.out.println(UserDModel.INSTANCE.getUser("NqCK1Xc93yx"));
//        System.out.println(EventDModel.INSTANCE.getEvent("wiTqM8Q51TF"));

        System.out.println(
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(
                        LocationFModel.INSTANCE.constructFhir(OrgUnitDModel.INSTANCE.getOrgUnit("TQ5DSmdliN7"))
                )
        );

        System.exit(0);
    }
}
