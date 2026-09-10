package br.com.fiap.campus_gigs.cep;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class CepService {

    private final ViaCepClient viaCepClient;

    public CepService(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public EnderecoDto buscarEndereco(String cep) {
        if (cep == null || cep.isBlank()) {
            return null;
        }

        String cepFormatado = cep.replaceAll("\\D", "");
        if (cepFormatado.length() != 8) {
            throw new IllegalArgumentException("CEP inválido: deve conter 8 dígitos");
        }

        try {
            ViaCepResponse response = viaCepClient.buscarEnderecoPorCep(cepFormatado);
            if (response == null || Boolean.TRUE.equals(response.erro()) || response.localidade() == null) {
                throw new IllegalArgumentException("CEP não encontrado");
            }
            return new EnderecoDto(cepFormatado, response.localidade(), response.uf());
        } catch (RestClientException ex) {
            throw new IllegalStateException("Serviço externo de consulta de CEP indisponível ou demorou para responder");
        }
    }
}
