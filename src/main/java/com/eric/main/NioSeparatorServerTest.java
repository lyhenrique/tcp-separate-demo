package com.eric.main;

import com.eric.server.NioSeparatorServer;

public class NioSeparatorServerTest {

    public static void main(String[] args) throws InterruptedException {
        NioSeparatorServer server = NioSeparatorServer.getInstance(8888);
        server.bind();
    }
}
