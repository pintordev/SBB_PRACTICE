package com.example.sbb.example.preparation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    // root URL redircet to /question/list
    @GetMapping("/")
    public String index() {
        return "redirect:/question/list";
    }
}
