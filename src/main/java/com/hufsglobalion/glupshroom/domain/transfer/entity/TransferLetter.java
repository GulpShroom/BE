package com.hufsglobalion.glupshroom.domain.transfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transfer_letter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferLetter {

    @Id
    @Column(name = "letter_id")
    private Long id;

    @Column(name = "transfer_id", nullable = false)
    private Long transferId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_ai_draft", nullable = false)
    private boolean aiDraft;

    @Column(name = "is_sealed", nullable = false)
    private boolean sealed;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;
}
