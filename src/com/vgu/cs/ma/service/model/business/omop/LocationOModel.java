package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 16/03/2021
 */

import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.dal.LocationDal;
import com.vgu.cs.engine.entity.LocationEntity;
import org.hl7.fhir.dstu3.model.Address;

public class LocationOModel {

    public static final LocationOModel INSTANCE = new LocationOModel();

    private LocationOModel() {

    }

    public Address getAddress(int locationId) {
        if (locationId < 0) {
            return null;
        }

        LocationEntity location = LocationDal.INSTANCE.get(locationId);
        if (location == null) {
            return null;
        }

        Address address = new Address();
        address.setUse(Address.AddressUse.HOME);
        address.setCity(location.city);
        address.setState(location.state);
        address.setPostalCode(location.zip);
        address.setCountry(location.county);
        if (!StringUtils.isNullOrEmpty(location.address_1) && !StringUtils.isNullOrEmpty(location.address_2)) {
            address.addLine(location.address_1 + " " + location.address_2);
        } else if (!StringUtils.isNullOrEmpty(location.address_1)) {
            address.addLine(location.address_1);
        } else if (!StringUtils.isNullOrEmpty(location.address_2)) {
            address.addLine(location.address_2);
        }

        return address;
    }
}
