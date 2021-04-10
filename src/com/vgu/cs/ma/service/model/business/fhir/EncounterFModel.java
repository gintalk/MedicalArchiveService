package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 10/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.CareSiteEntity;
import com.vgu.cs.engine.entity.ConceptEntity;
import com.vgu.cs.engine.entity.VisitOccurrenceEntity;
import com.vgu.cs.ma.service.model.data.CareSiteDModel;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
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
        CareSiteEntity careSite = CareSiteDModel.INSTANCE.getCareSite(visitOccurrence.care_site_id);
        if (careSite == null) {
            return;
        }

        Reference locationReference = new Reference();
        locationReference.setReferenceElement(new IdType("Organization", String.valueOf(visitOccurrence.care_site_id)));
        locationReference.setDisplay(careSite.care_site_name);

        encounter.addLocation(new EncounterLocationComponent().setLocation(locationReference));
    }

    /**
     * Corresponding FHIR field: Encounter.subject
     * The patient or group present at the encounter.
     */
    private void _addSubject(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        encounter.setSubject(new Reference(new IdType("Patient", String.valueOf(visitOccurrence.person_id))));
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
        ConceptEntity visitConcept = ConceptDModel.INSTANCE.getConcept(visitOccurrence.visit_concept_id);
        if (visitConcept == null) {
            return;
        }

        CodeableConcept typeCodeable = new CodeableConcept();
        typeCodeable.setText(visitConcept.concept_name);
        typeCodeable.setId(visitOccurrence.visit_source_value);

        encounter.addType(typeCodeable);
    }

    /**
     * Corresponding FHIR field: Encounter.extension (Proposed Name: source-data-type : CodeableConcept)
     * VISIT_OCCURRENCE.visit_type_concept_id can be used to understand the provenance of the visit record, or where
     * the record comes from.
     */
    private void _addVisitTypeExtension(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        ConceptEntity visitTypeConcept = ConceptDModel.INSTANCE.getConcept(visitOccurrence.visit_type_concept_id);
        if (visitTypeConcept == null) {
            return;
        }

        Extension visitTypeExtension = new Extension();
        visitTypeExtension.setProperty("source-data-type", new CodeableConcept().setText(visitTypeConcept.concept_name));

        encounter.addExtension(visitTypeExtension);
    }

    /**
     * Corresponding FHIR field: Encounter.hospitalization
     * Details about the admission to a healthcare service.
     */
    private void _addHospitalization(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        EncounterHospitalizationComponent hospitalization = new EncounterHospitalizationComponent();

        ConceptEntity admittingSourceConcept = ConceptDModel.INSTANCE.getConcept(visitOccurrence.admitting_source_concept_id);
        if (admittingSourceConcept != null) {
            hospitalization.setAdmitSource(new CodeableConcept().setText(admittingSourceConcept.concept_name));
        }
        if (!StringUtils.isNullOrEmpty(visitOccurrence.admitting_source_value)) {
            hospitalization.getAdmitSource().setId(visitOccurrence.admitting_source_value);
        }

        ConceptEntity dischargeToConcept = ConceptDModel.INSTANCE.getConcept(visitOccurrence.discharge_to_concept_id);
        if (dischargeToConcept != null) {
            hospitalization.setDischargeDisposition(new CodeableConcept().setText(dischargeToConcept.concept_name));
        }
        if (!StringUtils.isNullOrEmpty(visitOccurrence.discharge_to_source_value)) {
            hospitalization.getDischargeDisposition().setId(visitOccurrence.discharge_to_source_value);
        }

        encounter.setHospitalization(hospitalization);
    }
}
