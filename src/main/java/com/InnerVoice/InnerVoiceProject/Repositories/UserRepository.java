package com.InnerVoice.InnerVoiceProject.Repositories;

import com.InnerVoice.InnerVoiceProject.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.UserName = :userName")
    Optional<User> findByUserName(@Param("userName") String userName);

    @Query("SELECT u FROM User u WHERE u.EmailId = :emailId")
    Optional<User> findByEmailId(@Param("emailId") String emailId);
}
