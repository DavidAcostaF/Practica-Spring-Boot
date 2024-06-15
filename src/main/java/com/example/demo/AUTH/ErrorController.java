package com.example.demo.AUTH;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

    @GetMapping("/unauthorized")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView unauthorized() {
        return new ModelAndView("redirect:/login"); // Redirecciona al usuario a la página de login
    }
}