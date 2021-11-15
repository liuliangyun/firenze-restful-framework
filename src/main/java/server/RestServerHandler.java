package server;

import dispatcher.SimplyDispatcher;
import entity.RestfulResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.net.InetAddress;


public class RestServerHandler extends ChannelInboundHandlerAdapter {
    private SimplyDispatcher simplyDispatcher;

    public RestServerHandler(SimplyDispatcher simplyDispatcher) {
        this.simplyDispatcher = simplyDispatcher;
        simplyDispatcher.init();
        System.out.println(simplyDispatcher.getRequestHandlerMapping().getHandlers().size());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
            String body= "";
            if (response.getStatus().equals(HttpResponseStatus.OK)) {
                body = response.getBody().toString();
            }
            send(ctx, body, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 发送的返回值
     * @param ctx     返回
     * @param context 消息
     * @param status 状态
     */
    private void send(ChannelHandlerContext ctx, String context,HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /*
     * 建立连接时，返回消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
        ctx.writeAndFlush("客户端"+ InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
