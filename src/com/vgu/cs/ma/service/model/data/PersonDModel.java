package com.vgu.cs.ma.service.model.data;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 06/03/2021
 */

import com.vgu.cs.common.logger.VLogger;
import com.vgu.cs.engine.dal.PersonDal;
import com.vgu.cs.engine.entity.PersonEntity;
import org.apache.logging.log4j.Logger;

public class PersonDModel {
    public static final PersonDModel INSTANCE = new PersonDModel();
    private static final Logger LOGGER = VLogger.getLogger(PersonDModel.class);

    private PersonDModel() {

    }

    public PersonEntity getPerson(int personId) {
        return PersonDal.INSTANCE.get(personId);
    }
}
