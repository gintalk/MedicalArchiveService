package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 10/03/2021
 */

import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.CareSiteEntity;
import com.vgu.cs.engine.entity.LocationEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtil;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;

public class LocationFModel extends FhirOmopModel {

    public static LocationFModel INSTANCE = new LocationFModel();

    private LocationFModel() {
    }

    public Location constructFhir(CareSiteEntity careSite) {
        Location location = new Location();

        _addId(location, careSite);
        _addName(location, careSite);
        _addTypeCodeable(location, careSite);
        _addAddress(location, careSite);

        return location;
    }

    public Location constructFhir(LocationEntity oLocation) {
        Location fLocation = new Location();

        _addId(fLocation, oLocation);
        _addAddress(fLocation, oLocation);

        return fLocation;
    }

    private void _addId(Location location, CareSiteEntity careSite) {
        location.setId(new IdType(careSite.care_site_id));
    }

    private void _addId(Location fLocation, LocationEntity oLocation) {
        fLocation.setId(new IdType(oLocation.location_id));
    }

    private void _addName(Location location, CareSiteEntity careSite) {
        location.setName(careSite.care_site_name);
    }

    private void _addTypeCodeable(Location location, CareSiteEntity careSite) {
        CodeableConcept typeCodeable = CodeableConceptUtil.fromConceptId(careSite.place_of_service_concept_id);
        if (typeCodeable == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(careSite.place_of_service_source_value)) {
            typeCodeable.setId(careSite.place_of_service_source_value);
        }

        location.setType(typeCodeable);
    }

    private void _addAddress(Location location, CareSiteEntity careSite) {
        location.setAddress(LocationOModel.INSTANCE.getAddress(careSite.location_id));
    }

    private void _addAddress(Location fLocation, LocationEntity oLocation) {
        fLocation.setAddress(LocationOModel.INSTANCE.getAddress(oLocation.location_id));
    }
}
