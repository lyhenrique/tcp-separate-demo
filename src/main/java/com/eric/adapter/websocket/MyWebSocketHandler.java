package com.eric.adapter.websocket;

import com.eric.config.NettyConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

/**
 * 接收/处理/响应客户端WebSocket请求的核心业务处理类
 */
public class MyWebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    private static final String WEB_SOCKET_URL = "ws://localhost:8888/websocket";

    /**
     * 服务端处理客户端WebSocket请求的核心方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //处理客户端向服务端发起http握手请求的业务
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {//处理WebSocket连接业务
            handleWebSocketFram(ctx, (WebSocketFrame)msg);
        }
    }

    /**
     * 处理客户端与服务端之间的websocket业务
     * @param ctx
     * @param frame
     */
    private void handleWebSocketFram(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //判断是否是关闭websocket的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame)frame.retain());
            return;
        }

        //判断是否是ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        //判断是否是二进制消息,如果是二进制消息抛出异常
        if (!(frame instanceof TextWebSocketFrame)) {
            System.out.println("目前不支持二进制消息");
            throw new RuntimeException("[" + this.getClass().getName() + "], 不支持该消息");
        }

        //返回应答消息
        //TextWebSocketFrame的text方法是获取客户端向服务端发送的消息
        String request = ((TextWebSocketFrame) frame).text();
        System.out.println("服务端收到客户端的消息====>>" + request);

        //创建返回的响应消息
        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                                                        + ctx.channel().id()
                                                        + " ====>> "
                                                        + request);

        //群发，服务端向每个连接的客户端群发消息
        NettyConfig.group.writeAndFlush(tws);
    }

    /**
     * 处理客户端向服务端发起http握手请求的业务
     * @param ctx
     * @param req
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {

        if (!req.getDecoderResult().isSuccess() || !"websocket".equals(req.headers().get("Upgrade"))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //创建工厂
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL, null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 服务端向客户端响应消息
     * @param ctx
     * @param req
     * @param resp
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse resp) {
        //如果请求失败
        if (resp.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(resp.getStatus().toString(), CharsetUtil.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
        }

        //服务端向客户端发送数据
        ChannelFuture future = ctx.channel().writeAndFlush(resp);
        if (resp.getStatus().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 功能出现异常时调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 客户端与服务端创建连接时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //将channel加入到group中
        NettyConfig.group.add(ctx.channel());
        System.out.println("客户端与服务端连接开启");
    }

    /**
     * 客户端与服务端断开连接时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //将channel从group中移除
        NettyConfig.group.remove(ctx.channel());
        System.out.println("客户端与服务端连接关闭");
    }

    /**
     * 服务端接收客户端发送过来的数据结束后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
