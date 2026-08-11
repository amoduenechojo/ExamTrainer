package com.postutmetrainer.service;

import com.postutmetrainer.dto.*;
import com.postutmetrainer.exception.ApiException;
import com.postutmetrainer.model.*;
import com.postutmetrainer.repository.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExamService {

    // A topic counts as "weak" below this accuracy, once there's enough data to trust the number.
    private static final double WEAK_ACCURACY_THRESHOLD = 60.0;
    private static final int MIN_ATTEMPTS_FOR_WEAK_SIGNAL = 3;
    private static final int FULL_MOCK_QUESTION_COUNT = 40;

    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final ShortcutRepository shortcutRepository;
    private final ExamSessionRepository examSessionRepository;
    private final AttemptRepository attemptRepository;
    private final StudentRepository studentRepository;

    public ExamService(
            SubjectRepository subjectRepository,
            TopicRepository topicRepository,
            QuestionRepository questionRepository,
            ShortcutRepository shortcutRepository,
            ExamSessionRepository examSessionRepository,
            AttemptRepository attemptRepository,
            StudentRepository studentRepository) {
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.shortcutRepository = shortcutRepository;
        this.examSessionRepository = examSessionRepository;
        this.attemptRepository = attemptRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjects() {
        return subjectRepository.findAll().stream()
                .map(subject -> new SubjectResponse(
                        subject.getId(),
                        subject.getName(),
                        topicRepository.countBySubject_Id(subject.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopicResponse> getTopics(Long subjectId) {
        return topicRepository.findBySubject_Id(subjectId).stream()
                .map(topic -> new TopicResponse(
                        topic.getId(),
                        topic.getName(),
                        questionRepository.countByTopic_Id(topic.getId())))
                .toList();
    }

    @Transactional
    public StartSessionResponse startSession(String studentEmail, StartSessionRequest request) {
        Student student = getStudentByEmail(studentEmail);
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Subject not found"));

        ExamMode mode = parseMode(request.mode());
        Topic topic = null;
        if (mode == ExamMode.TOPIC) {
            if (request.topicId() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "topicId is required for a topic drill");
            }
            topic = topicRepository.findById(request.topicId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Topic not found"));
        }

        ExamSession session = ExamSession.builder()
                .student(student)
                .subject(subject)
                .topic(topic)
                .mode(mode)
                .startedAt(LocalDateTime.now())
                .build();
        examSessionRepository.save(session);

        return new StartSessionResponse(session.getId(), toModeString(mode));
    }

    @Transactional(readOnly = true)
    public SessionQuestionsResponse getSessionQuestions(Long sessionId, String studentEmail) {
        ExamSession session = getSession(sessionId);
        assertOwnsSession(session, studentEmail);

        List<Question> pool = switch (session.getMode()) {
            case TOPIC -> questionRepository.findByTopic_Id(session.getTopic().getId());
            case SUBJECT, FULL_MOCK -> questionRepository.findBySubject_Id(session.getSubject().getId());
        };

        List<Question> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled);

        List<Question> selected = session.getMode() == ExamMode.FULL_MOCK
                ? shuffled.stream().limit(FULL_MOCK_QUESTION_COUNT).toList()
                : shuffled;

        List<QuestionResponse> questionResponses = selected.stream()
                .map(question -> new QuestionResponse(
                        question.getId(),
                        question.getStem(),
                        question.getOptions().stream()
                                .map(option -> new QuestionOptionResponse(option.getId(), option.getText()))
                                .toList()))
                .toList();

        return new SessionQuestionsResponse(session.getId(), toModeString(session.getMode()), questionResponses);
    }

    @Transactional
    public AnswerFeedbackResponse submitAnswer(Long sessionId, SubmitAnswerRequest request, String studentEmail) {
        ExamSession session = getSession(sessionId);
        assertOwnsSession(session, studentEmail);

        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Question not found"));

        QuestionOption chosenOption = question.getOptions().stream()
                .filter(option -> option.getId().equals(request.chosenOptionId()))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "That option doesn't belong to this question"));

        QuestionOption correctOption = question.getOptions().stream()
                .filter(QuestionOption::isCorrect)
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Question has no correct option configured"));

        boolean isCorrect = chosenOption.isCorrect();

        Attempt attempt = Attempt.builder()
                .session(session)
                .question(question)
                .chosenOption(chosenOption)
                .correct(isCorrect)
                .answeredAt(LocalDateTime.now())
                .build();
        attemptRepository.save(attempt);

        String shortcut = question.getShortcutOverride() != null
                ? question.getShortcutOverride()
                : shortcutRepository.findByTopic_Id(question.getTopic().getId())
                        .map(Shortcut::getMethod)
                        .orElse(null);

        return new AnswerFeedbackResponse(isCorrect, correctOption.getId(), question.getDetailedExplanation(), shortcut);
    }

    @Transactional
    public void completeSession(Long sessionId, String studentEmail) {
        ExamSession session = getSession(sessionId);
        assertOwnsSession(session, studentEmail);
        List<Attempt> attempts = attemptRepository.findBySession_Id(sessionId);
        int score = (int) attempts.stream().filter(Attempt::isCorrect).count();

        session.setScore(score);
        session.setCompletedAt(LocalDateTime.now());
        examSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public SessionResultsResponse getSessionResults(Long sessionId, String studentEmail) {
        ExamSession session = getSession(sessionId);
        assertOwnsSession(session, studentEmail);
        List<Attempt> attempts = attemptRepository.findBySession_Id(sessionId);

        int total = attempts.size();
        int score = session.getScore() != null ? session.getScore() : (int) attempts.stream().filter(Attempt::isCorrect).count();
        double percentage = total == 0 ? 0 : Math.round((score * 1000.0) / total) / 10.0;

        return new SessionResultsResponse(score, total, percentage, computeWeakTopics(attempts));
    }

    @Transactional(readOnly = true)
    public List<WeakTopicResponse> getWeakTopics(String studentEmail) {
        Student student = getStudentByEmail(studentEmail);
        List<Attempt> attempts = attemptRepository.findBySession_Student_Id(student.getId());
        return computeWeakTopics(attempts);
    }

    @Transactional(readOnly = true)
    public StudentProgressResponse getStudentProgress(Long studentId) {
        List<Attempt> attempts = attemptRepository.findBySession_Student_Id(studentId);
        long correct = attempts.stream().filter(Attempt::isCorrect).count();
        double overallAccuracy = attempts.isEmpty() ? 0 : Math.round((correct * 1000.0) / attempts.size()) / 10.0;
        long sessionsCompleted = examSessionRepository.countByStudent_IdAndCompletedAtIsNotNull(studentId);

        return new StudentProgressResponse(overallAccuracy, sessionsCompleted, computeWeakTopics(attempts));
    }

    // --- helpers -----------------------------------------------------------

    private List<WeakTopicResponse> computeWeakTopics(List<Attempt> attempts) {
        Map<Topic, List<Attempt>> attemptsByTopic = attempts.stream()
                .collect(Collectors.groupingBy(attempt -> attempt.getQuestion().getTopic()));

        return attemptsByTopic.entrySet().stream()
                .filter(topicEntry -> topicEntry.getValue().size() >= MIN_ATTEMPTS_FOR_WEAK_SIGNAL)
                .map(topicEntry -> {
                    Topic topic = topicEntry.getKey();
                    List<Attempt> topicAttempts = topicEntry.getValue();
                    long correct = topicAttempts.stream().filter(Attempt::isCorrect).count();
                    double accuracy = Math.round((correct * 1000.0) / topicAttempts.size()) / 10.0;
                    return new WeakTopicResponse(topic.getId(), topic.getName(), topic.getSubject().getId(), topic.getSubject().getName(), accuracy);
                })
                .filter(weakTopic -> weakTopic.accuracy() < WEAK_ACCURACY_THRESHOLD)
                .sorted(Comparator.comparingDouble(WeakTopicResponse::accuracy))
                .toList();
    }

    private ExamSession getSession(Long sessionId) {
        return examSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Session not found"));
    }

    // Prevents one student from reading/answering/completing another student's session by
    // guessing or incrementing a session ID.
    private void assertOwnsSession(ExamSession session, String studentEmail) {
        if (!session.getStudent().getUser().getEmail().equals(studentEmail)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "This session doesn't belong to you");
        }
    }

    private Student getStudentByEmail(String email) {
        return studentRepository.findByUser_Email(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student profile not found"));
    }

    private ExamMode parseMode(String mode) {
        return switch (mode) {
            case "topic" -> ExamMode.TOPIC;
            case "subject" -> ExamMode.SUBJECT;
            case "full-mock" -> ExamMode.FULL_MOCK;
            default -> throw new ApiException(HttpStatus.BAD_REQUEST, "Unknown session mode: " + mode);
        };
    }

    private String toModeString(ExamMode mode) {
        return switch (mode) {
            case TOPIC -> "topic";
            case SUBJECT -> "subject";
            case FULL_MOCK -> "full-mock";
        };
    }
}
