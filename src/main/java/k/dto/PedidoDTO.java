package k.dto;

import java.util.List;

public record PedidoDTO(

                List<Long> listIdItemCompra,
                Long idComanda,
                String observacao,
                Integer quantidadePessoas) {

}
