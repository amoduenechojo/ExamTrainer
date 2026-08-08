package com.postutmetrainer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One reusable "how to attack this topic" method, shared by every question
 * in that topic instead of being rewritten per question.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Shortcut extends BaseEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "topic_id", nullable = false, unique = true)
    private Topic topic;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String method;
}
