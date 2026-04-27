package k.dto;

import java.util.List;

/**
 * Resposta paginada genérica para endpoints com offset pagination.
 *
 * <p>Modelo simples (page/size/total), pensado para listagens administrativas e
 * consultas no ERP. Para mobile (scroll infinito), uma estratégia baseada em
 * cursor é mais adequada — ver TODO em {@code docs/api-roadmap.md} (Fase 2).
 *
 * @param data  itens da página atual
 * @param page  índice da página (0-based)
 * @param size  tamanho da página
 * @param total total de elementos (após aplicar filtros)
 */
public record PagedResponse<T>(List<T> data, int page, int size, long total) {
}
