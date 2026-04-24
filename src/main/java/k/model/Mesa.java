package k.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Where;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Where(clause = "ativo = true")
public class Mesa extends EntityClass {

    @ManyToOne
    @JoinColumn(name = "empresa_mesa")
    private Empresa empresa;

    @Column(nullable = false)
    private String identificador;

    private Integer capacidade;

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }
}
