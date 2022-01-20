package com.taramin.testProject.controller;

import com.taramin.testProject.entity.CurrentUser;
import com.taramin.testProject.entity.Post;
import com.taramin.testProject.entity.User;
import com.taramin.testProject.repository.PostRepository;
import com.taramin.testProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class AccountController {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public AccountController(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/account/{id}")
    public String account(@PathVariable(value = "id") Long id, Model model) {

        if (id == null){
            id = CurrentUser.getCurrentUserId();
        }
        getUserById(id, model);
        model.addAttribute("currentUserId", id);

        Iterator<Post> posts = postRepository.findAll().iterator();
        ArrayList<Post> resultPosts = new ArrayList<>();

        while (posts.hasNext()) {
            Post currentPost = posts.next();

            if (currentPost.getAuthorId().equals(id)) {
                resultPosts.add(currentPost);
            }

        }
        model.addAttribute("posts", resultPosts);
        return "account";
    }

    @GetMapping("/account/null")
    public String account(Model model) {
        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());
        return "accountNull";
    }

    @GetMapping("/account/editing")
    public String accountEditing(Model model) {

        if (CurrentUser.getCurrentUserId() != null){
            getUserById(CurrentUser.getCurrentUserId(), model);
        } else {
            model.addAttribute("accountUser", new User());
        }

        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());
        return "accountEditing";
    }

    @PostMapping("/account/editing")
    public String accountEditing(@RequestParam String inputFirstName,
                                 @RequestParam String inputLastName,
                                 @RequestParam String inputUsername,
                                 @RequestParam String inputCountry,
                                 @RequestParam String inputCity,
                                 @RequestParam String inputDescription,
                                 @RequestParam("inputImage") MultipartFile file,
                                 Model model) throws IOException {

        Optional<User> optUser = userRepository.findById(CurrentUser.getCurrentUserId());
        List<User> currentUser = new ArrayList<>();
        optUser.ifPresent(currentUser::add);
        User cur = currentUser.get(0);

        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {

            if (userRepository.findById(CurrentUser.getCurrentUserId()).isPresent()) {
                userRepository.findById(CurrentUser.getCurrentUserId()).get().setImageName(uploadFile(file, uploadPath));
                userRepository.save(cur);
            }
        }

        if (userRepository.findById(CurrentUser.getCurrentUserId()).isPresent()) {
            String NAMES_REGEX = "^[A-ZА-ЯЁ][a-zа-яё-]+$";

            if (inputFirstName.matches(NAMES_REGEX)) {
                userRepository.findById(CurrentUser.getCurrentUserId()).get().setFirstName(inputFirstName);
                userRepository.save(cur);

            }
            if (inputLastName.matches(NAMES_REGEX)) {
                userRepository.findById(CurrentUser.getCurrentUserId()).get().setLastName(inputLastName);
                userRepository.save(cur);

            }

            if (inputCountry.matches(NAMES_REGEX)) {
                userRepository.findById(CurrentUser.getCurrentUserId()).get().setCountry(inputCountry);
                userRepository.save(cur);

            }
            if (inputCity.matches(NAMES_REGEX)) {
                userRepository.findById(CurrentUser.getCurrentUserId()).get().setCity(inputCity);
                userRepository.save(cur);

            }
            String ANY_SYMBOLS_REGEX = ".+";
            if (inputUsername.matches(ANY_SYMBOLS_REGEX)) {
                userRepository.findById(CurrentUser.getCurrentUserId()).get().setUsername(inputUsername);
                userRepository.save(cur);

            }
            if (inputDescription.matches(ANY_SYMBOLS_REGEX)) {
                userRepository.findById(CurrentUser.getCurrentUserId()).get().setDescription(inputDescription);
                userRepository.save(cur);
            }
        }

        if(CurrentUser.getCurrentUserId() != null){
            getUserById(CurrentUser.getCurrentUserId(), model);
        } else {
            model.addAttribute("accountUser", new User());
        }

        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());
        return "accountEditing";
    }

    public String uploadFile(@RequestParam("inputImage") MultipartFile file, String uploadPath) throws IOException {
        File uploadFile = new File(uploadPath);
        if (!uploadFile.exists()) {
            boolean mkdir = uploadFile.mkdir();
            if (!mkdir) {
                throw new RuntimeException("error of creating directory");
            }
        }
        String uniqueFileName = UUID.randomUUID() + "." + file.getOriginalFilename();

        file.transferTo(new File(uploadPath + '/'+uniqueFileName));
        return uniqueFileName;
    }

    private void getUserById(@PathVariable("id") Long id, Model model) {
        getCurrentUser(id, model, userRepository);
    }

    static void getCurrentUser(@PathVariable("id") Long id, Model model, UserRepository userRepository) {
        Optional<User> optUser = userRepository.findById(id);
        List<User> currentUser = new ArrayList<>();
        optUser.ifPresent(currentUser::add);
        User cur = currentUser.get(0);

        model.addAttribute("accountUser", cur);
    }

}
