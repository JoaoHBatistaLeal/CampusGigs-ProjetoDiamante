package br.com.fiap.campus_gigs;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.fiap.campus_gigs.dto.ContratacaoRequest;
import br.com.fiap.campus_gigs.dto.LoginRequest;
import br.com.fiap.campus_gigs.dto.ServicoRequest;
import br.com.fiap.campus_gigs.dto.UsuarioRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ServicoContratacaoSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tokenAdmin;
    private String tokenAluno1;
    private String tokenAluno2;

    @BeforeEach
    void setUp() throws Exception {
        tokenAdmin = obterToken("admin@campusgigs.br", "admin123");

        cadastrarUsuarioSeNecessario("Aluno Um", "aluno1@campusgigs.br", "senha123", "01001000");
        tokenAluno1 = obterToken("aluno1@campusgigs.br", "senha123");

        cadastrarUsuarioSeNecessario("Aluno Dois", "aluno2@campusgigs.br", "senha123", "01001000");
        tokenAluno2 = obterToken("aluno2@campusgigs.br", "senha123");
    }

    private void cadastrarUsuarioSeNecessario(String nome, String email, String senha, String cep) throws Exception {
        UsuarioRequest request = new UsuarioRequest(nome, email, senha, cep);
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private String obterToken(String email, String senha) throws Exception {
        LoginRequest login = new LoginRequest(email, senha);
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        Map mapa = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        return (String) mapa.get("token");
    }

    @Test
    void devePublicarEListarServicos() throws Exception {
        ServicoRequest request = new ServicoRequest("Monitoria de Java", "Aulas de apoio em POO", "Educação", new BigDecimal("50.00"));

        mockMvc.perform(post("/servicos")
                        .header("Authorization", "Bearer " + tokenAluno1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.situacao").value("ativo"));

        mockMvc.perform(get("/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deveRecusarContratacaoDoProprioServico() throws Exception {
        ServicoRequest request = new ServicoRequest("Formatação ABNT", "Revisão de TCC", "Trabalhos", new BigDecimal("70.00"));

        MvcResult result = mockMvc.perform(post("/servicos")
                        .header("Authorization", "Bearer " + tokenAluno1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Map mapa = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Long servicoId = ((Number) mapa.get("id")).longValue();

        ContratacaoRequest contratacao = new ContratacaoRequest(servicoId);

        mockMvc.perform(post("/contratacoes")
                        .header("Authorization", "Bearer " + tokenAluno1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contratacao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Não é permitido contratar o próprio serviço"));
    }

    @Test
    void devePermitirContratacaoPorOutroUsuario() throws Exception {
        ServicoRequest request = new ServicoRequest("Design de Slides", "Criacao de apresentacoes", "Design", new BigDecimal("40.00"));

        MvcResult result = mockMvc.perform(post("/servicos")
                        .header("Authorization", "Bearer " + tokenAluno1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Map mapa = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Long servicoId = ((Number) mapa.get("id")).longValue();

        ContratacaoRequest contratacao = new ContratacaoRequest(servicoId);

        mockMvc.perform(post("/contratacoes")
                        .header("Authorization", "Bearer " + tokenAluno2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contratacao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.situacao").value("solicitada"));
    }

    @Test
    void deveNegarAcessoQuandoOutroUsuarioTentaEncerrarServico() throws Exception {
        ServicoRequest request = new ServicoRequest("Instalação Linux", "Suporte técnico", "TI", new BigDecimal("35.00"));

        MvcResult result = mockMvc.perform(post("/servicos")
                        .header("Authorization", "Bearer " + tokenAluno1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Map mapa = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Long servicoId = ((Number) mapa.get("id")).longValue();

        mockMvc.perform(patch("/servicos/" + servicoId + "/encerrar")
                        .header("Authorization", "Bearer " + tokenAluno2))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro").value("Acesso negado"));
    }

    @Test
    void devePermitirAdminEncerrarServicoDeTerceiros() throws Exception {
        ServicoRequest request = new ServicoRequest("Aulas de SQL", "Consultas avancadas", "TI", new BigDecimal("60.00"));

        MvcResult result = mockMvc.perform(post("/servicos")
                        .header("Authorization", "Bearer " + tokenAluno1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Map mapa = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Long servicoId = ((Number) mapa.get("id")).longValue();

        mockMvc.perform(patch("/servicos/" + servicoId + "/encerrar")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("encerrado"));
    }

    @Test
    void deveRejeitarContratacaoDeServicoNaoAtivo() throws Exception {
        ServicoRequest request = new ServicoRequest("Mentoria Encerrada", "Conteudo antigo", "Educacao", new BigDecimal("90.00"));

        MvcResult result = mockMvc.perform(post("/servicos")
                        .header("Authorization", "Bearer " + tokenAluno1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Map mapa = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Long servicoId = ((Number) mapa.get("id")).longValue();

        mockMvc.perform(patch("/servicos/" + servicoId + "/encerrar")
                        .header("Authorization", "Bearer " + tokenAluno1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("encerrado"));

        ContratacaoRequest contratacao = new ContratacaoRequest(servicoId);

        mockMvc.perform(post("/contratacoes")
                        .header("Authorization", "Bearer " + tokenAluno2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contratacao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Serviço não está ativo para contratação"));
    }
}
