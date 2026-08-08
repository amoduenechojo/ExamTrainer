package com.postutmetrainer.repository;

import com.postutmetrainer.model.Attempt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findBySession_Id(Long sessionId);
    List<Attempt> findBySession_Student_Id(Long studentId);
    List<Attempt> findBySession_Student_IdAndQuestion_Topic_Id(Long studentId, Long topicId);
}
