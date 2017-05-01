package com.lzc.biotest;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * Created by liuzhichao on 17/4/29.
 */
public class TimeServerHandler implements Runnable{

    private Socket socket;

    public TimeServerHandler(Socket socket){
        this.socket = socket;
    }

    public void run() {
        if (socket == null){
            return;
        }
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);

            String body = in.readLine();
            if (body != null){
                if (body.equalsIgnoreCase("QUERY SERVER TIME")){
                    out.println("server time is :"+new Date(System.currentTimeMillis()));
                } else {
                    out.println("WRONG ORDER.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null){
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (out != null){
                try {
                    out.close();
                    out = null;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (socket != null){
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
