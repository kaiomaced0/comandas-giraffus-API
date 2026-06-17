package k.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresa_gateway_config")
@Inheritance(strategy = InheritanceType.JOINED)
public class EmpresaGatewayConfig extends EntityClass {

    @ManyToOne
    @JoinColumn(name = "empresa_gateway")
    private Empresa empresa;

    @Enumerated(EnumType.STRING)
    private TipoGateway tipo;

    // TODO produção: cifrar apiKey/apiSecret em repouso (Onda N)
    private String apiKey;

    // TODO produção: cifrar apiKey/apiSecret em repouso (Onda N)
    private String apiSecret;

    @Enumerated(EnumType.STRING)
    private AmbienteGateway ambiente;

    private Boolean habilitado = false;

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public TipoGateway getTipo() {
        return tipo;
    }

    public void setTipo(TipoGateway tipo) {
        this.tipo = tipo;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public AmbienteGateway getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(AmbienteGateway ambiente) {
        this.ambiente = ambiente;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }
}
