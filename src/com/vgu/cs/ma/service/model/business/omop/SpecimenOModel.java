package com.vgu.cs.ma.service.model.business.omop;

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.omop.SpecimenEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.Specimen;

public class SpecimenOModel {

    public static final SpecimenOModel INSTANCE = new SpecimenOModel();

    private SpecimenOModel() {

    }

    public SpecimenEntity constructOmop(Specimen fSpecimen) {
        SpecimenEntity oSpecimen = new SpecimenEntity();

        _setId(fSpecimen, oSpecimen);
        _setDiseaseStatusConceptIdAndSourceValue(fSpecimen, oSpecimen);
        _setPersonId(fSpecimen, oSpecimen);
        _setConceptIdAndSourceValue(fSpecimen, oSpecimen);
        _setTypeConceptId(fSpecimen, oSpecimen);
        _setDate(fSpecimen, oSpecimen);
        _setQuantity(fSpecimen, oSpecimen);

        return oSpecimen;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * SPECIMEN.specimen_id = Specimen.id
     */
    private void _setId(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        oSpecimen.specimen_id = ConvertUtils.toInteger(fSpecimen.getId());
    }

    /**
     * Maps Specimen.Extension (Proposed Name: disease-status-code : CodeableConcept) to SPECIMEN's disease_status_concept_id and disease_status_source_value
     */
    private void _setDiseaseStatusConceptIdAndSourceValue(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        CodeableConcept diseaseStatusCodeable = null;
        for (Extension extension : fSpecimen.getExtension()) {
            if (!"disease-status-code".equals(extension.getUserString("name"))) {
                continue;
            }

            diseaseStatusCodeable = (CodeableConcept) extension.getValue();
            break;
        }
        if (diseaseStatusCodeable == null) {
            return;
        }

        oSpecimen.disease_status_concept_id = CodeableConceptUtils.getConceptId(diseaseStatusCodeable);
        oSpecimen.disease_status_source_value = diseaseStatusCodeable.getId();
    }

    /**
     * SPECIMEN.person_id = Specimen.subject.id
     */
    private void _setPersonId(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        oSpecimen.person_id = PersonOModel.INSTANCE.getIdFromReference(fSpecimen.getSubject());
    }

    /**
     * Maps Specimen.type to SPECIMEN.specimen_type_concept_id
     */
    private void _setConceptIdAndSourceValue(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        CodeableConcept typeCodeable = fSpecimen.getType();
        if (typeCodeable == null) {
            return;
        }

        oSpecimen.specimen_concept_id = CodeableConceptUtils.getConceptId(typeCodeable);
        oSpecimen.specimen_source_value = typeCodeable.getId();
    }

    /**
     * Maps Specimen.Extension (Proposed Name: source-data-type : CodeableConcept) to SPECIMEN.specimen_type_concept_id
     */
    private void _setTypeConceptId(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        CodeableConcept typeCodeable = null;
        for (Extension extension : fSpecimen.getExtension()) {
            if (!"source-data-type".equals(extension.getUserString("name"))) {
                continue;
            }

            typeCodeable = (CodeableConcept) extension.getValue();
            break;
        }
        if (typeCodeable == null) {
            return;
        }

        oSpecimen.specimen_type_concept_id = CodeableConceptUtils.getConceptId(typeCodeable);
    }

    /**
     * Maps Specimen.collection.collected to SPECIMEN.specimen_date
     */
    private void _setDate(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        DateTimeType dateTimeType = fSpecimen.getCollection().getCollectedDateTimeType();
        if (dateTimeType == null) {
            return;
        }

        oSpecimen.specimen_date = DateTimeUtils.getDateString(dateTimeType.toCalendar().getTime());
    }

    /**
     * Maps Specimen.collection.quantity to SPECIMEN's quantity, unit_concept_id, unit_source_value
     */
    private void _setQuantity(Specimen fSpecimen, SpecimenEntity oSpecimen) {
        SimpleQuantity quantity = fSpecimen.getCollection().getQuantity();
        if (quantity == null) {
            return;
        }

        oSpecimen.quantity = quantity.getValue().intValue();
        oSpecimen.unit_concept_id = ConvertUtils.toInteger(quantity.getCode());
        oSpecimen.unit_source_value = quantity.getId();
    }
}
