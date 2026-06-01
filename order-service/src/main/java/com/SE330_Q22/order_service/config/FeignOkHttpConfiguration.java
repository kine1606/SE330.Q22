package com.SE330_Q22.order_service.config;

import feign.Client;
import feign.okhttp.OkHttpClient;
import java.time.Duration;
import okhttp3.OkHttpClient.Builder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignOkHttpConfiguration {

    @Bean
    @ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
    public okhttp3.OkHttpClient okHttpClient() {
        return new Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client feignClient(okhttp3.OkHttpClient okHttpClient) {
        // Force Feign to use OkHttp instead of the JDK default client
        return new OkHttpClient(okHttpClient);
    }
}
