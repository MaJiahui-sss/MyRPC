package com.mjh.rpc.protocol;

import com.mjh.rpc.constant.ProtocolConstant;
import com.mjh.rpc.model.RpcRequest;
import com.mjh.rpc.model.RpcResponse;
import com.mjh.rpc.serializer.Serializer;
import com.mjh.rpc.serializer.SerializerFactory;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageUtil {
    public static <T>Buffer Encode(RpcProtocol<T> rpcProtocol){
        Buffer buffer = Buffer.buffer();
        if(rpcProtocol==null) System.out.println("rpcProtocol is null我在这里");
        RpcProtocol.ProtocolHeader header = rpcProtocol.getProtocolHeader();
        buffer.appendByte(header.getMagicNum());
        buffer.appendByte(header.getSerialization());
        buffer.appendByte(header.getType());


        //获取序列化器
        ProtocolSerilizerEnum protocolSerilizerEnum = ProtocolSerilizerEnum.getSerilizerEnum(header.getSerialization());
        if(protocolSerilizerEnum != null){
            Serializer serializer = SerializerFactory.getInstance(protocolSerilizerEnum.serilizerName);
            try {
                byte[] serializedData = serializer.serialize(rpcProtocol.getData());
                buffer.appendInt(serializedData.length);
                buffer.appendBytes(serializedData);
            } catch (IOException e) {
                System.out.println("编码异常，未成功编码");
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("未成功获取到消息中指定的编码器类型");
        }
        return buffer;

    }
    public static <T>RpcProtocol Decode(Buffer buffer){
        byte magicNum = buffer.getByte(0);
        byte serilizerKey = buffer.getByte(1);
        byte msgType = buffer.getByte(2);
        int dataLength = buffer.getInt(3);

        RpcProtocol.ProtocolHeader header = new RpcProtocol.ProtocolHeader();
        header.setMagicNum(magicNum);
        header.setSerialization(serilizerKey);
        header.setType(msgType);
        header.setDataLength(dataLength);




        ProtocolSerilizerEnum protocolSerilizerEnum = ProtocolSerilizerEnum.getSerilizerEnum(serilizerKey);
        if(protocolSerilizerEnum != null){
            Serializer serializer = SerializerFactory.getInstance(protocolSerilizerEnum.serilizerName);
            byte[] dataBytes = buffer.getBytes(7, 7+dataLength);
            ProtocolTypeEnum protocolMsgType = ProtocolTypeEnum.getProtocolType(msgType);
            try {
                switch (protocolMsgType){
                    case REQUEST:{
                        RpcRequest rpcRequest = serializer.deserialize(dataBytes, RpcRequest.class);
                        return new RpcProtocol<>(header,rpcRequest);

                    }
                    case RESPONSE:{
                        RpcResponse rpcResponse = serializer.deserialize(dataBytes, RpcResponse.class);
                        return new RpcProtocol<>(header,rpcResponse);

                    }
                    case HEART_BEAT:
                    case OTHER:
                    default:
                        System.out.println("未找到当前消息的类型");

                }
            }catch (IOException e){
                System.out.println("反序列化失败");
                e.printStackTrace();
                throw new RuntimeException(e);
            }





        }else{
            System.out.println("未成功获取到消息中指定的编码器类型");
        }

        return null;
    }
}
