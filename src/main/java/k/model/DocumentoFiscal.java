package k.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Where;

import k.model.enums.StatusEmissaoFiscal;
import k.model.enums.TipoDocumentoFiscal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Where(clause = "ativo = true")
public class DocumentoFiscal extends EntityClass {

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoDocumentoFiscal tipo;

    @ManyToOne
    @JoinColumn(name = "comanda_documentofiscal")
    private Comanda comanda;

    @ManyToOne
    @JoinColumn(name = "cliente_documentofiscal")
    private Cliente cliente;

    @Column(length = 60)
    private String numero;

    @Column(length = 60)
    private String chaveAcesso;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusEmissaoFiscal statusEmissao;

    @Column(nullable = false)
    private Boolean emulado = false;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payloadEmissao;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payloadRetorno;

    @Column(name = "emitido_em")
    private LocalDateTime emitidoEm;

    @ManyToOne
    @JoinColumn(name = "usuario_emissao_documentofiscal")
    private Usuario usuarioEmissao;

    @ManyToMany
    @JoinTable(
            name = "documento_fiscal_pagamento",
            joinColumns = @JoinColumn(name = "documento_fiscal_id"),
            inverseJoinColumns = @JoinColumn(name = "pagamento_id"))
    private List<Pagamento> pagamentos;

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

    public StatusEmissaoFiscal getStatusEmissao() {
        return statusEmissao;
    }

    public void setStatusEmissao(StatusEmissaoFiscal statusEmissao) {
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

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }
}
