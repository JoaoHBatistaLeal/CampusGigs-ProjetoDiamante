-- =========================================
-- V1 - Schema inicial do CampusGigs (Postgres)
-- =========================================

CREATE TABLE usuarios (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    email         VARCHAR(180) NOT NULL UNIQUE,
    senha         VARCHAR(255) NOT NULL,
    cep           VARCHAR(9),
    cidade        VARCHAR(120),
    uf            VARCHAR(2),
    papel         VARCHAR(20) NOT NULL DEFAULT 'USER',
    criado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE servicos (
    id            BIGSERIAL PRIMARY KEY,
    prestador_id  BIGINT NOT NULL REFERENCES usuarios(id),
    titulo        VARCHAR(150) NOT NULL,
    descricao     TEXT NOT NULL,
    categoria     VARCHAR(80) NOT NULL,
    preco         NUMERIC(10,2) NOT NULL,
    situacao      VARCHAR(20) NOT NULL DEFAULT 'ativo',
    criado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE contratacoes (
    id             BIGSERIAL PRIMARY KEY,
    servico_id     BIGINT NOT NULL REFERENCES servicos(id),
    contratante_id BIGINT NOT NULL REFERENCES usuarios(id),
    situacao       VARCHAR(20) NOT NULL DEFAULT 'solicitada',
    criado_em      TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_servicos_prestador ON servicos(prestador_id);
CREATE INDEX idx_contratacoes_servico ON contratacoes(servico_id);
CREATE INDEX idx_contratacoes_contratante ON contratacoes(contratante_id);