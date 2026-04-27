package k.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "documento_fiscal")
public class DocumentoFiscal extends EntityClass {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 10)
    private TipoDocumentoFiscal tipo;

    @ManyToOne
    @JoinColumn(name = "comanda_id")
    private Comanda comanda;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "numero", length = 50)
    private String numero;

    @Column(name = "chave_acesso", length = 60)
    private String chaveAcesso;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_emissao", length = 20)
    private StatusDocumentoFiscal statusEmissao = StatusDocumentoFiscal.EMITIDO;

    @Column(name = "emulado")
    private Boolean emulado = true;

    @Column(name = "payload_emissao", length = 4000)
    private String payloadEmissao;

    @Column(name = "payload_retorno", length = 1000)
    private String payloadRetorno;

    @Column(name = "emitido_em")
    private LocalDateTime emitidoEm;

    @ManyToOne
    @JoinColumn(name = "usuario_emissao_id")
    private Usuario usuarioEmissao;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToMany
    @JoinTable(
            name = "documento_fiscal_pagamento",
            joinColumns = @JoinColumn(name = "documento_id"),
            inverseJoinColumns = @JoinColumn(name = "pagamento_id"))
    private List<Pagamento> pagamentos = new ArrayList<>();

    public TipoDocumentoFiscal getTipo() {
        return tipo;
    }

    public void setTipo(TipoDocumentoFiscal tipo) {
        this.tipo = tipo;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public StatusDocumentoFiscal getStatusEmissao() {
        return statusEmissao;
    }

    public void setStatusEmissao(StatusDocumentoFiscal statusEmissao) {
        this.statusEmissao = statusEmissao;
    }

    public Boolean getEmulado() {
        return emulado;
    }

    public void setEmulado(Boolean emulado) {
        this.emulado = emulado;
    }

    public String getPayloadEmissao() {
        return payloadEmissao;
    }

    public void setPayloadEmissao(String payloadEmissao) {
        this.payloadEmissao = payloadEmissao;
    }

    public String getPayloadRetorno() {
        return payloadRetorno;
    }

    public void setPayloadRetorno(String payloadRetorno) {
        this.payloadRetorno = payloadRetorno;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }

    public void setEmitidoEm(LocalDateTime emitidoEm) {
        this.emitidoEm = emitidoEm;
    }

    public Usuario getUsuarioEmissao() {
        return usuarioEmissao;
    }

    public void setUsuarioEmissao(Usuario usuarioEmissao) {
        this.usuarioEmissao = usuarioEmissao;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }
}
