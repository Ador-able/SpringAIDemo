package com.lxy.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.net.InetAddress;

@Slf4j
@SpringBootApplication
public class DemoApplication implements ApplicationListener<ApplicationReadyEvent> {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            int port = event.getApplicationContext().getEnvironment().getProperty("server.port", Integer.class, 8080);
            String swaggerUrl = "http://" + ip + ":" + port + "/swagger-ui.html";
            log.info("Swagger UI available at: {}", swaggerUrl);
            log.debug("日志级别：DEBUG");
        } catch (Exception e) {
            log.warn("Get Swagger UI Unknown Error.");
        }
    }

}
