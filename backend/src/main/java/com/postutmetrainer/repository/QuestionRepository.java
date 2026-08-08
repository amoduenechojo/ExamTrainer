package com.postutmetrainer.repository;

import com.postutmetrainer.model.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTopic_Id(Long topicId);
    List<Question> findBySubject_Id(Long subjectId);
    long countByTopic_Id(Long topicId);
}
