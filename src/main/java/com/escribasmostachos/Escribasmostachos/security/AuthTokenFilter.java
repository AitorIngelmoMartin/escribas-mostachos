package com.escribasmostachos.Escribasmostachos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDto;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    
        if (request.getRequestURI().startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = getJwtFromRequestHeaders(request);
            if (token == null){
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid or missing token");
                return;
            }

            jwtService.validateJwt(token);

            String username = jwtService.getUsernameFromToken(token);
            User user = new User();
            user.setUsername(username);
            user.setPassword("");

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: " + e.getMessage());
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT token");
            return;
       } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: " + e.getMessage());
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token is expired");
            return;
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: " + e.getMessage());
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token is unsupported");
            return;
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: " + e.getMessage());
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT claims string is empty");
            return;
        } catch (JwtException e) {
            log.warn("Invalid token: " + e.getMessage());
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token");
            return;
        } catch (Exception e) {
            log.error("Unexpected error during user authentication: " + e.getMessage());
            writeErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during user authentication");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequestHeaders(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private static void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ApiResponseDto<Void> dto = new ApiResponseDto<>(status, message);
        new ObjectMapper().writeValue(response.getWriter(), dto);
    }
}
