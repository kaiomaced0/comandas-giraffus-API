package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.ProdutoAdicionaRetiraDTO;
import k.dto.ProdutoDTO;
import k.dto.ProdutoResponseDTO;

public interface ProdutoService {

    public List<ProdutoResponseDTO> getAll();

    public List<ProdutoResponseDTO> getNome(String nome);

    public Response getId(Long id);

    public Response insert(ProdutoDTO produto);

    public Response update(@PathParam("idProduto") Long idProduto, ProdutoDTO produto);

    public Response delete(@PathParam("id") Long id);

    public Response retiraEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO);

    public Response adicionaEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO);
}
