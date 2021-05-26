package com.study.samplespringbootautoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "spring.mvc.request-parameter-logging-filter")
public class RequestParameterLoggingFilterProperties {
    private boolean enabled;
    private Level level;
}

