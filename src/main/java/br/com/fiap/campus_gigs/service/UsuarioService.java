package br.com.fiap.campus_gigs.service;

import br.com.fiap.campus_gigs.cep.CepService;
import br.com.fiap.campus_gigs.cep.EnderecoDto;
import br.com.fiap.campus_gigs.dto.UsuarioRequest;
import br.com.fiap.campus_gigs.model.Papel;
import br.com.fiap.campus_gigs.model.Usuario;
import br.com.fiap.campus_gigs.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CepService cepService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          CepService cepService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.cepService = cepService;
    }

    @Transactional
    public Usuario cadastrar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        String cidade = null;
        String uf = null;
        String cepFormatado = request.cep();

        if (request.cep() != null && !request.cep().isBlank()) {
            EnderecoDto endereco = cepService.buscarEndereco(request.cep());
            if (endereco != null) {
                cidade = endereco.cidade();
                uf = endereco.uf();
                cepFormatado = endereco.cep();
            }
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .cep(cepFormatado)
                .cidade(cidade)
                .uf(uf)
                .papel(Papel.USER)
                .build();

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizarCep(Long usuarioId, String novoCep) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        EnderecoDto endereco = cepService.buscarEndereco(novoCep);
        if (endereco != null) {
            usuario.setCep(endereco.cep());
            usuario.setCidade(endereco.cidade());
            usuario.setUf(endereco.uf());
        }

        return usuarioRepository.save(usuario);
    }
}