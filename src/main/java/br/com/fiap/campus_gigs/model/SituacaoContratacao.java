package br.com.fiap.campus_gigs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SituacaoContratacao {
    SOLICITADA,
    ACEITA,
    CONCLUIDA,
    CANCELADA;

    @JsonCreator
    public static SituacaoContratacao fromString(String value) {
        if (value == null) return null;
        String normalized = value.trim().toUpperCase().replace("Í", "I");
        return SituacaoContratacao.valueOf(normalized);
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
