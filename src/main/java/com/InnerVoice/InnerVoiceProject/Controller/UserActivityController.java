package com.InnerVoice.InnerVoiceProject.Controller;

import com.InnerVoice.InnerVoiceProject.Model.*;
import com.InnerVoice.InnerVoiceProject.Repositories.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/activity")
@CrossOrigin
public class UserActivityController {

    @Autowired
    private UserActivityRepository activityRepository;

    @PostMapping("/track")
    public void trackUserActivity(@RequestParam int userId, @RequestParam Integer bookId) {
        UserActivity activity = new UserActivity();

        User user = new User();
        user.setUserId(userId);
        activity.setUser(user);

        Book book = new Book();
        book.setBookId(bookId);
        activity.setBook(book);

        activity.setActivityType("PLAY");
        activity.setActivityTime(LocalDateTime.now());

        activityRepository.save(activity);
    }

    @GetMapping("/all")
    public List<UserActivity> getAllUserActivities() {
        return activityRepository.findAll();
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
        if (activityRepository.existsById(id)) {
            activityRepository.deleteById(id);
            return ResponseEntity.ok("Activity deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activity not found.");
        }
    }
}
