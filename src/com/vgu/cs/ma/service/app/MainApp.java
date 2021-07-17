package com.vgu.cs.ma.service.app;

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
