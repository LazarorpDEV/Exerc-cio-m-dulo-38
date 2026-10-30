package br.com.lazaro.modulo38.service;

import br.com.lazaro.modulo38.domain.Produto;
import br.com.lazaro.modulo38.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {
    private ProdutoRepository repository;
    private ProdutoService service;
    @BeforeEach void iniciar() {
        repository = mock(ProdutoRepository.class);
        service = new ProdutoService(repository);
        when(repository.buscarPorCodigo(anyString())).thenReturn(Optional.empty());
        when(repository.salvar(any())).thenAnswer(i -> i.getArgument(0));
    }
    private Produto valido() {
        Produto p = new Produto();
        p.setCodigo("MASSA-01"); p.setNome("Massa fresca");
        p.setPreco(new BigDecimal("19.90")); p.setEstoque(10);
        return p;
    }
    @Test void cadastraENormaliza() {
        Produto p = valido(); p.setCodigo(" massa-01 "); p.setNome(" Massa fresca ");
        assertSame(p, service.salvar(p));
        assertEquals("MASSA-01", p.getCodigo()); assertEquals("Massa fresca", p.getNome());
        verify(repository).salvar(p);
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings={"   "})
    void rejeitaCodigoAusente(String codigo) {
        Produto p = valido(); p.setCodigo(codigo);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(p));
        verify(repository, never()).salvar(any());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings={"   "})
    void rejeitaNomeAusente(String nome) {
        Produto p = valido(); p.setNome(nome);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(p));
    }
    @Test void rejeitaLimitesDeTexto() {
        Produto p = valido(); p.setCodigo("A".repeat(31));
        assertThrows(IllegalArgumentException.class, () -> service.salvar(p));
        p.setCodigo("A"); p.setNome("A".repeat(101));
        assertThrows(IllegalArgumentException.class, () -> service.salvar(p));
    }
    @ParameterizedTest @ValueSource(strings={"0", "-1", "0.001", "1.999", "10000000000.00"})
    void rejeitaPrecoInvalido(String valor) {
        Produto p = valido(); p.setPreco(new BigDecimal(valor));
        assertThrows(IllegalArgumentException.class, () -> service.salvar(p));
        verify(repository, never()).salvar(any());
    }
    @Test void rejeitaPrecoNulo() {
        Produto p = valido(); p.setPreco(null);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(p));
    }
    @Test void rejeitaEstoqueNegativoOuNulo() {
        Produto p = valido(); p.setEstoque(-1);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(p));
        p.setEstoque(null);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(p));
    }
    @Test void permiteEstoqueZero() {
        Produto p = valido(); p.setEstoque(0);
        service.salvar(p); verify(repository).salvar(p);
    }
    @Test void rejeitaCodigoDuplicado() {
        Produto existente = valido(); existente.setId(1L);
        when(repository.buscarPorCodigo("MASSA-01")).thenReturn(Optional.of(existente));
        assertThrows(IllegalArgumentException.class, () -> service.salvar(valido()));
        verify(repository, never()).salvar(any());
    }
    @Test void permiteEditarMesmoProduto() {
        Produto existente = valido(); existente.setId(1L);
        when(repository.buscarPorCodigo("MASSA-01")).thenReturn(Optional.of(existente));
        Produto edicao = new Produto(existente); edicao.setNome("Massa artesanal");
        service.salvar(edicao); verify(repository).salvar(edicao);
    }
    @Test void rejeitaTrocaDeCodigoParaOutroProduto() {
        Produto existente = valido(); existente.setId(1L);
        when(repository.buscarPorCodigo("MASSA-01")).thenReturn(Optional.of(existente));
        Produto outro = valido(); outro.setId(2L);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(outro));
    }
    @Test void listaProdutos() {
        when(repository.listarTodos()).thenReturn(List.of(valido()));
        assertEquals(1, service.listarTodos().size());
    }
    @Test void excluiProduto() { service.excluir(1L); verify(repository).excluir(1L); }
    @Test void rejeitaExclusaoSemId() {
        assertThrows(IllegalArgumentException.class, () -> service.excluir(null));
        verify(repository, never()).excluir(any());
    }
    @Test void rejeitaProdutoNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.salvar(null));
    }
}
