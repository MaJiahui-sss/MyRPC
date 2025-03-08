package com.mjh.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

public class VertxClient {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetClientOptions options = new NetClientOptions();
        NetClient client = vertx.createNetClient(options);
        client.connect(8099, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected!");
                NetSocket socket = res.result();
                for (int i = 0; i < 10000; i++) {
                    Buffer buffer = Buffer.buffer();
                    buffer.appendInt(12);
                    buffer.appendBytes("Hello World!".getBytes());
                    socket.write(buffer);

                }

                //设置处理器
//                socket.handler(buffer -> {
//                    // 处理接收到的数据
//                    System.out.println("Received data: " + buffer.toString());
//
//                    socket.close();
//                    client.close();
//
//                });
                // 处理连接关闭事件
                socket.closeHandler(v -> {
                    System.out.println("Connection closed");
                });
            } else {
                System.out.println("Failed to connect: " + res.cause().getMessage());
            }
        });

    }
}
