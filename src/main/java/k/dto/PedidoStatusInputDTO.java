package k.dto;

/**
 * Input para {@code PATCH /pedido/{id}/status}.
 *
 * @param status novo status do pedido (string case-insensitive: AGUARDANDO,
 *               PREPARANDO, PRONTO, ENTREGUE)
 */
public record PedidoStatusInputDTO(String status) {
}
