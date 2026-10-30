package br.com.lazaro.modulo38.controller;

import br.com.lazaro.modulo38.domain.Produto;
import br.com.lazaro.modulo38.service.ProdutoService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("produtoController")
@ViewScoped
public class ProdutoController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProdutoController.class.getName());
    @Inject private ProdutoService service;
    private Produto produto = new Produto();
    private List<Produto> produtos = new ArrayList<>();

    @PostConstruct
    public void iniciar() { atualizar(); }
    public void salvar() {
        try {
            service.salvar(produto);
            produto = new Produto();
            mensagem(FacesMessage.SEVERITY_INFO, "Produto salvo com sucesso!");
            atualizar();
        } catch (IllegalArgumentException e) { mensagem(FacesMessage.SEVERITY_ERROR, e.getMessage()); }
        catch (RuntimeException e) { falha("Não foi possível salvar. O código pode ter sido cadastrado ou o registro alterado em outra janela. Atualize a lista e tente novamente.", e); }
    }
    public void editar(Produto selecionado) { produto = new Produto(selecionado); }
    public void novo() { produto = new Produto(); }
    public void excluir(Long id) {
        try {
            service.excluir(id);
            if (id.equals(produto.getId())) novo();
            mensagem(FacesMessage.SEVERITY_INFO, "Produto excluído com sucesso!");
            atualizar();
        } catch (IllegalArgumentException e) { mensagem(FacesMessage.SEVERITY_ERROR, e.getMessage()); }
        catch (RuntimeException e) { falha("Não foi possível excluir o produto. Atualize a lista e tente novamente.", e); }
    }
    public void atualizar() {
        try { produtos = service.listarTodos(); }
        catch (RuntimeException e) { falha("Não foi possível consultar o banco. Confira se o PostgreSQL está ligado e se a conexão foi configurada.", e); }
    }
    private void falha(String texto, RuntimeException e) {
        LOG.log(Level.SEVERE, texto, e);
        mensagem(FacesMessage.SEVERITY_ERROR, texto);
    }
    private void mensagem(FacesMessage.Severity nivel, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(nivel, texto, null));
    }
    public Produto getProduto() { return produto; }
    public List<Produto> getProdutos() { return produtos; }
}
