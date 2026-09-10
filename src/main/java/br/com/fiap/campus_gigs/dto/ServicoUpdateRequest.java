package br.com.fiap.campus_gigs.dto;

import br.com.fiap.campus_gigs.model.SituacaoServico;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServicoUpdateRequest(

        @Size(max = 150, message = "Título deve ter no máximo 150 caracteres")
        String titulo,

        String descricao,

        @Size(max = 80, message = "Categoria deve ter no máximo 80 caracteres")
        String categoria,

        @Positive(message = "Preço deve ser maior que zero")
        BigDecimal preco,

        SituacaoServico situacao
) {}
