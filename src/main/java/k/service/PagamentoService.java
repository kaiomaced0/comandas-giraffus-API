package k.service;

import java.time.LocalDate;
import java.util.List;

import jakarta.ws.rs.core.Response;
import k.dto.PagamentoDTO;
import k.dto.PagamentoDeleteDTO;
import k.dto.PagamentoEstornarInputDTO;
import k.dto.PagamentoMultiInputDTO;
import k.dto.PagamentoMultiResponseDTO;
import k.dto.PagamentoResponseDTO;
import k.dto.PagedResponse;

public interface PagamentoService {
    public List<PagamentoResponseDTO> getAll();

    public PagamentoResponseDTO getId(Long id);

    public Response insert(PagamentoDTO pagamento);

    public Response delete(PagamentoDeleteDTO pagamentoDeleteDTO);

    // Onda F - pagamentos múltiplos por comanda
    public PagamentoMultiResponseDTO insert(Long comandaId, PagamentoMultiInputDTO dto);

    public PagamentoMultiResponseDTO estornar(Long pagamentoId, PagamentoEstornarInputDTO dto);

    public List<PagamentoMultiResponseDTO> listarPorComanda(Long comandaId);

    /**
     * Listagem paginada de pagamentos da empresa do usuário logado.
     */
    public PagedResponse<PagamentoResponseDTO> list(
            Long caixaId,
            Integer formaPagamento,
            LocalDate from,
            LocalDate to,
            Long usuarioId,
            int page,
            int size);
}
