package com.lzc.niotest;

/**
 * Created by liuzhichao on 17/4/29.
 */
public class TimeServer {

    public static void main(String[] args) {
        new Thread(new NioServerHandler()).start();
    }
}
