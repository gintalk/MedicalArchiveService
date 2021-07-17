package com.vgu.cs.ma.service.model.business.omop;

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.omop.VisitOccurrenceEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.*;

public class VisitOccurrenceOModel {
    
    public static final VisitOccurrenceOModel INSTANCE = new VisitOccurrenceOModel();
    
    private VisitOccurrenceOModel() {
    
    }
    
    /**
     * Unretained fields include:
     * - visit_start_datetime
     * - visit_end_datetime
     * - visit_source_concept_id
     * - provider_id
     * - preceding_visit_occurrence_id
     */
    public VisitOccurrenceEntity constructOmop(Encounter encounter) {
        VisitOccurrenceEntity visitOccurrence = new VisitOccurrenceEntity();
        
        _setId(encounter, visitOccurrence);
        _setLocation(encounter, visitOccurrence);
        _setPersonId(encounter, visitOccurrence);
        _setPeriod(encounter, visitOccurrence);
        _setConceptIdAndSourceValue(encounter, visitOccurrence);
        _setVisitTypeConceptId(encounter, visitOccurrence);
        _setAdmittingSourceAndDischargeTo(encounter, visitOccurrence);
        
        return visitOccurrence;
    }
    
    public Reference getReference(int visitOccurrenceId) {
        Reference reference = new Reference(new IdType("Encounter", String.valueOf(visitOccurrenceId)));
        reference.setId(String.valueOf(visitOccurrenceId));
        
        return reference;
    }
    
    public int getVisitOccurrenceIdFromReference(Reference reference) {
        return ConvertUtils.toInteger(reference.getId());
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * VISIT_OCCURRENCE.visit_occurrence_id = Encounter.id
     */
    private void _setId(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        visitOccurrence.visit_occurrence_id = ConvertUtils.toInteger(encounter.getId());
    }
    
    /**
     * VISIT_OCCURRENCE.care_site_id = Encounter.location[0].id
     */
    private void _setLocation(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        Encounter.EncounterLocationComponent component = encounter.getLocationFirstRep();
        if (component == null || component.getLocation() == null) {
            return;
        }
        
        visitOccurrence.care_site_id = CareSiteOModel.INSTANCE.getIdFromReference(component.getLocation());
    }
    
    /**
     * VISIT_OCCURRENCE.person_id = Encounter.subject.id
     */
    private void _setPersonId(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        visitOccurrence.person_id = PersonOModel.INSTANCE.getIdFromReference(encounter.getSubject());
    }
    
    /**
     * VISIT_OCCURRENCE.visit_start_date = Encounter.period.start
     * VISIT_OCCURRENCE.visit_end_date = Encounter.period.end
     */
    private void _setPeriod(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        Period period = encounter.getPeriod();
        if (period == null) {
            return;
        }
        
        if (period.getStart() != null) {
            visitOccurrence.visit_start_date = DateTimeUtils.getDateString(period.getStart());
        }
        if (period.getEnd() != null) {
            visitOccurrence.visit_end_date = DateTimeUtils.getDateString(period.getEnd());
        }
    }
    
    /**
     * Maps Encounter.type[0] to VISIT_OCCURRENCE's visit_concept_id and visit_source_value
     */
    private void _setConceptIdAndSourceValue(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        CodeableConcept typeCodeable = encounter.getTypeFirstRep();
        if (typeCodeable == null) {
            return;
        }
        
        visitOccurrence.visit_concept_id = CodeableConceptUtils.getConceptId(typeCodeable);
        visitOccurrence.visit_source_value = typeCodeable.getId();
    }
    
    /**
     * Maps Encounter.extension (Proposed Name: source-data-type : CodeableConcept) to VISIT_OCCURRENCE.visit_type_concept_id
     */
    private void _setVisitTypeConceptId(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        CodeableConcept visitTypeCodeable = null;
        for (Extension extension : encounter.getExtension()) {
            if (!"source-date-type".equals(extension.getUserString("name"))) {
                continue;
            }
            
            visitTypeCodeable = (CodeableConcept) extension.getValue();
            break;
        }
        if (visitTypeCodeable == null) {
            return;
        }
        
        visitOccurrence.visit_type_concept_id = CodeableConceptUtils.getConceptId(visitTypeCodeable);
    }
    
    /**
     * Maps:
     * - Encounter.hospitalization.admitSource to VISIT_OCCURRENCE's admitting_source_concept_id and admitting_source_value
     * - Encounter.hospitalization.dischargeComposition to VISIT_OCCURRENCE's discharge_to_concept_id and discharge_to_source_value
     */
    private void _setAdmittingSourceAndDischargeTo(Encounter encounter, VisitOccurrenceEntity visitOccurrence) {
        Encounter.EncounterHospitalizationComponent component = encounter.getHospitalization();
        if (component == null) {
            return;
        }
        
        CodeableConcept admitSourceCodeable = component.getAdmitSource();
        if (admitSourceCodeable != null) {
            visitOccurrence.admitting_source_concept_id = CodeableConceptUtils.getConceptId(admitSourceCodeable);
            visitOccurrence.admitting_source_value = admitSourceCodeable.getId();
        }
        
        CodeableConcept dischargeToCodeable = component.getDischargeDisposition();
        if (dischargeToCodeable != null) {
            visitOccurrence.discharge_to_concept_id = CodeableConceptUtils.getConceptId(dischargeToCodeable);
            visitOccurrence.discharge_to_source_value = dischargeToCodeable.getId();
        }
    }
}
