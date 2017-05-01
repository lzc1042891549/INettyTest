package com.lzc.niotest;

import constants.LocalConstant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by liuzhichao on 17/4/29.
 */
public class NioClientHandler implements Runnable{

    private Selector selector;

    private SocketChannel socketChannel;

    private volatile boolean stop;

    public NioClientHandler(){
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            doConnect();
        } catch (Exception e){
            //链接出现异常,直接推出
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop){
            //处理多路复用器上就绪的channel
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
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (selector != null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()){
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()){
                if (sc.finishConnect()){
                    sc.register(selector,SelectionKey.OP_READ);
                    doWrite(sc);
                } else {
                    System.exit(1);
                }
            }
            if (key.isReadable()){
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(byteBuffer);
                if (readBytes > 0){
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("get msg from:"+body);
                    this.stop = true;
                }else if (readBytes < 0){
                    key.cancel();
                    sc.close();
                    System.exit(1);
                }else {
                    //do nothing;
                }
            }
        }
    }

    private void doWrite(SocketChannel sc) throws IOException {
        byte[] orderByteArray = "QUERY SERVER TIME".getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(orderByteArray.length);
        byteBuffer.put(orderByteArray);
        byteBuffer.flip();
        sc.write(byteBuffer);
    }

    private void doConnect() throws IOException {
        if (socketChannel.connect(new InetSocketAddress(LocalConstant.IP,LocalConstant.MOST_USE_PORT))){
            //链接成功
            socketChannel.register(selector,SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            socketChannel.register(selector,SelectionKey.OP_CONNECT);
        }
    }
}
