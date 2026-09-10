package br.com.fiap.campus_gigs.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SituacaoServicoConverter implements AttributeConverter<SituacaoServico, String> {

    @Override
    public String convertToDatabaseColumn(SituacaoServico attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public SituacaoServico convertToEntityAttribute(String dbData) {
        return dbData == null ? null : SituacaoServico.valueOf(dbData.trim().toUpperCase());
    }
}
