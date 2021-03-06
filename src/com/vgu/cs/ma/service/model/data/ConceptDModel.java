package com.vgu.cs.ma.service.model.data;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 06/03/2021
 */

import com.vgu.cs.common.logger.VLogger;
import com.vgu.cs.engine.dal.ConceptDal;
import com.vgu.cs.engine.entity.ConceptEntity;
import org.apache.logging.log4j.Logger;

public class ConceptDModel {

    public static final ConceptDModel INSTANCE = new ConceptDModel();
    private static final Logger LOGGER = VLogger.getLogger(ConceptDModel.class);

    private ConceptDModel() {

    }

    public ConceptEntity getConcept(int conceptId) {
        return ConceptDal.INSTANCE.get(conceptId);
    }
}
