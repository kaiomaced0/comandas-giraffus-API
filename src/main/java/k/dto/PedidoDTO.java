package k.dto;

import java.util.List;

public record PedidoDTO(

                List<ItemCompraDTO> listItemCompraDTO,
                Long idComanda,
                String observacao,
                Integer quantidadePessoas) {

}
