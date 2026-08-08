package com.postutmetrainer.controller;

import com.postutmetrainer.dto.LinkStudentRequest;
import com.postutmetrainer.dto.LinkedStudentResponse;
import com.postutmetrainer.dto.StudentProgressResponse;
import com.postutmetrainer.service.ExamService;
import com.postutmetrainer.service.ParentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parents/me")
public class ParentController {

    private final ParentService parentService;
    private final ExamService examService;

    public ParentController(ParentService parentService, ExamService examService) {
        this.parentService = parentService;
        this.examService = examService;
    }

    @PostMapping("/link")
    public void link(Authentication authentication, @Valid @RequestBody LinkStudentRequest request) {
        parentService.linkToStudent(authentication.getName(), request.inviteCode());
    }

    @GetMapping("/students")
    public List<LinkedStudentResponse> getLinkedStudents(Authentication authentication) {
        return parentService.getLinkedStudents(authentication.getName());
    }

    @GetMapping("/students/{studentId}/progress")
    public StudentProgressResponse getStudentProgress(Authentication authentication, @PathVariable Long studentId) {
        parentService.assertLinked(authentication.getName(), studentId);
        return examService.getStudentProgress(studentId);
    }
}
