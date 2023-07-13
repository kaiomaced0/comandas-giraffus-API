package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.Produto;

public interface ProdutoService {
    
    public List<Produto> getAll();

    public List<Produto> getNome();

    public List<Produto> getId();

    public Response insert(Produto produto);

    public Response update(@PathParam("idProduto") Long idProduto, Produto produto);

    public Response delete(@PathParam("id") Long id);

    public Response retiraEstoque(@PathParam("id") Long id, int quantidade);
    
    public Response adicionaEstoque(@PathParam("id") Long id, int quantidade);
}
