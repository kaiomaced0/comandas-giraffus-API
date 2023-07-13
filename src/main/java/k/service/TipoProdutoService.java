package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.TipoProduto;

public interface TipoProdutoService {
    public List<TipoProduto> getAll();

    public List<TipoProduto> getNome();

    public List<TipoProduto> getId();

    public Response insert(TipoProduto tipoProduto);

    public Response update(@PathParam("idTipoProduto") Long idTipoProduto, TipoProduto tipoProduto);

    public Response delete(@PathParam("id") Long id);
}
