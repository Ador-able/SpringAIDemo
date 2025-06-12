package com.lxy.demo.controller;

import com.lxy.demo.config.DemoMarkdownReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Good Day
 *
 * @author LXY
 * @create 2025年6月12日
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    DemoMarkdownReader demoMarkdownReader;

    @Autowired
    @Qualifier("douBao")
    private ChatClient chatClient;


    PromptTemplate customPromptTemplate = PromptTemplate.builder()
            .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
            .template("""
                    <query>
                    
                    以下是上下文信息。
                    
                    ---------------------
                    <question_answer_context>
                    ---------------------
                    
                    根据上下文信息并且不使用先验知识，回答查询问题，可以调用适当的工具。
                    
                    请遵循以下规则：
                    
                    1. 如果答案不在上下文中，请直接说明你不知道。
                    2. 避免使用"根据上下文..."或"提供的信息..."等表述。
                    """)
            .build();

    /**
     * 处理MD文档的解析、分割和嵌入存储。
     */
    @GetMapping("/add")
    public void insertDocuments() {
        // 1. parse document
        List<Document> documents = demoMarkdownReader.loadMarkdown();
        log.info("{} documents loaded", documents.size());

        // 2. split trunks
        List<Document> splitDocuments = new TokenTextSplitter().apply(documents);
        log.info("{} documents split", splitDocuments.size());

        // 3. create embedding and store to vector store
        log.info("create embedding and save to vector store");
        vectorStore.add(splitDocuments);
    }

    /**
     * 根据用户输入的消息生成JSON格式的聊天响应。
     */
    @GetMapping(value = "/ask")
    public String ragJsonText(@RequestParam(value = "message", defaultValue = "如何使用spring ai alibaba?") String message) {

        var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(6).build())
                .promptTemplate(customPromptTemplate)
                .build();
        //这里可以增加过滤表达式，还可以使用RetrievalAugmentationAdvisor
        //RetrievalAugmentationAdvisor本质上与QuestionAnswerAdvisor具备类似功能，但其关键优势在于模块化。这使你能够用自己的实现对其任意组件进行定制和替换
        //还可以根据使用场景使用各种转换，如CompressionQueryTransformer

        return chatClient.prompt(message)
                .advisors(qaAdvisor, new SimpleLoggerAdvisor())
                .call()
                .content();
    }
}
