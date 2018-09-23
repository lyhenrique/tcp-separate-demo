package com.eric.adapter.separator;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 根据分隔符\r\n或\n拆分消息
 */
public class SeparatorDecoder extends MessageToMessageDecoder<ByteBuf> {

    private AtomicInteger count = new AtomicInteger(0);

//    //最大长度
//    private final int maxLength;
//
//    //是否超过最大长度丢弃
//    private boolean discarding = false;
//    private int discardedBytes;
//
    //最后扫描位置
    private int offset;
//
//    public SeparatorDecoder(int maxLength) {
//        this.maxLength = maxLength;
//    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        Object decoded = decode(ctx, msg);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    private Object decode(ChannelHandlerContext ctx, ByteBuf msg) throws UnsupportedEncodingException {
        if (msg == null) {
            return null;
        }

//        ByteBuf temp = msg;
//        byte[] tempByte = new byte[temp.readableBytes()];
//        msg.readBytes(tempByte);
//        System.out.println("解码器接收到的信息是：" + new String(tempByte, "UTF-8"));

        final ByteBuf frame;
        //获取此次信息是否有分隔符
        final int len = findTheSeparator(msg);
//        if (!discarding) {
            if (len >= 0) {
                //这条信息中分隔符前的长度
                int formerLen = len - msg.readerIndex();
                //分隔符长度
                int separatorLen = msg.getByte(len) == '\r' ? 2 : 1;

                //默认去除分隔符
                frame = msg.readBytes(formerLen);
                msg.skipBytes(separatorLen);

                return frame;
            }
//            else {
//                final int length = msg.readableBytes();
//                if (length > maxLength) {
//                    discardedBytes = length;
//                    msg.readerIndex(msg.writerIndex());
//                    discarding = true;
//                    offset = 0;
//                }
//                return null;
//            }
//        }
//        else {
//            if (len >= 0) {
////                final int formerLen = discardedBytes + len - msg.readerIndex();
//                final int separatorLen = msg.getByte(len) == '\r'? 2 : 1;
//                msg.readerIndex(len + separatorLen);
//                discardedBytes = 0;
//                discarding = false;
//            } else {
//                discardedBytes += msg.readableBytes();
//                msg.readerIndex(msg.writerIndex());
//            }
//        }


        return null;
    }

    /**
     * 找到缓冲区中的分隔符
     * @param byteBuf
     * @return
     */
    private static int findTheSeparator(final ByteBuf byteBuf) {
        //获取byteBuf的最大长度
        final int len = byteBuf.writerIndex();
        for (int i = byteBuf.readerIndex(); i < len; i++) {
            //获取字节
            final byte b = byteBuf.getByte(i);
            if (b == '\n') {
                //\n
                return i;
            } else if (b == '\r' && i < len - 1 && byteBuf.getByte(i + 1) == '\n') {
                //\r\n
                return i;
            }
        }
        //未找到
        return -1;
    }
}
