package com.tintomax.maxfighter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // carrega src/main/resources/templates/index.html
        return "index";
    }
}
