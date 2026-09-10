package br.com.fiap.campus_gigs.cep;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = "application/json")
public interface ViaCepClient {

    @GetExchange("/{cep}/json")
    ViaCepResponse buscarEnderecoPorCep(@PathVariable("cep") String cep);
}
