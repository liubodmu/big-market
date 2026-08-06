package cn.bugstack;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration.class})
@Configurable
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

}
