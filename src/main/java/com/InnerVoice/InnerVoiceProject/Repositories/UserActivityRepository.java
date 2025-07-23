package com.InnerVoice.InnerVoiceProject.Repositories;

import com.InnerVoice.InnerVoiceProject.Model.Book;
import com.InnerVoice.InnerVoiceProject.Model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
public interface UserActivityRepository extends JpaRepository<UserActivity,Long> {
    @Query("SELECT ua.book FROM UserActivity ua WHERE ua.user.id = :userId")
    List<Book> findBooksListenedByUser(int userId);
}
