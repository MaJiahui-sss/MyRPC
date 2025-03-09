# RPC 框架 

## 一、项目介绍
本 RPC（Remote Procedure Call）框架是一个轻量级且功能丰富的远程过程调用解决方案，旨在简化分布式系统中不同服务之间的通信。框架提供了灵活的配置选项，支持多种序列化方式、负载均衡策略和容错机制，同时集成了 Etcd 作为默认的服务注册中心，确保服务的高可用性和动态发现。

### 主要特性
1. **灵活配置**：支持通过配置文件或代码自定义框架的各项参数，如服务名称、版本、服务器地址、序列化方式、注册中心配置等。
2. **服务注册与发现**：使用 Etcd 作为注册中心，实现服务的自动注册和动态发现，确保服务的高可用性和弹性伸缩。
3. **本地缓存**：引入注册中心服务本地缓存机制，提高服务发现的性能和响应速度。
4. **负载均衡**：支持多种负载均衡策略，可根据实际需求进行选择。
5. **容错处理**：提供多种容错策略，确保在服务调用失败时能够进行有效的处理。
6. **Spring 集成**：通过 Spring Boot Starter 实现与 Spring 框架的无缝集成，方便在 Spring 项目中使用。

### 主要工作
1. **JDK 动态代理与工厂模式**：借助 JDK 动态代理和工厂模式，为服务接口类生成代理对象，实现远程方法调用。同时采用双检锁单例模式维护全局配置对象，利用 Hutool Props 工具加载多环境配置文件，确保配置的高效管理和灵活切换。
2. **SPI 机制**：通过自定义 `SpiLoader` 类实现 SPI（Service Provider Interface）机制，允许用户动态扩展序列化器、注册中心、负载均衡器、重试策略等组件。使用 `ConcurrentHashMap` 维护实例缓存，提升系统性能。
3. **Etcd 注册中心**：基于 Etcd 中间件搭建注册中心，利用其层级结构存储服务节点信息。通过定时任务和 Etcd Key 的 TTL 实现服务提供者的心跳检测与续期，使用 Etcd Watch 机制监听节点状态，自动更新本地缓存，保证服务的高可用性和动态发现。
4. **Vert.x TCP 服务器**：基于 Vert.x 构建 TCP 服务器，使用自定义的字节数组 RPC 协议，以及自定义的消息编码器和解码器，避免了 HTTP 协议冗余的首部字段，显著提升网络传输性能。
5. **半包粘包处理**：利用 Vert.x 的 `RecordParser` 完美解决半包粘包问题，并采用装饰者模式封装 `TcpBufferHandlerWrapper` 类，增强请求处理器的功能，使代码更简洁、易维护和扩展。
6. **负载均衡与重试策略**：基于一致性 Hash 算法实现负载均衡器，基于 Guava Retrying 实现固定时长的重试策略，提高服务提供者的集群处理能力和消费者的稳定调用。

## 二、快速使用

### 1. 环境准备
- **Java 环境**：确保已经安装 Java 8 或更高版本。
- **Maven**：用于项目的依赖管理和构建。
- **Etcd**：作为服务注册中心，需要提前启动 Etcd 服务，默认地址为 `http://localhost:2379`。

### 2. 添加依赖
在项目的 `pom.xml` 文件中添加以下依赖：
```xml
<dependency>
    <groupId>com.mjh</groupId>
    <artifactId>rpc-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 3. 配置文件
在 `src/main/resources` 目录下创建 `application.properties` 文件，并进行如下配置：
```properties
rpc.name = myRpc
rpc.version = 2.0
rpc.serverHost = localhost
rpc.serverPort = 8060
rpc.serializer = jdk
```
你可以根据实际需求修改这些配置。

### 4. 启用 RPC 框架
在 Spring Boot 主应用类上添加 `@EnableRPC` 注解，并设置 `needServer` 属性来决定是否启动服务器：
```java
import com.mjh.rpcspringbootstarter.annotation.EnableRPC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRPC(needServer = true)
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 5.  RPC 请求和响应类
 `RpcRequest` 和 `RpcResponse` 类，用于定义 RPC 调用的请求和响应信息：
```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {
    // 服务名称
    private String serviceName;
    // 方法名称
    private String methodName;
    // 参数类型列表
    private Class<?>[] parameterTypes;
    // 参数列表
    private Object[] params;
    // 服务版本
    private String serviceVersion = "1.0";
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResponse implements Serializable {
    // 返回数据
    private Object data;
    // 数据的类型
    private Class<?> dataType;
    // 响应信息
    private String message;
    // 异常信息
    private Exception exception;
}
```

### 6. 编写服务接口和实现类
创建服务接口和实现类，用于定义 RPC 服务的具体功能：
```java
// 服务接口
public interface YourService {
    String sayHello(String name);
}

// 服务实现类
import org.springframework.stereotype.Service;

@Service
public class YourServiceImpl implements YourService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }
}
```

### 7. 发起 RPC 调用
在需要调用 RPC 服务的地方，注入服务接口并调用相应的方法：
```java
import org.springframework.stereotype.Component;
import com.mjh.rpcspringbootstarter.annotation.RpcReference;
@Component
public class YourClient {
    @RpcReference
    private YourService yourService;

    public void callRpcService() {
        String result = yourService.sayHello("World");
        System.out.println(result);
    }
}
```

## 三、扩展功能

### 1. 自定义序列化器
在 `META-INF/rpc/custom` 目录下创建 `com.mjh.rpc.serializer.Serializer` 文件，指定自定义序列化器的实现类：
```properties
mySerializer=com.example.MySerializer
```
然后实现 `com.example.MySerializer` 类：
```java
import com.mjh.rpc.serializer.Serializer;

public class MySerializer implements Serializer {
    // 实现序列化和反序列化方法
}
```
在配置文件中指定使用自定义序列化器：
```properties
rpc.serializer = mySerializer
```

### 2. 自定义注册中心
在 `META-INF/rpc/custom` 目录下创建 `com.mjh.rpc.registry.Registry` 文件，指定自定义注册中心的实现类：
```properties
myRegistry=com.example.MyRegistry
```
然后实现 `com.example.MyRegistry` 类，实现 `Registry` 接口的所有方法：
```java
import com.mjh.rpc.registry.Registry;
import com.mjh.rpc.config.RegistryConfig;
import com.mjh.rpc.model.ServiceMetaInfo;

import java.util.List;

public class MyRegistry implements Registry {
    @Override
    public void init(RegistryConfig registryConfig) {
        // 初始化操作
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 注册服务
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        // 注销服务
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 服务发现
        return null;
    }

    @Override
    public void destroy() {
        // 销毁操作
    }

    @Override
    public void heartBeat() {
        // 心跳检测
    }

    @Override
    public void watch(String serviceNodeKey) {
        // 监听节点变化
    }
}
```
在配置文件中指定使用自定义注册中心：
```properties
rpc.registry.type = myRegistry
```

### 3. 自定义负载均衡器和重试策略
类似地，通过 SPI 机制可以自定义负载均衡器和重试策略，只需在相应的 SPI 文件中指定实现类即可。


## 四、注意事项
- 确保 Etcd 服务正常运行，否则服务注册和发现功能将无法正常使用。
- 根据实际需求调整配置文件中的参数，如服务器地址、端口号、序列化方式等。
- 在使用不同的负载均衡策略和容错机制时，需要确保相应的实现类已经正确配置。

通过以上步骤，你可以快速搭建并使用本 RPC 框架，实现分布式系统中不同服务之间的远程调用。
