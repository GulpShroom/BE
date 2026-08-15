package com.hufsglobalion.glupshroom.domain.transfer.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TransferStatusConverter implements AttributeConverter<TransferStatus, String> {

    @Override
    public String convertToDatabaseColumn(TransferStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TransferStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TransferStatus.from(dbData);
    }
}
