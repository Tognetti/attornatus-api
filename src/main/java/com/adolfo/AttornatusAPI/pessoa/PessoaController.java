package com.adolfo.AttornatusAPI.pessoa;

import com.adolfo.AttornatusAPI.endereco.EnderecoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class PessoaController {

    private final PessoaRepository pessoaRepository;
    private final EnderecoRepository enderecoRepository;

    public PessoaController(PessoaRepository pessoaRepository, EnderecoRepository enderecoRepository) {
        this.pessoaRepository = pessoaRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @GetMapping("/pessoas")
    List<Pessoa> findAll() {
        return pessoaRepository.findAll();
    }

    @GetMapping("/pessoas/{id}")
    ResponseEntity<Pessoa> find(@PathVariable Long id) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);
        if(pessoa.isPresent())
            return new ResponseEntity<>(pessoa.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/pessoas")
    Pessoa criaPessoa(@RequestBody Pessoa pessoa) {
        Pessoa newPessoa = pessoaRepository.save(pessoa);
        pessoa.getEnderecos().forEach(endereco -> {
            endereco.setPessoa(newPessoa);
            enderecoRepository.save(endereco);
        });
        return newPessoa;
    }

    @PutMapping("/pessoas/{id}")
    ResponseEntity<Pessoa> alteraPessoa(@RequestBody Pessoa newPessoa, @PathVariable Long id) {
        Optional<Pessoa> oldPessoa = pessoaRepository.findById(id);
        if(oldPessoa.isPresent()){
            Pessoa pessoa = oldPessoa.get();
            pessoa.setNome(newPessoa.getNome());
            pessoa.setDataNascimento(newPessoa.getDataNascimento());

            enderecoRepository.deleteAllByPessoa(oldPessoa.get());
            newPessoa.getEnderecos().forEach(endereco -> {
                endereco.setPessoa(pessoa);
                enderecoRepository.save(endereco);
            });

            pessoaRepository.save(pessoa);
            return new ResponseEntity<>(pessoa, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/pessoas/{id}")
    ResponseEntity<Object> removePessoa(@PathVariable Long id) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);
        if (pessoa.isPresent()) {
            pessoaRepository.delete(pessoa.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
