package br.com.lazaro.modulo38.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "produto", uniqueConstraints = @UniqueConstraint(name = "uk_produto_codigo", columnNames = "codigo"))
public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long versao;
    @NotBlank @Size(max = 30) @Column(nullable = false, length = 30)
    private String codigo;
    @NotBlank @Size(max = 100) @Column(nullable = false, length = 100)
    private String nome;
    @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;
    @NotNull @Min(0) @Column(nullable = false)
    private Integer estoque;

    public Produto() { }
    public Produto(Produto outro) {
        id = outro.id; versao = outro.versao; codigo = outro.codigo;
        nome = outro.nome; preco = outro.preco; estoque = outro.estoque;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }
}
