package com.postutmetrainer.repository;

import com.postutmetrainer.model.ExamSession;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    List<ExamSession> findByStudent_Id(Long studentId);
    long countByStudent_IdAndCompletedAtIsNotNull(Long studentId);
}
