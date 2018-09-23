package com.eric.adapter.separator;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 通过分隔符的方式处理拆包粘包问题的handler
 */
public class NioServerSeparatorHandler extends ChannelHandlerAdapter {

    private volatile int count;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("通过分隔符处理拆包粘包问题出现异常,异常原因是:" + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //与客户端建立连接后
        System.out.println("分隔符的服务端与客户端连接激活");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = null;
        if (msg instanceof ByteBuf) {
            ByteBuf bb = (ByteBuf) msg;
            str = bb.toString(CharsetUtil.UTF_8);
        } else if (msg != null){
            str = msg.toString();
        }
        count++;
        System.out.println("分隔符的服务端第" + count + "次收到客户端消息：" + str);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("分隔符的服务端读取客户端上送消息完毕！");
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("分隔符的服务端已经收到消息了", CharsetUtil.UTF_8));
    }
}
