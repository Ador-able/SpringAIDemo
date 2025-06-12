package com.lxy.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Good Day
 *
 * @author LXY
 * @create 2025年6月12日
 */
@Slf4j
@Component
public class DemoMarkdownReader {

    private final Resource resource;

    DemoMarkdownReader(@Value("classpath:code.md") Resource resource) {
        this.resource = resource;
    }

    public List<Document> loadMarkdown() {
        log.info("loading markdown file: {}", resource.getFilename());
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)  // 水平规则分文档
                .withIncludeCodeBlock(false)             // 排除代码块
                .withIncludeBlockquote(false)           // 排除引用块
                .withAdditionalMetadata("filename", "code.md") // 添加元数据
                .build();

        MarkdownDocumentReader reader = new MarkdownDocumentReader(this.resource, config);
        return reader.get();
    }
}  
