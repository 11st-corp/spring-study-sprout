package com.study.samplespringbootautoconfigure;

import javax.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(WebMvcAutoConfiguration.class) // 1
@EnableConfigurationProperties(RequestParameterLoggingFilterProperties.class) // 2
@RequiredArgsConstructor
public class SampleAutoConfiguration {
    private final RequestParameterLoggingFilterProperties requestParameterLoggingFilterProperties;

    @Bean
    @ConditionalOnProperty(name = "spring.mvc.request-parameter-logging-filter.enabled", havingValue = "true") // 3
    public Filter customUriLoggingFilter() {
        return new RequestParameterLoggingFilter(requestParameterLoggingFilterProperties.getLevel());
    }
}
