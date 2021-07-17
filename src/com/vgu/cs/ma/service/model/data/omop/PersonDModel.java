package com.vgu.cs.ma.service.model.data.omop;

import com.vgu.cs.common.logger.VLogger;
import com.vgu.cs.engine.dal.PersonDal;
import com.vgu.cs.engine.entity.omop.PersonEntity;
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
