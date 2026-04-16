package com.trackiq.backend.controller;

import java.util.*;
import com.trackiq.backend.entity.User;
import com.trackiq.backend.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/developers")
    @PreAuthorize("hasRole('MANAGER')")
    public List<User> getDevelopers() {
        return userService.getDevelopers();
    }
    // 🔹 GET USER PROFILE
    @PreAuthorize("hasAnyRole('REPORTER','MANAGER','DEVELOPER')")
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    @PreAuthorize("hasAnyRole('REPORTER','MANAGER','DEVELOPER')")
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }
}