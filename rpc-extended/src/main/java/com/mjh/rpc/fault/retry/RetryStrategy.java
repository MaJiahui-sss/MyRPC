package com.mjh.rpc.fault.retry;

import com.mjh.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {
    public  RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
