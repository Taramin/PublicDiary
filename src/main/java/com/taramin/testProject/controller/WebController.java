package com.taramin.testProject.controller;

import com.taramin.testProject.entity.CurrentUser;
import com.taramin.testProject.entity.User;
import com.taramin.testProject.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class WebController {
    private final UserRepository userRepository;

    public WebController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/greeting")
    public String greeting(Model model) {
        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());
        return "greeting";
    }

    @GetMapping("/signIn")
    public String signIn(Model model) {
        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());
        return "signIn";
    }

    @PostMapping("/signIn")
    public String signIn(@RequestParam String email,
                         @RequestParam String password,
                         Model model) {
        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());

        for (User currentUser : userRepository.findAll()) {
            if (currentUser.getEmailAddress().equals(email) &&
                    currentUser.getPassword().equals(password)) {
                CurrentUser.setCurrentUserId(currentUser.getId());

                return "redirect:/account/" + currentUser.getId().toString();
            }
        }
        return "signIn";
    }

    @GetMapping("/signUp")
    public String signUp(Model model) {
        model.addAttribute("reservedUsernames", userRepository.findAll());
        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());
        return "signUp";
    }

    @PostMapping("/signUp")
    public String signUp(@RequestParam String firstName,
                         @RequestParam String lastName,
                         @RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String email,
                         @RequestParam String country,
                         @RequestParam String city,
                         Model model) {
        User newUser = new User(firstName, lastName, email, username, password, country, city);
        userRepository.save(newUser);

        CurrentUser.setCurrentUserId(newUser.getId());
        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());
        return "redirect:/greeting";
    }

    @GetMapping ("/friends")
    private String friendsBySearchTerm(@RequestParam ("searchTerm") String searchTerm, Model model) {
        Iterator <User> users = userRepository.findAll().iterator();
        List <User> result = new ArrayList<>();
        if (searchTerm != null) {
            showBySearchTerm(searchTerm, model, users, result);

        } else {
            model.addAttribute("users", users);
        }

        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());
        return "friends";
    }

    private void showBySearchTerm(String searchTerm, Model model, Iterator<User> users, List<User> result) {
        String regModified = ".*(?i)"+ searchTerm +".*";
        while (users.hasNext()) {
            User currentUser = users.next();
            if (currentUser.getUsername().matches(regModified) || currentUser.getFirstName().matches(regModified)
                    || currentUser.getLastName().matches(regModified)) {
                result.add(currentUser);
            }
        }
        model.addAttribute("users", result);
    }
}
