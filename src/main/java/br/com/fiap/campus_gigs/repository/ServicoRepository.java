package br.com.fiap.campus_gigs.repository;

import br.com.fiap.campus_gigs.model.Servico;
import br.com.fiap.campus_gigs.model.SituacaoServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findBySituacao(SituacaoServico situacao);

    List<Servico> findByPrestadorId(Long prestadorId);

    List<Servico> findByCategoriaIgnoreCase(String categoria);

    List<Servico> findByCategoriaIgnoreCaseAndSituacao(String categoria, SituacaoServico situacao);
}
