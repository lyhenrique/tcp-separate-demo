package com.eric.adapter.head;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Created by WD41129 on 2018/9/12.
 */
public class NioServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //与客户端建立连接后
        System.out.println("服务端与客户端连接激活");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = null;
        if (msg instanceof ByteBuf) {
            ByteBuf bb = (ByteBuf) msg;
            str = bb.toString(CharsetUtil.UTF_8);
        } else if (msg != null) {
            str = msg.toString();
        }
        System.out.println("服务端收到客户端消息：" + str);
    }

    /**
     * 当读取不到消息后时触发（会受到粘包、断包等影响，所以未必是客户定义的一个数据包读取完成即调用）
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端读取客户端上送消息完毕！");
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("服务端已经收到消息了", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("服务端通道异常，异常消息："  + cause.getMessage());
        ctx.close();
    }
}
