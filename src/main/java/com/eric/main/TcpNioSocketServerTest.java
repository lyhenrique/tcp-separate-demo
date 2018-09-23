package com.eric.main;

import com.eric.server.NioSocketServer;

/**
 * NioSocketServer测试类
 * Created by WD41129 on 2018/9/12.
 */
public class TcpNioSocketServerTest {

    public static void main(String[] args) throws InterruptedException {
        NioSocketServer server = NioSocketServer.getInstance(8888);
        server.bind();
    }
}
