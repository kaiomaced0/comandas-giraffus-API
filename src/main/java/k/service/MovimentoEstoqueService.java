package k.service;

import java.util.List;

import k.dto.MovimentoEstoqueResponseDTO;
import k.model.Produto;
import k.model.TipoMovimentoEstoque;

public interface MovimentoEstoqueService {

    MovimentoEstoqueResponseDTO registrar(Produto p, TipoMovimentoEstoque tipo, int quantidade, String motivo);

    List<MovimentoEstoqueResponseDTO> getByProduto(Long produtoId);
}
