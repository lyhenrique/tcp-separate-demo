package com.eric.server;

import com.eric.adapter.websocket.MyWebSocketChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * WebSocket的启动类
 */
public class MyWebSocketServer {

    public void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new MyWebSocketChannelHandler());
            System.out.println("服务端开启等待客户端连接");

            final Channel channel = bootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new MyWebSocketServer().bind(8888);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
