package com.mjh.rpc.config;



import com.mjh.rpc.fault.tolerant.TolerantStrategyKeys;
import com.mjh.rpc.loadBalancer.LoadBalancerKeys;
import com.mjh.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost= "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort= 8080;

    /**
     * 序列化映射
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ORDERLY;

    private String tolerantStrategy = TolerantStrategyKeys.FAIL_SAFE_TOLERANT_STRATEGY;
}
