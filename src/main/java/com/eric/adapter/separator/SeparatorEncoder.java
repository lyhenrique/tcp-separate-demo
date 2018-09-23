package com.eric.adapter.separator;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by WD41129 on 2018/9/17.
 */
public class SeparatorEncoder extends MessageToMessageEncoder<Object> {

    private static final String SEPARATOR = "\r\n";

    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (msg == null) {
            return;
        }

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

        //分隔符读入byte[]
        byte[] separatorByte = SEPARATOR.getBytes();
        //msg读入byte[]
        byte[] message = new byte[buf.readableBytes()];
        buf.readBytes(message);
        //最终的数据是信息加分隔符的长度
        byte[] totalMsg = new byte[separatorByte.length + message.length];

        //加入原始信息
        System.arraycopy(message, 0, totalMsg, 0, message.length);
        //加入分隔符
        System.arraycopy(separatorByte, 0, totalMsg, message.length, separatorByte.length);

        out.add(Unpooled.copiedBuffer(totalMsg));
    }
}
