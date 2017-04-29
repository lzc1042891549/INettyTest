package com.lzc.bio.test;

import constants.LocalConstant;

import java.io.*;
import java.net.Socket;
import java.util.Locale;

/**
 * Created by liuzhichao on 17/4/29.
 */
public class TimeClient {

    public static void main(String[] args) {
        //与服务端建立链接
        BufferedReader in = null;
        PrintWriter out = null;
        Socket socket = null;
        try {
            socket = new Socket(LocalConstant.IP,LocalConstant.MOST_USE_PORT);

            in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);

            out.println("QUERY SERVER TIME");
            String body = in.readLine();
            if (body != null){
                System.out.println("get msg from server:<"+body+">");
            } else {
                System.out.println("get no msg from server");
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
