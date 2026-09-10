package br.com.fiap.campus_gigs.controller;

import br.com.fiap.campus_gigs.dto.LoginRequest;
import br.com.fiap.campus_gigs.dto.LoginResponse;
import br.com.fiap.campus_gigs.dto.UsuarioResponse;
import br.com.fiap.campus_gigs.model.Usuario;
import br.com.fiap.campus_gigs.service.AuthService;
import br.com.fiap.campus_gigs.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        Usuario usuario = authService.autenticar(request);
        String token = jwtService.gerarToken(usuario);
        return ResponseEntity.ok(LoginResponse.of(token, UsuarioResponse.from(usuario)));
    }
}