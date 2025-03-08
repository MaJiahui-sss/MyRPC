package com.mjh.vertx;


import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;

public class TcpHandler implements Handler<Buffer> {
    @Override
    public void handle(Buffer buffer) {
                            // 处理接收到的数据
                    System.out.println("Received data: " + buffer.toString());


    }
}
