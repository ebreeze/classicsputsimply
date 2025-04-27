package com.viannele.classicsputsimply;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/") // Maps directly to /
    @ResponseBody // Add @ResponseBody if you want to return a String directly
    public String home() {
        return "Welcome to the Classics-put-simply API!";
    }
}