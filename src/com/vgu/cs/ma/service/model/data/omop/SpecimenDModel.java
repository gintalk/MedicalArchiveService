package com.vgu.cs.ma.service.model.data.omop;

import com.vgu.cs.engine.dal.SpecimenDal;
import com.vgu.cs.engine.entity.omop.SpecimenEntity;

public class SpecimenDModel {

    public static final SpecimenDModel INSTANCE = new SpecimenDModel();

    private SpecimenDModel() {

    }

    public SpecimenEntity getSpecimen(int specimenId) {
        return SpecimenDal.INSTANCE.get(specimenId);
    }
}
