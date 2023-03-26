package com.example.dbnode.authentication;

import com.example.dbnode.authentication.nodes.NodesJwtAuthenticationFilter;
import com.example.dbnode.authentication.nodes.NodesJwtService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<NodesJwtAuthenticationFilter> jwtAuthenticationFilterRegistrationBean(){
        FilterRegistrationBean<NodesJwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<NodesJwtAuthenticationFilter>();
        registrationBean.setFilter(new NodesJwtAuthenticationFilter(new NodesJwtService()));
        registrationBean.addUrlPatterns("/node/broadcast/*");
        registrationBean.addUrlPatterns("/node/redirect/*");
        return registrationBean;
    }
}
