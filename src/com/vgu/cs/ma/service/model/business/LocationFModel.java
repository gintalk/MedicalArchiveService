package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 10/03/2021
 */

import com.vgu.cs.engine.entity.CareSiteEntity;
import com.vgu.cs.engine.entity.ConceptEntity;
import com.vgu.cs.engine.entity.LocationEntity;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;

public class LocationFModel extends FhirOmopModel {

    public static LocationFModel INSTANCE = new LocationFModel();

    private LocationFModel() {
    }

    public Location constructFhir(CareSiteEntity careSite) {
        Location location = new Location();
        location.setId(new IdType(careSite.care_site_id));
        location.setName(careSite.care_site_name);

        CodeableConcept type = getType(careSite.place_of_service_concept_id, careSite.place_of_service_source_value);
        if (type != null) {
            location.setType(type);
        }

        Address address = getAddress(careSite.location_id);
        if (address != null) {
            location.setAddress(address);
        }

        return location;
    }

    public Location constructFhir(LocationEntity oLocation) {
        Location fLocation = new Location();
        fLocation.setId(new IdType(oLocation.location_id));

        Address address = getAddress(oLocation.location_id);
        if (address != null) {
            fLocation.setAddress(address);
        }

        return fLocation;
    }

    public CodeableConcept getType(int posConceptId, String posSourceValue) {
        ConceptEntity posConcept = ConceptDModel.INSTANCE.getConcept(posConceptId);
        if (posConcept == null) {
            return null;
        }

        CodeableConcept typeCodeable = new CodeableConcept();
        typeCodeable.setText(posConcept.concept_name);
        typeCodeable.setId(posSourceValue);

        return typeCodeable;
    }

    public Address getAddress(int locationId) {
        return LocationOModel.INSTANCE.getAddress(locationId);
    }
}
