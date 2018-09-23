package com.eric.adapter.head;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import javax.xml.bind.DatatypeConverter;
import java.util.List;

/**
 * 自定义报文解码器
 * Created by WD41129 on 2018/9/11.
 */
public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    private byte[] remainingBytes;

    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ByteBuf buf = null;
        if (remainingBytes == null) {
            buf = msg;
        } else {
            //先将上一个请求中粘连的信息添加入tb中
            byte[] tb = new byte[remainingBytes.length + msg.readableBytes()];
            System.arraycopy(remainingBytes, 0, tb, 0, remainingBytes.length);
            //再将这次请求的信息添加
            byte[] vb = new byte[msg.readableBytes()];
            msg.readBytes(vb);
            System.arraycopy(vb, 0, tb, remainingBytes.length, vb.length);
            buf = Unpooled.copiedBuffer(tb);
        }

        while (buf.readableBytes() > 0) {
            if (!doDecode(ctx, buf, out)) {
                break;
            }
        }
        if (buf.readableBytes() > 0) {
            remainingBytes = new byte[buf.readableBytes()];
            buf.readBytes(remainingBytes);
        } else {
            remainingBytes = null;
        }
    }

    /**
     * 此方法处理同mina中doDecode方法
     * @param ctx
     * @param msg
     * @param out
     * @return
     */
    private boolean doDecode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        if (msg.readableBytes() < 4) {
            return false;
        }
        // 标记读索引位置
        msg.markReaderIndex();
        byte[] header = new byte[4];
        msg.readBytes(header);

        int len = Integer.parseInt(DatatypeConverter.printHexBinary(header), 16);
        //如果剩余的可读字节数小于长度
        if (msg.readableBytes() < len) {
            msg.resetReaderIndex();
            return false;
        }
        byte[] body = new byte[len];
        msg.readBytes(body);
        out.add(Unpooled.copiedBuffer(body));
        if (msg.readableBytes() > 0) {
            return true;
        }

        return false;
    }

    public static int byteToInt2(byte[] b) {

        int mask=0xff;
        int temp=0;
        int n=0;
        for(int i=0;i<b.length;i++){
            n<<=8;
            temp=b[i]&mask;
            n|=temp;
        }
        return n;
    }
}
