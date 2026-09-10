package br.com.fiap.campus_gigs.service;

import br.com.fiap.campus_gigs.dto.ServicoRequest;
import br.com.fiap.campus_gigs.dto.ServicoResponse;
import br.com.fiap.campus_gigs.dto.ServicoUpdateRequest;
import br.com.fiap.campus_gigs.model.Papel;
import br.com.fiap.campus_gigs.model.Servico;
import br.com.fiap.campus_gigs.model.SituacaoServico;
import br.com.fiap.campus_gigs.model.Usuario;
import br.com.fiap.campus_gigs.repository.ServicoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Transactional
    public ServicoResponse cadastrar(ServicoRequest request, Usuario prestador) {
        Servico servico = Servico.builder()
                .prestador(prestador)
                .titulo(request.titulo())
                .descricao(request.descricao())
                .categoria(request.categoria())
                .preco(request.preco())
                .situacao(SituacaoServico.ATIVO)
                .build();

        return ServicoResponse.from(servicoRepository.save(servico));
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> listar(String categoria, SituacaoServico situacao) {
        List<Servico> servicos;

        if (categoria != null && !categoria.isBlank() && situacao != null) {
            servicos = servicoRepository.findByCategoriaIgnoreCaseAndSituacao(categoria, situacao);
        } else if (categoria != null && !categoria.isBlank()) {
            servicos = servicoRepository.findByCategoriaIgnoreCase(categoria);
        } else if (situacao != null) {
            servicos = servicoRepository.findBySituacao(situacao);
        } else {
            servicos = servicoRepository.findAll();
        }

        return servicos.stream().map(ServicoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ServicoResponse buscarPorId(Long id) {
        Servico servico = buscarEntidadePorId(id);
        return ServicoResponse.from(servico);
    }

    @Transactional
    public ServicoResponse atualizar(Long id, ServicoUpdateRequest request, Usuario usuarioLogado) {
        Servico servico = buscarEntidadePorId(id);

        boolean isDono = servico.getPrestador().getId().equals(usuarioLogado.getId());
        boolean isAdmin = usuarioLogado.getPapel() == Papel.ADMIN;

        if (!isDono && !isAdmin) {
            throw new AccessDeniedException("Não autorizado a alterar este serviço");
        }

        if (request.titulo() != null && !request.titulo().isBlank()) {
            servico.setTitulo(request.titulo());
        }
        if (request.descricao() != null && !request.descricao().isBlank()) {
            servico.setDescricao(request.descricao());
        }
        if (request.categoria() != null && !request.categoria().isBlank()) {
            servico.setCategoria(request.categoria());
        }
        if (request.preco() != null) {
            servico.setPreco(request.preco());
        }
        if (request.situacao() != null) {
            servico.setSituacao(request.situacao());
        }

        return ServicoResponse.from(servicoRepository.save(servico));
    }

    @Transactional
    public ServicoResponse encerrar(Long id, Usuario usuarioLogado) {
        Servico servico = buscarEntidadePorId(id);

        boolean isDono = servico.getPrestador().getId().equals(usuarioLogado.getId());
        boolean isAdmin = usuarioLogado.getPapel() == Papel.ADMIN;

        if (!isDono && !isAdmin) {
            throw new AccessDeniedException("Não autorizado a encerrar este serviço");
        }

        servico.setSituacao(SituacaoServico.ENCERRADO);
        return ServicoResponse.from(servicoRepository.save(servico));
    }

    public Servico buscarEntidadePorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));
    }
}
