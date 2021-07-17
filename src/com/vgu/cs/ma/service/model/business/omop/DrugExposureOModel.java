package com.vgu.cs.ma.service.model.business.omop;

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.omop.DrugExposureEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.MedicationStatement;
import org.hl7.fhir.dstu3.model.Period;

public class DrugExposureOModel {
    
    public static final DrugExposureOModel INSTANCE = new DrugExposureOModel();
    
    private DrugExposureOModel() {
    
    }
    
    /**
     * Unretained fields include:
     * - drug_exposure_start_datetime
     * - drug_exposure_end_datetime
     * - verbatim_end_date
     * - drug_type_concept_id
     * - stop_reason
     * - sig
     * - route_concept_id
     * - lot_number
     * - provider_id
     * - drug_source_concept_id
     * - route_source_value
     * - does_unit_source_value
     */
    public DrugExposureEntity constructOmop(MedicationStatement medicationStatement) {
        DrugExposureEntity drugExposure = new DrugExposureEntity();
        
        _setId(medicationStatement, drugExposure);
        _setMedication(medicationStatement, drugExposure);
        _setVisitOccurrenceId(medicationStatement, drugExposure);
        _setDrugConceptIdAndSourceValue(medicationStatement, drugExposure);
        _setPersonId(medicationStatement, drugExposure);
        _setPeriod(medicationStatement, drugExposure);
        
        return drugExposure;
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * DRUG_EXPOSURE.drug_exposure_id = MedicationStatement.id
     */
    private void _setId(MedicationStatement medicationStatement, DrugExposureEntity drugExposure) {
        drugExposure.drug_exposure_id = ConvertUtils.toInteger(medicationStatement.getId());
    }
    
    /**
     * Maps MedicationStatement.basedOn (MedicationRequest) to DRUG_EXPOSURE's refills, quantity, days_supply
     */
    private void _setMedication(MedicationStatement medicationStatement, DrugExposureEntity drugExposure) {
        Extension basedOnExtension = null;
        for (Extension extension : medicationStatement.getExtension()) {
            if (!"based-on".equals(extension.getUserString("name"))) {
                continue;
            }
            
            basedOnExtension = extension;
            break;
        }
        if (basedOnExtension == null) {
            return;
        }
        
        drugExposure.refills = basedOnExtension.getUserInt("number-of-repeats-allowed");
        drugExposure.quantity = basedOnExtension.getUserInt("quantity");
        drugExposure.days_supply = basedOnExtension.getUserInt("expected-supply-duration");
    }
    
    /**
     * DRUG_EXPOSURE.visit_occurrence_id = MedicationStatement.context.id
     */
    private void _setVisitOccurrenceId(MedicationStatement medicationStatement, DrugExposureEntity drugExposure) {
        drugExposure.visit_occurrence_id = VisitOccurrenceOModel.INSTANCE.getVisitOccurrenceIdFromReference(medicationStatement.getContext());
    }
    
    /**
     * Maps MedicationStatement.medication to DRUG_EXPOSURE's drug_concept_id and drug_source_value
     */
    private void _setDrugConceptIdAndSourceValue(MedicationStatement medicationStatement, DrugExposureEntity drugExposure) {
        CodeableConcept medicationCodeable = medicationStatement.getMedicationCodeableConcept();
        if (medicationCodeable == null) {
            return;
        }
        
        drugExposure.drug_concept_id = CodeableConceptUtils.getConceptId(medicationCodeable);
        drugExposure.drug_source_value = medicationCodeable.getId();
    }
    
    /**
     * DRUG_EXPOSURE.person_id = MedicationStatement.subject.id
     */
    private void _setPersonId(MedicationStatement medicationStatement, DrugExposureEntity drugExposure) {
        drugExposure.person_id = PersonOModel.INSTANCE.getIdFromReference(medicationStatement.getSubject());
    }
    
    /**
     * DRUG_EXPOSURE.drug_exposure_start_date = MedicationStatement.effectivePeriod.start
     * DRUG_EXPOSURE.drug_exposure_end_date = MedicationStatement.effectivePeriod.end
     */
    private void _setPeriod(MedicationStatement medicationStatement, DrugExposureEntity drugExposure) {
        Period period = (Period) medicationStatement.getEffective();
        if (period == null) {
            return;
        }
        
        if (period.getStart() != null) {
            drugExposure.drug_exposure_start_date = DateTimeUtils.getDateString(period.getStart());
        }
        if (period.getEnd() != null) {
            drugExposure.drug_exposure_end_date = DateTimeUtils.getDateString(period.getEnd());
        }
    }
}
