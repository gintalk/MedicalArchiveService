package com.vgu.cs.ma.service.app;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 27/02/2021
 */

import com.vgu.cs.ma.service.server.TServer;

public class MainApp {

    public static void main(String[] args) {
        TServer server = new TServer();
        if (!server.setupAndStart()) {
            System.err.println("Could not start Thrift server. Exiting");
            System.exit(-1);
        }
    }
}
