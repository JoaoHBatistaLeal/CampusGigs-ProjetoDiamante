package br.com.fiap.campus_gigs.controller;

import br.com.fiap.campus_gigs.dto.CepUpdateRequest;
import br.com.fiap.campus_gigs.dto.UsuarioRequest;
import br.com.fiap.campus_gigs.dto.UsuarioResponse;
import br.com.fiap.campus_gigs.model.Usuario;
import br.com.fiap.campus_gigs.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioRequest request) {
        Usuario usuario = usuarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(usuario));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @PatchMapping("/me/cep")
    public ResponseEntity<UsuarioResponse> atualizarCep(@RequestBody @Valid CepUpdateRequest request,
                                                        @AuthenticationPrincipal Usuario usuario) {
        Usuario atualizado = usuarioService.atualizarCep(usuario.getId(), request.cep());
        return ResponseEntity.ok(UsuarioResponse.from(atualizado));
    }
}