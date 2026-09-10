package br.com.fiap.campus_gigs.dto;

import br.com.fiap.campus_gigs.model.Papel;
import br.com.fiap.campus_gigs.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String cep,
        String cidade,
        String uf,
        Papel papel
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(
                u.getId(), u.getNome(), u.getEmail(),
                u.getCep(), u.getCidade(), u.getUf(), u.getPapel()
        );
    }
}