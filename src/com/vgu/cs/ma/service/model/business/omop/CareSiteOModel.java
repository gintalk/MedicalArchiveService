package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 24/04/2021
 */

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.engine.entity.omop.CareSiteEntity;
import com.vgu.cs.ma.service.model.data.omop.CareSiteDModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;

public class CareSiteOModel {
    
    public static final CareSiteOModel INSTANCE = new CareSiteOModel();
    
    private CareSiteOModel() {
    
    }
    
    /**
     * Unretained fields include:
     * - care_site_source_value
     */
    public CareSiteEntity constructOmop(Location location) {
        CareSiteEntity careSite = new CareSiteEntity();
        
        _setId(location, careSite);
        _setName(location, careSite);
        _setPlaceOfServiceConceptIdAndSourceValue(location, careSite);
        _setLocationId(location, careSite);
        
        return careSite;
    }
    
    public Reference getReference(int careSiteId) {
        Reference reference = new Reference(new IdType("Organization", String.valueOf(careSiteId)));
        reference.setId(String.valueOf(careSiteId));
        
        CareSiteEntity careSite = CareSiteDModel.INSTANCE.getCareSite(careSiteId);
        if (careSite == null) {
            return reference;
        }
        reference.setDisplay(careSite.care_site_name);
        
        return reference;
    }
    
    public int getIdFromReference(Reference reference) {
        if (reference == null) {
            return 0;
        }
        return ConvertUtils.toInteger(reference.getId());
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * CARE_SITE.care_site_id = Location.id
     */
    private void _setId(Location location, CareSiteEntity careSite) {
        careSite.care_site_id = ConvertUtils.toInteger(location.getId());
    }
    
    /**
     * CARE_SITE.care_site_name = Location.name
     */
    private void _setName(Location location, CareSiteEntity careSite) {
        careSite.care_site_name = location.getName();
    }
    
    /**
     * Maps Location.type to CARE_SITE's place_of_service_concept_id and place_of_service_source_value
     */
    private void _setPlaceOfServiceConceptIdAndSourceValue(Location location, CareSiteEntity careSite) {
        CodeableConcept typeCodeable = location.getType();
        if (typeCodeable == null) {
            return;
        }
        
        careSite.place_of_service_concept_id = CodeableConceptUtils.getConceptId(typeCodeable);
        careSite.place_of_service_source_value = typeCodeable.getId();
    }
    
    /**
     * CARE_SITE.location_id = Location.address.id
     */
    private void _setLocationId(Location location, CareSiteEntity careSite) {
        careSite.location_id = LocationOModel.INSTANCE.getLocationIdFromAddress(location.getAddress());
    }
}
