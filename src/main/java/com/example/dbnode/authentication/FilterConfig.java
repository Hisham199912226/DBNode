package com.example.dbnode.authentication;

import com.example.dbnode.authentication.nodes.NodesJwtAuthenticationFilter;
import com.example.dbnode.authentication.nodes.NodesJwtService;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<NodesJwtAuthenticationFilter> NodesjwtAuthenticationFilterRegistrationBean(){
        FilterRegistrationBean<NodesJwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<NodesJwtAuthenticationFilter>();
        registrationBean.setFilter(new NodesJwtAuthenticationFilter(new NodesJwtService()));
        registrationBean.addUrlPatterns("/node/broadcast/*");
        registrationBean.addUrlPatterns("/node/redirect/*");
        registrationBean.addUrlPatterns("/node/bootstrap/*");
        return registrationBean;
    }

    /*@Bean
    public FilterRegistrationBean<UserJwtAuthenticationFilter> UserjwtAuthenticationFilterRegistrationBean(){
        FilterRegistrationBean<UserJwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<UserJwtAuthenticationFilter>();
        registrationBean.setFilter(new UserJwtAuthenticationFilter(new UserJwtService()));
        registrationBean.addUrlPatterns("/node/client/*");
        return registrationBean;
    }*/
}
