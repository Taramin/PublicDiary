package com.taramin.testProject.controller;

import com.taramin.testProject.entity.CurrentUser;
import com.taramin.testProject.entity.PostLike;
import com.taramin.testProject.entity.Post;
import com.taramin.testProject.entity.User;
import com.taramin.testProject.repository.PostLikeRepository;
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
public class CollectionController {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository likesRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public CollectionController(UserRepository userRepository, PostRepository postRepository, PostLikeRepository likesRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likesRepository = likesRepository;
    }


    @GetMapping("/collection")
    public String collection(Model model) {
        Iterable <Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());
        return "collection";
    }

    @GetMapping("/collection/{id}/edit")
    public String collectionEdit(@PathVariable(value = "id") Long id, Model model) {
        if (CurrentUser.getCurrentUserId() != null){
            getUserById(CurrentUser.getCurrentUserId(), model);
        } else {
            model.addAttribute("accountUser", new User());
        }
        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());

        if (postRepository.findById(id).isPresent() &&
                postRepository.findById(id).get().getAuthorId().equals(CurrentUser.getCurrentUserId())){

            Optional<Post> post = postRepository.findById(id);
            ArrayList<Post> cur = new ArrayList<>();
            post.ifPresent(cur::add);

            model.addAttribute("currentPost", cur.get(0));
            return "collectionEditing";
        } else {
            return "redirect:/collection";
        }
    }

    @PostMapping("/collection/{id}/edit")
    public String collectionEditPost(@PathVariable(value = "id") Long id,
                                     @RequestParam String inputDescription,
                                     @RequestParam("inputImage") MultipartFile file,
                                     Model model) throws IOException {
        if(CurrentUser.getCurrentUserId() != null){
            getUserById(CurrentUser.getCurrentUserId(), model);
        } else {
            model.addAttribute("accountUser", new User());
        }

        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());

        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty() &&
                inputDescription != null && CurrentUser.getCurrentUserId() != null) {

            if (userRepository.findById(CurrentUser.getCurrentUserId()).isPresent() &&
                    postRepository.findById(id).isPresent()) {
                Post currentPost = postRepository.findById(id).get();
                currentPost.setDescription(inputDescription);
                currentPost.setImageName(uploadFile(file, uploadPath));
                currentPost.setAuthorId(CurrentUser.getCurrentUserId());

                postRepository.save(currentPost);
            }
            return "redirect:/collection";

        } else {
            return "collectionEditing";
        }
    }

    @GetMapping("/collection/{id}/delete")
    public String collectionDeletePost(@PathVariable(value = "id") Long id) {

        if (postRepository.findById(id).isPresent() &&
                postRepository.findById(id).get().getAuthorId().equals(CurrentUser.getCurrentUserId())) {
            postRepository.deleteById(id);
        }

        return "redirect:/collection";
    }

    @GetMapping("/collection/{id}/like")
    public String collectionLikePost(@PathVariable(value = "id") Long id) {

        if (postRepository.findById(id).isPresent()  && CurrentUser.getCurrentUserId() != null
                && userRepository.findById(CurrentUser.getCurrentUserId()).isPresent()) {
            Post currentPost = postRepository.findById(id).get();

            Iterator<PostLike> allLikes = likesRepository.findAll().iterator();
            boolean isLike = false;
            while (allLikes.hasNext()) {
                PostLike postLike = allLikes.next();
                if (postLike.getPostId().equals(id) && postLike.getUserId().equals(CurrentUser.getCurrentUserId())) {
                    isLike = true;
                    likesRepository.delete(postLike);
                    postRepository.findById(id).get().setLikes(postRepository.findById(id).get().getLikes()-1L);
                    postRepository.save(currentPost);
                    break;
                }
            }
            if(!isLike) {
                postRepository.findById(id).get().setLikes(postRepository.findById(id).get().getLikes()+1L);
                postRepository.save(currentPost);
                likesRepository.save(new PostLike(id, CurrentUser.getCurrentUserId()));
            }

        }

        return "redirect:/collection";
    }



    @GetMapping("/collection/null/edit")
    public String collectionAddNewPost(Model model) {
        if(CurrentUser.getCurrentUserId() != null){
            getUserById(CurrentUser.getCurrentUserId(), model);
        } else {
            model.addAttribute("accountUser", new User());
        }
        model.addAttribute("currentPost", new Post());
        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());
        return "collectionEditing";
    }

    @PostMapping("/collection/null/edit")
    public String collectionAdd(@RequestParam String inputDescription,
                                @RequestParam("inputImage") MultipartFile file,
                                Model model) throws IOException {

        if(CurrentUser.getCurrentUserId() != null){
            getUserById(CurrentUser.getCurrentUserId(), model);
        } else {
            model.addAttribute("accountUser", new User());
        }
        model.addAttribute("currentPost", new Post());
        model.addAttribute("currentUserId", CurrentUser.getCurrentUserId());

        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty() &&
                inputDescription != null && CurrentUser.getCurrentUserId() != null) {

            if (userRepository.findById(CurrentUser.getCurrentUserId()).isPresent()) {
                Post newPost = new Post(inputDescription, uploadFile(file, uploadPath), CurrentUser.getCurrentUserId());
                postRepository.save(newPost);

            }
            return "redirect:/collection";
        } else {
            return "collectionEditing";
        }
    }

    private void getUserById(@PathVariable("id") Long id, Model model) {
        AccountController.getCurrentUser(id, model, userRepository);
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

}
