package com.hufsglobalion.glupshroom.domain.resell.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resell_selected_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResellSelectedTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resell_selected_tag_id")
    private Long id;

    @Column(name = "resell_id", nullable = false)
    private Long resellId;

    @Column(name = "journey_id", nullable = false)
    private Long journeyId;

    @Column(name = "tag_type", nullable = false, length = 20)
    private String tagType;

    @Builder
    public ResellSelectedTag(Long resellId, Long journeyId, String tagType) {
        this.resellId = resellId;
        this.journeyId = journeyId;
        this.tagType = tagType;
    }
}
