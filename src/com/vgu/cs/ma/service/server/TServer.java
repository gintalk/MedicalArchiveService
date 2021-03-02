package com.vgu.cs.ma.service.server;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 27/02/2021
 */

import com.vgu.cs.common.thrift.ThriftServer;

public class TServer {

    public boolean setupAndStart() {
        ThriftServer server = new ThriftServer("main");
        COVIDDSService.Processor<COVIDDSService.Iface> processor = new COVIDDSService.Processor<>(new COVIDDSServiceHandler());
        server.registerProcessor(processor);

        return server.start();
    }
}
