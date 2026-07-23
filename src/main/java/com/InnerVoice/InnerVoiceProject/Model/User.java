package com.InnerVoice.InnerVoiceProject.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int UserId;
    private String UserName;
    @Enumerated(EnumType.STRING)
    private Category UserCategory;
    private String Password;

    private String EmailId;
    private String Name;
    @Enumerated(EnumType.STRING)
    private PremiumPlan premiumPlan;

    private LocalDate premiumStartDate;

    private String profilePicture; // stores uploaded filename e.g. "avatar_3.jpg"

    public enum PremiumPlan{
        MONTHLY , YEARLY
    }
    public enum Category{
        FREE_USER,PREMIUM_USER
    }


}
