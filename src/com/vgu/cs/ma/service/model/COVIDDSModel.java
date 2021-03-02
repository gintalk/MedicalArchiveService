package com.vgu.cs.ma.service.model;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 27/02/2021
 */

import com.vgu.cs.common.logger.VLogger;
import org.apache.logging.log4j.Logger;

public class COVIDDSModel {

    public static final COVIDDSModel INSTANCE = new COVIDDSModel();
    private static final Logger LOGGER = VLogger.getLogger(COVIDDSModel.class);

    private COVIDDSModel() {

    }

    public int add(int a, int b) {
        return a + b;
    }
}
