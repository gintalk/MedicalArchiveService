package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.SpecimenEntity;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.*;

import java.util.Date;

/**
 * <p>
 * A sample to be used for analysis.
 * </p>
 * <p>
 * The class <code>SpecimenFModel</code> constructs <code>Specimen</code>> from any record in the OMOP-compliant
 * table <code>specimen</code>.
 * </p>
 * <p>
 * The <code>SpecimenFModel</code> class contains one single public method accepting a <code>SpecimenEntity</code>, which
 * represents a record in <code>spcimen</code>, and returns a FHIR-compliant <code>Specimen</code>.
 * </p>
 *
 * @author namnh16 on 05/03/2021
 * @see <a href="http://build.fhir.org/ig/HL7/cdmh/profiles.html#omop-to-fhir-mappings">OMOP to FHIR mappings</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#SPECIMEN">OMOP SPECIMEN</a>
 * @see <a href="https://www.hl7.org/fhir/specimen.html">FHIR Specimen</a>
 */
public class SpecimenFModel {
    
    public static final SpecimenFModel INSTANCE = new SpecimenFModel();
    
    private SpecimenFModel() {
    
    }
    
    public Specimen constructFhir(SpecimenEntity oSpecimen) {
        Specimen fSpecimen = new Specimen();
        
        _addId(fSpecimen, oSpecimen);
        _addDiseaseStatusExtension(fSpecimen, oSpecimen);
        _addSubjectReference(fSpecimen, oSpecimen);
        _addType(fSpecimen, oSpecimen);
        _addTypeExtension(fSpecimen, oSpecimen);
        _addCollectedDateTime(fSpecimen, oSpecimen);
        _addQuantity(fSpecimen, oSpecimen);
        
        return fSpecimen;
    }
    
    /**
     * Corresponding FHIR field: Specimen.id
     * Unique identifier for each specimen.
     */
    private void _addId(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        fSpecimen.setId(new IdType(oSpecimen.specimen_id));
    }
    
    /**
     * Corresponding FHIR field: Specimen.Extension (Proposed Name: disease-status-code : CodeableConcept)
     */
    private void _addDiseaseStatusExtension(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        CodeableConcept diseaseStatusCodeable = CodeableConceptUtils.fromConceptIdAndSourceValue(oSpecimen.disease_status_concept_id, oSpecimen.disease_status_source_value);
        if (diseaseStatusCodeable == null) {
            return;
        }
        
        Extension diseaseStatusCodeExtension = new Extension();
        diseaseStatusCodeExtension.setUserData("name", "disease-status-code");
        diseaseStatusCodeExtension.setValue(diseaseStatusCodeable);
        
        fSpecimen.addExtension(diseaseStatusCodeExtension);
    }
    
    /**
     * Corresponding FHIR field: Specimen.subject
     * Where the specimen came from. This may be from patient(s), from a location (e.g., the source of an environmental
     * sample), or a sampling of a substance or a device. SPECIMEN.person_id identifiestThe person from whom the
     * specimen is collected.
     */
    private void _addSubjectReference(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        fSpecimen.setSubject(PersonOModel.INSTANCE.getReference(oSpecimen.person_id));
    }
    
    /**
     * Corresponding FHIR field: Specimen.type
     * Kind of material that forms the specimen.
     *
     * @see <a href="https://www.hl7.org/fhir/v2/0487/index.html">Available values for specimen type</a>
     */
    private void _addType(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        CodeableConcept specimenCodeable = CodeableConceptUtils.fromConceptIdAndSourceValue(oSpecimen.specimen_concept_id, oSpecimen.specimen_source_value);
        if (specimenCodeable == null) {
            return;
        }
        
        fSpecimen.setType(specimenCodeable);
    }
    
    /**
     * Corresponding FHIR field: Specimen.Extension (Proposed Name: source-data-type : CodeableConcept)
     */
    private void _addTypeExtension(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        CodeableConcept typeCodeable = CodeableConceptUtils.fromConceptId(oSpecimen.specimen_type_concept_id);
        if (typeCodeable == null) {
            return;
        }
        
        Extension sourceDataTypeExtension = new Extension();
        sourceDataTypeExtension.setUserData("name", "source-data-type");
        sourceDataTypeExtension.setValue(typeCodeable);
        
        fSpecimen.addExtension(sourceDataTypeExtension);
    }
    
    /**
     * Corresponding FHIR field: Specimen.collection.collectedDateTime
     * The date the specimen was collected.
     */
    private void _addCollectedDateTime(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        Date dateOrDateTime = DateTimeUtils.parseDateOrDateTime(oSpecimen.specimen_date, oSpecimen.specimen_datetime);
        if (dateOrDateTime == null) {
            return;
        }
        fSpecimen.getCollection().setCollected(new DateTimeType(dateOrDateTime));
    }
    
    /**
     * Corresponding FHIR field: Specimen.collection.quantity
     * The quantity of specimen collected.
     */
    private void _addQuantity(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        SimpleQuantity quantity = new SimpleQuantity();
        quantity.setValue(oSpecimen.quantity);
        
        CodeableConcept quantityCodeable = CodeableConceptUtils.fromConceptIdAndSourceValue(oSpecimen.unit_concept_id, oSpecimen.unit_source_value);
        if (quantityCodeable == null) {
            return;
        }
        
        quantity.setSystem(quantityCodeable.getCodingFirstRep().getSystem());
        quantity.setUnit(quantityCodeable.getCodingFirstRep().getDisplay());
        
        fSpecimen.getCollection().setQuantity(quantity);
    }
}
