package k.service;

import java.util.List;

import jakarta.ws.rs.core.Response;
import k.dto.PagamentoDTO;
import k.dto.PagamentoDeleteDTO;
import k.dto.PagamentoResponseDTO;

public interface PagamentoService {

    List<PagamentoResponseDTO> getAll();

    PagamentoResponseDTO getId(Long id);

    List<PagamentoResponseDTO> getByComanda(Long idComanda);

    Response insert(PagamentoDTO pagamento);

    Response estornar(Long id);

    Response delete(PagamentoDeleteDTO pagamentoDeleteDTO);
}
