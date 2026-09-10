package br.com.fiap.campus_gigs;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.fiap.campus_gigs.cep.CepService;
import br.com.fiap.campus_gigs.cep.ViaCepClient;
import br.com.fiap.campus_gigs.dto.CepUpdateRequest;
import br.com.fiap.campus_gigs.dto.LoginRequest;
import br.com.fiap.campus_gigs.dto.UsuarioRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CepIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CepService cepService;

    @MockBean
    private ViaCepClient viaCepClientMock;

    @Test
    void devePreencherCidadeEUfComCepValido() throws Exception {
        br.com.fiap.campus_gigs.cep.ViaCepResponse mockResp = new br.com.fiap.campus_gigs.cep.ViaCepResponse(
                "01001-000", "Praça da Sé", "São Paulo", "SP", false);
        when(viaCepClientMock.buscarEnderecoPorCep("01001000")).thenReturn(mockResp);

        String email = "cep." + UUID.randomUUID() + "@campusgigs.br";
        UsuarioRequest request = new UsuarioRequest("Aluno CEP", email, "senha123", "01001-000");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"))
                .andExpect(jsonPath("$.cep").value("01001000"));
    }

    @Test
    void deveRecusarCadastroComCepNaoEncontrado() throws Exception {
        br.com.fiap.campus_gigs.cep.ViaCepResponse mockResp = new br.com.fiap.campus_gigs.cep.ViaCepResponse(
                null, null, null, null, true);
        when(viaCepClientMock.buscarEnderecoPorCep("99999999")).thenReturn(mockResp);

        String email = "inexistente." + UUID.randomUUID() + "@campusgigs.br";
        UsuarioRequest request = new UsuarioRequest("Aluno Inexistente", email, "senha123", "99999-999");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("CEP não encontrado"));
    }

    @Test
    void deveRecusarCadastroComCepFormatoInvalido() throws Exception {
        String email = "invalido." + UUID.randomUUID() + "@campusgigs.br";
        UsuarioRequest request = new UsuarioRequest("Aluno Invalido", email, "senha123", "123");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("CEP inválido: deve conter 8 dígitos"));
    }

    @Test
    void deveResponderApropriadamenteQuandoServicoExternoFalhar() throws Exception {
        when(viaCepClientMock.buscarEnderecoPorCep(anyString())).thenThrow(new RestClientException("Timeout ao conectar com ViaCEP"));

        String email = "falha." + UUID.randomUUID() + "@campusgigs.br";
        UsuarioRequest request = new UsuarioRequest("Aluno Falha", email, "senha123", "01310100");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Serviço externo de consulta de CEP indisponível ou demorou para responder"));
    }

    @Test
    void deveAtualizarCepDoUsuarioAutenticado() throws Exception {
        br.com.fiap.campus_gigs.cep.ViaCepResponse mockResp = new br.com.fiap.campus_gigs.cep.ViaCepResponse(
                "20020-010", "Praça XV", "Rio de Janeiro", "RJ", false);
        when(viaCepClientMock.buscarEnderecoPorCep("20020010")).thenReturn(mockResp);

        String email = "update." + UUID.randomUUID() + "@campusgigs.br";
        UsuarioRequest cad = new UsuarioRequest("Aluno Update", email, "senha123", null);
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cad)));

        LoginRequest login = new LoginRequest(email, "senha123");
        MvcResult res = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        Map mapa = objectMapper.readValue(res.getResponse().getContentAsString(), Map.class);
        String token = (String) mapa.get("token");

        CepUpdateRequest update = new CepUpdateRequest("20020-010");
        mockMvc.perform(patch("/usuarios/me/cep")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cidade").value("Rio de Janeiro"))
                .andExpect(jsonPath("$.uf").value("RJ"));
    }
}
