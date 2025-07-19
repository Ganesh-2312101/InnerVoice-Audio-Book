package com.InnerVoice.InnerVoiceProject.Model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int BookId;
    private String BookAuthor;
    private String BookName;
    @Enumerated(EnumType.STRING)
    private Category BookCategory;
    private String AudioFileLink;
    private String ImageLink;
    private String BookType;
    public enum Category{
        Free,Premium
    }
}
