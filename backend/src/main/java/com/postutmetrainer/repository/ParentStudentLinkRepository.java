package com.postutmetrainer.repository;

import com.postutmetrainer.model.ParentStudentLink;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentStudentLinkRepository extends JpaRepository<ParentStudentLink, Long> {
    List<ParentStudentLink> findByParent_Id(Long parentId);
    boolean existsByParent_IdAndStudent_Id(Long parentId, Long studentId);
}
