package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.CaixaDTO;
import k.dto.CaixaResponseDTO;

public interface CaixaService {
    public List<CaixaResponseDTO> getAll();

    public CaixaResponseDTO getId(@PathParam("id") Long id);

    public Response insert(CaixaDTO caixa);

    public Response delete(@PathParam("id") Long id);

    public Response fechar(@PathParam("id") Long id);
}
