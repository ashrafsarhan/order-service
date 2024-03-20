package com.codepole;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(OrderServiceApplication.class);
        ApplicationContext ctx = app.run(args);
        Environment env = ctx.getEnvironment();
        log.info("""
                        {} URL:
                        ----------------------------------------------------------
                        \t\thttp://{}:{}/{}{}
                        ----------------------------------------------------------""",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getProperty("spring.application.name"),
                env.getProperty("springdoc.swagger-ui.path"));

    }

}
