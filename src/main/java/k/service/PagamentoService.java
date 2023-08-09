package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.PagamentoDTO;
import k.dto.PagamentoResponseDTO;

public interface PagamentoService {
    public List<PagamentoResponseDTO> getAll();

    public PagamentoResponseDTO getId(Long id);

    public Response insert(PagamentoDTO pagamento);

    public Response delete(@PathParam("id") Long id, String observacao);

}
