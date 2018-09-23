package com.eric.adapter.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf firstMessage;

    public TimeClientHandler() {
        byte[] req = "QUERY TIME ORDER".getBytes();
        firstMessage = Unpooled.buffer(req.length);
        this.firstMessage.writeBytes(req);
    }

    //当客户端和服务端TCP链路建立成功之后，Netty的NIO线程会调用channelActive方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(firstMessage);
    }


    //当服务端返回应答消息时，channelRead方法被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //从Netty的ByteBuf中读取并打印应答消息。
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("Now client received : " + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Client遇到异常: " + cause.getMessage());
        ctx.close();
    }
}
