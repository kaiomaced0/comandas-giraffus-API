package k.dto;

import java.util.List;

public record PedidoDTO(

        List<Long> listIdItemCompra,
        String observacao,
        Integer quantidadePessoas) {

}
