package br.com.fiap.campus_gigs.cep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String logradouro,
        String localidade,
        String uf,
        Boolean erro
) {}
