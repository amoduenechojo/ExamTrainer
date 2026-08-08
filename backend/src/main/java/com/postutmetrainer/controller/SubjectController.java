package com.postutmetrainer.controller;

import com.postutmetrainer.dto.SubjectResponse;
import com.postutmetrainer.dto.TopicResponse;
import com.postutmetrainer.service.ExamService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final ExamService examService;

    public SubjectController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    public List<SubjectResponse> getSubjects() {
        return examService.getSubjects();
    }

    @GetMapping("/{subjectId}/topics")
    public List<TopicResponse> getTopics(@PathVariable Long subjectId) {
        return examService.getTopics(subjectId);
    }
}
