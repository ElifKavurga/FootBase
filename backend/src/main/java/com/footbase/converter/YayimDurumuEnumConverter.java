package com.footbase.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class YayimDurumuEnumConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        // PostgreSQL enum'a cast etmek için değeri olduğu gibi döndür
        // Hibernate otomatik olarak enum'a cast edecek
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return dbData;
    }
}
