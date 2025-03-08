package com.mjh.rpc.fault.tolerant;

import com.mjh.rpc.model.RpcResponse;



import java.util.Map;

public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        System.out.println(("容错策略，静默处理" + e.getMessage()));
        return new RpcResponse();
    }
}
