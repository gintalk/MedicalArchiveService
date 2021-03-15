package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 10/03/2021
 */

import com.vgu.cs.engine.entity.CareSiteEntity;
import com.vgu.cs.engine.entity.ConceptEntity;
import com.vgu.cs.engine.entity.VisitOccurrenceEntity;
import com.vgu.cs.ma.service.model.data.CareSiteDModel;
import com.vgu.cs.ma.service.model.data.ConceptDModel;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterLocationComponent;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.codesystems.V3ActCode;

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

        EncounterLocationComponent location = getLocation(visitOccurrence.care_site_id);
        if(location != null){
            encounter.addLocation(location);
        }


    }

    public EncounterLocationComponent getLocation(int careSiteId){
        CareSiteEntity careSite = CareSiteDModel.INSTANCE.getCareSite(careSiteId);
        if(careSite == null){
            return null;
        }

        Reference locationReference= new Reference();
        locationReference.setReferenceElement(new IdType("Organization", String.valueOf(careSiteId)));
        locationReference.setDisplay(careSite.care_site_name);

        return new EncounterLocationComponent().setLocation(locationReference);
    }

    public Coding getActCoding(int visitOccurrenceConceptId) {
        ConceptEntity visitOccurrenceConcept = ConceptDModel.INSTANCE.getConcept(visitOccurrenceConceptId);
        if (visitOccurrenceConcept == null) {
            return null;
        }

        String visitOccurrenceConceptName = visitOccurrenceConcept.concept_name.toLowerCase();

        Coding actCoding = new Coding();
        if (visitOccurrenceConceptName.contains("inpatient")) {
            actCoding.setSystem(V3ActCode.IMP.getSystem());
            actCoding.setCode(V3ActCode.IMP.toCode());
            actCoding.setDisplay(V3ActCode.IMP.getDisplay());
        } else if (visitOccurrenceConceptName.toLowerCase().contains("outpatient")) {
            actCoding.setSystem(V3ActCode.AMB.getSystem());
            actCoding.setCode(V3ActCode.AMB.toCode());
            actCoding.setDisplay(V3ActCode.AMB.getDisplay());
        } else if (visitOccurrenceConceptName.toLowerCase().contains("ambulatory") || visitOccurrenceConceptName.toLowerCase().contains("office")) {
            actCoding.setSystem(V3ActCode.AMB.getSystem());
            actCoding.setCode(V3ActCode.AMB.toCode());
            actCoding.setDisplay(V3ActCode.AMB.getDisplay());
        } else if (visitOccurrenceConceptName.toLowerCase().contains("home")) {
            actCoding.setSystem(V3ActCode.HH.getSystem());
            actCoding.setCode(V3ActCode.HH.toCode());
            actCoding.setDisplay(V3ActCode.HH.getDisplay());
        } else if (visitOccurrenceConceptName.toLowerCase().contains("emergency")) {
            actCoding.setSystem(V3ActCode.EMER.getSystem());
            actCoding.setCode(V3ActCode.EMER.toCode());
            actCoding.setDisplay(V3ActCode.EMER.getDisplay());
        } else if (visitOccurrenceConceptName.toLowerCase().contains("field")) {
            actCoding.setSystem(V3ActCode.FLD.getSystem());
            actCoding.setCode(V3ActCode.FLD.toCode());
            actCoding.setDisplay(V3ActCode.FLD.getDisplay());
        } else if (visitOccurrenceConceptName.toLowerCase().contains("daytime")) {
            actCoding.setSystem(V3ActCode.SS.getSystem());
            actCoding.setCode(V3ActCode.SS.toCode());
            actCoding.setDisplay(V3ActCode.SS.getDisplay());
        } else if (visitOccurrenceConceptName.toLowerCase().contains("virtual")) {
            actCoding.setSystem(V3ActCode.VR.getSystem());
            actCoding.setCode(V3ActCode.VR.toCode());
            actCoding.setDisplay(V3ActCode.VR.getDisplay());
        } else {
            actCoding = null;
        }

        return actCoding;
    }
}
