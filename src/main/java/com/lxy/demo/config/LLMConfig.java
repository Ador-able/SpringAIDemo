package com.lxy.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class LLMConfig {
    @Bean
    ChatClient douBao(RestClient.Builder restClientBuilder, LoggingRequestInterceptor loggingRequestInterceptor, LLMProperties llmProperties) {
        //对话记忆，不同人的对话应该使用不同的ChatClient
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder().defaultOptions(
                        OpenAiChatOptions.builder().model(llmProperties.getChatModel()).build()).openAiApi(
                        OpenAiApi.builder()
                                .baseUrl(llmProperties.getBaseUrl())
                                .completionsPath("/chat/completions")
                                .embeddingsPath("/embeddings")
                                .apiKey(llmProperties.getApiKey())
                                .restClientBuilder(restClientBuilder.requestInterceptor(loggingRequestInterceptor))
                                //输出详细的请求报文，方便排查问题
                                .build())
                .build();
        ChatClient.Builder builder = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                //.defaultToolCallbacks(toolCallbackProvider)如果集成了mcp客户端，这么配置就可以
                .defaultTools(new ToolsConfig());

        return builder.build();
    }

    @Bean
    public EmbeddingModel embeddingModel(LLMProperties llmProperties) {
        return new OpenAiEmbeddingModel(OpenAiApi.builder()
                .baseUrl(llmProperties.getBaseUrl())
                .completionsPath("/chat/completions")
                .embeddingsPath("/embeddings")
                .apiKey(llmProperties.getApiKey())
                .build(), MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder().model(llmProperties.getEmbeddingModel()).build());
    }


    @Bean
    RetryTemplate customRetry() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // 配置重试策略
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // 最大重试次数（包含第一次）

        // 配置退避策略
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000L); // 初始等待时间（毫秒）
        backOffPolicy.setMultiplier(2); // 间隔时间倍数
        backOffPolicy.setMaxInterval(10000L); // 最大等待时间

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
