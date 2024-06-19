package com.example.demo.AUTH;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(Model model, WebRequest webRequest) {
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        int statusCode = (int) errorAttributes.get("status");

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            model.addAttribute("errorCode", "404");
            model.addAttribute("errorMessage", "Page not found");
            return "404";  // Ensure you have this template
        } else {
            model.addAttribute("errorCode", statusCode);
            model.addAttribute("errorMessage", errorAttributes.get("error"));
            return "error";  // Ensure you have this template
        }
    }
}
