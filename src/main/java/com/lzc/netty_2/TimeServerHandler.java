package com.lzc.netty_2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

/**
 * Created by liuzhichao on 17/5/1.
 */
public class TimeServerHandler extends SimpleChannelInboundHandler {

    int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String reqStr = (String) msg;
        System.out.println("("+(counter++)+")receive request:"+reqStr);
        String response = "";
        if (reqStr.equalsIgnoreCase("QUERY TIME ORDER")){
            response = new Date(System.currentTimeMillis()).toString();
        } else {
            response = "ERROR ORDER["+reqStr+"]";
        }
        ByteBuf respBytes = Unpooled.copiedBuffer((response+System.getProperty("line.separator")).getBytes());
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
