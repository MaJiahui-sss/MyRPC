package com.mjh.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

public class VertxDemo {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        NetServer server = vertx.createNetServer();
        server.connectHandler(socket -> {
            socket.handler(buffer -> {
                System.out.println("I received some bytes: " + buffer.length());
            });
        });
        server.listen(8089, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Server is now listening!");
            } else {
                System.out.println("Failed to bind!");
            }
        });
    }
}
