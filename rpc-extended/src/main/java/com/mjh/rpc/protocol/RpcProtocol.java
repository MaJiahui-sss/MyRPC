package com.mjh.rpc.protocol;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcProtocol<T> {
    //协议首部
    ProtocolHeader protocolHeader;
    //请求体
    T data;

    public RpcProtocol() {
    }

    public RpcProtocol(ProtocolHeader protocolHeader, T data) {
        this.protocolHeader = protocolHeader;
        this.data = data;
    }

    @Data
    @Builder
    public static class ProtocolHeader{
        //魔数
        byte magicNum;
        //序列化器
        byte serialization;
        //消息类型
        byte type;
        //数据长度
        int dataLength;

        public ProtocolHeader(byte magicNum, byte serialization, byte type, int dataLength) {
            this.magicNum = magicNum;
            this.serialization = serialization;
            this.type = type;
            this.dataLength = dataLength;
        }

        public ProtocolHeader() {
        }
    }
}
