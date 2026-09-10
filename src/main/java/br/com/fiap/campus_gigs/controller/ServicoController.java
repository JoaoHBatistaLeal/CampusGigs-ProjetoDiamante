package br.com.fiap.campus_gigs.controller;

import br.com.fiap.campus_gigs.dto.ServicoRequest;
import br.com.fiap.campus_gigs.dto.ServicoResponse;
import br.com.fiap.campus_gigs.dto.ServicoUpdateRequest;
import br.com.fiap.campus_gigs.model.SituacaoServico;
import br.com.fiap.campus_gigs.model.Usuario;
import br.com.fiap.campus_gigs.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping
    public ResponseEntity<ServicoResponse> cadastrar(@RequestBody @Valid ServicoRequest request,
                                                     @AuthenticationPrincipal Usuario usuario) {
        ServicoResponse response = servicoService.cadastrar(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ServicoResponse>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) SituacaoServico situacao) {
        return ResponseEntity.ok(servicoService.listar(categoria, situacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponse> atualizar(@PathVariable Long id,
                                                     @RequestBody @Valid ServicoUpdateRequest request,
                                                     @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(servicoService.atualizar(id, request, usuario));
    }

    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<ServicoResponse> encerrar(@PathVariable Long id,
                                                    @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(servicoService.encerrar(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServicoResponse> excluirOuEncerrar(@PathVariable Long id,
                                                             @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(servicoService.encerrar(id, usuario));
    }
}
