package com.lxy.demo.config;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

@Configuration
public class ToolsConfig {
    //我的建议是新建一个服务作为mcp服务器，然后agent服务集成mcp客户端，然后使用工具，优化模型表现。
    //这里为了方便直接使用工具调用
    @Tool(description = "获取当前时间")
    String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }
}
