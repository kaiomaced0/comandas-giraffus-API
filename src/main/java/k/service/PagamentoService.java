package k.service;

import java.util.List;

import jakarta.ws.rs.core.Response;
import k.dto.PagamentoDTO;
import k.dto.PagamentoDeleteDTO;
import k.dto.PagamentoResponseDTO;

public interface PagamentoService {
    public List<PagamentoResponseDTO> getAll();

    public PagamentoResponseDTO getId(Long id);

    public Response insert(PagamentoDTO pagamento);

    public Response delete(PagamentoDeleteDTO pagamentoDeleteDTO);

}
