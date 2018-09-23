package com.eric.client;

import com.eric.adapter.head.MessageDecoder;
import com.eric.adapter.head.MessageEncoder;
import com.eric.adapter.head.NioClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WD41129 on 2018/9/12.
 */
public class NioSocketClient {

    //存放NIO客户端实例对象
    private static Map<String, NioSocketClient> map = new HashMap<String, NioSocketClient>();

    private String serverIp;
    private int serverPort;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private ChannelFuture channelFuture;

    private NioSocketClient(String ip, int port) throws InterruptedException {
        this.serverIp = ip;
        this.serverPort = port;
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new MessageEncoder())
                                .addLast(new MessageDecoder())
                                .addLast(new NioClientHandler());

                    }
                });
        channelFuture = bootstrap.connect(serverIp, serverPort).sync();
    }

    public static NioSocketClient getInstance(String ip, int port) throws InterruptedException {
        NioSocketClient client = null;
        if (ip != null
                && ip.matches("^((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))")
                && String.valueOf(port).matches("^([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$")) {
            String key = ip + ":" + port;
            if (map.containsKey(key)) {
                client = map.get(key);
            } else {
                client = new NioSocketClient(ip, port);
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
