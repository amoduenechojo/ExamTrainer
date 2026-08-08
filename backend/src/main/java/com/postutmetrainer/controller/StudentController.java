package com.postutmetrainer.controller;

import com.postutmetrainer.dto.WeakTopicResponse;
import com.postutmetrainer.service.ExamService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final ExamService examService;

    public StudentController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/me/weak-topics")
    public List<WeakTopicResponse> getWeakTopics(Authentication authentication) {
        return examService.getWeakTopics(authentication.getName());
    }
}
