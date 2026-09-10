package br.com.fiap.campus_gigs.controller;

import br.com.fiap.campus_gigs.dto.ContratacaoRequest;
import br.com.fiap.campus_gigs.dto.ContratacaoResponse;
import br.com.fiap.campus_gigs.dto.ContratacaoStatusRequest;
import br.com.fiap.campus_gigs.model.Usuario;
import br.com.fiap.campus_gigs.service.ContratacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contratacoes")
public class ContratacaoController {

    private final ContratacaoService contratacaoService;

    public ContratacaoController(ContratacaoService contratacaoService) {
        this.contratacaoService = contratacaoService;
    }

    @PostMapping
    public ResponseEntity<ContratacaoResponse> contratar(@RequestBody @Valid ContratacaoRequest request,
                                                         @AuthenticationPrincipal Usuario usuario) {
        ContratacaoResponse response = contratacaoService.contratar(request.servicoId(), usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ContratacaoResponse>> listar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(contratacaoService.listar(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratacaoResponse> buscarPorId(@PathVariable Long id,
                                                           @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(contratacaoService.buscarPorId(id, usuario));
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<ContratacaoResponse> atualizarSituacao(@PathVariable Long id,
                                                                 @RequestBody @Valid ContratacaoStatusRequest request,
                                                                 @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(contratacaoService.atualizarSituacao(id, request.situacao(), usuario));
    }
}
