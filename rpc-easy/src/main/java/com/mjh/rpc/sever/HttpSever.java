package com.mjh.rpc.sever;

public interface HttpSever {

    /**
     * 启动服务器
     * @param port
     * @throws Exception
     */
    public void start(int port) throws Exception;
}
