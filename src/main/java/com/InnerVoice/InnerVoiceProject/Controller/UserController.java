package com.InnerVoice.InnerVoiceProject.Controller;

import com.InnerVoice.InnerVoiceProject.Model.*;
import com.InnerVoice.InnerVoiceProject.Services.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserByID(@PathVariable int userId)
    {
        return userService.getUserById(userId);
    }
    @PostMapping
    public ResponseEntity<Object> register(@RequestBody User user)
    {
        return userService.addNewUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestParam String name,@RequestParam String password) {
        return userService.login(name, password);
    }
    @PutMapping("/upgrade/{id}")
    public ResponseEntity<Object> upgradeUser(@PathVariable int id, @RequestParam User.PremiumPlan plan)
    {
        return userService.UnlockPremium(id,plan);
    }

    @GetMapping("/premium-days-left/{id}")
    public ResponseEntity<String> getDaysLeft(@PathVariable int id) {
        long daysLeft = userService.getPremiumDaysLeft(id);

        if (daysLeft > 0) {
            return ResponseEntity.ok("Premium valid for " + daysLeft + " more day(s).");
        } else {
            return ResponseEntity.ok("Premium has expired or not active.");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id)
    {
        userService.deleteUser(id);
    }
}
