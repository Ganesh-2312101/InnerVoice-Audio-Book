package com.InnerVoice.InnerVoiceProject.Services;

import com.InnerVoice.InnerVoiceProject.Model.*;
import com.InnerVoice.InnerVoiceProject.Repositories.LoginHistoryRepository;
import com.InnerVoice.InnerVoiceProject.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

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

    public ResponseEntity<Object> login(String name, String password, String ipAddress, String deviceName)
    {
        Optional<User> temp = userRepository.findByUserName(name);
        if(temp.isPresent())
        {
            User user = temp.get();
            if(password.equals(user.getPassword()))
            {
                // Save login history on every successful login
                LoginHistory history = new LoginHistory();
                history.setUserId(user.getUserId());
                history.setUserName(user.getUserName());
                history.setLoginTime(LocalDateTime.now());
                history.setIpAddress(ipAddress != null && ipAddress.length() > 255 ? ipAddress.substring(0, 255) : ipAddress);
                history.setDeviceName(deviceName != null && deviceName.length() > 255 ? deviceName.substring(0, 255) : deviceName);
                loginHistoryRepository.save(history);

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

    public List<LoginHistory> getLoginHistory(int userId) {
        return loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId);
    }

    public List<LoginHistory> getAllLoginHistory() {
        return loginHistoryRepository.findAllByOrderByLoginTimeDesc();
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

    public ResponseEntity<Object> uploadProfilePicture(int userId, MultipartFile file) {
        Optional<User> temp = userRepository.findById(userId);
        if (temp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        }

        try {
            // Save to src/main/resources/static/profile-pics/
            String uploadDir = "src/main/resources/static/profile-pics/";
            Files.createDirectories(Paths.get(uploadDir));

            // Unique filename: avatar_{userId}_{uuid}.ext
            String originalName = file.getOriginalFilename();
            String ext = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : ".jpg";
            String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, file.getBytes());

            User user = temp.get();
            user.setProfilePicture(fileName);
            userRepository.save(user);

            return ResponseEntity.ok("/profile-pics/" + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
        }
    }
}
