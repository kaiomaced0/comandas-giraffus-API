package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.TipoProdutoDTO;
import k.dto.TipoProdutoResponseDTO;

public interface TipoProdutoService {
    public List<TipoProdutoResponseDTO> getAll();

    public List<TipoProdutoResponseDTO> getNome(String nome);

    public TipoProdutoResponseDTO getId(Long id);

    public Response insert(TipoProdutoDTO tipoProduto);

    public Response update(@PathParam("idTipoProduto") Long idTipoProduto, TipoProdutoDTO tipoProduto);

    public Response delete(@PathParam("id") Long id);
}
