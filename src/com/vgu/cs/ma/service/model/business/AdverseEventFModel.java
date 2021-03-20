package com.vgu.cs.ma.service.model.business;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 19/03/2021
 */

import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.DeathEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtil;
import org.hl7.fhir.dstu3.model.AdverseEvent;
import org.hl7.fhir.dstu3.model.AdverseEvent.AdverseEventSuspectEntityComponent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Extension;

import java.util.Date;

public class AdverseEventFModel extends FhirOmopModel {

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

    private void _addSubjectReference(AdverseEvent adverseEvent, DeathEntity death) {
        adverseEvent.setSubject(PersonOModel.INSTANCE.getReference(death.person_id));
    }

    private void _addDate(AdverseEvent adverseEvent, DeathEntity death) {
        Date dateOrDateTime = DateTimeUtils.parseDateOrDateTime(death.death_date, death.death_datetime);
        if (dateOrDateTime == null) {
            return;
        }
        adverseEvent.setDate(dateOrDateTime);
    }

    private void _addDeathTypeComponent(AdverseEvent adverseEvent, DeathEntity death) {
        CodeableConcept deathTypeCodeable = CodeableConceptUtil.fromConceptId(death.death_type_concept_id);
        if (deathTypeCodeable == null) {
            return;
        }

        Extension deathTypeExtension = new Extension();
        deathTypeExtension.setProperty("cause-code", deathTypeCodeable);

        AdverseEventSuspectEntityComponent component = new AdverseEventSuspectEntityComponent();
        component.addExtension(deathTypeExtension);

        adverseEvent.addSuspectEntity(component);
    }

    private void _addCauseComponent(AdverseEvent adverseEvent, DeathEntity death) {
        CodeableConcept causeCodeable = CodeableConceptUtil.fromConceptId(death.cause_concept_id);
        if (causeCodeable == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(death.cause_source_value)) {
            causeCodeable.setId(death.cause_source_value);
        }

        Extension causeExtension = new Extension();
        causeExtension.setProperty("cause-code", causeCodeable);

        AdverseEventSuspectEntityComponent component = new AdverseEventSuspectEntityComponent();
        component.addExtension(causeExtension);

        adverseEvent.addSuspectEntity(component);
    }
}
