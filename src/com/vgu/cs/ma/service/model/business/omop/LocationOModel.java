package com.vgu.cs.ma.service.model.business.omop;

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.engine.dal.LocationDal;
import com.vgu.cs.engine.entity.omop.LocationEntity;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Location;

public class LocationOModel {
    
    public static final LocationOModel INSTANCE = new LocationOModel();
    
    private LocationOModel() {
    
    }
    
    /**
     * Unretained fields include:
     * - location_source_value
     */
    public LocationEntity constructOmop(Location fLocation) {
        LocationEntity oLocation = new LocationEntity();
        
        _setId(fLocation, oLocation);
        _setAddress(fLocation, oLocation);
        
        return oLocation;
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
        address.setId(String.valueOf(locationId));
        address.setUse(Address.AddressUse.HOME);
        address.setCity(location.city);
        address.setState(location.state);
        address.setPostalCode(location.zip);
        address.setCountry(location.county);
        address.addLine(location.address_1);
        address.addLine(location.address_2);
        
        return address;
    }
    
    public int getLocationIdFromAddress(Address address) {
        if (address == null) {
            return 0;
        }
        return ConvertUtils.toInteger(address.getId());
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * LOCATION.location_id = Location.id
     */
    private void _setId(Location fLocation, LocationEntity oLocation) {
        oLocation.location_id = ConvertUtils.toInteger(fLocation.getId());
    }
    
    /**
     * Maps Location.address to LOCATION's city, state, zip, county, address_1, address_2
     */
    private void _setAddress(Location fLocation, LocationEntity oLocation) {
        Address address = fLocation.getAddress();
        if (address == null) {
            return;
        }
        
        oLocation.city = address.getCity();
        oLocation.state = address.getState();
        oLocation.zip = address.getPostalCode();
        oLocation.county = address.getCountry();
        oLocation.address_1 = address.getLine().get(0).toString();
        oLocation.address_2 = address.getLine().get(1).toString();
    }
}
