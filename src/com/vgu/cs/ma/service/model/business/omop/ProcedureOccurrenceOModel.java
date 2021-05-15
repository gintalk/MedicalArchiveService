package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 29/04/2021
 */

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.omop.ProcedureOccurrenceEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Procedure;

public class ProcedureOccurrenceOModel {
    
    public static final ProcedureOccurrenceOModel INSTANCE = new ProcedureOccurrenceOModel();
    
    private ProcedureOccurrenceOModel() {
    
    }

    /**
     * Unretained fields include:
     * - procedure_datetime
     * - visit_occurrence_id
     */
    public ProcedureOccurrenceEntity constructOmop(Procedure procedure) {
        ProcedureOccurrenceEntity procedureOccurrence = new ProcedureOccurrenceEntity();
        
        _setId(procedure, procedureOccurrence);
        _setPersonId(procedure, procedureOccurrence);
        _setConceptIdAndSourceValue(procedure, procedureOccurrence);
        _setDate(procedure, procedureOccurrence);
        _setTypeConceptId(procedure, procedureOccurrence);
        _setQuantity(procedure, procedureOccurrence);
        _setProviderId(procedure, procedureOccurrence);
        
        return procedureOccurrence;
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * PROCEDURE_OCCURRENCE.procedure_occurrence_id = Procedure.id
     */
    private void _setId(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        procedureOccurrence.procedure_occurrence_id = ConvertUtils.toInteger(procedure.getId());
    }
    
    /**
     * PROCEDURE_OCCURRENCE.person_id = Procedure.subject.id
     */
    private void _setPersonId(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        procedureOccurrence.person_id = PersonOModel.INSTANCE.getIdFromReference(procedure.getSubject());
    }
    
    /**
     * Maps Procedure.code to PROCEDURE_OCCURRENCE's procedure_concept_id and procedure_source_value
     */
    private void _setConceptIdAndSourceValue(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        CodeableConcept codeable = procedure.getCode();
        if (codeable == null) {
            return;
        }
        
        procedureOccurrence.procedure_concept_id = CodeableConceptUtils.getConceptId(codeable);
        procedureOccurrence.procedure_source_value = codeable.getId();
    }
    
    /**
     * PROCEDURE_OCCURRENCE.procedure_date = Procedure.performed
     */
    private void _setDate(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        DateTimeType dateTimeType = procedure.getPerformedDateTimeType();
        if (dateTimeType == null) {
            return;
        }
        
        procedureOccurrence.procedure_date = DateTimeUtils.getDateString(dateTimeType.toCalendar().getTime());
    }
    
    /**
     * Maps Procedure.Extension (Proposed Name: source-data-type : CodeableConcept) to PROCEDURE_OCCURRENCE.procedure_type_concept_id
     */
    private void _setTypeConceptId(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        CodeableConcept typeCodeable = null;
        for (Extension extension : procedure.getExtension()) {
            if (!"source-data-type".equals(extension.getUserString("name"))) {
                continue;
            }
            
            typeCodeable = (CodeableConcept) extension.getValue();
            break;
        }
        if (typeCodeable == null) {
            return;
        }
        
        procedureOccurrence.procedure_type_concept_id = CodeableConceptUtils.getConceptId(typeCodeable);
    }
    
    /**
     * Maps Procedure.Extension (Proposed Name: num-of-procedures : CodeableConcept) to PROCEDURE_OCCURRENCE.quantity
     */
    private void _setQuantity(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        CodeableConcept quantityCodeable = null;
        for (Extension extension : procedure.getExtension()) {
            if (!"num-of-procedures".equals(extension.getUserString("name"))) {
                continue;
            }
            
            quantityCodeable = (CodeableConcept) extension.getValue();
            break;
        }
        if (quantityCodeable == null) {
            return;
        }
        
        procedureOccurrence.quantity = ConvertUtils.toInteger(quantityCodeable.getText());
    }
    
    /**
     * PROCEDURE_OCCURRENCE.provider_id = Procedure.performer[0].actor.id
     */
    private void _setProviderId(Procedure procedure, ProcedureOccurrenceEntity procedureOccurrence) {
        Procedure.ProcedurePerformerComponent component = procedure.getPerformerFirstRep();
        if (component == null) {
            return;
        }
        
        procedureOccurrence.provider_id = ProviderOModel.INSTANCE.getProviderIdFromReference(component.getActor());
    }
}
