package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 10/03/2021
 */

import com.vgu.cs.common.util.DatetimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.CareSiteEntity;
import com.vgu.cs.engine.entity.ConceptEntity;
import com.vgu.cs.engine.entity.VisitOccurrenceEntity;
import com.vgu.cs.ma.service.model.data.CareSiteDModel;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Encounter.EncounterHospitalizationComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterLocationComponent;

public class EncounterModel {

    public static final EncounterModel INSTANCE = new EncounterModel();
    private final LocationModel LOCATION_MODEL;
    private final String US_CORE_ENCOUNTER_URL;

    private EncounterModel() {
        LOCATION_MODEL = LocationModel.INSTANCE;
        US_CORE_ENCOUNTER_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-encounter";
    }

    public Encounter constructFhir(VisitOccurrenceEntity visitOccurrence) {
        Encounter encounter = new Encounter();
        encounter.setId(String.valueOf(visitOccurrence.visit_occurrence_id));

        EncounterLocationComponent location = getLocationComponent(visitOccurrence.care_site_id);
        if (location != null) {
            encounter.addLocation(location);
        }

        Reference subject = getSubjectReference(visitOccurrence.person_id);
        if (subject != null) {
            encounter.setSubject(subject);
        }

        Period period = getPeriod(visitOccurrence.visit_start_date, visitOccurrence.visit_start_datetime, visitOccurrence.visit_end_date, visitOccurrence.visit_end_datetime);
        if (period != null) {
            encounter.setPeriod(period);
        }

        CodeableConcept type = getTypeCodeable(visitOccurrence.visit_type_concept_id);
        if (type != null) {
            encounter.addType(type);
        }

        Extension visitType = getVisitTypeExtension(visitOccurrence.visit_concept_id);
        if (visitType != null) {
            encounter.addExtension(visitType);
        }

        EncounterHospitalizationComponent hospitalization = getHospitalizationComponent(visitOccurrence.admitting_source_concept_id, visitOccurrence.admitting_source_value, visitOccurrence.discharge_to_concept_id, visitOccurrence.discharge_to_source_value);
        if (hospitalization != null) {
            encounter.setHospitalization(hospitalization);
        }

        return encounter;
    }

    public EncounterLocationComponent getLocationComponent(int careSiteId) {
        CareSiteEntity careSite = CareSiteDModel.INSTANCE.getCareSite(careSiteId);
        if (careSite == null) {
            return null;
        }

        Reference locationReference = new Reference();
        locationReference.setReferenceElement(new IdType("Organization", String.valueOf(careSiteId)));
        locationReference.setDisplay(careSite.care_site_name);

        return new EncounterLocationComponent().setLocation(locationReference);
    }

    public Reference getSubjectReference(int personId) {
        if (personId <= 0) {
            return null;
        }
        return new Reference(new IdType("Patient", String.valueOf(personId)));
    }

    public Period getPeriod(String startDate, String startDatetime, String endDate, String endDatetime) {
        Period period = new Period();
        if (!StringUtils.isNullOrEmpty(startDatetime)) {
            period.setStart(DatetimeUtils.parseDatetime(startDatetime));
        } else if (!StringUtils.isNullOrEmpty(startDate)) {
            period.setStart(DatetimeUtils.parseDate(startDate));
        } else {
            return null;
        }

        if (!StringUtils.isNullOrEmpty(endDatetime)) {
            period.setEnd(DatetimeUtils.parseDatetime(endDatetime));
        } else if (!StringUtils.isNullOrEmpty(endDate)) {
            period.setEnd(DatetimeUtils.parseDate(endDate));
        }

        return period;
    }

    public CodeableConcept getTypeCodeable(int visitConceptId) {
        ConceptEntity visitConcept = ConceptDModel.INSTANCE.getConcept(visitConceptId);
        if (visitConcept == null) {
            return null;
        }

        return new CodeableConcept().setText(visitConcept.concept_name);
    }

    public Extension getVisitTypeExtension(int visitTypeConceptId) {
        ConceptEntity visitTypeConcept = ConceptDModel.INSTANCE.getConcept(visitTypeConceptId);
        if (visitTypeConcept == null) {
            return null;
        }

        return new Extension().setValue(new CodeableConcept().setText(visitTypeConcept.concept_name));
    }

    public EncounterHospitalizationComponent getHospitalizationComponent(int admittingSourceConceptId, String admittingSourceValue, int dischargeToConceptId, String dischargeToSourceValue) {
        EncounterHospitalizationComponent hospitalization = new EncounterHospitalizationComponent();

        ConceptEntity admittingSourceConcept = ConceptDModel.INSTANCE.getConcept(admittingSourceConceptId);
        if (admittingSourceConcept != null) {
            hospitalization.setAdmitSource(new CodeableConcept().setText(admittingSourceConcept.concept_name));
        }
        if (!StringUtils.isNullOrEmpty(admittingSourceValue)) {
            hospitalization.getAdmitSource().setId(admittingSourceValue);
        }

        ConceptEntity dischargeToConcept = ConceptDModel.INSTANCE.getConcept(dischargeToConceptId);
        if (dischargeToConcept != null) {
            hospitalization.setDischargeDisposition(new CodeableConcept().setText(dischargeToConcept.concept_name));
        }
        if (!StringUtils.isNullOrEmpty(dischargeToSourceValue)) {
            hospitalization.getDischargeDisposition().setId(dischargeToSourceValue);
        }

        return hospitalization;
    }
}
