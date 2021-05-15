package com.vgu.cs.ma.service.model.business.omop;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 08/05/2021
 */

import com.vgu.cs.common.util.ConvertUtils;
import com.vgu.cs.common.util.DateTimeUtils;
import com.vgu.cs.engine.entity.omop.DeviceExposureEntity;
import com.vgu.cs.ma.service.util.CodeableConceptUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Procedure;

public class DeviceExposureOModel {

    public static final DeviceExposureOModel INSTANCE = new DeviceExposureOModel();

    private DeviceExposureOModel() {

    }

    /**
     * Unretained fields include:
     * - device_concept_id
     * - device_exposure_start_datetime
     * - device_exposure_end_datetime
     * - unique_device_id
     * - visit_occurrence_id
     * - device_source_value
     * - device_source_concept_id
     */
    public DeviceExposureEntity constructOmop(Procedure procedure) {
        DeviceExposureEntity deviceExposure = new DeviceExposureEntity();

        _setId(procedure, deviceExposure);
        _setQuantity(procedure, deviceExposure);
        _setProviderId(procedure, deviceExposure);
        _setPersonId(procedure, deviceExposure);
        _setDate(procedure, deviceExposure);
        _setDeviceTypeConceptId(procedure, deviceExposure);

        return deviceExposure;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * DEVICE_OCCURRENCE.device_occurrence_id = Procedure.id
     */
    private void _setId(Procedure procedure, DeviceExposureEntity deviceExposure) {
        deviceExposure.device_exposure_id = ConvertUtils.toInteger(procedure.getId());
    }

    /**
     * Maps Procedure.Extension (Proposed Name: num-of-procedures : CodeableConcept) to DEVICE_EXPOSURE.quantity
     */
    private void _setQuantity(Procedure procedure, DeviceExposureEntity deviceExposure) {
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

        deviceExposure.quantity = ConvertUtils.toInteger(quantityCodeable.getText());
    }

    /**
     * DEVICE_EXPOSURE.provider_id = Procedure.performer[0].actor.id
     */
    private void _setProviderId(Procedure procedure, DeviceExposureEntity deviceExposure) {
        Procedure.ProcedurePerformerComponent component = procedure.getPerformerFirstRep();
        if (component == null) {
            return;
        }

        deviceExposure.provider_id = ProviderOModel.INSTANCE.getProviderIdFromReference(component.getActor());
    }

    /**
     * DEVICE_EXPOSURE.person_id = Procedure.subject.id
     */
    private void _setPersonId(Procedure procedure, DeviceExposureEntity deviceExposure) {
        deviceExposure.person_id = PersonOModel.INSTANCE.getIdFromReference(procedure.getSubject());
    }

    /**
     * Maps Procedure.period to DEVICE_EXPOSURE's device_exposure_start_date and device_exposure_end_date
     */
    private void _setDate(Procedure procedure, DeviceExposureEntity deviceExposure) {
        Period period = procedure.getPerformedPeriod();
        if (period == null) {
            return;
        }

        if (period.getStart() != null) {
            deviceExposure.device_exposure_start_date = DateTimeUtils.getDateString(period.getStart());
        }

        if (period.getEnd() != null) {
            deviceExposure.device_exposure_end_date = DateTimeUtils.getDateString(period.getEnd());
        }
    }

    /**
     * Maps Procedure.Extension (Proposed Name: raw-value : CodeableConcept) to DEVICE_EXPOSURE.device_type_concept_id
     */
    private void _setDeviceTypeConceptId(Procedure procedure, DeviceExposureEntity deviceExposure) {
        CodeableConcept typeCodeable = null;
        for (Extension extension : procedure.getExtension()) {
            if (!"raw-value".equals(extension.getUserString("name"))) {
                continue;
            }

            typeCodeable = (CodeableConcept) extension.getValue();
            break;
        }
        if (typeCodeable == null) {
            return;
        }

        deviceExposure.device_type_concept_id = CodeableConceptUtils.getConceptId(typeCodeable);
    }
}
