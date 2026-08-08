package com.postutmetrainer.repository;

import com.postutmetrainer.model.Shortcut;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortcutRepository extends JpaRepository<Shortcut, Long> {
    Optional<Shortcut> findByTopic_Id(Long topicId);
}
