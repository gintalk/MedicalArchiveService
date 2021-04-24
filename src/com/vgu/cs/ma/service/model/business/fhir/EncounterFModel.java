package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 10/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.VisitOccurrenceEntity;
import com.vgu.cs.ma.service.model.business.omop.CareSiteOModel;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Encounter.EncounterHospitalizationComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterLocationComponent;

/**
 * <p>
 * An interaction between a patient and healthcare provider(s) for the purpose of providing healthcare service(s) or
 * assessing the health status of a patient.
 * </p>
 * <p>
 * The class <code>EncounterFModel</code> constructs <code>Encounter</code>> from any record in the OMOP-compliant
 * table <code>visit_occurrence</code>.
 * </p>
 * <p>
 * The <code>EncounterFModel</code> class contains one single public method accepting a <code>VisitOccurrenceEntity</code>, which
 * represents a record in <code>visit_occurrence</code>, and returns a FHIR-compliant <code>Encounter</code>.
 * </p>
 *
 * @author namnh16 on 19/03/2021
 * @see <a href="http://build.fhir.org/ig/HL7/cdmh/profiles.html#omop-to-fhir-mappings">OMOP to FHIR mappings</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#VISIT_OCCURRENCE">OMOP VISIT_OCCURRENCE</a>
 * @see <a href="https://www.hl7.org/fhir/encounter.html">FHIR Encounter</a>
 */
public class EncounterFModel {
    
    public static final EncounterFModel INSTANCE = new EncounterFModel();
    
    private EncounterFModel() {
    }
    
    public Encounter constructFhir(VisitOccurrenceEntity visitOccurrence) {
        Encounter encounter = new Encounter();
        
        _addId(encounter, visitOccurrence);
        _addLocation(encounter, visitOccurrence);
        _addSubject(encounter, visitOccurrence);
        _addPeriod(encounter, visitOccurrence);
        _addType(encounter, visitOccurrence);
        _addVisitTypeExtension(encounter, visitOccurrence);
        _addHospitalization(encounter, visitOccurrence);
        
        return encounter;
    }
    
    /**
     * Corresponding FHIR field: Encounter.id
     * Identifier(s) by which this encounter is known.
     */
    private void _addId(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        encounter.setId(String.valueOf(visitOccurrence.visit_occurrence_id));
    }
    
    /**
     * Corresponding FHIR field: Encounter.location.location
     * Location the encounter takes place.
     */
    private void _addLocation(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        encounter.addLocation(new EncounterLocationComponent().setLocation(CareSiteOModel.INSTANCE.getReference(visitOccurrence.care_site_id)));
    }
    
    /**
     * Corresponding FHIR field: Encounter.subject
     * The patient or group present at the encounter.
     */
    private void _addSubject(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        encounter.setSubject(PersonOModel.INSTANCE.getReference(visitOccurrence.person_id));
    }
    
    /**
     * Corresponding FHIR field: Encounter.period
     * The start and end time of the encounter.
     */
    private void _addPeriod(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        Period period = new Period();
        if (!StringUtils.isNullOrEmpty(visitOccurrence.visit_start_datetime)) {
            period.setStart(DateTimeUtils.parseDatetime(visitOccurrence.visit_start_datetime));
        } else if (!StringUtils.isNullOrEmpty(visitOccurrence.visit_start_date)) {
            period.setStart(DateTimeUtils.parseDate(visitOccurrence.visit_start_date));
        } else {
            return;
        }
        
        if (!StringUtils.isNullOrEmpty(visitOccurrence.visit_end_datetime)) {
            period.setEnd(DateTimeUtils.parseDatetime(visitOccurrence.visit_end_datetime));
        } else if (!StringUtils.isNullOrEmpty(visitOccurrence.visit_end_date)) {
            period.setEnd(DateTimeUtils.parseDate(visitOccurrence.visit_end_date));
        }
        
        encounter.setPeriod(period);
    }
    
    /**
     * Corresponding FHIR field: Encounter.type
     * Specific type of encounter. VISIT_OCCURRENCE.visit_concept_id contains a concept id representing the kind of
     * visit, like inpatient or outpatient. VISIT_OCCURRENCE.visit_source_value houses the verbatim value from the
     * source data representing the kind of visit that took place (inpatient, outpatient, emergency, etc.).
     *
     * @see <a href="https://www.hl7.org/fhir/valueset-encounter-type.html">Available values for encounter type</a>
     */
    private void _addType(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        CodeableConcept typeCodeable = CodeableConceptUtils.fromConceptIdAndSourceValue(visitOccurrence.visit_concept_id, visitOccurrence.visit_source_value);
        if (typeCodeable == null) {
            return;
        }
        encounter.addType(typeCodeable);
    }
    
    /**
     * Corresponding FHIR field: Encounter.extension (Proposed Name: source-data-type : CodeableConcept)
     * VISIT_OCCURRENCE.visit_type_concept_id can be used to understand the provenance of the visit record, or where
     * the record comes from.
     */
    private void _addVisitTypeExtension(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        Extension visitTypeExtension = new Extension();
        visitTypeExtension.setUserData("name", "source-date-type");
        visitTypeExtension.setValue(CodeableConceptUtils.fromConceptId(visitOccurrence.visit_type_concept_id));
        
        encounter.addExtension(visitTypeExtension);
    }
    
    /**
     * Corresponding FHIR field: Encounter.hospitalization
     * Details about the admission to a healthcare service.
     */
    private void _addHospitalization(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        EncounterHospitalizationComponent hospitalization = new EncounterHospitalizationComponent();
        
        hospitalization.setAdmitSource(CodeableConceptUtils.fromConceptIdAndSourceValue(visitOccurrence.admitting_source_concept_id, visitOccurrence.admitting_source_value));
        hospitalization.setDischargeDisposition(CodeableConceptUtils.fromConceptIdAndSourceValue(visitOccurrence.discharge_to_concept_id, visitOccurrence.discharge_to_source_value));
        
        encounter.setHospitalization(hospitalization);
    }
}
