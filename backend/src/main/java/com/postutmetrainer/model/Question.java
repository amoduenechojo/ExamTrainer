package com.postutmetrainer.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Question extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Lob
    @Column(nullable = false)
    private String stem;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionOption> options = new ArrayList<>();

    @Lob
    @Column(nullable = false)
    private String detailedExplanation;

    // Optional: overrides the topic-level shortcut for this specific question.
    // Null means "use the topic's shortcut".
    @Lob
    private String shortcutOverride;

    // Attribution — where this question came from, for provenance tracking.
    private String source;
}
