package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.Comanda;

public interface ComandaService {
    public List<Comanda> getAll();

    public List<Comanda> getNome(@PathParam("nome") String nome);

    public Comanda getId(@PathParam("id") Long id);

    public Response insert(Comanda comanda);

    public Response pagar(@PathParam("id") Long id, Long idPagamento);

    public Response delete(@PathParam("id") Long id);

}
