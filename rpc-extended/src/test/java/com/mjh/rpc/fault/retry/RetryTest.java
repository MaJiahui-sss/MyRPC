package com.mjh.rpc.fault.retry;

import com.mjh.rpc.model.RpcResponse;
import org.junit.Test;

import java.util.concurrent.Callable;

public class RetryTest {
    @Test
    public void retryTest() throws Exception {
        RetryStrategy retryStrategy=new FixedIntervalRetryStrategy();
        retryStrategy.doRetry(new Callable<RpcResponse>() {
            @Override
            public RpcResponse call() throws Exception {
                System.out.println("模拟重试");
                throw new Exception();
                //return null;
            }
        });
    }
}
