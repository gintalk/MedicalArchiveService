package com.vgu.cs.ma.service.model.business.fhir;

import com.vgu.cs.engine.entity.dhis2.model.OrganisationUnit;
import com.vgu.cs.engine.entity.omop.CareSiteEntity;
import com.vgu.cs.engine.entity.omop.LocationEntity;
import com.vgu.cs.ma.service.model.business.omop.LocationOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;

/**
 * <p>
 * Details and position information for a physical place where services are provided and resources and participants may
 * be stored, found, contained, or accommodated.
 * </p>
 * <p>
 * The class <code>LocationFModel</code> constructs <code>Location</code>> from any record in the OMOP-compliant
 * table <code>location</code> or <code>care_site</code>.
 * </p>
 * <p>
 * The <code>LocationFModel</code> class contains two public methods accepting either a <code>CareSiteEntity</code> or a
 * <code>LocationEntity</code>, and returns a FHIR-compliant <code>Location</code>.
 * </p>
 *
 * @see <a href="http://build.fhir.org/ig/HL7/cdmh/profiles.html#omop-to-fhir-mappings">OMOP to FHIR mappings</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#CARE_SITE">OMOP CARE_SITE</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#LOCATION">OMOP LOCATION</a>
 * @see <a href="https://www.hl7.org/fhir/location.html">FHIR Location</a>
 */
public class LocationFModel {

    public static LocationFModel INSTANCE = new LocationFModel();

    private LocationFModel() {
    }

    public Location constructFhir(CareSiteEntity careSite) {
        Location location = new Location();

        _addId(location, careSite);
        _addName(location, careSite.care_site_name);
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

    public Location constructFhir(OrganisationUnit orgUnit) {
        Location location = new Location();

        _addId(location, orgUnit);
        _addName(location, orgUnit.getName());

        return location;
    }

    private void _addId(Location location, CareSiteEntity careSite) {
        location.setId(new IdType(careSite.care_site_id));
    }

    /**
     * LOCATION.location_id is the unique key given to a unique Location.
     */
    private void _addId(Location fLocation, LocationEntity oLocation) {
        fLocation.setId(new IdType(oLocation.location_id));
    }

    private void _addId(Location location, OrganisationUnit orgUnit) {
        location.setId(orgUnit.getId());
    }

    /**
     * Corresponding FHIR field: Location.name
     * Name of the location as used by humans. CARE_SITE.care_site_name is the name of the care_site as it appears in
     * the source data.
     */
    private void _addName(Location location, String name) {
        location.setName(name);
    }

    /**
     * Corresponding FHIR field: Location.type
     * Type of function performed. CARE_SITE.place_of_service_concept_id is a high-level way of characterizing a Care
     * Site. Typically, however, Care Sites can provide care in multiple settings (inpatient, outpatient, etc.) and
     * this granularity should be reflected in the visit.
     */
    private void _addTypeCodeable(Location location, CareSiteEntity careSite) {
        location.setType(CodeableConceptUtils.fromConceptIdAndSourceValue(careSite.place_of_service_concept_id, careSite.place_of_service_source_value));
    }

    /**
     * Corresponding FHIR field: Location.address
     * Physical location. CARE_SITE.location_id is the location_id from the LOCATION table representing the physical
     * location of the care_site.
     */
    private void _addAddress(Location location, CareSiteEntity careSite) {
        location.setAddress(LocationOModel.INSTANCE.getAddress(careSite.location_id));
    }

    /**
     * Corresponding FHIR field: Location.address
     * Physical location.
     */
    private void _addAddress(Location fLocation, LocationEntity oLocation) {
        fLocation.setAddress(LocationOModel.INSTANCE.getAddress(oLocation.location_id));
    }
}
