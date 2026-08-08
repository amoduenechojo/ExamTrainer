package com.postutmetrainer.service;

import com.postutmetrainer.dto.LinkedStudentResponse;
import com.postutmetrainer.exception.ApiException;
import com.postutmetrainer.model.Parent;
import com.postutmetrainer.model.ParentStudentLink;
import com.postutmetrainer.model.Student;
import com.postutmetrainer.repository.ParentRepository;
import com.postutmetrainer.repository.ParentStudentLinkRepository;
import com.postutmetrainer.repository.StudentRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final ParentStudentLinkRepository linkRepository;

    public ParentService(
            ParentRepository parentRepository,
            StudentRepository studentRepository,
            ParentStudentLinkRepository linkRepository) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.linkRepository = linkRepository;
    }

    @Transactional
    public void linkToStudent(String parentEmail, String inviteCode) {
        Parent parent = getParentByEmail(parentEmail);
        Student student = studentRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No student found with that invite code"));

        if (linkRepository.existsByParent_IdAndStudent_Id(parent.getId(), student.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "You're already linked to this student");
        }

        ParentStudentLink link = ParentStudentLink.builder()
                .parent(parent)
                .student(student)
                .linkedAt(LocalDateTime.now())
                .build();
        linkRepository.save(link);
    }

    @Transactional(readOnly = true)
    public List<LinkedStudentResponse> getLinkedStudents(String parentEmail) {
        Parent parent = getParentByEmail(parentEmail);
        return linkRepository.findByParent_Id(parent.getId()).stream()
                .map(link -> new LinkedStudentResponse(link.getStudent().getId(), link.getStudent().getFullName()))
                .toList();
    }

    // Confirms this parent is actually linked to this student before exposing their progress.
    @Transactional(readOnly = true)
    public void assertLinked(String parentEmail, Long studentId) {
        Parent parent = getParentByEmail(parentEmail);
        if (!linkRepository.existsByParent_IdAndStudent_Id(parent.getId(), studentId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You are not linked to this student");
        }
    }

    private Parent getParentByEmail(String email) {
        return parentRepository.findByUser_Email(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Parent profile not found"));
    }
}
