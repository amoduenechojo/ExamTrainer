package com.postutmetrainer.controller;

import com.postutmetrainer.dto.*;
import com.postutmetrainer.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final ExamService examService;

    public SessionController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    public StartSessionResponse startSession(Authentication authentication, @Valid @RequestBody StartSessionRequest request) {
        return examService.startSession(authentication.getName(), request);
    }

    @GetMapping("/{sessionId}/questions")
    public SessionQuestionsResponse getQuestions(@PathVariable Long sessionId) {
        return examService.getSessionQuestions(sessionId);
    }

    @PostMapping("/{sessionId}/answers")
    public AnswerFeedbackResponse submitAnswer(@PathVariable Long sessionId, @Valid @RequestBody SubmitAnswerRequest request) {
        return examService.submitAnswer(sessionId, request);
    }

    @PostMapping("/{sessionId}/complete")
    public void completeSession(@PathVariable Long sessionId) {
        examService.completeSession(sessionId);
    }

    @GetMapping("/{sessionId}/results")
    public SessionResultsResponse getResults(@PathVariable Long sessionId) {
        return examService.getSessionResults(sessionId);
    }
}
