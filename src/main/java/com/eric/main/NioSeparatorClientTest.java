package com.eric.main;

import com.eric.client.NioSeparatorClient;

/**
 * 分隔符客户端测试类
 */
public class NioSeparatorClientTest {

    public static void main(String[] args) throws InterruptedException {
        NioSeparatorClient client = NioSeparatorClient.getInstance("127.0.0.1", 8888);
        for (int i=0; i<100000; i++) {
            StringBuffer sb = new StringBuffer();
            Thread.sleep(50);
            sb.append("This is the " + Integer.valueOf(i+1) + " msg for separator by delimiter!");
//            sb.append("This is the " + Integer.valueOf(i+1) + " msg for separator by delimiter!\r\n");
            client.write(sb.toString());
        }

    }
}
