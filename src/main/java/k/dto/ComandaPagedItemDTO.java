package k.dto;

import java.time.LocalDateTime;

import k.model.Comanda;

/**
 * DTO leve para listagem paginada de comandas. Não traz a árvore de pedidos
 * (que infla o payload). Para a versão completa use {@link ComandaResponseDTO}.
 */
public record ComandaPagedItemDTO(
        Long id,
        String nome,
        Long mesaId,
        Long atendenteId,
        Double preco,
        Boolean finalizada,
        LocalDateTime dataInclusao) {

    public ComandaPagedItemDTO(Comanda comanda) {
        this(
                comanda.getId(),
                comanda.getNome(),
                comanda.getMesa() != null ? comanda.getMesa().getId() : null,
                comanda.getAtendente() != null ? comanda.getAtendente().getId() : null,
                comanda.getPreco(),
                comanda.getFinalizada(),
                comanda.getDataInclusao());
    }
}
