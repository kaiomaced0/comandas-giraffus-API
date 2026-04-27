package k.repository;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import k.model.Caixa;
import k.model.Empresa;
import k.model.Usuario;

@ApplicationScoped
public class CaixaRepository implements PanacheRepository<Caixa> {
    public List<Caixa> findByNome(String nome) {
        if (nome == null)
            return null;
        return find("UPPER(nome) LIKE ?1 ", "%" + nome.toUpperCase() + "%").list();
    }

    public Caixa findAbertoByUsuario(Usuario u) {
        if (u == null) {
            return null;
        }
        return find("usuario = ?1 and fechado = false and ativo = true", u).firstResult();
    }

    public List<Caixa> findAbertosByEmpresa(Empresa e) {
        if (e == null || e.getCaixas() == null) {
            return List.of();
        }
        return e.getCaixas().stream()
                .filter(c -> c != null
                        && Boolean.TRUE.equals(c.getAtivo())
                        && !Boolean.TRUE.equals(c.getFechado()))
                .toList();
    }
}
