package com.eric.adapter.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.util.Date;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //将信息强转为bytebuf
        ByteBuf buf = (ByteBuf) msg;

        //通过ByteBuf的readableBytes方法可以获取缓冲区可读的字节数，
        //根据可读的字节数创建byte数组
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);

        String body = new String(req, "UTF-8");
        System.out.println("The TimeServer received order : " + body);

        //如果是QUERY TIME ORDER则创建应答消息
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                new Date(System.currentTimeMillis()).toString() : "BAD ORDER";

        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        //通过ChannelHandlerContext的write方法异步发送应答消息到客户端
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //调用了ChannelHandlerContext的flush方法，它的作用是将消息发送队列中的消息写入到SocketChannel中发送给对方。
        //从性能角度考虑，为了防止频繁地唤醒Selector进行消息发送，
        //Netty的write方法并不直接将消息写入SocketChannel中，调用write方法只是把待发送的消息放到发送缓冲数组中，
        //再通过调用flush方法，将发送缓冲区中的消息全部写到SocketChannel中。
        ctx.flush();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Server发生了异常 : " + cause.getMessage());
        ctx.close();
    }
}
