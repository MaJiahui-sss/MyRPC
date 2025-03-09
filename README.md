# RPC 框架项目 README

## 一、项目介绍
本 RPC（Remote Procedure Call）框架是一个轻量级且功能丰富的远程过程调用解决方案，旨在简化分布式系统中不同服务之间的通信。框架提供了灵活的配置选项，支持多种序列化方式、负载均衡策略和容错机制，同时集成了 Etcd 作为默认的服务注册中心，确保服务的高可用性和动态发现。

### 主要特性
1. **灵活配置**：支持通过配置文件或代码自定义框架的各项参数，如服务名称、版本、服务器地址、序列化方式、注册中心配置等。
2. **服务注册与发现**：使用 Etcd 作为注册中心，实现服务的自动注册和动态发现，确保服务的高可用性和弹性伸缩。
3. **本地缓存**：引入注册中心服务本地缓存机制，提高服务发现的性能和响应速度。
4. **负载均衡**：支持多种负载均衡策略，可根据实际需求进行选择。
5. **容错处理**：提供多种容错策略，确保在服务调用失败时能够进行有效的处理。
6. **Spring 集成**：通过 Spring Boot Starter 实现与 Spring 框架的无缝集成，方便在 Spring 项目中使用。

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

## 三、注意事项
- 确保 Etcd 服务正常运行，否则服务注册和发现功能将无法正常使用。
- 根据实际需求调整配置文件中的参数，如服务器地址、端口号、序列化方式等。
- 在使用不同的负载均衡策略和容错机制时，需要确保相应的实现类已经正确配置。

通过以上步骤，你可以快速搭建并使用本 RPC 框架，实现分布式系统中不同服务之间的远程调用。
