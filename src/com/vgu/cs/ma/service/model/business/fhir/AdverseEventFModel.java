package com.vgu.cs.ma.service.model.business.fhir;

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.DeathEntity;
import com.vgu.cs.ma.service.model.business.omop.PersonOModel;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.AdverseEvent;
import org.hl7.fhir.dstu3.model.AdverseEvent.AdverseEventSuspectEntityComponent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Extension;

import java.util.Date;

/**
 * <p>
 * Actual or potential/avoided event causing unintended physical injury resulting from or contributed to by medical
 * care, a research study or other healthcare setting factors that requires additional monitoring, treatment, or
 * hospitalization, or that results in death.
 * </p>
 * <p>
 * The class <code>AdverseEventFModel</code> constructs <code>AdverseEvent</code>> from any record in the OMOP-compliant
 * table <code>death</code>.
 * </p>
 * <p>
 * The <code>AdverseEventFModel</code> class contains one single public method accepting a <code>DeathEntity</code>, which
 * represents a record in <code>death</code>, and returns a FHIR-compliant <code>AdverseEvent</code>.
 * </p>
 *
 * @author namnh16 on 19/03/2021
 * @see <a href="http://build.fhir.org/ig/HL7/cdmh/profiles.html#omop-to-fhir-mappings">OMOP to FHIR mappings</a>
 * @see <a href="https://ohdsi.github.io/CommonDataModel/cdm531.html#DEATH">OMOP DEATH</a>
 * @see <a href="https://www.hl7.org/fhir/adverseevent.html">FHIR Patient</a>
 */
public class AdverseEventFModel {
    
    public static final AdverseEventFModel INSTANCE = new AdverseEventFModel();
    
    private AdverseEventFModel() {
    }
    
    public AdverseEvent constructFhir(DeathEntity death) {
        AdverseEvent adverseEvent = new AdverseEvent();
        
        _addSubjectReference(adverseEvent, death);
        _addDate(adverseEvent, death);
        _addDeathTypeComponent(adverseEvent, death);
        _addCauseComponent(adverseEvent, death);
        
        return adverseEvent;
    }
    
    /**
     * Corresponding FHIR field: AdverseEvent.subject
     * Subject impacted by event.
     */
    private void _addSubjectReference(AdverseEvent adverseEvent, DeathEntity death) {
        adverseEvent.setSubject(PersonOModel.INSTANCE.getReference(death.person_id));
    }
    
    /**
     * Corresponding FHIR field: AdverseEvent.date
     * When the event occurred.
     */
    private void _addDate(AdverseEvent adverseEvent, DeathEntity death) {
        Date dateOrDateTime = DateTimeUtils.parseDateOrDateTime(death.death_date, death.death_datetime);
        if (dateOrDateTime == null) {
            return;
        }
        adverseEvent.setDate(dateOrDateTime);
    }
    
    /**
     * Corresponding FHIR field: AdverseEvent.suspectedEntity.causality.Extension (Proposed Name: cause-code : CodeableConcept)
     * Death.death_type_concept_id is the place of origin of the death record.
     */
    private void _addDeathTypeComponent(AdverseEvent adverseEvent, DeathEntity death) {
        CodeableConcept deathTypeCodeable = CodeableConceptUtils.fromConceptId(death.death_type_concept_id);
        if (deathTypeCodeable == null) {
            return;
        }
        
        Extension deathTypeExtension = new Extension();
        deathTypeExtension.setValue(deathTypeCodeable);
        
        AdverseEventSuspectEntityComponent component = new AdverseEventSuspectEntityComponent();
        component.setUserData("name", "death-type");
        component.addExtension(deathTypeExtension);
        
        adverseEvent.addSuspectEntity(component);
    }
    
    /**
     * Corresponding FHIR field: AdverseEvent.suspectedEntity.causality.Extension (Proposed Name: cause-code : CodeableConcept)
     * Information on the possible cause of the event. Death.cause_concept_id is the Standard Concept representing the
     * Person’s cause of death.
     */
    private void _addCauseComponent(AdverseEvent adverseEvent, DeathEntity death) {
        CodeableConcept causeCodeable = CodeableConceptUtils.fromConceptIdAndSourceValue(death.cause_concept_id, death.cause_source_value);
        if (causeCodeable == null) {
            return;
        }
        
        Extension causeExtension = new Extension();
        causeExtension.setValue(causeCodeable);
        
        AdverseEventSuspectEntityComponent component = new AdverseEventSuspectEntityComponent();
        component.setUserData("name", "cause");
        component.addExtension(causeExtension);
        
        adverseEvent.addSuspectEntity(component);
    }
}
