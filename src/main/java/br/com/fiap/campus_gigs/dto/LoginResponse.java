package br.com.fiap.campus_gigs.dto;

public record LoginResponse(
        String token,
        String tipo,
        UsuarioResponse usuario
) {
    public static LoginResponse of(String token, UsuarioResponse usuario) {
        return new LoginResponse(token, "Bearer", usuario);
    }
}