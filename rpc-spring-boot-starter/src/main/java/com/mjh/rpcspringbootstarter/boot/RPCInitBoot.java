package com.mjh.rpcspringbootstarter.boot;



import com.mjh.rpc.RpcApplication;
import com.mjh.rpc.config.RpcConfig;
import com.mjh.rpc.sever.VertxTcpServer;
import com.mjh.rpcspringbootstarter.annotation.EnableRPC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Rpc 框架启动
 *
 */
@Slf4j
public class RPCInitBoot implements ImportBeanDefinitionRegistrar {

    /**
     * Spring 初始化时执行，初始化 RPC 框架
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 EnableRpc 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRPC.class.getName())
                .get("needServer");

        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if (needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            try {
                vertxTcpServer.start(rpcConfig.getServerPort());
            } catch (Exception e) {
                System.out.println("启动失败");
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("不启动sever");
        }

    }
}

