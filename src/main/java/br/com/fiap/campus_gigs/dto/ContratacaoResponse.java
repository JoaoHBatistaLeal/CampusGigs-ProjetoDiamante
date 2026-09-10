package br.com.fiap.campus_gigs.dto;

import br.com.fiap.campus_gigs.model.Contratacao;
import br.com.fiap.campus_gigs.model.SituacaoContratacao;

import java.time.LocalDateTime;

public record ContratacaoResponse(
        Long id,
        Long servicoId,
        String servicoTitulo,
        Long prestadorId,
        String prestadorNome,
        Long contratanteId,
        String contratanteNome,
        SituacaoContratacao situacao,
        LocalDateTime criadoEm
) {
    public static ContratacaoResponse from(Contratacao c) {
        return new ContratacaoResponse(
                c.getId(),
                c.getServico().getId(),
                c.getServico().getTitulo(),
                c.getServico().getPrestador().getId(),
                c.getServico().getPrestador().getNome(),
                c.getContratante().getId(),
                c.getContratante().getNome(),
                c.getSituacao(),
                c.getCriadoEm()
        );
    }
}
