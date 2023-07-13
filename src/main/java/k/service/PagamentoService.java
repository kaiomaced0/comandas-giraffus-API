package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.Pagamento;

public interface PagamentoService {
    public List<Pagamento> getAll();

    public List<Pagamento> getNome();

    public List<Pagamento> getId();

    public Response insert(Pagamento pagamento);

    public Response delete(@PathParam("id") Long id, String observacao);

}
