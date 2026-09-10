package br.com.fiap.campus_gigs.dto;

import br.com.fiap.campus_gigs.model.Servico;
import br.com.fiap.campus_gigs.model.SituacaoServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServicoResponse(
        Long id,
        Long prestadorId,
        String prestadorNome,
        String prestadorEmail,
        String titulo,
        String descricao,
        String categoria,
        BigDecimal preco,
        SituacaoServico situacao,
        LocalDateTime criadoEm
) {
    public static ServicoResponse from(Servico s) {
        return new ServicoResponse(
                s.getId(),
                s.getPrestador().getId(),
                s.getPrestador().getNome(),
                s.getPrestador().getEmail(),
                s.getTitulo(),
                s.getDescricao(),
                s.getCategoria(),
                s.getPreco(),
                s.getSituacao(),
                s.getCriadoEm()
        );
    }
}
