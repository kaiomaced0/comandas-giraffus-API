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

    public Caixa findAbertoPorUsuario(Usuario usuario) {
        return find("usuario = ?1 and fechado = false", usuario).firstResult();
    }

    public List<Caixa> findAbertosPorEmpresa(Empresa empresa) {
        return find("empresa = ?1 and fechado = false", empresa).list();
    }

    public List<Caixa> findPorEmpresa(Empresa empresa) {
        return find("empresa = ?1", empresa).list();
    }
}
