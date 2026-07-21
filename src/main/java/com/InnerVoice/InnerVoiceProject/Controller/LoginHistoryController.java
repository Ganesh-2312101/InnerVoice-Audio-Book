package com.InnerVoice.InnerVoiceProject.Controller;

import com.InnerVoice.InnerVoiceProject.Model.LoginHistory;
import com.InnerVoice.InnerVoiceProject.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/login-history")
@CrossOrigin
public class LoginHistoryController {

    @Autowired
    private UserService userService;

    // GET /login-history/all  — all logins across all users (newest first)
    @GetMapping("/all")
    public List<LoginHistory> getAllLoginHistory() {
        return userService.getAllLoginHistory();
    }

    // GET /login-history/user/{userId}  — logins for a specific user (newest first)
    @GetMapping("/user/{userId}")
    public List<LoginHistory> getLoginHistoryByUser(@PathVariable int userId) {
        return userService.getLoginHistory(userId);
    }
}
