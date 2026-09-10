package br.com.fiap.campus_gigs.dto;

import jakarta.validation.constraints.NotNull;

public record ContratacaoRequest(

        @NotNull(message = "ID do serviço é obrigatório")
        Long servicoId
) {}
