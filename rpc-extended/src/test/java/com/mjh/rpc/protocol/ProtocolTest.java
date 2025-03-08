package com.mjh.rpc.protocol;

import com.mjh.rpc.model.RpcRequest;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;

public class ProtocolTest {
    @Test
    public void testEncodeAndDecode() throws Exception {
        //
        RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
        //封装rpcRequest
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("test");
        rpcRequest.setMethodName("testEncodeAndDecode");
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setParams(new Object[]{"test"});
        rpcRequest.setServiceVersion("1.0");
        //封装头部
        RpcProtocol.ProtocolHeader header = new RpcProtocol.ProtocolHeader();
        header.setMagicNum((byte)1);
        header.setSerialization(ProtocolSerilizerEnum.JDK.serilizerKey);
        header.setType(ProtocolTypeEnum.REQUEST.key);
        header.setDataLength(0);

        rpcProtocol.setProtocolHeader(header);
        rpcProtocol.setData(rpcRequest);

        Buffer buffer = ProtocolMessageUtil.Encode(rpcProtocol);

        RpcProtocol decode = ProtocolMessageUtil.Decode(buffer);
        RpcRequest newRpcRequest = (RpcRequest) decode.getData();
        System.out.println(newRpcRequest.toString());


    }
}
