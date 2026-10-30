package br.com.lazaro.modulo38.repository;

import br.com.lazaro.modulo38.domain.Produto;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {
    Produto salvar(Produto produto);
    List<Produto> listarTodos();
    Optional<Produto> buscarPorCodigo(String codigo);
    void excluir(Long id);
}
