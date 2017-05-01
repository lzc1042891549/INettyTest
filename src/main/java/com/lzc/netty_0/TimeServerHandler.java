package com.lzc.netty_0;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.SimpleChannelInboundHandler;


import java.util.Date;

/**
 * Created by liuzhichao on 17/5/1.
 */
public class TimeServerHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String reqStr = new String(req,"UTF-8");
        System.out.println("receive request:"+reqStr);
        String response = "";
        if (reqStr.equalsIgnoreCase("QUERY TIME ORDER")){
            response = new Date(System.currentTimeMillis()).toString();
        } else {
            response = "ERROR ORDER["+reqStr+"]";
        }
        ByteBuf respBytes = Unpooled.copiedBuffer(response.getBytes());
        ctx.write(respBytes);

    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        channelRead(channelHandlerContext,o);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
