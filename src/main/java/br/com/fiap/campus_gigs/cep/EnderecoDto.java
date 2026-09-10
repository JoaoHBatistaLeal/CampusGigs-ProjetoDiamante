package br.com.fiap.campus_gigs.cep;

public record EnderecoDto(
        String cep,
        String cidade,
        String uf
) {}
