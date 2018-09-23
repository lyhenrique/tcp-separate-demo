package com.eric.config;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 存储整个工程的静态配置
 */
public class NettyConfig {

    /**
     * 接收所有客户端的发送的channel对象
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
