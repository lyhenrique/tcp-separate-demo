package com.eric.client;

import com.eric.adapter.separator.NioClientSeparatorHandler;
import com.eric.adapter.separator.SeparatorDecoder;
import com.eric.adapter.separator.SeparatorEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WD41129 on 2018/9/17.
 */
public class NioSeparatorClient {

    //存放NIO客户端实例对象
    private static Map<String, NioSeparatorClient> map = new HashMap<String, NioSeparatorClient>();
    private String serverIp;
    private int serverPort;

    private static Bootstrap bootstrap;

    private EventLoopGroup group;
    private ChannelFuture channelFuture;

    private NioSeparatorClient(String host, Integer port) throws InterruptedException {
        this.serverIp = host;
        this.serverPort = port;
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new SeparatorEncoder())
                                .addLast(new SeparatorDecoder())
//                                .addLast(new LineBasedFrameDecoder(1024))
//                                .addLast(new StringEncoder())
//                                .addLast(new StringDecoder())
                                .addLast(new NioClientSeparatorHandler());
                    }
                });
        channelFuture = bootstrap.connect(serverIp, serverPort).sync();
    }

    public static NioSeparatorClient getInstance(String host, Integer port) throws InterruptedException {
        NioSeparatorClient client = null;
        if (host != null
                && host.matches("^((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))")
                && String.valueOf(port).matches("^([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$")) {
            String key = host + ":" + port;
            if (map.containsKey(key)) {
                client = map.get(key);
            } else {
                client = new NioSeparatorClient(host, port);
                map.put(key, client);
            }
        }
        return client;
    }

    public void write(Object obj) throws InterruptedException {
        if (!channelFuture.channel().isOpen()
                || !channelFuture.channel().isWritable()
                || obj == null) {
            return;
        }
        channelFuture.channel().writeAndFlush(obj).sync();
    }

    public void close() throws InterruptedException {
        channelFuture.channel().close().sync();
        group.shutdownGracefully();
        map.remove(serverIp + ":" + serverPort);
    }
}
