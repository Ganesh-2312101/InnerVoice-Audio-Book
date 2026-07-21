package com.InnerVoice.InnerVoiceProject.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;

    private String userName;

    private LocalDateTime loginTime;

    private String ipAddress;

    private String deviceName;
}
