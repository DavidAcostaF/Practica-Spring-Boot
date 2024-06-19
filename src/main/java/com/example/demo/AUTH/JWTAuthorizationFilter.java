package com.example.demo.AUTH;

import com.example.demo.student.StudentService;
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
    private final JWTUtilityService jwtUtilityService;
    private final StudentService studentService;

    public JWTAuthorizationFilter(JWTUtilityService jwtUtilityService, StudentService studentService) {
        this.jwtUtilityService = jwtUtilityService;
        this.studentService = studentService;
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
                String studentId = claims.getSubject(); // Asumiendo que el ID del estudiante está en el subject del token

                // Verificar si el estudiante aún existe
                if (studentService.isStudentDeleted(Long.valueOf(studentId))) {
                    if (tokenCookie != null) {
                        tokenCookie.setMaxAge(0);
                        tokenCookie.setPath("/");
                        response.addCookie(tokenCookie);
                    }
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.sendRedirect("/login");
                    return;
                }

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(studentId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | ParseException | JOSEException e) {
                System.out.println("Failed to parse JWT token: " + e.getMessage());
                if (tokenCookie != null) {
                    tokenCookie.setMaxAge(0);
                    tokenCookie.setPath("/");
                    response.addCookie(tokenCookie);
                }
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
