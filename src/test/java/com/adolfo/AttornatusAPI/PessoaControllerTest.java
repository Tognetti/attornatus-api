package com.adolfo.AttornatusAPI;

import com.adolfo.AttornatusAPI.endereco.Endereco;
import com.adolfo.AttornatusAPI.endereco.EnderecoRepository;
import com.adolfo.AttornatusAPI.pessoa.Pessoa;
import com.adolfo.AttornatusAPI.pessoa.PessoaController;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebMvcTest(PessoaController.class)
public class PessoaControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    PessoaRepository pessoaRepository;
    @MockBean
    EnderecoRepository enderecoRepository;

    Endereco endereco1 = new Endereco("Rua 1", "80050-510", "55", "Curitba", true);
    List<Endereco> enderecos = new ArrayList<>(Arrays.asList(endereco1));

    Pessoa pessoa1 = new Pessoa(1L,"Adolfo", LocalDate.of(1997, 8, 18), enderecos);
    Pessoa pessoa2 = new Pessoa(2L,"Raquel", LocalDate.of(1995, 1, 5), null);
    Pessoa pessoa3 = new Pessoa(3L,"Victor", LocalDate.of(1996, 6, 20), null);

    @Test
    public void findAll_success() throws Exception {
        List<Pessoa> pessoas = new ArrayList<>(Arrays.asList(pessoa1, pessoa2, pessoa3));

        Mockito.when(pessoaRepository.findAll()).thenReturn(pessoas);

        mockMvc.perform(MockMvcRequestBuilders.get("/pessoas").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].nome", is("Victor")));
    }

    @Test
    public void find_success() throws Exception {
        List<Pessoa> pessoas = new ArrayList<>(Arrays.asList(pessoa1, pessoa2, pessoa3));

        Mockito.when(pessoaRepository.findById(pessoa1.getId())).thenReturn(Optional.of(pessoa1));

        mockMvc.perform(MockMvcRequestBuilders.get("/pessoas/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Adolfo")));
    }

    @Test
    public void criaPessoa_success() throws Exception {
        Pessoa novaPessoa = new Pessoa("João", LocalDate.of(1999, 6, 20), enderecos);

        Mockito.when(pessoaRepository.save(Mockito.any(Pessoa.class))).thenReturn(novaPessoa);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(novaPessoa));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.nome", is("João")));
    }

    @Test
    public void alteraPessoa_success() throws Exception {
        Pessoa pessoaAlterada = new Pessoa("Victor Adati", LocalDate.of(2000, 1, 11), enderecos);

        Mockito.when(pessoaRepository.findById(pessoa3.getId())).thenReturn(Optional.of(pessoa3));
        Mockito.when(pessoaRepository.save(Mockito.any(Pessoa.class))).thenReturn(pessoaAlterada);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/pessoas/3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(pessoaAlterada));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Victor Adati")));
    }

    @Test
    public void alteraPessoa_notFound() throws Exception {
        Pessoa pessoaAlterada = new Pessoa("Victor Adati", LocalDate.of(2000, 1, 11), enderecos);

        Mockito.when(pessoaRepository.findById(10L)).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/pessoas/10")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(pessoaAlterada));

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void removePessoa_success() throws Exception {
        Mockito.when(pessoaRepository.findById(pessoa2.getId())).thenReturn(Optional.of(pessoa2));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/pessoas/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void removePessoa_notFound() throws Exception {
        Mockito.when(pessoaRepository.findById(10L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/pessoa/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
