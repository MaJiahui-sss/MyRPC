package com.mjh.rpc.config;


import com.mjh.rpc.loadBalancer.LoadBalancerFactory;
import lombok.Data;

/**
 * RPC 框架注册中心配置
 */
@Data
public class RegistryConfig {



    /**
     * 注册中心类别
     */
    private String registry = "etcd";

    /**
     * 注册中心地址
     */
    private String address = "http://localhost:2379";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间（单位毫秒）
     */
    private Long timeout = 10000L;



    public String getRegistry() {
        return registry;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Long getTimeout() {
        return timeout;
    }
}
