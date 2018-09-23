package com.eric.adapter.head;

import com.eric.utils.FormatUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 自定义报文编码类
 * Created by WD41129 on 2018/9/11.
 */
public class MessageEncoder extends MessageToMessageEncoder<Object> {


    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (msg == null) {
            return;
        }

        //用缓存获取消息
        ByteBuf buf = null;
        if (msg instanceof byte[]) {
            buf = Unpooled.copiedBuffer((byte[]) msg);
        } else if (msg instanceof ByteBuf) {
            buf = (ByteBuf) msg;
        } else if (msg instanceof ByteBuffer) {
            buf = Unpooled.copiedBuffer((ByteBuffer) msg);
        } else {
            String ostr = msg.toString();
            buf = Unpooled.copiedBuffer(ostr, CharsetUtil.UTF_8);
        }

        //数据包
        byte[] pkg = new byte[4 + buf.readableBytes()];
        //报文包头,头部四个字节保存报文长度
        byte[] header = FormatUtils.intToBytesHighToLow(buf.readableBytes());
        //包体
        byte[] body = new byte[buf.readableBytes()];

        buf.readBytes(body);
        System.arraycopy(header, 0, pkg, 0, header.length);
        System.arraycopy(body, 0, pkg, header.length, body.length);

        out.add(Unpooled.copiedBuffer(pkg));
    }
}
