package com.mjh.rpc.sever;

import com.mjh.rpc.constant.ProtocolConstant;
import com.mjh.rpc.model.RpcResponse;
import com.mjh.rpc.protocol.ProtocolMessageUtil;
import com.mjh.rpc.protocol.RpcProtocol;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;


public class TCPBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser parser;

    public TCPBufferHandlerWrapper(Handler<Buffer> handler) {
        parser=init(handler);
    }
    public RecordParser init(Handler<Buffer> handler) {
        RecordParser recordParser=RecordParser.newFixed(ProtocolConstant.PROTOCOL_HEAD_LENGTH);
        recordParser.setOutput(new Handler<Buffer>() {
            int size = -1;
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (size == -1) {
                    //在头部中，下标3开始，是长度的起始字节
                    size = buffer.getInt(3);
                    parser.fixedSizeMode(size);
                    resultBuffer.appendBuffer(buffer);

                } else {
                    resultBuffer.appendBuffer(buffer);
                    //TODO: resultBuffer的处理逻辑
                    handler.handle(resultBuffer);
                    //重置一轮
                    size = -1;
                    parser.fixedSizeMode(7);
                    resultBuffer = Buffer.buffer();
                }
            }

        });
        return recordParser;
    }

    @Override
    public void handle(Buffer buffer) {
        parser.handle(buffer);
    }
}
