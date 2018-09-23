package com.eric.adapter.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 用于初始化连接时候的各个组件
 */
public class MyWebSocketChannelHandler extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //http编码解码器
        socketChannel.pipeline().addLast("http-codec", new HttpServerCodec());
        //可以聚合HTTP消息，消除了断裂消息，保证了消息的完整
        socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        //在一边产生数据，一边发给客户端情况下使用
        socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        socketChannel.pipeline().addLast(new MyWebSocketHandler());
    }
}
