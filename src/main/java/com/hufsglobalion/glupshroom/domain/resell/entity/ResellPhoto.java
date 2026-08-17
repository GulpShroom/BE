package com.hufsglobalion.glupshroom.domain.resell.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resell_photo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResellPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    @Column(name = "resell_id", nullable = false)
    private Long resellId;

    @Column(name = "photo_url", nullable = false, length = 500)
    private String photoUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Builder
    public ResellPhoto(Long resellId, String photoUrl, Integer sortOrder) {
        this.resellId = resellId;
        this.photoUrl = photoUrl;
        this.sortOrder = sortOrder;
    }
}
