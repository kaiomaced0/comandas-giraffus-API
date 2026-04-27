package k.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Caixa extends EntityClass {

    @Column(name = "nome_caixa")
    private String nome;

    @OneToMany
    @JoinColumn(name = "comanda_caixa")
    private List<Comanda> comandas;

    @OneToMany
    @JoinColumn(name = "pagamento_caixa")
    private List<Pagamento> pagamentos;

    private Double valorTotal;

    private LocalDate dataCaixa;

    private String comentario;

    private Boolean fechado;

    // Onda E - Caixa por usuario
    // Nullable=true para nao quebrar registros antigos (hbm2ddl=update sem backfill).
    // Service valida no abrir() para registros novos.
    @ManyToOne
    @JoinColumn(name = "usuario_caixa")
    private Usuario usuario;

    @Column(name = "valor_abertura", precision = 19, scale = 2)
    private BigDecimal valorAbertura;

    @Column(name = "valor_fechamento_esperado", precision = 19, scale = 2)
    private BigDecimal valorFechamentoEsperado;

    @Column(name = "valor_fechamento_informado", precision = 19, scale = 2)
    private BigDecimal valorFechamentoInformado;

    @Column(name = "diferenca_fechamento", precision = 19, scale = 2)
    private BigDecimal diferenca;

    @Column(name = "hora_abertura")
    private LocalDateTime horaAbertura;

    @Column(name = "hora_fechamento")
    private LocalDateTime horaFechamento;

    @Column(name = "observacoes_fechamento", length = 500)
    private String observacoesFechamento;

    @ManyToOne
    @JoinColumn(name = "fechado_por_usuario")
    private Usuario fechadoPor;


    public Boolean getFechado() {
        return fechado;
    }

    public void setFechado(Boolean fechado) {
        this.fechado = fechado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public List<Comanda> getComandas() {
        return comandas;
    }

    public void setComandas(List<Comanda> comandas) {
        this.comandas = comandas;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDate getDataCaixa() {
        return dataCaixa;
    }

    public void setDataCaixa(LocalDate dataCaixa) {
        this.dataCaixa = dataCaixa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getValorAbertura() {
        return valorAbertura;
    }

    public void setValorAbertura(BigDecimal valorAbertura) {
        this.valorAbertura = valorAbertura;
    }

    public BigDecimal getValorFechamentoEsperado() {
        return valorFechamentoEsperado;
    }

    public void setValorFechamentoEsperado(BigDecimal valorFechamentoEsperado) {
        this.valorFechamentoEsperado = valorFechamentoEsperado;
    }

    public BigDecimal getValorFechamentoInformado() {
        return valorFechamentoInformado;
    }

    public void setValorFechamentoInformado(BigDecimal valorFechamentoInformado) {
        this.valorFechamentoInformado = valorFechamentoInformado;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public LocalDateTime getHoraAbertura() {
        return horaAbertura;
    }

    public void setHoraAbertura(LocalDateTime horaAbertura) {
        this.horaAbertura = horaAbertura;
    }

    public LocalDateTime getHoraFechamento() {
        return horaFechamento;
    }

    public void setHoraFechamento(LocalDateTime horaFechamento) {
        this.horaFechamento = horaFechamento;
    }

    public String getObservacoesFechamento() {
        return observacoesFechamento;
    }

    public void setObservacoesFechamento(String observacoesFechamento) {
        this.observacoesFechamento = observacoesFechamento;
    }

    public Usuario getFechadoPor() {
        return fechadoPor;
    }

    public void setFechadoPor(Usuario fechadoPor) {
        this.fechadoPor = fechadoPor;
    }
}
