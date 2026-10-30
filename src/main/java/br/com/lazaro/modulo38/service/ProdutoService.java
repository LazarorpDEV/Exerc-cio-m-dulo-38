package br.com.lazaro.modulo38.service;

import br.com.lazaro.modulo38.domain.Produto;
import br.com.lazaro.modulo38.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@ApplicationScoped
public class ProdutoService {
    @Inject
    private ProdutoRepository repository;
    public ProdutoService() { }
    public ProdutoService(ProdutoRepository repository) { this.repository = repository; }

    public Produto salvar(Produto p) {
        if (p == null) throw new IllegalArgumentException("Informe os dados do produto.");
        String codigo = p.getCodigo() == null ? "" : p.getCodigo().strip().toUpperCase(Locale.ROOT);
        String nome = p.getNome() == null ? "" : p.getNome().strip();
        if (codigo.isBlank() || codigo.length() > 30)
            throw new IllegalArgumentException("O código deve ter entre 1 e 30 caracteres.");
        if (nome.isBlank() || nome.length() > 100)
            throw new IllegalArgumentException("O nome deve ter entre 1 e 100 caracteres.");
        if (p.getPreco() == null || p.getPreco().compareTo(new BigDecimal("0.01")) < 0)
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        if (p.getPreco().stripTrailingZeros().scale() > 2 || p.getPreco().compareTo(new BigDecimal("9999999999.99")) > 0)
            throw new IllegalArgumentException("Informe um preço com até 10 dígitos inteiros e 2 casas decimais.");
        if (p.getEstoque() == null || p.getEstoque() < 0)
            throw new IllegalArgumentException("O estoque deve ser zero ou positivo.");
        p.setCodigo(codigo); p.setNome(nome);
        repository.buscarPorCodigo(codigo).ifPresent(existente -> {
            if (!Objects.equals(existente.getId(), p.getId()))
                throw new IllegalArgumentException("Já existe um produto com esse código.");
        });
        return repository.salvar(p);
    }
    public List<Produto> listarTodos() { return repository.listarTodos(); }
    public void excluir(Long id) {
        if (id == null) throw new IllegalArgumentException("Selecione um produto.");
        repository.excluir(id);
    }
}
