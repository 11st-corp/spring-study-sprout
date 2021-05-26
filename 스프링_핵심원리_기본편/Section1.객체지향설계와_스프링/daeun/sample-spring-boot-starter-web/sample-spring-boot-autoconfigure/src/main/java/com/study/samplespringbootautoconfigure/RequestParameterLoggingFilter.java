package com.study.samplespringbootautoconfigure;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

@Slf4j
public class RequestParameterLoggingFilter implements Filter {

    public RequestParameterLoggingFilter(Level level) {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String params = request.getParameterMap()
            .entrySet()
            .stream()
            .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
            .flatMap(Stream::of)
            .collect(Collectors.joining("&"));
        log(params);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private void log(String params) {  }
}
