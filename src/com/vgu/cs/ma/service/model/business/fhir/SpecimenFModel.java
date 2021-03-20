package com.vgu.cs.ma.service.model.business.fhir;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 20/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.SpecimenEntity;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtil;
import org.hl7.fhir.dstu3.model.*;

import java.util.Date;

public class SpecimenFModel  {

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

    private void _addId(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        fSpecimen.setId(new IdType(oSpecimen.specimen_id));
    }

    private void _addDiseaseStatusExtension(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        CodeableConcept diseaseStatusCodeable = CodeableConceptUtil.fromConceptId(oSpecimen.disease_status_concept_id);
        if (diseaseStatusCodeable == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(oSpecimen.disease_status_source_value)) {
            diseaseStatusCodeable.setId(oSpecimen.disease_status_source_value);
        }

        Extension diseaseStatusCodeExtension = new Extension();
        diseaseStatusCodeExtension.setProperty("disease-status-code", diseaseStatusCodeable);

        fSpecimen.addExtension(diseaseStatusCodeExtension);
    }

    private void _addSubjectReference(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        fSpecimen.setSubject(PersonOModel.INSTANCE.getReference(oSpecimen.person_id));
    }

    private void _addType(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        CodeableConcept specimenCodeable = CodeableConceptUtil.fromConceptId(oSpecimen.specimen_concept_id);
        if (specimenCodeable == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(oSpecimen.specimen_source_value)) {
            specimenCodeable.setId(oSpecimen.specimen_source_value);
        }

        fSpecimen.setType(specimenCodeable);
    }

    private void _addTypeExtension(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        CodeableConcept typeCodeable = CodeableConceptUtil.fromConceptId(oSpecimen.specimen_type_concept_id);
        if (typeCodeable == null) {
            return;
        }

        Extension sourceDataTypeExtension = new Extension();
        sourceDataTypeExtension.setProperty("source-data-type", typeCodeable);

        fSpecimen.addExtension(sourceDataTypeExtension);
    }

    private void _addCollectedDateTime(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        Date dateOrDateTime = DateTimeUtils.parseDateOrDateTime(oSpecimen.specimen_date, oSpecimen.specimen_datetime);
        if (dateOrDateTime == null) {
            return;
        }
        fSpecimen.getCollection().setCollected(new DateTimeType(dateOrDateTime));
    }

    private void _addQuantity(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        SimpleQuantity quantity = new SimpleQuantity();
        quantity.setValue(oSpecimen.quantity);

        CodeableConcept quantityCodeable = CodeableConceptUtil.fromConceptId(oSpecimen.unit_concept_id);
        if (quantityCodeable == null) {
            return;
        }

        quantity.setSystem(quantityCodeable.getCodingFirstRep().getSystem());
        quantity.setUnit(quantityCodeable.getCodingFirstRep().getDisplay());

        fSpecimen.getCollection().setQuantity(quantity);
    }
}
