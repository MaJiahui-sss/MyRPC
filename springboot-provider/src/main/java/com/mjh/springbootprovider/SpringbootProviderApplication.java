package com.mjh.springbootprovider;

import com.mjh.rpcspringbootstarter.annotation.EnableRPC;
import io.grpc.stub.annotations.RpcMethod;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRPC()
public class SpringbootProviderApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpringbootProviderApplication.class, args);
    }

}
