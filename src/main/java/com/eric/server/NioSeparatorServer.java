package com.eric.server;

import com.eric.adapter.separator.NioServerSeparatorHandler;
import com.eric.adapter.separator.SeparatorDecoder;
import com.eric.adapter.separator.SeparatorEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by WD41129 on 2018/9/17.
 */
public class NioSeparatorServer {

    private static NioSeparatorServer nioSeparatorServer;

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    private NioSeparatorServer(int port) {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler(LogLevel.INFO))
                .localAddress(port)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new SeparatorEncoder())
                                .addLast(new SeparatorDecoder())
//                                .addLast(new LineBasedFrameDecoder(1024))
//                                .addLast(new StringEncoder())
//                                .addLast(new StringDecoder())
                                .addLast(new NioServerSeparatorHandler());
                    }
                });
    }

    public static NioSeparatorServer getInstance(int port) {
        if (nioSeparatorServer == null) {
            nioSeparatorServer = new NioSeparatorServer(port);
        }
        return nioSeparatorServer;
    }

    //打开server
    public boolean bind() throws InterruptedException {
        channelFuture = bootstrap.bind().sync();
        channelFuture.channel().closeFuture().sync();
        return channelFuture.isSuccess();
    }

    //关闭server
    public void close() {
        channelFuture.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {
        NioSeparatorServer server = NioSeparatorServer.getInstance(8889);
        server.bind();
    }
}
