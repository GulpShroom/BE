package com.hufsglobalion.glupshroom.domain.ownership.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OwnershipStatusConverter implements AttributeConverter<OwnershipStatus, String> {

    @Override
    public String convertToDatabaseColumn(OwnershipStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public OwnershipStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : OwnershipStatus.from(dbData);
    }
}
