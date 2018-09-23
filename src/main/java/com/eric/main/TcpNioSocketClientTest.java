package com.eric.main;

import com.eric.client.NioSocketClient;

/**
 * NioSocketClient测试类
 * Created by WD41129 on 2018/9/12.
 */
public class TcpNioSocketClientTest {

    public static void main(String[] args) throws InterruptedException {
        NioSocketClient client = NioSocketClient.getInstance("127.0.0.1", 8888);
        for (int i=0; i<100; i++) {
            StringBuffer sb = new StringBuffer();
            sb.append("This is the " + i+1 + " msg for head and body!");
            client.write(sb.toString());
        }
    }
}
