package com.postutmetrainer.repository;

import com.postutmetrainer.model.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUser_Email(String email);
    Optional<Student> findByInviteCode(String inviteCode);
    boolean existsByInviteCode(String inviteCode);
}
