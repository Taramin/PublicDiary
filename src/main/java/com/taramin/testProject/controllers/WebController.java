package com.taramin.testProject.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping("/account")
    public String account(Model model) {
        return "account";
    }

    @GetMapping("/signIn")
    public String signIn(Model model) {
        return "signIn";
    }

    @GetMapping("/signUp")
    public String signUp(Model model) {
        return "signUp";
    }

    @GetMapping("/collection")
    public String collection(Model model) {
        return "collection";
    }

    @GetMapping("/editing")
    public String editing(Model model) {
        return "editing";
    }

    @GetMapping("/friends")
    public String friends(Model model) {
        return "friends";
    }

}
