package com.mjh.rpcspringbootstarter.annotation;

import com.mjh.rpcspringbootstarter.boot.RPCConsumerBoot;
import com.mjh.rpcspringbootstarter.boot.RPCInitBoot;
import com.mjh.rpcspringbootstarter.boot.RPCProviderBoot;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RPCInitBoot.class, RPCProviderBoot.class, RPCConsumerBoot.class})
public @interface EnableRPC {
    /**
     * 需要启动sever，用以区分消费者还是服务者
     * @return
     */
    public boolean needServer() default true;
}
