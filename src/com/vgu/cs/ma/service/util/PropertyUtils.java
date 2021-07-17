package com.vgu.cs.ma.service.util;

import com.vgu.cs.common.util.CollectionUtils;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Property;

public class PropertyUtils {
    
    public static void setProperty(Extension extension, String name, CodeableConcept value) {
        extension.setProperty(name, value);
    }
    
    public static String getStringProperty(Base base, String name) {
        Property property = base.getNamedProperty(name);
        if (property == null || CollectionUtils.isNullOrEmpty(property.getValues())) {
            return "";
        }
        
        return property.getValues().get(0).toString();
    }
    
    public static CodeableConcept getCodeableProperty(Base base, String name) {
        Property property = base.getNamedProperty(name);
        if (property == null || CollectionUtils.isNullOrEmpty(property.getValues())) {
            return null;
        }
        
        return property.getValues().get(0).castToCodeableConcept(property.getValues().get(0));
    }
}
