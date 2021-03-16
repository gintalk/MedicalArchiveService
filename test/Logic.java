/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 04/03/2021
 */

import org.hl7.fhir.dstu3.model.codesystems.EncounterAdmitSource;

public class Logic {

    public static void main(String[] args) {
        EncounterAdmitSource admitSource = EncounterAdmitSource.fromCode("born");
        System.out.println(admitSource);
    }
}
