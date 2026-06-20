package com.suoduan.controller;

import com.suoduan.entity.Result;
import com.suoduan.entity.User;
import com.suoduan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService accountService;

    @Autowired
    public UserController(UserService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User form) {
        try {
            accountService.register(form.getUsername(), form.getPassword(), form.getEmail());
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody User credentials, HttpSession session) {
        try {
            User signedInUser = accountService.login(credentials.getUsername(), credentials.getPassword());
            bindLoginState(session, signedInUser);
            signedInUser.setPassword(null);
            return Result.ok(signedInUser);
        } catch (RuntimeException e) {
            return Result.fail(401, e.getMessage());
        }
    }

    @GetMapping("/logout")
    public Result<?> logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }

    @GetMapping("/user/current")
    public Result<User> currentUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Result.fail(401, "未登录");
        }
        User user = accountService.findById(userId);
        user.setPassword(null);
        return Result.ok(user);
    }

    private void bindLoginState(HttpSession session, User user) {
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
    }
}
