INSERT INTO usuarios (nome, email, senha, cep, cidade, uf, papel)
VALUES ('Administrador', 'admin@campusgigs.br', '$2a$10$wV270Mavuj8lr2hFSwB9FeYmrIDoyjxX2RdzkCarZV8z0dbzQXNL.', '01310100', 'São Paulo', 'SP', 'ADMIN')
ON CONFLICT (email) DO NOTHING;
