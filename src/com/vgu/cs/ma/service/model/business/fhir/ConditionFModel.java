package com.vgu.cs.ma.service.model.business.fhir;

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.ConditionOccurrenceEntity;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.model.business.omop.ProviderOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;

import java.util.Date;

/**
 * <p>
 * A clinical condition, problem, diagnosis, or other event, situation, issue, or clinical concept that has risen to a
 * level of concern.
 * </p>
 * <p>
 * The class <code>ConditionFModel</code> constructs <code>Condition</code>> from any record in the OMOP-compliant
 * table <code>condition_occurrence</code>.
 * </p>
 * <p>
 * The <code>ConditionFModel</code> class contains one single public method accepting a <code>ConditionOccurrenceEntity</code>, which
 * represents a record in <code>condition_occurrence</code>, and returns a FHIR-compliant <code>Condition</code>.
 * </p>
 *
 * @author namnh16 on 19/03/2021
 * @see <a href="http://build.fhir.org/ig/HL7/cdmh/profiles.html#omop-to-fhir-mappings">OMOP to FHIR mappings</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#CONDITION_OCCURRENCE">OMOP CONDITION_OCCURRENCE</a>
 * @see <a href="https://www.hl7.org/fhir/condition.html">FHIR Condition</a>
 */
public class ConditionFModel {

    public static final ConditionFModel INSTANCE = new ConditionFModel();

    private ConditionFModel() {

    }

    public Condition constructFhir(ConditionOccurrenceEntity conditionOccurrence) {
        Condition condition = new Condition();

        _addId(condition, conditionOccurrence);
        _addAsserterReference(condition, conditionOccurrence);
        _addClinicalStatus(condition, conditionOccurrence);
        _addSubjectReference(condition, conditionOccurrence);
        _addCode(condition, conditionOccurrence);
        _addOnsetDateTime(condition, conditionOccurrence);
        _addAbatementDateTime(condition, conditionOccurrence);
        _addRawValueExtension(condition, conditionOccurrence);
        _addAbatementReasonExtension(condition, conditionOccurrence);

        return condition;
    }

    /**
     * Corresponding FHIR field: Condition.id
     * CONDITION_OCCURRENCE.condition_occurrence_id is the unique key given to a condition record for a person.
     */
    private void _addId(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        condition.setId(new IdType(String.valueOf(conditionOccurrence.condition_occurrence_id)));
    }

    /**
     * Corresponding FHIR field: Condition.asserter
     * Person who asserts this condition. CONDITION_OCCURRENCE.provider_id identifies the provider associated with
     * condition record, e.g. the provider who made the diagnosis or the provider who recorded the symptom.
     */
    private void _addAsserterReference(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        condition.setAsserter(ProviderOModel.INSTANCE.getReference(conditionOccurrence.provider_id));
    }

    /**
     * Corresponding FHIR field: Condition.clinicalStatus
     *
     * @see <a href="https://www.hl7.org/fhir/valueset-condition-clinical.html">Available values for clinical status</a>
     * Due to a lack of corresponding field in the OMOP table <code>condition_occurrence</code>, this field is set to
     * ACTIVE.
     */
    private void _addClinicalStatus(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        condition.setClinicalStatus(ConditionClinicalStatus.ACTIVE);
    }

    /**
     * Corresponding FHIR field: Condition.subject
     * The person for whom the condition is recorded.
     */
    private void _addSubjectReference(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        condition.setSubject(PersonOModel.INSTANCE.getReference(conditionOccurrence.person_id));
    }

    /**
     * Corresponding FHIR field: Condition.code
     * Identification of the condition, problem or diagnosis. CONDITION_OCCURRENCE.condition_concept_id is the standard
     * concept mapped from the source value which represents a condition. CONDITION_OCCURRENCE.condition_source_value
     * houses the verbatim value from the source data representing the condition that occurred.
     *
     * @see <a href="https://www.hl7.org/fhir/valueset-condition-code.html">Available values for condition code</a>
     */
    private void _addCode(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        CodeableConcept conditionCodeable = CodeableConceptUtils.fromConceptId(conditionOccurrence.condition_concept_id);
        if (conditionCodeable == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(conditionOccurrence.condition_source_value)) {
            conditionCodeable.setId(conditionOccurrence.condition_source_value);
        }
        condition.setCode(conditionCodeable);
    }

    /**
     * Corresponding FHIR field: Condition.onset[x]
     * Estimated or actual date, date-time, or age. CONDITION_OCCURRENCE.condition_start_datetime is used to determine
     * the start date of the condition.
     */
    private void _addOnsetDateTime(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        Date dateOrDatetime = DateTimeUtils.parseDateOrDateTime(conditionOccurrence.condition_start_date, conditionOccurrence.condition_start_datetime);
        if (dateOrDatetime == null) {
            return;
        }
        condition.setOnset(new DateTimeType(dateOrDatetime));
    }

    /**
     * Corresponding FHIR field: Condition.onset[x]
     * When in resolution/remission. CONDITION_OCCURRENCE.condition_end_datetime is used to determine the end date of
     * the condition.
     */
    private void _addAbatementDateTime(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        Date dateOrDatetime = DateTimeUtils.parseDateOrDateTime(conditionOccurrence.condition_end_date, conditionOccurrence.condition_end_datetime);
        if (dateOrDatetime == null) {
            return;
        }
        condition.setAbatement(new DateTimeType(dateOrDatetime));
    }

    /**
     * Corresponding FHIR field: Condition.Extension (Proposed Name: raw-value : CodeableConcept)
     * CONDITION_OCCURRENCE.condition_type_concept_id can be used to determine the provenance of the Condition record,
     * as in whether the condition was from an EHR system, insurance claim, registry, or other sources.
     */
    private void _addRawValueExtension(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        CodeableConcept conditionTypeCodeable = CodeableConceptUtils.fromConceptId(conditionOccurrence.condition_type_concept_id);
        if (conditionTypeCodeable == null) {
            return;
        }

        Extension rawValueExtension = new Extension();
        rawValueExtension.setProperty("raw-value", conditionTypeCodeable);

        condition.addExtension(rawValueExtension);
    }

    /**
     * Corresponding FHIR field: Condition.Extension (Proposed Name: abatement-reason : CodeableConcept)
     * CONDITION_OCCURRENCE.stop_reason indicates why a Condition is no longer valid with respect to the purpose within
     * the source data. Note that a Stop Reason does not necessarily imply that the condition is no longer occurring.
     */
    private void _addAbatementReasonExtension(Condition condition, ConditionOccurrenceEntity conditionOccurrence) {
        CodeableConcept stopReasonCodeable = CodeableConceptUtils.fromText(conditionOccurrence.stop_reason);
        if (stopReasonCodeable == null) {
            return;
        }

        Extension abatementReasonExtension = new Extension();
        abatementReasonExtension.setProperty("abatement-reason", stopReasonCodeable);

        condition.addExtension(abatementReasonExtension);
    }
}
