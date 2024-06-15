package com.example.demo.AUTH;

import com.example.demo.dtos.LoginDTO;
import com.example.demo.student.Student;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    private ResponseEntity<Student> register(@ModelAttribute Student student) throws Exception {
        return new ResponseEntity<>(authService.register(student), HttpStatus.CREATED);

    }

    @PostMapping("/login")
    private ResponseEntity<HashMap<String, String>> login(@RequestBody LoginDTO loginRequest, HttpServletResponse response) throws Exception {
        HashMap<String, String> login = authService.login(loginRequest);
        if (login.containsKey("jwt")) {
            // Configura la cookie con el JWT
            String jwt = login.get("jwt");
            Cookie cookie = new Cookie("token", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Asegúrate de que la aplicación esté en HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 días

            response.addCookie(cookie);
            return new ResponseEntity<>(login, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(login, HttpStatus.UNAUTHORIZED);
        }
    }

//    @GetMapping("/logout")
//    private ResponseEntity<String> logout(HttpServletResponse response) throws Exception {
//        SecurityContextHolder.clearContext();
//        //CookieUti.deleteCookie(request, response, "jwtToken"); // Implementa el método según tu configuración de cookies
//
//        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
//    }
}
