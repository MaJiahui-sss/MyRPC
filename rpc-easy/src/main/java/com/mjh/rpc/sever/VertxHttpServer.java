package com.mjh.rpc.sever;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class VertxHttpServer implements HttpSever {
    @Override
    public void start(int port) throws Exception {
        //创建vertx服务器实例
        Vertx vertx = Vertx.vertx();
        //创建httpSever实例
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();
        //绑定请求处理器
        httpServer.requestHandler(new HTTPServerHandler());
//        httpServer.requestHandler(req -> {
//            //处理http请求
//            System.out.println("request: " + req.path()+" "+req.method()+" "+req.uri());
//            //响应http请求
//            req.response()
//                    .putHeader("content-type", "text/plain")
//                    .end("Hello from Vert.x");
//        });
        //启动服务器并开始监听端口
        httpServer.listen(port,rel->{
            if(rel.succeeded()){
                System.out.println("HTTP server started on port "+port);
            }
            else {
                System.out.println("HTTP server failed to start on port "+port);
            }

        });

    }
}
