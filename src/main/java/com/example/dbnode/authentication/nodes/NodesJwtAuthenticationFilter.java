package com.example.dbnode.authentication.nodes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class NodesJwtAuthenticationFilter implements Filter {

    private final NodesJwtService nodesJwtService;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header must be provided");
            return;
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();
        System.out.println(token);
        boolean isTokenValid = nodesJwtService.validateToken(token);
        if(isTokenValid){
            System.out.println("Token is valid");
        }
        else {
            System.out.println("Token is not valid");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
