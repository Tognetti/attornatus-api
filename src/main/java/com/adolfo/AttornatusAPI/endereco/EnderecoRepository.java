package com.adolfo.AttornatusAPI.endereco;

import com.adolfo.AttornatusAPI.pessoa.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    List<Endereco> findByPessoa(Pessoa pessoa);

    @Transactional
    List<Endereco> deleteAllByPessoa(Pessoa pessoa);

}
