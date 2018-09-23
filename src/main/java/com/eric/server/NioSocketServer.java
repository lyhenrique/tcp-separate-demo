package com.eric.server;

import com.eric.adapter.head.MessageDecoder;
import com.eric.adapter.head.MessageEncoder;
import com.eric.adapter.head.NioServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by WD41129 on 2018/9/12.
 */
public class NioSocketServer {

    private static NioSocketServer server;//声明server单例对象

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    private NioSocketServer(int port) {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)//允许连接数
                .localAddress(port)//监听端口
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new MessageEncoder())
                                .addLast(new MessageDecoder())
                                .addLast(new NioServerHandler());
                    }
                });
    }

    public static NioSocketServer getInstance(int port) {
        if (server == null) {
            synchronized (NioSocketServer.class) {
                if (server == null) {
                    server = new NioSocketServer(port);
                }
            }
        }
        return server;
    }

    //打开server
    public boolean bind() throws InterruptedException {
        channelFuture = bootstrap.bind().sync();
        channelFuture.channel().closeFuture().sync();
        return channelFuture.isSuccess();
    }

    //关闭server
    public void close() throws InterruptedException {
        channelFuture.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
