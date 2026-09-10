package br.com.fiap.campus_gigs.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SituacaoContratacaoConverter implements AttributeConverter<SituacaoContratacao, String> {

    @Override
    public String convertToDatabaseColumn(SituacaoContratacao attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public SituacaoContratacao convertToEntityAttribute(String dbData) {
        return dbData == null ? null : SituacaoContratacao.valueOf(dbData.trim().toUpperCase());
    }
}
