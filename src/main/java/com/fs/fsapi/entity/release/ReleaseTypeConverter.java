package com.fs.fsapi.entity.release;

import com.fs.fsapi.metallum.result.search.ReleaseType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReleaseTypeConverter implements AttributeConverter<ReleaseType, String> {

    @Override
    public String convertToDatabaseColumn(ReleaseType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.label;
    }

    @Override
    public ReleaseType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return ReleaseType.valueOfLabel(dbData).orElseGet(() -> null);
    }
}
