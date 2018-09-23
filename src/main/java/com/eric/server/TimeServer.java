package com.eric.server;

import com.eric.adapter.websocket.TimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.atomic.AtomicInteger;

public class TimeServer {

    private AtomicInteger count = new AtomicInteger(0);

    public void bind(int port) throws Exception {
        //配置服务端的NIO线程组
        //NioEventLoopGroup是个线程组，它包含了一组NIO线程，专门用于网络事件的处理，
        //实际上它们就是Reactor线程组。
        //这里创建两个的原因是一个用于服务端接受客户端的连接，另一个用于进行SocketChannel的网络读写。
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //创建ServerBootstrap对象，它是Netty用于启动NIO服务端的辅助启动类，目的是降低服务端的开发复杂度。
            ServerBootstrap bootstrap = new ServerBootstrap();

            //调用ServerBootstrap的group方法，将两个NIO线程组当作入参传递到ServerBootstrap中。
            //接着设置创建的Channel为NioServerSocketChannel，它的功能对应于JDK NIO类库中的ServerSocketChannel类。
            //然后配置NioServerSocketChannel的TCP参数，此处将它的backlog设置为1024，
            //最后绑定I/O事件的处理类ChildChannelHandler，它的作用类似于Reactor模式中的handler类，
            //主要用于处理网络I/O事件，例如记录日志、对消息进行编解码等。
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());

            //绑定端口，同步等待成功
            //服务端启动辅助类配置完成之后，调用它的bind方法绑定监听端口
            //随后，调用它的同步阻塞方法sync等待绑定操作完成。
            //完成之后Netty会返回一个ChannelFuture，它的功能类似于JDK的java.util.concurrent.Future，主要用于异步操作的通知回调。
            ChannelFuture future = bootstrap.bind(port).sync();

            System.out.println("TimeServer 绑定成功了,port=" + port);

            //等待服务端监听端口关闭
            //使用f.channel().closeFuture().sync()方法进行阻塞,等待服务端链路关闭之后main函数才退出。
            future.channel().closeFuture().sync();
            count.addAndGet(1);
            System.out.println("这是TimeServer第" + count.get() + "次接受到信息");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public AtomicInteger getCount() {
        return count;
    }

    class ChildChannelHandler extends ChannelInitializer {

        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        new TimeServer().bind(7787);
    }
}
