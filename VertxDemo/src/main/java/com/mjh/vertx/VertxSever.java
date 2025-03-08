package com.mjh.vertx;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

public class VertxSever {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        NetServer server = vertx.createNetServer();
        RecordParser parser=RecordParser.newFixed(7);
        parser.setOutput(new Handler<Buffer>() {
            int size=-1;
            Buffer resultBuffer=Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if(size==-1){
                    size = buffer.getInt(3);
                    parser.fixedSizeMode(size);
                    resultBuffer.appendBuffer(buffer);

                }else{
                    resultBuffer.appendBuffer(buffer);
                    //TODO: resultBuffer的处理逻辑
                    System.out.println("接受到消息"+new String(resultBuffer.getBytes()));
                    //重置一轮
                    size=-1;
                    parser.fixedSizeMode(7);
                    resultBuffer=Buffer.buffer();
                }
            }
        });
        server.connectHandler(new Handler<NetSocket>() {
            @Override
            public void handle(NetSocket netSocket) {
                netSocket.handler(parser);
            }
        });
        server.listen(8060, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Server is now listening!");
            } else {
                System.out.println("Failed to bind!");
            }
        });
    }
}
