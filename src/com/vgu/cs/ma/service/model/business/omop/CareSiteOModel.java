package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 24/04/2021
 */

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.engine.entity.CareSiteEntity;
import com.vgu.cs.ma.service.model.data.CareSiteDModel;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;

public class CareSiteOModel {
    
    public static final CareSiteOModel INSTANCE = new CareSiteOModel();
    
    private CareSiteOModel() {
    
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
}
