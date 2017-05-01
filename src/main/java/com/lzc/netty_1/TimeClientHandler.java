package com.lzc.netty_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * Created by liuzhichao on 17/5/1.
 */
public class TimeClientHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //通道激活,准备就绪,想服务端发起请求
        for (int i = 0; i < 100; i++) {
            byte[] req = "QUERY TIME ORDER".getBytes();
            ByteBuf requestMsg = Unpooled.buffer(req.length);
            requestMsg.writeBytes(req);
            ctx.writeAndFlush(requestMsg);
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接收到服务端的响应
        ByteBuf buf = (ByteBuf) msg;
        byte[] response = new byte[buf.readableBytes()];
        buf.readBytes(response);
        String respStr = new String(response,"UTF-8");

        System.out.println("get time from server:"+respStr);
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        channelRead(channelHandlerContext,o);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
