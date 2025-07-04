package com.escribasmostachos.Escribasmostachos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDTO;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public AuthTokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    
        String uri = request.getRequestURI();
        if (uri.startsWith("/auth")
                || uri.startsWith("/h2-console")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            log.debug("Skipping token validation for Swagger or auth path");
            return;
        }
        try {
            String token = getJwtFromRequestHeaders(request);
            if (token == null){
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid or missing token");
                return;
            }

            jwtService.validateJwt(token);

            User user = jwtService.buildUserFromToken(token);

            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, List.of(authority));
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
        ApiResponseDTO<Void> dto = new ApiResponseDTO<>(status, message);
        new ObjectMapper().writeValue(response.getWriter(), dto);
    }
}
