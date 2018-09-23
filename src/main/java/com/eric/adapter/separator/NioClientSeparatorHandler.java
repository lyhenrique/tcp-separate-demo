package com.eric.adapter.separator;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 分隔符的客户端处理handler
 */
public class NioClientSeparatorHandler extends ChannelHandlerAdapter {

    private volatile int count;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("分隔符的客户端处理消息出现问题了, 错误消息是: " + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //与服务端建立连接后
        System.out.println("分隔符的客户端-服务端连接通道激活！");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = null;
        if (msg instanceof ByteBuf) {
            ByteBuf bb = (ByteBuf) msg;
            byte[] b = new byte[bb.readableBytes()];
            bb.readBytes(b);
            str = new String(b, CharsetUtil.UTF_8);
        } else if (msg != null) {
            str = msg.toString();
        }

        count++;
        System.out.println("分隔符的客户端第" + count + "次收到消息：" + str);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("分隔符的客服端读取服务端下发消息完毕！");
    }
}
