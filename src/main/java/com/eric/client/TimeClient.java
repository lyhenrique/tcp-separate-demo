package com.eric.client;

import com.eric.adapter.websocket.TimeClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {

    public void connect(String host, int port) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer () {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });

            bootstrap.remoteAddress(host, port);
            // 发起异步连接操作
            //客户端启动辅助类设置完成之后，调用connect方法发起异步连接，
            //然后调用同步方法等待连接成功。
            ChannelFuture future = bootstrap.bind(host, port).sync();

            // 等待客户端链路关闭
            //当客户端连接关闭之后，客户端主函数退出.
            future.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new TimeClient().connect("127.0.0.1", 7787);
    }
}
