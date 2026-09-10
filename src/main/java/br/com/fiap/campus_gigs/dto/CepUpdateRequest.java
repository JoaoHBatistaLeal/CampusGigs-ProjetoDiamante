package br.com.fiap.campus_gigs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CepUpdateRequest(

        @NotBlank(message = "CEP é obrigatório")
        @Size(max = 9, message = "CEP inválido")
        String cep
) {}
