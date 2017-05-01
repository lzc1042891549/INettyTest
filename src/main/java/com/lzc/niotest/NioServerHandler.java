package com.lzc.niotest;

import constants.LocalConstant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by liuzhichao on 17/4/29.
 */
public class NioServerHandler implements Runnable{

    //多路复用器
    private Selector selector;

    //服务端通道
    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop = false;

    //初始化多路复用器,绑定监听端口
    public NioServerHandler(){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);//设置为非阻塞模式
            //@param   backlog         requested maximum length of the queue ofincoming connections.
            serverSocketChannel.socket().bind(new InetSocketAddress(LocalConstant.IP,LocalConstant.MOST_USE_PORT),1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("the server is start in port:"+LocalConstant.MOST_USE_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public void run() {
        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keySet.iterator();
                while (keyIterator.hasNext()){
                    SelectionKey key = keyIterator.next();
                    try {
                        keySet.remove(key);
                        handleInput(key);
                    }catch (Exception e){
                        e.printStackTrace();
                        if (key != null){
                            key.cancel();
                            if (key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }

                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if (selector != null){
            try {
                selector.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()){
            if (key.isAcceptable()){
                //channel 处于可链接状态,启动监听链接
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = ssc.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector,SelectionKey.OP_READ);
            }
            if (key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0){
                    //缓冲区标志为置0
                    readBuffer.flip();
                    //读取到内容
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("server listened order:"+body);
                    String response = "QUERY SERVER TIME".equalsIgnoreCase(body)?"server time is "+new Date(System.currentTimeMillis()):"bad order from client:["+body+"]";
                    doWrite(sc,response);
                } else if(readBytes < 0){
                    key.cancel();
                    sc.close();
                } else {
                    //do nothing
                }
            }
        }
    }

    private void doWrite(SocketChannel sc, String response) {
        try {
            if (response == null || response.trim().length() == 0){
                return;
            }
            byte[] respByte = response.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocate(respByte.length);
            byteBuffer.put(respByte);
            byteBuffer.flip();
            sc.write(byteBuffer);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
