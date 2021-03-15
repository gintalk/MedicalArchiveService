/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 07/03/2021
 */

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.Patient;

public class Client {

    public static void main(String[] args) {
        FhirContext fhirContext = FhirContext.forDstu3();

        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseDstu3");

        Patient patient = client.read().resource(Patient.class).withId("2238758").execute();

        String string = fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
        System.out.println(string);
    }
}
