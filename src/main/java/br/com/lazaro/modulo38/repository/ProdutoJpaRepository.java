package br.com.lazaro.modulo38.repository;

import br.com.lazaro.modulo38.domain.Produto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import java.util.*;
import java.util.function.Function;

@ApplicationScoped
public class ProdutoJpaRepository implements ProdutoRepository {
    private EntityManagerFactory factory;

    @PostConstruct
    public void iniciar() {
        // A fábrica é compartilhada; cada operação recebe seu próprio EntityManager.
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", env("DB_URL", "jdbc:postgresql://localhost:5432/modulo38"));
        props.put("jakarta.persistence.jdbc.user", env("DB_USER", "postgres"));
        props.put("jakarta.persistence.jdbc.password", env("DB_PASSWORD", "postgres"));
        factory = Persistence.createEntityManagerFactory("cadastroPU", props);
    }
    private String env(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
    @PreDestroy
    public void fechar() { if (factory != null && factory.isOpen()) factory.close(); }

    private <T> T transacao(Function<EntityManager, T> operacao) {
        EntityManager em = factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T resultado = operacao.apply(em);
            tx.commit();
            return resultado;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally { em.close(); }
    }
    @Override
    public Produto salvar(Produto produto) {
        return transacao(em -> {
            if (produto.getId() == null) { em.persist(produto); return produto; }
            if (em.find(Produto.class, produto.getId()) == null)
                throw new IllegalArgumentException("Produto não encontrado. Atualize a listagem.");
            return em.merge(produto);
        });
    }
    @Override
    public List<Produto> listarTodos() {
        return transacao(em -> em.createQuery("select p from Produto p order by p.nome, p.id", Produto.class).getResultList());
    }
    @Override
    public Optional<Produto> buscarPorCodigo(String codigo) {
        return transacao(em -> em.createQuery("select p from Produto p where p.codigo = :codigo", Produto.class)
            .setParameter("codigo", codigo).getResultStream().findFirst());
    }
    @Override
    public void excluir(Long id) {
        transacao(em -> {
            Produto produto = em.find(Produto.class, id);
            if (produto == null) throw new IllegalArgumentException("Produto não encontrado. Atualize a listagem.");
            em.remove(produto);
            return null;
        });
    }
}
