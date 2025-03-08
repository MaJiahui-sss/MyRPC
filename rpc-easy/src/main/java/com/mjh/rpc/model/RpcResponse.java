package com.mjh.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResponse implements Serializable {
    //返回数据
    private Object data;
    //数据的类型
    private Class<?> dataType;
    //响应信息
    private String message;
    //异常信息
    private Exception exception;
}
