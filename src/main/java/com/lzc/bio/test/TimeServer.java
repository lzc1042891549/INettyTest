package com.lzc.bio.test;

import constants.LocalConstant;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 时间服务器的服务端
 * Created by liuzhichao on 17/4/29.
 */
public class TimeServer {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(LocalConstant.MOST_USE_PORT,1,InetAddress.getByName(LocalConstant.IP));
            while (true){
                Socket socket = serverSocket.accept();
                new Thread(new TimeServerHandler(socket)).start();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }




    }
}
