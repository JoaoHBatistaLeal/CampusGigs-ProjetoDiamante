package br.com.fiap.campus_gigs.dto;

import br.com.fiap.campus_gigs.model.SituacaoContratacao;
import jakarta.validation.constraints.NotNull;

public record ContratacaoStatusRequest(

        @NotNull(message = "Situação é obrigatória")
        SituacaoContratacao situacao
) {}
