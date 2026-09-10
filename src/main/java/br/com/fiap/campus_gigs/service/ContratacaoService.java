package br.com.fiap.campus_gigs.service;

import br.com.fiap.campus_gigs.dto.ContratacaoResponse;
import br.com.fiap.campus_gigs.model.*;
import br.com.fiap.campus_gigs.repository.ContratacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContratacaoService {

    private final ContratacaoRepository contratacaoRepository;
    private final ServicoService servicoService;

    public ContratacaoService(ContratacaoRepository contratacaoRepository,
                              ServicoService servicoService) {
        this.contratacaoRepository = contratacaoRepository;
        this.servicoService = servicoService;
    }

    @Transactional
    public ContratacaoResponse contratar(Long servicoId, Usuario contratante) {
        Servico servico = servicoService.buscarEntidadePorId(servicoId);

        if (servico.getSituacao() != SituacaoServico.ATIVO) {
            throw new IllegalStateException("Serviço não está ativo para contratação");
        }

        if (servico.getPrestador().getId().equals(contratante.getId())) {
            throw new IllegalArgumentException("Não é permitido contratar o próprio serviço");
        }

        Contratacao contratacao = Contratacao.builder()
                .servico(servico)
                .contratante(contratante)
                .situacao(SituacaoContratacao.SOLICITADA)
                .build();

        return ContratacaoResponse.from(contratacaoRepository.save(contratacao));
    }

    @Transactional(readOnly = true)
    public List<ContratacaoResponse> listar(Usuario usuarioLogado) {
        List<Contratacao> lista;

        if (usuarioLogado.getPapel() == Papel.ADMIN) {
            lista = contratacaoRepository.findAll();
        } else {
            lista = contratacaoRepository.findByContratanteIdOrServicoPrestadorId(
                    usuarioLogado.getId(), usuarioLogado.getId());
        }

        return lista.stream().map(ContratacaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ContratacaoResponse buscarPorId(Long id, Usuario usuarioLogado) {
        Contratacao contratacao = buscarEntidadePorId(id);

        boolean isContratante = contratacao.getContratante().getId().equals(usuarioLogado.getId());
        boolean isPrestador = contratacao.getServico().getPrestador().getId().equals(usuarioLogado.getId());
        boolean isAdmin = usuarioLogado.getPapel() == Papel.ADMIN;

        if (!isContratante && !isPrestador && !isAdmin) {
            throw new AccessDeniedException("Não autorizado a visualizar esta contratação");
        }

        return ContratacaoResponse.from(contratacao);
    }

    @Transactional
    public ContratacaoResponse atualizarSituacao(Long id, SituacaoContratacao novaSituacao, Usuario usuarioLogado) {
        Contratacao contratacao = buscarEntidadePorId(id);

        boolean isContratante = contratacao.getContratante().getId().equals(usuarioLogado.getId());
        boolean isPrestador = contratacao.getServico().getPrestador().getId().equals(usuarioLogado.getId());
        boolean isAdmin = usuarioLogado.getPapel() == Papel.ADMIN;

        if (!isContratante && !isPrestador && !isAdmin) {
            throw new AccessDeniedException("Não autorizado a alterar esta contratação");
        }

        contratacao.setSituacao(novaSituacao);
        return ContratacaoResponse.from(contratacaoRepository.save(contratacao));
    }

    public Contratacao buscarEntidadePorId(Long id) {
        return contratacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contratação não encontrada"));
    }
}
