package server;

import dispatcher.DispatcherConfig;
import dispatcher.SimplyDispatcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class RestServer {
    private int port;
    private SimplyDispatcher simplyDispatcher;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public RestServer(int port, DispatcherConfig dispatcherConfig) {
        this.port = port;
        this.simplyDispatcher = new SimplyDispatcher(dispatcherConfig);
        bossGroup = new NioEventLoopGroup(1); // (1) bossGroup 用于接收连接，workerGroup 用于具体的处理
        workerGroup = new NioEventLoopGroup();
    }

    public void run()  {
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2) 创建服务端启动引导/辅助类：ServerBootstrap
            // (3) 给引导类配置两大线程组,确定了线程模型
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (4) 指定 IO 模型
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(65536));
                            // (5) 自定义客户端消息的业务处理逻辑
                            ch.pipeline().addLast(new RestServerHandler(simplyDispatcher));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // (6) 绑定端口,调用 sync 方法阻塞直到绑定完成
            ChannelFuture f = b.bind(port).sync();

            // (7) 阻塞等待直到服务器Channel关闭(closeFuture()方法获取Channel 的CloseFuture对象,然后调用sync()方法)
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // (8) 优雅关闭相关线程组资源
    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
