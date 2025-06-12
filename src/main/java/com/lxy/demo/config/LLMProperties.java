package com.lxy.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMProperties {
    
    private String apiKey;
    
    private String baseUrl;
    
    private String chatModel;
    
    private String embeddingModel;
}
