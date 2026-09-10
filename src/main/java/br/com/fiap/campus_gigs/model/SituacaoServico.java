package br.com.fiap.campus_gigs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SituacaoServico {
    ATIVO,
    PAUSADO,
    ENCERRADO;

    @JsonCreator
    public static SituacaoServico fromString(String value) {
        if (value == null) return null;
        return SituacaoServico.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
