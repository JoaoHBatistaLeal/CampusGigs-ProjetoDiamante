package br.com.fiap.campus_gigs.controller;

import br.com.fiap.campus_gigs.dto.LoginRequest;
import br.com.fiap.campus_gigs.dto.UsuarioResponse;
import br.com.fiap.campus_gigs.model.Usuario;
import br.com.fiap.campus_gigs.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@RequestBody @Valid LoginRequest request) {
        Usuario usuario = authService.autenticar(request);
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }
}