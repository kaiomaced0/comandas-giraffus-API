package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.ComandaDTO;
import k.dto.ComandaPagarDTO;
import k.dto.ComandaResponseDTO;

public interface ComandaService {
    public List<ComandaResponseDTO> getAll();

    public List<ComandaResponseDTO> getEmAberto();

    public List<ComandaResponseDTO> getAllComandasAdm(Long idEmpresa);

    public List<ComandaResponseDTO> getNome(@PathParam("nome") String nome);

    public ComandaResponseDTO getId(@PathParam("id") Long id);

    public Response insert(ComandaDTO comanda);

    public Response pagar(ComandaPagarDTO comandaPagarDTO);

    public Response delete(@PathParam("id") Long id);

    public Response updatePreco(Long id);

}
