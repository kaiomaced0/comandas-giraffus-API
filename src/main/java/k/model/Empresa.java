package k.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Empresa extends EntityClass {

    private String nome;
    private String cnpj;
    private String nomeFantasia;

    @OneToMany
    @JoinColumn(name = "lista_empresapagamento_empresa")
    private List<EmpresaPagamento> empresaPagamento;

    @OneToOne
    @JoinColumn(name = "usuario_admin_empresa")
    private Usuario admin;

    @Column(name = "proximo_pagamento_empresapagamento")
    private LocalDate proximoPagamento;

    @OneToMany
    @JoinColumn(name = "lista_produto_empresa")
    private List<Produto> produtos;

    @OneToMany
    @JoinColumn(name = "tipoproduto_empresa")
    private List<TipoProduto> tipoProdutos;

    @OneToMany
    @JoinColumn(name = "lista_caixa_empresa")
    private List<Caixa> caixas;

    @OneToMany
    @JoinColumn(name = "lista_pagamento_empresa")
    private List<Pagamento> pagamentos;

    @OneToMany
    @JoinColumn(name = "lista_comanda_empresa")
    private List<Comanda> comandas;

    @OneToMany
    @JoinColumn(name = "lista_pedido_empresa")
    private List<Pedido> pedidos;

    @OneToMany
    @JoinColumn(name = "lista_funcionario_empresa")
    private List<Usuario> funcionarios;

    private String comentario;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public List<EmpresaPagamento> getEmpresaPagamento() {
        return empresaPagamento;
    }

    public void setEmpresaPagamento(List<EmpresaPagamento> empresaPagamento) {
        this.empresaPagamento = empresaPagamento;
    }

    public Usuario getAdmin() {
        return admin;
    }

    public void setAdmin(Usuario admin) {
        this.admin = admin;
    }

    public LocalDate getProximoPagamento() {
        return proximoPagamento;
    }

    public void setProximoPagamento(LocalDate proximoPagamento) {
        this.proximoPagamento = proximoPagamento;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public List<Caixa> getCaixas() {
        return caixas;
    }

    public void setCaixas(List<Caixa> caixas) {
        this.caixas = caixas;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public List<Comanda> getComandas() {
        return comandas;
    }

    public void setComandas(List<Comanda> comandas) {
        this.comandas = comandas;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public List<Usuario> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<Usuario> funcionarios) {
        this.funcionarios = funcionarios;
    }

    public List<TipoProduto> getTipoProdutos() {
        return tipoProdutos;
    }

    public void setTipoProdutos(List<TipoProduto> tipoProdutos) {
        this.tipoProdutos = tipoProdutos;
    }
}
