package com.hufsglobalion.glupshroom.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_master")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMaster {

    @Id
    @Column(name = "serial_no", nullable = false, length = 50)
    private String serialNo;

    @Column(name = "official_name", nullable = false, length = 100)
    private String officialName;

    @Column(name = "official_image_url", length = 500)
    private String officialImageUrl;

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(name = "product_line", length = 50)
    private String productLine;

    @Column(name = "color", length = 50)
    private String color;
}
