package com.mjh.rpc.fault.tolerant;

import com.mjh.rpc.model.RpcRequest;
import com.mjh.rpc.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {
    public RpcResponse doTolerant(Map<String,Object> context, Exception e);
}
