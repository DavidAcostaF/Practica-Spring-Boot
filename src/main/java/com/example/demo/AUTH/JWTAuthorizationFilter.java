package com.example.demo.AUTH;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Collections;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    JWTUtilityService jwtUtilityService;

    public JWTAuthorizationFilter(JWTUtilityService jwtUtilityService) {
        this.jwtUtilityService = jwtUtilityService;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        Cookie tokenCookie = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    tokenCookie = cookie;
                }
            }
        }
        if (token != null) {
            try {
                JWTClaimsSet claims = jwtUtilityService.parseJWT(token);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | ParseException | JOSEException e) {
                // Logging the exception for debugging purposes
                System.out.println("Failed to parse JWT token: " + e.getMessage());
                if (tokenCookie != null) {
                    tokenCookie.setMaxAge(0);
                    tokenCookie.setPath("/"); // Ensure you set the correct path for your application
                    response.addCookie(tokenCookie);
                }
                e.printStackTrace();
                // Optionally, you can send an error response or handle it in a custom way
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
