package com.adolfo.AttornatusAPI.endereco;

import com.adolfo.AttornatusAPI.pessoa.Pessoa;
import com.adolfo.AttornatusAPI.pessoa.PessoaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class EnderecoController {

    private final EnderecoRepository enderecoRepository;
    private final PessoaRepository pessoaRepository;

    public EnderecoController(EnderecoRepository enderecoRepository, PessoaRepository pessoaRepository) {
        this.enderecoRepository = enderecoRepository;
        this.pessoaRepository = pessoaRepository;
    }

    @GetMapping("/pessoas/{pessoaId}/enderecos")
    ResponseEntity<List<Endereco>> find(@PathVariable Long pessoaId) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(pessoaId);
        if(pessoa.isPresent()) {
            List<Endereco> enderecos = enderecoRepository.findByPessoa(pessoa.get());
            return new ResponseEntity<>(enderecos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/pessoas/{pessoaId}/enderecos")
    ResponseEntity<Endereco> criaEndereco(@RequestBody Endereco endereco, @PathVariable Long pessoaId) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(pessoaId);
        if(pessoa.isPresent()) {
            endereco.setPessoa(pessoa.get());
            return new ResponseEntity<>(enderecoRepository.save(endereco), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
