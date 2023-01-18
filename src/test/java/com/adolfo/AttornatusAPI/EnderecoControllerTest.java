package com.adolfo.AttornatusAPI;

import com.adolfo.AttornatusAPI.endereco.Endereco;
import com.adolfo.AttornatusAPI.endereco.EnderecoController;
import com.adolfo.AttornatusAPI.endereco.EnderecoRepository;
import com.adolfo.AttornatusAPI.pessoa.Pessoa;
import com.adolfo.AttornatusAPI.pessoa.PessoaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnderecoController.class)
public class EnderecoControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    PessoaRepository pessoaRepository;
    @MockBean
    EnderecoRepository enderecoRepository;

    Endereco endereco1 = new Endereco("Rua 1", "80050-510", "55", "Curitba", true);
    Endereco endereco2 = new Endereco("Rua 2", "80050-510", "55", "Curitba", false);
    List<Endereco> enderecos = new ArrayList<>(Arrays.asList(endereco1, endereco2));

    Pessoa pessoa1 = new Pessoa(1L,"Adolfo", LocalDate.of(1997, 8, 18), enderecos);
    Pessoa pessoa2 = new Pessoa(2L,"Raquel", LocalDate.of(1995, 1, 5), null);
    Pessoa pessoa3 = new Pessoa(3L,"Victor", LocalDate.of(1996, 6, 20), null);

    @Test
    public void find_success() throws Exception {

        Mockito.when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa1));
        Mockito.when(enderecoRepository.findByPessoa(pessoa1)).thenReturn(enderecos);

        mockMvc.perform(MockMvcRequestBuilders.get("/pessoas/1/enderecos").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].logradouro", is("Rua 2")));
    }

    @Test
    public void criaEndereco_success() throws Exception {
        Endereco novoEndereco = new Endereco("Rua Governador Agamenon", "80050-510", "55", "Curitba", false);

        Mockito.when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa1));
        Mockito.when(enderecoRepository.save(Mockito.any(Endereco.class))).thenReturn(novoEndereco);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/pessoas/1/enderecos")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(novoEndereco));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.logradouro", is("Rua Governador Agamenon")));
    }

}
