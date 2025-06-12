package com.lxy.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        // 打印请求的URL
        System.out.println("Request URL: " + request.getURI());

        // 打印请求头
        request.getHeaders()
                .forEach((name, values) -> values.forEach(value -> log.debug("{} : {}", name, value)));

        // 如果有请求体，打印出来
        if (body.length > 0) {
            log.debug("Request Body: {}", new String(body, StandardCharsets.UTF_8));
        } else {
            log.debug("Request Body is empty");
        }

        return execution.execute(request, body);
    }
}