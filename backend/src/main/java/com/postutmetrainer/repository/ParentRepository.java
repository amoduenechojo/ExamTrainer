package com.postutmetrainer.repository;

import com.postutmetrainer.model.Parent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByUser_Email(String email);
}
