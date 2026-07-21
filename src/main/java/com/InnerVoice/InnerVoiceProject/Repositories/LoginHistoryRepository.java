package com.InnerVoice.InnerVoiceProject.Repositories;

import com.InnerVoice.InnerVoiceProject.Model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Integer> {
    List<LoginHistory> findByUserIdOrderByLoginTimeDesc(int userId);
    List<LoginHistory> findAllByOrderByLoginTimeDesc();
}
