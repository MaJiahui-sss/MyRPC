package com.mjh.rpc.model;

import lombok.Data;

@Data
public class ServiceRegisteInfo {
    private String serviceName;
    private Class<?> serviceClass;
}
