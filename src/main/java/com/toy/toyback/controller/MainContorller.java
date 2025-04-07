package com.toy.toyback.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MainContorller {

    @GetMapping("/")
    public String mainP(){

        return "main controller";
    }
}
