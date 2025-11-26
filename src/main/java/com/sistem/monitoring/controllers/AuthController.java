package com.sistem.monitoring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // harus sama persis dengan loginPage di Security config: "/Auth/login"
    @GetMapping("/Auth/login")
    public String login() {
        return "Auth/login"; // -> templates/Auth/login.html
    }
}
