package com.InnerVoice.InnerVoiceProject.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "BookId")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "UserId")
    private User user;

    private String activityType;
    private LocalDateTime activityTime;
}
