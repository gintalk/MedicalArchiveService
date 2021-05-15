/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 07/03/2021
 */

import org.hisp.dhis.Dhis2;
import org.hisp.dhis.Dhis2Config;
import org.hisp.dhis.model.OrgUnit;
import org.hisp.dhis.model.TrackedEntityAttribute;
import org.hisp.dhis.query.Query;

import java.util.List;

public class Client {

    public static void main(String[] args) {
        Dhis2Config config = new Dhis2Config(
                "https://play.dhis2.org/2.36.0",
                "admin",
                "district"
        );

        Dhis2 client = new Dhis2(config);
        try {
            List<OrgUnit> orgUnitGroups = client.getOrgUnits(Query.instance());
            for(OrgUnit orgUnitGroup: orgUnitGroups){
                System.out.println(orgUnitGroup);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
