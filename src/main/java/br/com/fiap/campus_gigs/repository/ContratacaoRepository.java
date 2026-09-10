package br.com.fiap.campus_gigs.repository;

import br.com.fiap.campus_gigs.model.Contratacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratacaoRepository extends JpaRepository<Contratacao, Long> {

    List<Contratacao> findByContratanteId(Long contratanteId);

    List<Contratacao> findByServicoPrestadorId(Long prestadorId);

    List<Contratacao> findByContratanteIdOrServicoPrestadorId(Long contratanteId, Long prestadorId);
}
