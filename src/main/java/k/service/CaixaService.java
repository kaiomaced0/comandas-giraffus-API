package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.Caixa;

public interface CaixaService {
    public List<Caixa> getAll();

    public List<Caixa> getNome();

    public List<Caixa> getId();

    public Response insert(Caixa caixa);

    public Response delete(@PathParam("id") Long id);

    public Response fechar(@PathParam("id") Long id);
}
