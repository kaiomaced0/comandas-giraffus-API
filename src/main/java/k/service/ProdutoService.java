package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.MovimentoEstoqueResponseDTO;
import k.dto.ProdutoAdicionaRetiraDTO;
import k.dto.ProdutoDTO;
import k.dto.ProdutoResponseDTO;

public interface ProdutoService {

    List<ProdutoResponseDTO> getAll();

    List<ProdutoResponseDTO> getNome(String nome);

    Response getId(Long id);

    Response insert(ProdutoDTO produto);

    Response update(@PathParam("idProduto") Long idProduto, ProdutoDTO produto);

    Response delete(@PathParam("id") Long id);

    Response retiraEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO);

    Response adicionaEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO);

    List<MovimentoEstoqueResponseDTO> listarMovimentacoes(Long idProduto);
}
