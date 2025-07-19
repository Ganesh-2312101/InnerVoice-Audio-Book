package com.InnerVoice.InnerVoiceProject.Services;

import com.InnerVoice.InnerVoiceProject.Model.*;
import com.InnerVoice.InnerVoiceProject.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Object> addNewUser(User user)
    {
        Optional<User> existingUser = userRepository.findByUserName(user.getUserName());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists with this username.");
        }

        Optional<User> existingUser1=userRepository.findByEmailId(user.getEmailId());
        if (existingUser1.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This Email is Already Registered.");
        }
        User savedUser=userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
    public ResponseEntity<Object> getUserById(int userId)
    {
        Optional<User> temp= userRepository.findById(userId);
        if(temp.isPresent())
        {
            User user=temp.get();
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
    }
    public ResponseEntity<Object> UnlockPremium(int id, User.PremiumPlan plan)
    {
        Optional<User> temp = userRepository.findById(id);
        if(temp.isPresent())
        {
            User user=temp.get();
            user.setUserCategory(User.Category.PREMIUM_USER);
            user.setPremiumPlan(plan);
            user.setPremiumStartDate(LocalDate.now());
            User upgradedUser=userRepository.save(user);
            return ResponseEntity.ok(upgradedUser);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
    }

    public ResponseEntity<Object> login(String name,String password)
    {
        Optional<User> temp = userRepository.findByUserName(name);
        if(temp.isPresent())
        {
            User user=temp.get();
            if(password.equals(user.getPassword()))
            {
                return ResponseEntity.ok(user);
            }
            else
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect Password");
            }
        }
        else
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        }
    }

    public long getPremiumDaysLeft(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getUserCategory() == User.Category.PREMIUM_USER && user.getPremiumStartDate() != null) {
                long daysValid = (user.getPremiumPlan() == User.PremiumPlan.MONTHLY) ? 30 : 365;
                LocalDate expiryDate = user.getPremiumStartDate().plusDays(daysValid);
                long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
                return Math.max(daysLeft, 0);
            }
        }
        return 0;
    }

    public void deleteUser(int id)
    {
        userRepository.deleteById(id);
    }
}
